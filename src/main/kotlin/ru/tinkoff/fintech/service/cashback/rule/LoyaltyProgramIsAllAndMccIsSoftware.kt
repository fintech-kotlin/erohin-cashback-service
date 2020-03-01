package ru.tinkoff.fintech.service.cashback.rule

import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.LOYALTY_PROGRAM_ALL
import ru.tinkoff.fintech.service.cashback.MCC_SOFTWARE

class LoyaltyProgramIsAllAndMccIsSoftware(private val transactionInfo: TransactionInfo): CashbackRule {

    override fun getCashback(): Double {
        if (transactionInfo.mccCode == MCC_SOFTWARE &&
            transactionInfo.loyaltyProgramName == LOYALTY_PROGRAM_ALL &&
            isPalindromeWithOneReplace((transactionInfo.transactionSum * 100).toLong().toString())) {
            val percent = lcm(transactionInfo.firstName.length, transactionInfo.lastName.length) / 1000.0
            return transactionInfo.transactionSum * percent / 100.0
        }
        return 0.0
    }

    private fun isPalindromeWithOneReplace(str: String): Boolean {
        var numberOfReplacements = 0
        for (i in 0 until str.length/2) {
            if (str[i] != str[str.length - 1 - i]) {
                numberOfReplacements++
            }
            if (numberOfReplacements > 1) return false
        }
        return true
    }

    private fun lcm(num1: Int, num2: Int): Int {
        var gcd = 1
        var i = 1
        while (i <= num1 && i <= num2) {
            if (num1 % i == 0 && num2 % i == 0)
                gcd = i
            ++i
        }

        return num1 * num2 / gcd
    }
}