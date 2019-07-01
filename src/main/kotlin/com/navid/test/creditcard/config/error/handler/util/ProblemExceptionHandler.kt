package com.navid.test.creditcard.config.error.handler.util

import com.navid.test.creditcard.web.rest.vm.ProblemVM

interface ProblemExceptionHandler<T : Throwable> {

    fun supports(clazz: Throwable): Boolean

    fun generateProblemVM(e: T): ProblemVM

}
