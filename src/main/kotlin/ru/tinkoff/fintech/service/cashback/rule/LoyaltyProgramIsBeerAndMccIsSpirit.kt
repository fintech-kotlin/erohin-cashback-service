package ru.tinkoff.fintech.service.cashback.rule

import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.FIRST_NAME_OLEG
import ru.tinkoff.fintech.service.cashback.LAST_NAME_OLEGOV
import ru.tinkoff.fintech.service.cashback.LOYALTY_PROGRAM_BEER
import ru.tinkoff.fintech.service.cashback.MCC_BEER
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class LoyaltyProgramIsBeerAndMccIsSpirit(private val transactionInfo: TransactionInfo): CashbackRule {

    val months = mapOf(Month.JANUARY.value to "январь",
        Month.FEBRUARY.value to "февраль",
        Month.MARCH.value to "март",
        Month.APRIL.value to "апрель",
        Month.MAY.value to "май",
        Month.JUNE.value to "июнь",
        Month.JULY.value to "июль",
        Month.AUGUST.value to "август",
        Month.SEPTEMBER.value to "сентябрь",
        Month.OCTOBER.value to "октябрь",
        Month.NOVEMBER.value to "ноябрь",
        Month.DECEMBER.value to "декабрь")

    override fun getCashback(): Double {
        if (transactionInfo.loyaltyProgramName == LOYALTY_PROGRAM_BEER && transactionInfo.mccCode == MCC_BEER) {
            return if (transactionInfo.firstName.toLowerCase() == FIRST_NAME_OLEG && transactionInfo.lastName.toLowerCase() == LAST_NAME_OLEGOV) {
                transactionInfo.transactionSum * 0.1
            } else if (transactionInfo.firstName.toLowerCase() == FIRST_NAME_OLEG) {
                transactionInfo.transactionSum * 0.07
            } else if (transactionInfo.firstName[0].toLowerCase() == getFirstLetterOfMonth(LocalDate.now().month)) {
                transactionInfo.transactionSum * 0.05
            } else if (transactionInfo.firstName[0].toLowerCase() == getFirstLetterOfMonth(LocalDate.now().month.plus(1))) {
                transactionInfo.transactionSum * 0.03
            } else if (transactionInfo.firstName[0].toLowerCase() == getFirstLetterOfMonth(LocalDate.now().month.minus(1))) {
                transactionInfo.transactionSum * 0.03
            } else {
                transactionInfo.transactionSum * 0.02
            }
        }
        return 0.0
    }

    private fun getFirstLetterOfMonth(month: Month): Char {
        return months[month.value].toString()[0]
    }
}