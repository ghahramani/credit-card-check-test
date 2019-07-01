package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.NEW_EMAIL_WITH_ID_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Crud.Companion.CREATE_ID_EXISTS
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class CreateIdExistsException : BadRequestException(NEW_EMAIL_WITH_ID_TYPE, CREATE_ID_EXISTS)
