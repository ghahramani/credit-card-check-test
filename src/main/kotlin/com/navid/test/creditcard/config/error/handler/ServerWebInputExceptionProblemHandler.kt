package com.navid.test.creditcard.config.error.handler

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.navid.test.creditcard.config.AppConstants.Error.Companion.FIELD_REQUIRED_FOUND
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.FIELD_REQUIRED_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebInputException

@Component
class ServerWebInputExceptionProblemHandler : ProblemExceptionHandler<ServerWebInputException> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is ServerWebInputException
    }

    override fun generateProblemVM(e: ServerWebInputException): ProblemVM {
        var field = ""

        if (e.mostSpecificCause is MissingKotlinParameterException) {
            val parameterException = e.mostSpecificCause as MissingKotlinParameterException
            field = parameterException.parameter.name.orEmpty()
        }

        return ProblemVM(FIELD_REQUIRED_TYPE, FIELD_REQUIRED_FOUND, e.status.value(), hashMapOf("field" to field))
    }

}
