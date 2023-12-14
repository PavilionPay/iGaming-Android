package com.pavilionpay.igaming.remote

import kotlinx.serialization.Serializable

@Serializable
data class NewUserSessionRequestDto(
    val patronId: String,
    val firstName: String,
    val middleInitial: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String,
    val mobilePhone: String,
    val streetName: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    val idType: String,
    val idNumber: String,
    val idState: String? = "",
    val routingNumber: String,
    val accountNumber: String,
    val walletBalance: String,
    val remainingDailyDeposit: String,
    val transactionId: String,
    val transactionAmount: Float,
    val returnURL: String,
    val productType: String,
    val androidPackageName: String,
)
