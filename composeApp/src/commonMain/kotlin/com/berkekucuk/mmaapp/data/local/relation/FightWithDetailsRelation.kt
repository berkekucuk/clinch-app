package com.berkekucuk.mmaapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import com.berkekucuk.mmaapp.data.local.entity.FightOddsEntity
import com.berkekucuk.mmaapp.data.local.entity.FightStatEntity

data class FightWithDetailsRelation(
    @Embedded val fight: FightEntity,
    @Relation(
        parentColumn = "fight_id",
        entityColumn = "fight_id"
    )
    val stats: List<FightStatEntity>,
    @Relation(
        parentColumn = "fight_id",
        entityColumn = "fight_id"
    )
    val odds: List<FightOddsEntity>
)
