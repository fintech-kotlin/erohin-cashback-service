package ru.tinkoff.fintech.db.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "loyalty_payment")
data class LoyaltyPaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val value: Double,

    @field:Column(name = "CARD_ID")
    val cardId: String,

    val sign: String,

    @field:Column(name = "TRANSACTION_ID")
    val transactionId: String,

    @field:Column(name = "date_time")
    val dateTime: LocalDateTime
)
