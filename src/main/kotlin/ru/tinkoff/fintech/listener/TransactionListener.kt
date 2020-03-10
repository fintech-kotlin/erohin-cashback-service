package ru.tinkoff.fintech.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.service.TransactionService

@Component
class TransactionListener(
    private val transactionService: TransactionService,
    private val myObjectMapper: ObjectMapper
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    @KafkaListener(topics = ["\${paimentprocessing.kafka.consumer.topic}"])
    fun onMessage(message: String) {
        try {
            val transaction = stringToTransaction(message)
            transactionService.process(transaction)
        } catch (e: Exception) {
            LOGGER.info("Сообщение не удалось обработать: '$message'" , e)
        }
    }

    private fun stringToTransaction(str: String): Transaction {
        return myObjectMapper.readValue(str, Transaction::class.java)
    }
}


