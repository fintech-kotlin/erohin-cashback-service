package ru.tinkoff.fintech.service.cashback

import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.rule.LoyaltyProgramIsAllAndMccIsSoftware
import ru.tinkoff.fintech.service.cashback.rule.LoyaltyProgramIsBeerAndMccIsSpirit
import ru.tinkoff.fintech.service.cashback.rule.LoyaltyProgramIsBlack
import ru.tinkoff.fintech.service.cashback.rule.TransactionSumMultiples666
import kotlin.math.round

internal const val LOYALTY_PROGRAM_BLACK = "BLACK"
internal const val LOYALTY_PROGRAM_ALL = "ALL"
internal const val LOYALTY_PROGRAM_BEER = "BEER"
internal const val MAX_CASH_BACK = 3000.0
internal const val MCC_SOFTWARE = 5734
internal const val MCC_BEER = 5921
internal const val FIRST_NAME_OLEG = "олег"
internal const val LAST_NAME_OLEGOV = "олегов"

@Component
class CashbackCalculatorImpl : CashbackCalculator {

    override fun calculateCashback(transactionInfo: TransactionInfo): Double {
        var cashback = 0.0
        cashback += LoyaltyProgramIsBlack(transactionInfo).getCashback()
        cashback += TransactionSumMultiples666(transactionInfo).getCashback()
        cashback += LoyaltyProgramIsAllAndMccIsSoftware(transactionInfo).getCashback()
        cashback += LoyaltyProgramIsBeerAndMccIsSpirit(transactionInfo).getCashback()

        if (cashback + transactionInfo.cashbackTotalValue >= 3000){
            return 3000 - transactionInfo.cashbackTotalValue
        }

        return round(cashback * 100) / 100
    }
}