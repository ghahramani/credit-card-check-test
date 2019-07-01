package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants.Error.Companion.ERR_CONCURRENCY_FAILURE
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INTERNAL_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class ConcurrencyFailureExceptionProblemHandler : ProblemExceptionHandler<ConcurrencyFailureException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is ConcurrencyFailureException
    }

    override fun generateProblemVM(e: ConcurrencyFailureException): ProblemVM {
        return ProblemVM(INTERNAL_TYPE, ERR_CONCURRENCY_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR.value())
    }

}
