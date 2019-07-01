package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.NO_MATCHED_UPDATE_ID_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Crud.Companion.NOT_MATCHED_UPDATE_ID
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class UpdateIdNotEqualException : BadRequestException(NO_MATCHED_UPDATE_ID_TYPE, NOT_MATCHED_UPDATE_ID)
