package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.NO_ID_PROVIDED_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Crud.Companion.NO_ID_PROVIDED
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class NoIdProvidedException : BadRequestException(NO_ID_PROVIDED_TYPE, NO_ID_PROVIDED)
