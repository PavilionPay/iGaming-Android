package com.pavilionpay.igaming.presentation.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavilionpay.igaming.BuildConfig
import com.pavilionpay.igaming.domain.ProductType
import com.pavilionpay.igaming.remote.ExistingPatronRequestDto
import com.pavilionpay.igaming.remote.HttpRoutes
import com.pavilionpay.igaming.remote.NewUserSessionRequestDto
import com.pavilionpay.igaming.remote.PavilionService
import com.pavilionpay.igaming.remote.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PavilionPlaidViewModel : ViewModel() {
    private val _patronSessionUrlState = MutableStateFlow(PavilionPlaidState())
    val patronSessionUrlState: StateFlow<PavilionPlaidState> = _patronSessionUrlState

    private val pavilionService = PavilionService()

    fun initializePatronSession(
            productType: String,
            patronType: String,
            amount: Float,
            mode: String,
            packageName: String,
    ) {
        viewModelScope.launch {
            val url = queryServiceForSession(
                productType = productType,
                patronType = patronType,
                amount = amount,
                mode = mode,
                packageName = packageName,
            )
            _patronSessionUrlState.tryEmit(
                PavilionPlaidState(
                    patronSessionUrl = url ?: "",
                    patronSessionRedirectUrl = if (url.isNullOrBlank()) "" else redirectUrl,
                )
            )
        }
    }

    private suspend fun queryServiceForSession(
            productType: String,
            patronType: String,
            amount: Float,
            mode: String,
            packageName: String,
    ): String? {
        // Implement your logic to load Pavilion SDK and return the URL
        return withContext(Dispatchers.IO) {
            val payload: Any = when (patronType) {
                "new" -> {
                    NewUserSessionRequestDto(
                        city = "prZYfAYqa",
                        country = "USA",
                        dateOfBirth = "07/03/1964",
                        email = "8zN2Emyp@5JP6oEaZ.com",
                        firstName = "hello",
                        idNumber = "123746689",
                        idState = "",
                        idType = "SS",
                        lastName = "frd",
                        mobilePhone = "8434811326",
                        patronId = "a25b5c17-6541-4890-a4c9-098c1c0ec226",
                        remainingDailyDeposit = "100",
                        state = "AR",
                        streetName = "28 NQqodHNdv",
                        zip = "282926017",
                        walletBalance = "100",
                        accountNumber = "",
                        routingNumber = "",
                        middleInitial = "M",
                        transactionType = if (mode == "deposit") 0 else 1,
                        transactionId = UUID.randomUUID().toString().replace("-", "").substring(1..24),
                        transactionAmount = amount,
                        returnURL = redirectUrl,
                        androidPackageName = packageName,
                        patronType = patronType,
                        productType = productType,
                    )
                }

                "existing" -> {
                    when (ProductType.fromString(productType)) {
                        ProductType.Online ->
                            ExistingPatronRequestDto(
                                patronType = patronType,
                                patronID = "cb7c887d-6687-4aa5-a664-31cf6c810df7",
                                vipCardNumber = "7210536159", // online
                                dateOfBirth = "07/03/1964",
                                remainingDailyDeposit = 999.99,
                                walletBalance = 1000.0,
                                transactionID = UUID.randomUUID().toString().replace("-", "")
                                        .substring(1..24),
                                transactionAmount = amount,
                                transactionType = if (mode == "deposit") 0 else 1,
                                returnURL = redirectUrl,
                                productType = productType,
                                androidPackageName = packageName,
                            )

                        ProductType.Preferred ->
                            ExistingPatronRequestDto(
                                patronType = patronType,
                                patronID = "1ef56720-47b6-46bc-9a3a-b11bd511d10b",
                                vipCardNumber = "7210908875", // preferred
                                dateOfBirth = "11/13/1994",
                                remainingDailyDeposit = 5000.0,
                                walletBalance = 24.0,
                                transactionID = UUID.randomUUID().toString().replace("-", "")
                                        .substring(1..24),
                                transactionAmount = amount,
                                transactionType = if (mode == "deposit") 0 else 1,
                                returnURL = redirectUrl,
                                productType = productType,
                                androidPackageName = packageName,
                            )
                    }
                }

                else -> throw IllegalArgumentException("Invalid mode: $mode")
            }

            val patronResponseDtoResult = pavilionService.initializePatronSession(patronType, payload)
            if (patronResponseDtoResult is Resource.Success) {
                val result = "${HttpRoutes.BASE_URL}?mode=$mode&native=true&redirectUrl=$redirectUrl#${patronResponseDtoResult.data?.sessionId}"
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

data class PavilionPlaidState(
        val patronSessionUrl: String = "",
        val patronSessionRedirectUrl: String = "",
)
