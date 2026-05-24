package com.berkekucuk.mmaapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.berkekucuk.mmaapp.data.local.entity.FighterEntity
import com.berkekucuk.mmaapp.data.local.entity.InteractionEntity

data class InteractionWithFighterRelation(
    @Embedded val interaction: InteractionEntity,

    @Relation(
        parentColumn = "fighter_id",
        entityColumn = "fighter_id"
    )
    val fighter: FighterEntity?
)