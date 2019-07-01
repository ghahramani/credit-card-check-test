package com.navid.test.creditcard.config.error.handler

import com.navid.test.creditcard.config.AppConstants.Error.Companion.ERR_VALIDATION
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.CONSTRAINT_VIOLATION_TYPE
import com.navid.test.creditcard.config.error.handler.util.ProblemExceptionHandler
import com.navid.test.creditcard.web.rest.error.FieldErrorVM
import com.navid.test.creditcard.web.rest.vm.ProblemVM
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.support.WebExchangeBindException

@Component
class ExchangeBindExceptionProblemHandler : ProblemExceptionHandler<Exception> {

    override fun supports(clazz: Throwable): Boolean {
        return clazz is WebExchangeBindException || clazz is MethodArgumentNotValidException
    }

    override fun generateProblemVM(e: Exception): ProblemVM {
        var fieldErrors: List<FieldErrorVM> = arrayListOf()
        var status = HttpStatus.BAD_REQUEST

        if (e is WebExchangeBindException) {
            status = e.status
            fieldErrors = convertToFieldErrorVM(e.bindingResult)
        } else if (e is MethodArgumentNotValidException) {
            fieldErrors = convertToFieldErrorVM(e.bindingResult)
        }

        return ProblemVM(CONSTRAINT_VIOLATION_TYPE, ERR_VALIDATION, status.value(), hashMapOf("errors" to fieldErrors))
    }

    private fun convertToFieldErrorVM(e: BindingResult): List<FieldErrorVM> {
        return e.fieldErrors.map {
            FieldErrorVM(it.objectName, it.field, it.code ?: "Unknown")
        }
    }

}
