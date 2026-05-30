package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.FighterDto

interface FighterRemoteDataSource {

    suspend fun fetchFighter(id: String): FighterDto

    suspend fun searchFighters(query: String): List<FighterDto>
}