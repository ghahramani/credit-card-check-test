package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class EntityNotFoundException(entityName: String) : BadRequestException(
    AppConstants.Error.Companion.Url.NOT_FOUND_TYPE,
    AppConstants.Error.ERR_NOT_FOUND,
    mutableMapOf("entity" to entityName)
)
