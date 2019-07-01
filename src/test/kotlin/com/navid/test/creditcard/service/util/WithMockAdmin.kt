package com.navid.test.creditcard.service.util

import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.ADMIN
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.USER
import org.springframework.security.test.context.support.WithMockUser

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Retention(AnnotationRetention.RUNTIME)
@WithMockUser(value = "admin", username = "admin", roles = [USER, ADMIN])
annotation class WithMockAdmin
