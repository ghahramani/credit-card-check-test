package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.AUTHORIZATION_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.FORBIDDEN_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INTERNAL_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.NOT_FOUND_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class ResponseStatusExceptionProblemHandler : ProblemExceptionHandler<ResponseStatusException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is ResponseStatusException
    }

    override fun generateProblemVM(e: ResponseStatusException): ProblemVM {
        val status = e.status

        val url = when (status) {
            HttpStatus.NOT_FOUND -> NOT_FOUND_TYPE
            HttpStatus.UNAUTHORIZED -> AUTHORIZATION_TYPE
            HttpStatus.FORBIDDEN -> FORBIDDEN_TYPE
            else -> INTERNAL_TYPE
        }

        val message = when (status) {
            HttpStatus.NOT_FOUND -> AppConstants.Error.ERR_NOT_FOUND
            else -> e.message
        }

        return ProblemVM(url, message, status.value())
    }

}
