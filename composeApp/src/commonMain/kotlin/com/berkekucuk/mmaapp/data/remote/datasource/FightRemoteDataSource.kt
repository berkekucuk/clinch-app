package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.FightDto

interface FightRemoteDataSource {
    suspend fun fetchFight(fightId: String): FightDto?
}
