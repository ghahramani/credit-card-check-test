package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants.Error.Companion.ERR_DUPLICATED
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.DUPLICATED_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class DuplicateKeyExceptionProblemHandler : ProblemExceptionHandler<DuplicateKeyException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is DuplicateKeyException
    }

    override fun generateProblemVM(e: DuplicateKeyException): ProblemVM {
        return ProblemVM(
            DUPLICATED_TYPE,
            ERR_DUPLICATED,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            hashMapOf("stackTrace" to e.message.orEmpty())
        )
    }

}
