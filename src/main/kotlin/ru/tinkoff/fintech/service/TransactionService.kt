package ru.tinkoff.fintech.service

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.client.NotificationServiceClient
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.*
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationMessageGenerator

@Service
class TransactionService @Autowired constructor(
    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val notificationServiceClient: NotificationServiceClient,
    private val cashbackCalculator: CashbackCalculator,
    private val notificationMessageGenerator: NotificationMessageGenerator,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository
) {

    @Value("\${paimentprocessing.sign}")
    private val sign: String = ""

    fun process(transaction: Transaction) {
        val card = cardServiceClient.getCard(transaction.cardNumber)
        val client = clientService.getClient(card.client)
        val loyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)

        val payments: Set<LoyaltyPaymentEntity> = loyaltyPaymentRepository.findAllBySignAndCardIdAndDateTimeAfter(sign, card.id, transaction.time)

        val transactionInfo = makeTransactionInfo(loyaltyProgram, transaction, client, cashbackPerMonth(payments))
        val cashback = cashbackCalculator.calculateCashback(transactionInfo)

        val notification = makeNotificationMessageInfo(client, card, cashback, transaction, loyaltyProgram)

        val message = notificationMessageGenerator.generateMessage(notification)

        notificationServiceClient.sendNotification(client.id, message)

        val loyaltyPaymentEntity = makeLoyaltyPaymentEntity(cashback, card, sign, transaction)

        val loyaltyPaymentEntity2 = loyaltyPaymentRepository.save(loyaltyPaymentEntity)

        println(loyaltyPaymentEntity2)
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private fun cashbackPerMonth(payments: Set<LoyaltyPaymentEntity>): Double {
        var sum = 0.0
        payments.forEach{payment -> sum += payment.value}
        return sum
    }

    private fun makeTransactionInfo(loyaltyProgram: LoyaltyProgram, transaction: Transaction, client: Client, cashbackPerMonth: Double): TransactionInfo {
        return TransactionInfo(
            loyaltyProgramName = loyaltyProgram.name,
            transactionSum = transaction.value,
            cashbackTotalValue = cashbackPerMonth,
            mccCode = transaction.mccCode,
            clientBirthDate = client.birthDate,
            firstName = client.firstName,
            lastName = client.lastName,
            middleName = client.middleName
        )
    }

    private fun makeNotificationMessageInfo(client: Client, card: Card, cashback: Double, transaction: Transaction, loyaltyProgram: LoyaltyProgram): NotificationMessageInfo {
        return NotificationMessageInfo(
            name = client.firstName,
            cardNumber = card.cardNumber,
            cashback = cashback,
            transactionSum = transaction.value,
            category = loyaltyProgram.name,
            transactionDate = transaction.time
        )
    }

    private fun makeLoyaltyPaymentEntity(cashback: Double, card: Card, sign: String, transaction: Transaction): LoyaltyPaymentEntity {
        return LoyaltyPaymentEntity(
            value = cashback,
            cardId = card.id,
            sign = sign,
            transactionId = transaction.transactionId,
            dateTime = transaction.time
        )
    }
}