package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@Service
class NotificationServiceClientImpl(val webClient: WebClient): NotificationServiceClient {

    @Value("\${paimentprocessing.services.uri.notification}")
    private val uri: String? = null

    override fun sendNotification(clientId: String, message: String) {

        webClient.post()
            .uri("$uri/$clientId/message")
            .body(Mono.just(message), String::class.java)
            .retrieve()
    }
}