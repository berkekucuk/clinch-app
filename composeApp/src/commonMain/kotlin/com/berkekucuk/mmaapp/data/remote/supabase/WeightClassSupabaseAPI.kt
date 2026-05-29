package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.WeightClassRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.WeightClassDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class WeightClassSupabaseAPI(
    private val client: SupabaseClient
) : WeightClassRemoteDataSource {

    override suspend fun fetchWeightClasses(): List<WeightClassDto> {
        return client.from("weight_class_view")
            .select()
            .decodeList<WeightClassDto>()
    }
}