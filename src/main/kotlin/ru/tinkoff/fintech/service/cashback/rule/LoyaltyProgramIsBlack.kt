package ru.tinkoff.fintech.service.cashback.rule

import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.LOYALTY_PROGRAM_BLACK

class LoyaltyProgramIsBlack(private val transactionInfo: TransactionInfo) : CashbackRule {

    override fun getCashback(): Double {
         return if (transactionInfo.loyaltyProgramName == LOYALTY_PROGRAM_BLACK) transactionInfo.transactionSum * 0.01 else 0.0
    }
}