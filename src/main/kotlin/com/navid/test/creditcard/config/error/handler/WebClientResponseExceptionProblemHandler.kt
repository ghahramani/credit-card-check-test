package com.navid.test.creditcard.config.error.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class WebClientResponseExceptionProblemHandler(private val objectMapper: ObjectMapper) :
    ProblemExceptionHandler<WebClientResponseException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is WebClientResponseException
    }

    override fun generateProblemVM(e: WebClientResponseException): ProblemVM {
        val body = e.responseBodyAsString

        return ProblemVM(
            AppConstants.Error.Companion.Url.EXTERNAL_SOURCE_TYPE,
            e.message ?: AppConstants.Error.ERR_INTERNAL_ERROR,
            e.statusCode.value(),
            objectMapper.readValue(body)
        )
    }

}
