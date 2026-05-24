package com.berkekucuk.mmaapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.berkekucuk.mmaapp.data.local.entity.InteractionEntity
import com.berkekucuk.mmaapp.data.local.entity.UserEntity

data class UserProfileRelation(
    @Embedded val user: UserEntity,

    @Relation(
        entity = InteractionEntity::class,
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val interactions: List<InteractionWithFighterRelation>
)
