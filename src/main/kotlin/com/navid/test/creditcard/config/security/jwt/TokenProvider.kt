package com.navid.test.creditcard.config.security.jwt

import com.navid.test.creditcard.config.AppConstants.Security.Jwt.Companion.KEY_ID
import com.navid.test.creditcard.config.AppConstants.Security.Jwt.Companion.KEY_ROLES
import com.navid.test.creditcard.config.AppProperties
import com.navid.test.creditcard.config.security.AuthenticationManager
import com.navid.test.creditcard.web.rest.error.InvalidTokenException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.Date

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Component
class TokenProvider(private val properties: AppProperties, private val manager: AuthenticationManager) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun createToken(token: AuthenticationToken, rememberMe: Boolean): String {
        val now = Date().time
        val validity: Date
        val jwt = properties.security.authentication.jwt

        validity = when {
            rememberMe -> Date(now + (jwt.tokenValidityInSecondsForRememberMe * TO_MILLI_SECOND))
            else -> Date(now + (jwt.tokenValidityInSeconds * TO_MILLI_SECOND))
        }

        return Jwts.builder()
            .setClaims(token.values())
            .setSubject(token.name)
            .signWith(SignatureAlgorithm.HS512, jwt.secret)
            .setExpiration(validity)
            .compact()
    }

    fun retrieveAuthentication(token: String): Mono<Authentication> {
        val claims = Jwts.parser()
            .setSigningKey(properties.security.authentication.jwt.secret)
            .parseClaimsJws(token)
            .body

        val id = claims[KEY_ID] as String
        val roles = claims[KEY_ROLES] as Collection<*>?

        return manager.authenticate(
            AuthenticationToken(
                id,
                claims.subject,
                "",
                roles.orEmpty().toSet().map { SimpleGrantedAuthority(it.toString()) }
            )
        )
    }

    @Throws(InvalidTokenException::class)
    fun validateToken(authToken: String): Boolean {
        return try {
            if (StringUtils.isEmpty(authToken)) {
                return false
            }
            Jwts.parser()
                .setSigningKey(properties.security.authentication.jwt.secret)
                .parseClaimsJws(authToken)
            true
        } catch (ignore: Exception) {
            logger.error("Invalid JWT signature: " + ignore.message)
            throw InvalidTokenException(ignore.javaClass.isAssignableFrom(ExpiredJwtException::class.java))
        }
    }

    companion object {
        private const val TO_MILLI_SECOND = 1000
    }
}
