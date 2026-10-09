package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getUpcomingEvents(): Flow<List<Event>>
    fun getCompletedEventsByYear(year: Int): Flow<List<Event>>
    fun getEventById(eventId: String): Flow<Event>
    suspend fun initializeEvents(): Result<Unit>
    suspend fun syncEventById(eventId: String): Result<Unit>
    suspend fun syncEventsByYear(year: Int, forceRefresh: Boolean = false): Result<Unit>
    suspend fun isYearSynced(year: Int): Boolean
    suspend fun syncPendingEvents(): Result<Unit>
    suspend fun hasEventById(eventId: String): Boolean
    suspend fun getNearestActiveEvent(): Event?
}
