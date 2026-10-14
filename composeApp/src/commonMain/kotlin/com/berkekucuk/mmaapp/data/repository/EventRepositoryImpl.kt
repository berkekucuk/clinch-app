package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.core.utils.DateTimeProvider
import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.EventDao
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.remote.datasource.EventRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.Event
import com.berkekucuk.mmaapp.domain.repository.EventRepository
import com.berkekucuk.mmaapp.data.local.entity.SyncedYearEntity
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.dto.EventDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.filterNotNull

class EventRepositoryImpl(
    private val remoteDataSource: EventRemoteDataSource,
    private val eventDao: EventDao,
    private val fightDao: FightDao,
    private val dateTimeProvider: DateTimeProvider,
    private val rateLimiter: RateLimiter
) : EventRepository {

    private companion object {
        const val KEY_REFRESH_PENDING_EVENTS = "refresh_pending_events"
        fun getSyncKey(year: Int) = "sync_events_$year"
        fun getRefreshEventKey(eventId: String) = "refresh_event_$eventId"
    }

    override fun getUpcomingEvents(): Flow<List<Event>> {
        return eventDao.getUpcomingEvents()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override fun getCompletedEventsByYear(year: Int): Flow<List<Event>> {
        return eventDao.getCompletedEventsByYear(year)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override fun getEventById(eventId: String): Flow<Event> {
        return eventDao.getEventById(eventId)
            .filterNotNull()
            .map { it.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun initializeEvents(): Result<Unit> {
        val currentYear = dateTimeProvider.currentYear
        return if (needsInitialSync(currentYear)) {
            syncEventsByYear(currentYear).onSuccess {
                syncEventsByYear(currentYear + 1)
            }
        } else {
            syncPendingEvents()
        }
    }

    override suspend fun syncEventById(eventId: String): Result<Unit> {
        val refreshKey = getRefreshEventKey(eventId)
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(refreshKey)) {
                return@runSuspendCatching
            }
            val events = remoteDataSource.fetchEventById(eventId)
            saveEventsAndFights(events)
        }.onFailure {
            rateLimiter.reset(refreshKey)
        }
    }

    override suspend fun syncEventsByYear(year: Int, forceRefresh: Boolean): Result<Unit> {
        val syncKey = getSyncKey(year)
        return runSuspendCatching(Dispatchers.Default) {
            if (!forceRefresh && eventDao.isYearFullySynced(year)) return@runSuspendCatching
            if (!rateLimiter.shouldFetch(syncKey)) return@runSuspendCatching
            val events = remoteDataSource.fetchEventsByYear(year)
            if (events.isNotEmpty()) {
                saveEventsAndFights(events)
                eventDao.markYearSynced(SyncedYearEntity(year))
            }
        }.onFailure {
            rateLimiter.reset(syncKey)
        }
    }

    override suspend fun isYearSynced(year: Int): Boolean {
        return eventDao.isYearFullySynced(year)
    }

    override suspend fun syncPendingEvents(): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(KEY_REFRESH_PENDING_EVENTS)) return@runSuspendCatching
            val syncAfterDate = eventDao.getOldestPendingEventDate() ?: dateTimeProvider.now
            val events = remoteDataSource.fetchEventsAfter(syncAfterDate)
            saveEventsAndFights(events)
        }.onFailure {
            rateLimiter.reset(KEY_REFRESH_PENDING_EVENTS)
        }
    }

    override suspend fun hasEventById(eventId: String): Boolean {
        return eventDao.hasEventById(eventId)
    }

    override suspend fun getNearestActiveEvent(): Event? {
        return getUpcomingEvents().first().firstOrNull()
    }

    private suspend fun saveEventsAndFights(events: List<EventDto>) {
        if (events.isEmpty()) return
        eventDao.upsertEvents(events.map { it.toEntity() })

        val eventsMap = events.associate { event ->
            event.eventId to (event.fights?.map { it.toEntity() } ?: emptyList())
        }
        fightDao.replaceFights(eventsMap)
    }

    private suspend fun needsInitialSync(year: Int): Boolean {
        return !eventDao.hasEventsForYear(year)
    }
}
