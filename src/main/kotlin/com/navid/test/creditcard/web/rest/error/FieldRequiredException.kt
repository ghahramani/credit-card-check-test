package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.FIELD_REQUIRED_FOUND
import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.FIELD_REQUIRED_TYPE
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class FieldRequiredException(field: String) : BadRequestException(
    FIELD_REQUIRED_TYPE,
    FIELD_REQUIRED_FOUND,
    mutableMapOf("field" to field)
)
