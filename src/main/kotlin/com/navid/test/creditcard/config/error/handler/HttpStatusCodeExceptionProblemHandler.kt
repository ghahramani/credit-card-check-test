package com.navid.test.creditcard.config.error.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.AUTHORIZATION_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.FORBIDDEN_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INTERNAL_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.NOT_FOUND_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException

@Component
class HttpStatusCodeExceptionProblemHandler(private val objectMapper: ObjectMapper) :
    ProblemExceptionHandler<HttpStatusCodeException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is HttpStatusCodeException
    }

    override fun generateProblemVM(e: HttpStatusCodeException): ProblemVM {
        val status = e.statusCode

        var url = when (status) {
            HttpStatus.NOT_FOUND -> NOT_FOUND_TYPE
            HttpStatus.UNAUTHORIZED -> AUTHORIZATION_TYPE
            HttpStatus.FORBIDDEN -> FORBIDDEN_TYPE
            else -> INTERNAL_TYPE
        }

        val body = e.responseBodyAsString
        var params: MutableMap<String, Any> = objectMapper.readValue(body)

        var message = when (status) {
            HttpStatus.NOT_FOUND -> AppConstants.Error.ERR_NOT_FOUND
            else -> e.message
        }

        e.responseHeaders?.contentType?.let { mediaType ->
            if (mediaType.includes(MediaType.APPLICATION_PROBLEM_JSON_UTF8)) {
                val vm = objectMapper.readValue<ProblemVM>(body)
                message = vm.message
                params = vm.params
                url = vm.url
            }
        }

        return ProblemVM(url, message.orEmpty(), status.value(), params)
    }

}
