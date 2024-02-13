package com.pavilionpay.igaming.presentation.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pavilionpay.igaming.BuildConfig
import com.pavilionpay.igaming.remote.ExistingPatronRequestDto
import com.pavilionpay.igaming.remote.HttpRoutes
import com.pavilionpay.igaming.remote.NewPatronRequestDto
import com.pavilionpay.igaming.remote.PavilionService
import com.pavilionpay.igaming.remote.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * View model responsible for creating a valid session url to pass to the PavilionPlaidWebview.
 * This example gets a session url by passing preset mock user data to a Pavilion test endpoint;
 * other implementations may obtain session urls through other means.
 */
class VIPSessionUrlViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val defaultNewUser = NewUser(
            patronId = "a25b5c17-6541-4890-a4c9-098c1c0ec226",
            firstName = "hello",
            middleInitial = "M",
            lastName = "frd",
            dateOfBirth = LocalDate.of(1964, 7, 3),
            email = "8zN2Emyp@5JP6oEaZ.com",
            mobilePhone = "8434811326",
            streetName = "28 NQqodHNdv",
            city = "prZYfAYqa",
            state = "AR",
            zip = "282926017",
            country = "USA",
            idType = "SS",
            idNumber = "123746689",
            idState = "",
            accountNumber = "",
            routingNumber = "",
            remainingDailyDeposit = 100.0,
            walletBalance = 100.0,
        )

        val defaultOnlineUser = ExistingUser(
            patronId = "cb7c887d-6687-4aa5-a664-31cf6c810df7",
            vipCardNumber = "7210536159", // online
            dateOfBirth = LocalDate.of(1964, 7, 3),
            remainingDailyDeposit = 999.99,
            walletBalance = 1000.0
        )

        val defaultPreferredUser = ExistingUser(
            patronId = "1ef56720-47b6-46bc-9a3a-b11bd511d10b",
            vipCardNumber = "7210908875", // preferred
            dateOfBirth = LocalDate.of(1994, 11, 13),
            remainingDailyDeposit = 5000.0,
            walletBalance = 24.0,
        )
    }

    private val _patronSessionUrlState = MutableStateFlow(PavilionPlaidState())
    val patronSessionUrlState = _patronSessionUrlState.asStateFlow()

    private val _productType = MutableStateFlow(ProductType.Online)
    var productType = _productType.asStateFlow()

    private val _transactionType = MutableStateFlow(TransactionType.Deposit)
    var transactionType = _transactionType.asStateFlow()

    private val _amount = MutableStateFlow(15.toDouble())
    var amount = _amount.asStateFlow()

    private val _patronType = MutableStateFlow(PatronType.New)
    var patronType = _patronType.asStateFlow()

    private val _currentUser = MutableStateFlow<UserObject>(defaultNewUser)
    val currentUser = _currentUser.asStateFlow()

    private val pavilionService = PavilionService()

    fun setProductType(productType: ProductType) {
        val oldProductType = _productType.value
        if (oldProductType == productType) return

        _productType.tryEmit(productType)
        resetDefaultUser(newProductType = productType)
    }

    fun setTransactionType(transactionType: TransactionType) {
        _transactionType.tryEmit(transactionType)
    }

    fun setAmount(amount: Double) {
        _amount.tryEmit(amount)
    }

    fun setPatronType(patronType: PatronType) {
        val oldPatronType = _patronType.value
        if (oldPatronType == patronType) return

        _patronType.tryEmit(patronType)
        resetDefaultUser(newPatronType = patronType)
    }

    fun setCurrentUser(user: UserObject) {
        _currentUser.tryEmit(user)
    }

    fun initializePatronSession() {
        viewModelScope.launch {
            // Implement your logic to load Pavilion SDK and return the URL
            val url = withContext(Dispatchers.IO) {
                val payload = currentUser.value.toPatronRequest(
                    patronType.value,
                    amount.value,
                    transactionType.value,
                    productType.value,
                    getApplication<Application>().packageName
                )
                val patronResponseDtoResult = pavilionService.initializePatronSession<Any>(patronType.value.paramName, payload)
                if (patronResponseDtoResult is Resource.Success) {
                    val result = "${HttpRoutes.BASE_URL}?mode=${transactionType.value.paramName}&native=true&redirectUrl=${BuildConfig.REDIRECT_URL}#${patronResponseDtoResult.data?.sessionId}"
                    Log.d("PPI", result)
                    return@withContext result
                }
                patronResponseDtoResult.message?.let { Log.d("PPI", it) }
                Log.d("PPI", patronResponseDtoResult.data.toString())
                return@withContext null
            }

            _patronSessionUrlState.tryEmit(
                PavilionPlaidState(
                    patronSessionUrl = url ?: "",
                    patronSessionRedirectUrl = if (url.isNullOrBlank()) "" else BuildConfig.REDIRECT_URL,
                )
            )
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

    private fun resetDefaultUser(
            newProductType: ProductType? = null,
            newPatronType: PatronType? = null
    ) {
        newPatronType?.let {
            _currentUser.tryEmit(
                if (it == PatronType.New) defaultNewUser else if (_productType.value == ProductType.Online) defaultOnlineUser else defaultPreferredUser
            )

            return
        }

        if (_patronType.value == PatronType.New) return //no change if user is still a new user

        newProductType?.let {
            _currentUser.tryEmit(
                if (it == ProductType.Online) defaultOnlineUser else defaultPreferredUser
            )
        }
    }
}

data class PavilionPlaidState(
        val patronSessionUrl: String = "",
        val patronSessionRedirectUrl: String = "",
)

interface UserObject {
    val dateOfBirth: LocalDate

    fun toPatronRequest(
            patronType: PatronType,
            amount: Double,
            transactionType: TransactionType,
            productType: ProductType,
            packageName: String
    ): Any
}

data class NewUser(
        val patronId: String,
        val firstName: String,
        val middleInitial: String,
        val lastName: String,
        override val dateOfBirth: LocalDate,
        val email: String,
        val mobilePhone: String,
        val streetName: String,
        val city: String,
        val state: String,
        val zip: String,
        val country: String,
        val idType: String,
        val idNumber: String,
        val idState: String,
        val routingNumber: String,
        val accountNumber: String,
        val walletBalance: Double,
        val remainingDailyDeposit: Double
) : UserObject {
    override fun toPatronRequest(
            patronType: PatronType,
            amount: Double,
            transactionType: TransactionType,
            productType: ProductType,
            packageName: String
    ): NewPatronRequestDto {
        return NewPatronRequestDto(
            city = city,
            country = country,
            dateOfBirth = VIPSessionUrlViewModel.dateFormat.format(dateOfBirth),
            email = email,
            firstName = firstName,
            idNumber = idNumber,
            idState = state,
            idType = idType,
            lastName = lastName,
            mobilePhone = mobilePhone,
            patronId = patronId,
            remainingDailyDeposit = remainingDailyDeposit,
            state = state,
            streetName = streetName,
            zip = zip,
            walletBalance = walletBalance,
            accountNumber = "",
            routingNumber = "",
            middleInitial = middleInitial,
            transactionType = if (transactionType == TransactionType.Deposit) 0 else 1,
            transactionId = UUID.randomUUID().toString().replace("-", "").substring(1..24),
            transactionAmount = amount,
            returnURL = BuildConfig.REDIRECT_URL,
            androidPackageName = packageName,
            patronType = patronType.paramName,
            productType = productType.name,
        )
    }
}

data class ExistingUser(
        val patronId: String,
        val vipCardNumber: String,
        override val dateOfBirth: LocalDate,
        val remainingDailyDeposit: Double,
        val walletBalance: Double
) : UserObject {
    override fun toPatronRequest(
            patronType: PatronType,
            amount: Double,
            transactionType: TransactionType,
            productType: ProductType,
            packageName: String
    ): ExistingPatronRequestDto {
        return ExistingPatronRequestDto(
            patronType = patronType.paramName,
            patronID = patronId,
            vipCardNumber = vipCardNumber,
            dateOfBirth = VIPSessionUrlViewModel.dateFormat.format(dateOfBirth),
            remainingDailyDeposit = remainingDailyDeposit,
            walletBalance = walletBalance,
            transactionID = UUID.randomUUID().toString().replace("-", "").substring(1..24),
            transactionAmount = amount,
            transactionType = if (transactionType == TransactionType.Deposit) 0 else 1,
            returnURL = BuildConfig.REDIRECT_URL,
            productType = productType.name,
            androidPackageName = packageName,
        )
    }
}

@Suppress("unused")
enum class ProductType {
    Online,
    Preferred
}

@Suppress("unused")
enum class TransactionType(val paramName: String) {
    Deposit("deposit"),
    Withdrawal("withdraw")
}

@Suppress("unused")
enum class PatronType(val paramName: String) {
    New("new"),
    Existing("existing")
}