package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.tinkoff.fintech.model.Client

@Service
class ClientServiceImpl(val webClient: WebClient): ClientService {

    @Value("\${paimentprocessing.services.uri.client}")
    private val uri: String? = null

    override fun getClient(id: String): Client {
        val response = webClient.get()
            .uri("$uri/$id;")
            .retrieve()
            .bodyToMono(Client::class.java)

        return response.blockOptional().get()
    }
}