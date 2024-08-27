package com.pavilionpay.vipconnect

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

/**
 * A ViewModel responsible for creating a valid session url to pass to the PavilionPlaidWebview.
 * This example gets a session url by passing preset mock user data to a Pavilion test endpoint;
 * other implementations will obtain session urls through accessing other services.
 */
class VIPSessionUrlViewModel : ViewModel() {
    companion object {

        // You need to provide these values in order to run this demo app against your operator.
        // These values were created when your Pavilion Operator account was created.
        // Contact your Pavilion representative if you need help obtaining these values.
        const val JWT_ISSUER: String = <YOUR VALUE HERE>
        const val JWT_SECRET: String = <YOUR VALUE HERE>
        const val ENVIRONMENT: String = <YOUR VALUE HERE>


        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        //
        val defaultPreferredUser = UserObject(
            patronId = "1ef56720-47b6-46bc-9a3a-b11bd511d10b",
            vipCardNumber = "7210908875", // preferred
            dateOfBirth = LocalDate.of(1994, 11, 13)
        )
    }

    /**
     * Retrieves a VIP Session id from a Pavilion test endpoint.
     * This method is sufficient for the demo app, but your app should not connect to Pavilion's APIs directly;
     * instead, you should create your own web services that hold your secret values and have your app
     * connect to those.
     */
    suspend fun getPatronSessionId(): String? {
        val patronResponseDtoResult: PatronResponseDto? = try {
            client.post("https://$ENVIRONMENT.api-gaming.paviliononline.io/sdk/api/patronsession/existing") {
                contentType(ContentType.Application.Json)
                setBody(
                    defaultPreferredUser.toPatronRequest()
                )
            }.body()
        } catch (e: Exception) {
            return null
        }

        if (patronResponseDtoResult != null) {
            return patronResponseDtoResult.sessionId
        }

        return null
    }

    /**
     * Creates the url to launch a VIP SDK session.
     * For more info on how to build this url, see
     * https://developer.vippreferred.com/integration-steps/invoke-web-component#open-via-url
     */
    fun createVIPSessionUrl(sessionId: String): String {
        return "https://$ENVIRONMENT.api-gaming.paviliononline.io/sdk?mode=deposit#$sessionId"
    }

    private val client: HttpClient = HttpClient {
        // Install the Auth feature and configure it to use bearer authentication.
        install(Auth) {
            bearer {
                loadTokens {
                    val token = generateJWT()
                    BearerTokens(token, token)
                }
            }
        }
        // Install the ContentNegotiation feature and configure it to use JSON.
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        // Install the Logging feature and configure it to log all requests and responses.
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    /**
     * Generates a JWT.
     *
     * The JWT is signed with HMAC256 using a secret key. It has an issuer, an expiration time,
     * a "not before" time, and an audience.
     *
     * @return The generated JWT as a string.
     */
    private fun generateJWT(): String {
        // Decode the secret key
        val decoded = Base64.decode(JWT_SECRET.toByteArray(), Base64.DEFAULT)
        // Create the HMAC256 algorithm with the secret key
        val algorithm = Algorithm.HMAC256(decoded)
        // Set the expiration time to 2000 seconds from now
        val exp = Calendar.getInstance().run {
            add(Calendar.SECOND, 2000)
            time
        }
        // Set the "not before" time to 5 seconds from now
        val nbf = Calendar.getInstance().run {
            add(Calendar.SECOND, 5)
            time
        }
        // Create and sign the JWT
        return JWT.create()
                .withIssuer(JWT_ISSUER)
                .withExpiresAt(exp)
                .withNotBefore(nbf)
                .withAudience("vip-api-${ENVIRONMENT}")
                .sign(algorithm)
    }
}

data class UserObject(
        val patronId: String,
        val vipCardNumber: String,
        val dateOfBirth: LocalDate
) {
    fun toPatronRequest(): ExistingPatronRequestDto {
        return ExistingPatronRequestDto(
            patronID = patronId,
            vipCardNumber = vipCardNumber,
            dateOfBirth = VIPSessionUrlViewModel.dateFormat.format(dateOfBirth),
            remainingDailyDeposit = 1000.0,
            walletBalance = 1000.0,
            transactionID = UUID.randomUUID().toString().replace("-", "").substring(1..24),
            transactionAmount = 10.0,
            transactionType = 0,
            returnURL = "closevip://done",
            productType = "0",
        )
    }
}

@Serializable
data class ExistingPatronRequestDto(
        val patronID: String,
        val vipCardNumber: String,
        val dateOfBirth: String,
        val remainingDailyDeposit: Double,
        val walletBalance: Double,
        val transactionID: String,
        val transactionAmount: Double,
        val transactionType: Byte,
        val returnURL: String,
        val productType: String
)

@Serializable
data class PatronResponseDto(val sessionId: String)
