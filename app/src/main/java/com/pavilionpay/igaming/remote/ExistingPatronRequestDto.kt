package com.pavilionpay.igaming.remote

import kotlinx.serialization.Serializable

@Serializable
data class ExistingPatronRequestDto(
        val patronType: String,
        val patronID: String,
        val vipCardNumber: String,
        val dateOfBirth: String,
        val remainingDailyDeposit: Double,
        val walletBalance: Double,
        val transactionID: String,
        val transactionAmount: Double,
        val transactionType: Byte,
        val returnURL: String,
        val productType: String,
        val androidPackageName: String,
)
