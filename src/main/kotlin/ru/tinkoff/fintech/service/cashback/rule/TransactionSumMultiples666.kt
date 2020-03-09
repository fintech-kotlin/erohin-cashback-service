package ru.tinkoff.fintech.service.cashback.rule

import ru.tinkoff.fintech.model.TransactionInfo

class TransactionSumMultiples666(private val transactionInfo: TransactionInfo): CashbackRule {

    override fun getCashback(): Double {
        return if (transactionInfo.transactionSum % 666 == 0.0) 6.66 else 0.0
    }
}