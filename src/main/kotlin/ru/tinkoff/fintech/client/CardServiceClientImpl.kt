package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.config.RestConfiguration
import ru.tinkoff.fintech.model.Card
import javax.annotation.Resource

@Service
class CardServiceClientImpl(val myRestTemplate: RestTemplate): CardServiceClient {

    @Value("\${paimentprocessing.services.uri.card}")
    private val uri: String? = null

    override fun getCard(id: String): Card {
        val response = myRestTemplate.getForEntity("$uri/$id", Card::class.java)

        if (response.statusCode.is2xxSuccessful) {
            val card: Card = response.body
            println(card)
            return card
        } else {
            throw HttpClientErrorException(response.statusCode ,"Сервер вернул ошибку, ответ: '${response.statusCode}'")
        }
    }
}