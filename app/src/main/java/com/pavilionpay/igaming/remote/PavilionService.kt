package com.pavilionpay.igaming.remote

import android.util.Base64
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.pavilionpay.igaming.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
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
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.Calendar

class PavilionService {

    val client: HttpClient = HttpClient {
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
     * Initializes a patron session.
     *
     * @param patronType The type of the patron.
     * @param request The request data for the session.
     * @return A Resource containing the response data or an error.
     */
    suspend inline fun <reified T> initializePatronSession(
            patronType: String,
            request: T,
    ): Resource<PatronResponseDto> = try {
        Resource.Success(
            client
                    .post(HttpRoutes.INITIALIZE_PATRON_SESSION) {
                        contentType(ContentType.Application.Json)
                        url {
                            appendPathSegments(patronType)
                        }
                        setBody(request)
                    }
                    .body(),
        )
    } catch (e: RedirectResponseException) {
        // 3xx responses
        Resource.Error(e.response.status.description, null)
    } catch (e: ClientRequestException) {
        // 4xx responses
        Resource.Error("${e.response.status.description}\n\n${e.response.body<String>()}", null)
    } catch (e: ServerResponseException) {
        // 5xx responses
        Resource.Error(e.response.status.description, null)
    } catch (e: Exception) {
        // All other exceptions
        Resource.Error(e.message ?: "Unknown error")
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
        val decoded = Base64.decode(BuildConfig.JWT_SECRET.toByteArray(), Base64.DEFAULT)
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
                .withIssuer(BuildConfig.JWT_ISSUER)
                .withExpiresAt(exp)
                .withNotBefore(nbf)
                .withAudience(BuildConfig.JWT_AUDIENCE)
                .sign(algorithm)
    }
}

sealed class Resource<T>(
        val data: T? = null,
        val message: String? = null,
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}