package com.navid.test.creditcard.config.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INTERNAL_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 *
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Configuration
class ExceptionTranslator : ServerAccessDeniedHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        return Mono.error(denied)
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Suppress("UNCHECKED_CAST")
    fun caught(
        objectMapper: ObjectMapper,
        handlers: List<ProblemExceptionHandler<*>>
    ): ErrorWebExceptionHandler {
        return ErrorWebExceptionHandler { exchange, ex ->

            if (exchange.response.isCommitted) {
                return@ErrorWebExceptionHandler Mono.error(ex)
            }

            val response = exchange.response
            response.headers.contentType = MediaType.APPLICATION_PROBLEM_JSON_UTF8

            var result = ProblemVM(INTERNAL_TYPE, ex.message.orEmpty(), HttpStatus.INTERNAL_SERVER_ERROR.value())

            for (item in handlers) {
                if (item.supports(ex)) {
                    result = (item as ProblemExceptionHandler<Throwable>).generateProblemVM(ex)
                    break
                }
            }

            logger.error("Unable to proceed due to: {}", ex)

            response.statusCode = HttpStatus.valueOf(result.status)
            val bytes = objectMapper.writeValueAsBytes(result)
            val buffer = response.bufferFactory().wrap(bytes)
            response.writeWith(Mono.just(buffer))
        }
    }
}
