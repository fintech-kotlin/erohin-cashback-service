package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.LoyaltyProgram

@Service
class LoyaltyServiceClientImpl: LoyaltyServiceClient {

    @Value("\${paimentprocessing.services.uri.loyalty}")
    private val uri: String? = null

    override fun getLoyaltyProgram(id: String): LoyaltyProgram {
        val response = RestTemplate().getForEntity("$uri/$id", LoyaltyProgram::class.java)

        if (response.statusCode.is2xxSuccessful) {
            val loyaltyProgram: LoyaltyProgram = response.body
            println(loyaltyProgram)
            return loyaltyProgram
        } else {
            throw HttpClientErrorException(response.statusCode ,"Сервер вернул ошибку, ответ: '${response.statusCode}'")
        }
    }
}