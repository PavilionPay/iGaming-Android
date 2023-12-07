package com.pavilionpay.igaming.remote

import android.util.Base64
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.pavilionpay.igaming.BuildConfig
import java.util.Calendar

/**
 * A class that generates a JWT (JSON Web Token).
 */
class TokenGenerator {
    /**
     * Generates a JWT.
     *
     * The JWT is signed with HMAC256 using a secret key. It has an issuer, an expiration time,
     * a "not before" time, and an audience.
     *
     * @return The generated JWT as a string.
     */
    fun generate(): String {
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
