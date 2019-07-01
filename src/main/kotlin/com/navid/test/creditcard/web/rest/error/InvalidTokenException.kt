package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.ERR_INVALID_TOKEN
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INVALID_TOKEN_TYPE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@ResponseStatus(UNAUTHORIZED)
class InvalidTokenException(expired: Boolean) : ParametricException(
    INVALID_TOKEN_TYPE,
    ERR_INVALID_TOKEN,
    UNAUTHORIZED,
    hashMapOf("expired" to expired)
)
