package com.navid.test.creditcard.config.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Component
class AuthenticationManager : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
    }

}
