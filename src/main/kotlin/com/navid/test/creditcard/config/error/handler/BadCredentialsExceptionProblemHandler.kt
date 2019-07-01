package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants.Error.Companion.ERR_CREDENTIALS
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INVALID_CREDENTIAL_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component

@Component
class BadCredentialsExceptionProblemHandler : ProblemExceptionHandler<BadCredentialsException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is BadCredentialsException
    }

    override fun generateProblemVM(e: BadCredentialsException): ProblemVM {
        return ProblemVM(INVALID_CREDENTIAL_TYPE, e.message ?: ERR_CREDENTIALS, HttpStatus.SERVICE_UNAVAILABLE.value())
    }

}
