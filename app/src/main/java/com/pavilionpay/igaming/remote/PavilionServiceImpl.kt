package com.pavilionpay.igaming.remote

import com.pavilionpay.igaming.core.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType

class PavilionServiceImpl(
    private val client: HttpClient,
) : PavilionService {
    /**
     * Initializes a new user session.
     *
     * @param patronType The type of the patron.
     * @param mode The mode of the session.
     * @param newUserSessionRequest The request data for the new user session.
     * @return A Resource containing the response data or an error.
     */
    override suspend fun initializePatronSession(
        productType: String,
        patronType: String,
        mode: String,
        newUserSessionRequest: NewUserSessionRequestDto,
    ): Resource<PatronResponseDto> {
        return initializePatronSession(patronType, newUserSessionRequest)
    }

    /**
     * Initializes an existing user session.
     *
     * @param patronType The type of the patron.
     * @param mode The mode of the session.
     * @param existingUserSessionRequest The request data for the existing user session.
     * @return A Resource containing the response data or an error.
     */
    override suspend fun initializePatronSession(
        productType: String,
        patronType: String,
        mode: String,
        existingUserSessionRequest: ExistingPatronRequestDto,
    ): Resource<PatronResponseDto> {
        return initializePatronSession(patronType, existingUserSessionRequest)
    }

    /**
     * Initializes a patron session.
     *
     * @param patronType The type of the patron.
     * @param request The request data for the session.
     * @return A Resource containing the response data or an error.
     */
    private suspend inline fun <reified T> initializePatronSession(
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
}
