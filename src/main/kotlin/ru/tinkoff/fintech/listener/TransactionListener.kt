package ru.tinkoff.fintech.listener

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.service.TransactionService
import ru.tinkoff.fintech.util.Util.Factory.stringToTransaction

@Component
class TransactionListener @Autowired constructor(
    private val transactionService: TransactionService
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
}


