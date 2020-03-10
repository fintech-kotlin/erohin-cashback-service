package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class NotificationServiceClientImpl: NotificationServiceClient {

    @Value("\${paimentprocessing.services.uri.notification}")
    private val uri: String? = null

    override fun sendNotification(clientId: String, message: String) {
        val rest = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val request = HttpEntity(message, headers)
        rest.postForObject("$uri/$clientId/message", request, String::class.java)
    }
}