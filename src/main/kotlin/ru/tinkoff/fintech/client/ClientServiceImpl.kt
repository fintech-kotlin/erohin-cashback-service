package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client

@Service
class ClientServiceImpl: ClientService {

    @Value("\${paimentprocessing.services.uri.client}")
    private val uri: String? = null

    override fun getClient(id: String): Client {
        val response = RestTemplate().getForEntity("$uri/$id;", Client::class.java)

        if (response.statusCode.is2xxSuccessful) {
            val client: Client = response.body
            println(client)
            return client
        } else {
            throw HttpClientErrorException(response.statusCode ,"Сервер вернул ошибку, ответ: '${response.statusCode}'")
        }
    }
}