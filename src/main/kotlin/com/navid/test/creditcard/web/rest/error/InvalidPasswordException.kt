package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.INVALID_PASSWORD
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.INVALID_PASSWORD_TYPE
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class InvalidPasswordException : BadRequestException(INVALID_PASSWORD_TYPE, INVALID_PASSWORD)
