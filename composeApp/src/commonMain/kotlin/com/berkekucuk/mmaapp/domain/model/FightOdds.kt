package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

enum class OddsMethod(val dbValue: String) {
    ANY("ANY"),
    KO_TKO("KO/TKO"),
    SUBMISSION("SUB"),
    DECISION("DEC");

    companion object {
        val METHODS = listOf(KO_TKO, SUBMISSION, DECISION)
        val FINISH_METHODS = listOf(KO_TKO, SUBMISSION)

        fun fromDbValue(value: String): OddsMethod? = entries.firstOrNull { it.dbValue == value }
    }
}

@Immutable
data class FightOdds(
    val fighterId: String,
    val method: OddsMethod,
    val round: Int,
    val oddsValue: Int,
)

fun List<FightOdds>.oddsFor(fighterId: String?, method: OddsMethod, round: Int = 0): Int? =
    firstOrNull { it.fighterId == fighterId && it.method == method && it.round == round }?.oddsValue

fun List<FightOdds>.moneyline(fighterId: String?): Int? = oddsFor(fighterId, OddsMethod.ANY)
