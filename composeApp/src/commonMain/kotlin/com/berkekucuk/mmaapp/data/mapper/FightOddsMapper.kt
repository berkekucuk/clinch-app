package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.FightOddsEntity
import com.berkekucuk.mmaapp.data.remote.dto.FightDto
import com.berkekucuk.mmaapp.data.remote.dto.FightOddsDto
import com.berkekucuk.mmaapp.domain.model.FightOdds
import com.berkekucuk.mmaapp.domain.model.OddsMethod

fun FightOddsDto.toEntity(fightId: String): FightOddsEntity = FightOddsEntity(
    fightId = fightId,
    fighterId = fighterId,
    method = method,
    round = round,
    oddsValue = oddsValue,
)

fun FightOddsEntity.toDomain(): FightOdds? = fightOdds(fighterId, method, round, oddsValue)

fun FightOddsDto.toDomain(): FightOdds? = fightOdds(fighterId, method, round, oddsValue)

fun List<FightDto>.toOddsEntities(): List<FightOddsEntity> =
    flatMap { fight -> fight.odds.orEmpty().map { it.toEntity(fight.fightId) } }

private fun fightOdds(fighterId: String, method: String, round: Int, oddsValue: Int): FightOdds? {
    val oddsMethod = OddsMethod.fromDbValue(method) ?: return null
    return FightOdds(
        fighterId = fighterId,
        method = oddsMethod,
        round = round,
        oddsValue = oddsValue,
    )
}
