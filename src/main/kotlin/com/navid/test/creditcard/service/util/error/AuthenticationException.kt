package com.navid.test.creditcard.service.util.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.ERR_CREDENTIALS
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.AUTHENTICATION_TYPE

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class AuthenticationException(error: String? = null) : BadRequestException(
    AUTHENTICATION_TYPE,
    error ?: ERR_CREDENTIALS
)
