package com.navid.test.creditcard.service.util.error

import com.navid.test.creditcard.web.rest.error.ParametricException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
open class BadRequestException(url: String, error: String, params: MutableMap<String, Any> = mutableMapOf()) :
    ParametricException(url, error, HttpStatus.BAD_REQUEST, params)
