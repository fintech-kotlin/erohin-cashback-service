package ru.tinkoff.fintech.service

import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
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
import javax.swing.text.html.parser.Entity

@Service
class TransactionService (
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

    fun process(transaction: Transaction) = CoroutineScope(Dispatchers.Default).launch {

        val card = cardServiceClient.getCard(transaction.cardNumber)

        val clientDef = async {
            clientService.getClient(card.client)
        }
        val loyaltyProgramDef = async {
            loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)
        }

        val payments = loyaltyPaymentRepository.findAllBySignAndCardIdAndDateTimeAfter(sign, card.id, transaction.time)

        val client = clientDef.await()
        val loyaltyProgram = loyaltyProgramDef.await()

        val cashback = cashbackCalculator.calculateCashback(makeTransactionInfo(loyaltyProgram, transaction, client, cashbackPerMonth(payments)))
        val notification = makeNotificationMessageInfo(client, card, cashback, transaction, loyaltyProgram)
        val message = notificationMessageGenerator.generateMessage(notification)

        launch {
            notificationServiceClient.sendNotification(client.id, message)
        }

        val loyaltyPaymentEntity = makeLoyaltyPaymentEntity(cashback, card, sign, transaction)
        loyaltyPaymentRepository.save(loyaltyPaymentEntity)
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