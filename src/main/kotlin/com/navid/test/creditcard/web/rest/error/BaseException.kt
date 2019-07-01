package com.navid.test.creditcard.web.rest.error

import org.springframework.http.HttpStatus

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

abstract class BaseException(val url: String, override val message: String, val status: HttpStatus) :
    RuntimeException(message)
