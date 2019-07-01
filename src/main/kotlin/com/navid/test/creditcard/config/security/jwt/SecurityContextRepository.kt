package com.navid.test.creditcard.config.security.jwt

import com.navid.test.creditcard.config.AppConstants
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Component
class SecurityContextRepository(private val tokenProvider: TokenProvider) : ServerSecurityContextRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        throw UnsupportedOperationException("Save is not supported")
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        var result = Mono.empty<SecurityContext>()

        val jwt = resolveToken(exchange.request)
        try {
            if (this.tokenProvider.validateToken(jwt)) {
                result = this.tokenProvider
                    .retrieveAuthentication(jwt)
                    .map { auth ->
                        val context = SecurityContextImpl(auth)
                        // We need this to have context in non-reactive calls as well
                        SecurityContextHolder.setContext(context)
                        context
                    }
            }
        } catch (ignore: Exception) {
            logger.error("Unable to authenticate user with token: {} due to: {}", jwt, ignore)
            result = Mono.error(ignore)
        }

        return result
    }

    private fun resolveToken(request: ServerHttpRequest): String {
        val bearerToken = request.headers.getFirst(AppConstants.Security.Jwt.HEADER_STRING) ?: ""
        return if (bearerToken.startsWith(AppConstants.Security.Jwt.TOKEN_PREFIX)) {
            bearerToken.substring(AppConstants.Security.Jwt.TOKEN_PREFIX.length, bearerToken.length)
        } else ""
    }
}
