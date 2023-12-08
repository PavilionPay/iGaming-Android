package com.pavilionpay.igaming.presentation.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavilionpay.igaming.BuildConfig
import com.pavilionpay.igaming.core.Resource
import com.pavilionpay.igaming.remote.ExistingPatronRequestDto
import com.pavilionpay.igaming.remote.HttpRoutes
import com.pavilionpay.igaming.remote.NewUserSessionRequestDto
import com.pavilionpay.igaming.remote.PavilionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PavilionPlaidViewModel(
    private val pavilionService: PavilionService,
) : ViewModel() {
    private val _patronSessionUrlState = MutableStateFlow(PavilionPlaidState())
    val patronSessionUrlState: StateFlow<PavilionPlaidState> = _patronSessionUrlState

    fun initializePatronSession(
        patronType: String,
        amount: Float,
        mode: String,
        packageName: String,
    ) {
        viewModelScope.launch {
            val url = queryServiceForSession(
                patronType = patronType,
                amount = amount,
                mode = mode,
                packageName = packageName,
            )
            _patronSessionUrlState.update {
                it.copy(
                    patronSessionUrl = url ?: "",
                    patronSessionRedirectUrl = if (url.isNullOrBlank()) "" else redirectUrl,
                )
            }
        }
    }

    private suspend fun queryServiceForSession(
        patronType: String,
        amount: Float,
        mode: String,
        packageName: String,
    ): String? {
        // Implement your logic to load Pavilion SDK and return the URL
        return withContext(Dispatchers.IO) {
            val patronResponseDtoResult = when (patronType) {
                "new" -> {
                    val payload = NewUserSessionRequestDto(
                        patronId = UUID.randomUUID().toString(),
                        firstName = "Jane",
                        middleInitial = "",
                        lastName = "Public",
                        dateOfBirth = "01/22/1981",
                        email = "Jane@Jane.com",
                        mobilePhone = "3023492103",
                        streetName = "1301 E Main ST",
                        city = "Carbondale",
                        state = "IL",
                        zip = "62901",
                        country = "USA",
                        idType = "DL",
                        idNumber = "P7948777775", // 5 for new
                        idState = "IL",
                        routingNumber = "",
                        accountNumber = "",
                        walletBalance = "1000",
                        remainingDailyDeposit = "1000",
                        transactionId = UUID.randomUUID().toString().replace("-", "").substring(1..24),
                        transactionAmount = amount,
                        returnURL = redirectUrl,
                        productType = "preferred",
                        androidPackageName = packageName,
                    )
                    pavilionService.initializePatronSession(
                        patronType = patronType,
                        mode = mode,
                        newUserSessionRequest = payload,
                    )
                }
                "existing" -> {
                    val payload = ExistingPatronRequestDto(
                        patronID = "cb7c887d-6687-4aa5-a664-31cf6c810df7",
                        vipCardNumber = "7210645917",
                        dateOfBirth = "5/28/1974",
                        remainingDailyDeposit = 999.99,
                        walletBalance = 1000.0,
                        transactionID = UUID.randomUUID().toString().replace("-", "").substring(1..24),
                        transactionAmount = amount,
                        transactionType = if (mode == "deposit") 0 else 1,
                        returnURL = redirectUrl,
                        productType = "preferred",
                        androidPackageName = packageName,
                    )
                    pavilionService.initializePatronSession(
                        patronType = patronType,
                        mode = mode,
                        existingUserSessionRequest = payload,
                    )
                }
                else -> throw IllegalArgumentException("Invalid mode: $mode")
            }
            if (patronResponseDtoResult is Resource.Success) {
                val result =
                    "${HttpRoutes.BASE_URL}?mode=$mode&native=true&redirectUrl=$redirectUrl#${patronResponseDtoResult.data?.sessionId}"
                Log.d("PPI", result)
                return@withContext result
            }
            patronResponseDtoResult.message?.let { Log.d("PPI", it) }
            Log.d("PPI", patronResponseDtoResult.data.toString())
            return@withContext null
        }
    }

    fun clearPatronSession() {
        _patronSessionUrlState.update {
            it.copy(
                patronSessionUrl = "",
                patronSessionRedirectUrl = "",
            )
        }
    }

    private val redirectUrl = BuildConfig.REDIRECT_URL
}
