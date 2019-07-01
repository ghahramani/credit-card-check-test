package com.navid.test.creditcard.config.error

import org.apache.commons.lang3.exception.ExceptionUtils.getMessage
import org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Component
class ProblemHandler : ServerAuthenticationEntryPoint {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun commence(exchange: ServerWebExchange, exception: AuthenticationException): Mono<Void> {
        logger.error("UNAUTHORIZED: {}, {}", getMessage(exception), getRootCauseMessage(exception))
        return Mono.error(exception)
    }

}
