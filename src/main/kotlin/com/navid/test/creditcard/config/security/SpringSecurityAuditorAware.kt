package com.navid.test.creditcard.config.security

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional

/**
 * Implementation of AuditorAware based on Spring Security.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Component
class SpringSecurityAuditorAware : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of("admin")
    }
}
