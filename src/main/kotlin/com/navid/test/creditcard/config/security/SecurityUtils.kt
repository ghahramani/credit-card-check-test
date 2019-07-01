package com.navid.test.creditcard.config.security

import com.navid.test.creditcard.config.security.jwt.AuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

/**
 * Utility class for Spring Security.
 */
object SecurityUtils {

    val currentOssTokenReactive: Mono<String>
        get() {
            val securityContext = ReactiveSecurityContextHolder.getContext()
            return securityContext.flatMap { context ->
                val authentication = context.authentication
                if (authentication is AuthenticationToken) {
                    return@flatMap Mono.justOrEmpty(authentication.id)
                }
                Mono.empty<String>()
            }
        }

    val currentOssToken: String?
        get() {
            val securityContext = SecurityContextHolder.getContext()
            val authentication = securityContext.authentication
            if (authentication is AuthenticationToken) {
                return authentication.id
            }

            return null
        }

    /**
     * Get the username of the current user.
     *
     * @return the username of the current user
     */
    val currentUsername: String?
        get() {
            val securityContext = SecurityContextHolder.getContext()
            val authentication = securityContext.authentication
            var userName: String? = null
            if (authentication != null) {
                if (authentication.principal is UserDetails) {
                    val springSecurityUser = authentication.principal as UserDetails
                    userName = springSecurityUser.username
                } else if (authentication.principal is String) {
                    userName = authentication.principal as String
                }
            }
            return userName
        }

}
