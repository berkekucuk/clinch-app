package com.berkekucuk.mmaapp.data.repository

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
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.filterNotNull

class EventRepositoryImpl(
    private val remoteDataSource: EventRemoteDataSource,
    private val eventDao: EventDao,
    private val fightDao: FightDao,
    private val dateTimeProvider: DateTimeProvider,
    private val rateLimiter: RateLimiter
) : EventRepository {

    private companion object {
        private const val KEY_REFRESH_PENDING_EVENTS = "refresh_pending_events"
        fun getSyncKey(year: Int) = "sync_events_$year"
        fun getRefreshEventKey(eventId: String) = "refresh_event_$eventId"
    }

    override fun getUpcomingEvents(): Flow<List<Event>> {
        return eventDao.getUpcomingEvents()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }

    override fun getCompletedEventsByYear(year: Int): Flow<List<Event>> {
        return eventDao.getCompletedEventsByYear(year)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }

    override fun getEventById(eventId: String): Flow<Event> {
        return eventDao.getEventById(eventId)
            .filterNotNull()
            .map { it.toDomain() }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }

    override suspend fun initializeEvents(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val currentYear = dateTimeProvider.currentYear
            if (needsInitialSync(currentYear)) {
                syncEventsByYear(currentYear)
            } else {
                syncPendingEvents()
            }
        }
    }

    override suspend fun syncEventById(eventId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val refreshKey = getRefreshEventKey(eventId)
            runCatching {
                if (!rateLimiter.shouldFetch(refreshKey)) {
                    return@runCatching
                }
                val events = remoteDataSource.fetchEventById(eventId)
                saveEventsAndFights(events)
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(refreshKey)
            }
        }
    }

    override suspend fun syncEventsByYear(year: Int, forceRefresh: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val syncKey = getSyncKey(year)
            runCatching {
                if (!forceRefresh && eventDao.isYearFullySynced(year)) return@runCatching
                if (!rateLimiter.shouldFetch(syncKey)) return@runCatching
                val events = remoteDataSource.fetchEventsByYear(year)
                if (events.isNotEmpty()) {
                    saveEventsAndFights(events)
                    eventDao.markYearSynced(SyncedYearEntity(year))
                }
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey)
            }
        }
    }

    override suspend fun isYearSynced(year: Int): Boolean {
        return withContext(Dispatchers.IO) { eventDao.isYearFullySynced(year) }
    }

    override suspend fun syncPendingEvents(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(KEY_REFRESH_PENDING_EVENTS)) return@runCatching
                val syncAfterDate = eventDao.getOldestPendingEventDate() ?: dateTimeProvider.now
                val events = remoteDataSource.fetchEventsAfter(syncAfterDate)
                saveEventsAndFights(events)
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(KEY_REFRESH_PENDING_EVENTS)
            }
        }
    }

    override suspend fun hasEventById(eventId: String): Boolean {
        return withContext(Dispatchers.IO) { eventDao.hasEventById(eventId) }
    }

    private suspend fun saveEventsAndFights(events: List<EventDto>) {
        if (events.isEmpty()) return
        eventDao.upsertEvents(events.map { it.toEntity() })

        val eventIds = events.map { it.eventId }
        fightDao.deleteFightsByEventIds(eventIds)

        val allFights = events.flatMap { event ->
            event.fights?.map { it.toEntity() } ?: emptyList()
        }
        if (allFights.isNotEmpty()) {
            fightDao.upsertFights(allFights)
        }
    }

    private suspend fun needsInitialSync(year: Int): Boolean {
        return !eventDao.hasEventsForYear(year)
    }
}
