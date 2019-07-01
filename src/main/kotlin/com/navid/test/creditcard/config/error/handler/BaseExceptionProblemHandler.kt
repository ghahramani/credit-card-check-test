package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.error.BaseException
import com.navid.test.creditcard.web.rest.error.ParametricException
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.stereotype.Component

@Component
class BaseExceptionProblemHandler : ProblemExceptionHandler<BaseException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is BaseException
    }

    override fun generateProblemVM(e: BaseException): ProblemVM {
        var params: MutableMap<String, Any> = hashMapOf()

        if (e is ParametricException) {
            params = e.params
        }

        return ProblemVM(e.url, e.message, e.status.value(), params)
    }

}
