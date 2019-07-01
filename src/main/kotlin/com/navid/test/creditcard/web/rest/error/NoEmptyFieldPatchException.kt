package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.NO_EMPTY_FIELD_PATCH_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.Crud.Companion.NO_EMPTY_PATCH_FIELDS_ALLOWED
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class NoEmptyFieldPatchException : BadRequestException(NO_EMPTY_FIELD_PATCH_TYPE, NO_EMPTY_PATCH_FIELDS_ALLOWED)
