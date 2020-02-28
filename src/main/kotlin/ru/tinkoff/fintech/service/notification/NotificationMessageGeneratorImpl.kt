package ru.tinkoff.fintech.service.notification

import ru.tinkoff.fintech.model.NotificationMessageInfo

class NotificationMessageGeneratorImpl(
    private val cardNumberMasker: CardNumberMasker
) : NotificationMessageGenerator {

    override fun generateMessage(notificationMessageInfo: NotificationMessageInfo): String {
        var cardNumber = cardNumberMasker.mask(notificationMessageInfo.cardNumber)
        return """
                Уважаемый, ${notificationMessageInfo.name}!
                Спешим Вам сообщить, что на карту $cardNumber
                начислен cashback в размере ${notificationMessageInfo.cashback}
                за категорию ${notificationMessageInfo.category}.
                Спасибо за покупку ${notificationMessageInfo.transactionDate}
        """.trimIndent()
    }
}