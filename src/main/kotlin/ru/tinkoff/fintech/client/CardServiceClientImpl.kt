package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.tinkoff.fintech.model.Card

@Service
class CardServiceClientImpl(val webClient: WebClient): CardServiceClient {

    @Value("\${paimentprocessing.services.uri.card}")
    private val uri: String? = null

    override fun getCard(id: String): Card {

        val response = webClient.get()
            .uri("$uri/$id")
            .retrieve()
            .bodyToMono(Card::class.java)

        return response.blockOptional().get()
    }
}