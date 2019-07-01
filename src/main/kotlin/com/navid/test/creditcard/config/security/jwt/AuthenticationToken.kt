package com.navid.test.creditcard.config.security.jwt

import com.navid.test.creditcard.config.AppConstants.Security.Jwt.Companion.KEY_ID
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.annotation.AuthenticationPrincipal

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class AuthenticationToken @JvmOverloads constructor(
    val id: String? = null,
    @AuthenticationPrincipal username: String,
    password: String,
    authorities: Collection<GrantedAuthority> = emptyList()
) : UsernamePasswordAuthenticationToken(username, password, authorities) {

    fun values(): Map<String, Any> {
        val params = hashMapOf<String, Any>()

        params[KEY_ID] = id!!

        return params
    }
}
