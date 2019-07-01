package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.FORBIDDEN_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component
class AccessDeniedExceptionProblemHandler : ProblemExceptionHandler<AccessDeniedException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is AccessDeniedException
    }

    override fun generateProblemVM(e: AccessDeniedException): ProblemVM {
        return ProblemVM(FORBIDDEN_TYPE, e.message.orEmpty(), FORBIDDEN.value())
    }

}
