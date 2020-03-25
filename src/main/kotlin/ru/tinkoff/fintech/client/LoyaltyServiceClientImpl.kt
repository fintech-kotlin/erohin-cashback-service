package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.tinkoff.fintech.model.LoyaltyProgram

@Service
class LoyaltyServiceClientImpl(val webClient: WebClient): LoyaltyServiceClient {

    @Value("\${paimentprocessing.services.uri.loyalty}")
    private val uri: String? = null

    override fun getLoyaltyProgram(id: String): LoyaltyProgram {
        val response = webClient.get()
            .uri("$uri/$id")
            .retrieve()
            .bodyToMono(LoyaltyProgram::class.java)

        return response.blockOptional().get()
    }
}