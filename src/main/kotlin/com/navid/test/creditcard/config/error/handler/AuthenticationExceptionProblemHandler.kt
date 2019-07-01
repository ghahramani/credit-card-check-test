package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.ACCESS_DENIED_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.error.ParametricException
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Component
class AuthenticationExceptionProblemHandler : ProblemExceptionHandler<AuthenticationException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is AuthenticationException
    }

    override fun generateProblemVM(e: AuthenticationException): ProblemVM {
        var params: MutableMap<String, Any> = hashMapOf()

        if (e is ParametricException) {
            params = e.params
        }

        return ProblemVM(ACCESS_DENIED_TYPE, e.message.orEmpty(), HttpStatus.UNAUTHORIZED.value(), params)
    }

}
