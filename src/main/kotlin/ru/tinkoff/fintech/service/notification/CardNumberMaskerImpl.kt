package ru.tinkoff.fintech.service.notification

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class CardNumberMaskerImpl: CardNumberMasker {

    override fun mask(cardNumber: String, maskChar: Char, start: Int, end: Int): String {
        if (cardNumber.isEmpty()) return ""
        val end = getEndSequence(end, cardNumber.length)
        return cardNumber.replaceRange(start, end, maskChar.toString().repeat(end - start))
    }

    private fun getEndSequence(end: Int, length: Int) : Int {
        return if (end > length) length else end
    }
}