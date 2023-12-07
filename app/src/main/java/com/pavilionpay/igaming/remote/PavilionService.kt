package com.pavilionpay.igaming.remote

import com.pavilionpay.igaming.core.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * This is the interface that will be used by the rest of the app to make network calls.
 * It is implemented by [PavilionServiceImpl] which is the actual implementation of the interface.
 */
interface PavilionService {

    /**
     * This function will make a network call to initialize the patron session and return [Resource.Success] or [Resource.Error]
     * with the URL to finish the deposit. This is for a new user.
     */
    suspend fun initializePatronSession(patronType: String, mode: String, newUserSessionRequest: NewUserSessionRequestDto): Resource<PatronResponseDto>

    /**
     * This function will make a network call to initialize the patron session and return [Resource.Success] or [Resource.Error]
     * with the URL to finish the deposit. This is for an existing user.
     */
    suspend fun initializePatronSession(patronType: String, mode: String, existingUserSessionRequest: ExistingPatronRequestDto): Resource<PatronResponseDto>

    companion object {
        /**
         * Creates a new instance of the PavilionService.
         *
         * @param token The bearer token to use for authentication.
         * @return A new instance of the PavilionService.
         */
        fun create(token: String): PavilionService = PavilionServiceImpl(
            client = HttpClient {
                // Install the Auth feature and configure it to use bearer authentication.
                install(Auth) {
                    bearer {
                        loadTokens {
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
            },
        )
    }
}
