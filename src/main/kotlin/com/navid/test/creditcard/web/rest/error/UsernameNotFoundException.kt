package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.USERNAME_NOT_FOUND_TYPE
import com.navid.test.creditcard.config.AppConstants.Error.User.Companion.USERNAME_NOT_FOUND
import com.navid.test.creditcard.service.util.error.BadRequestException

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class UsernameNotFoundException : BadRequestException(USERNAME_NOT_FOUND_TYPE, USERNAME_NOT_FOUND)
