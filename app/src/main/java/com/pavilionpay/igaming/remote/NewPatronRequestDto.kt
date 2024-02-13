package com.pavilionpay.igaming.remote

import kotlinx.serialization.Serializable

@Serializable
data class NewPatronRequestDto(
    val patronType: String,
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
    val walletBalance: Double,
    val remainingDailyDeposit: Double,
    val transactionId: String,
    val transactionAmount: Double,
    val returnURL: String,
    val productType: String,
    val androidPackageName: String,
    val transactionType: Int,
)
