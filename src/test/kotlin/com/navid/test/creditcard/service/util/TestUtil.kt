package com.navid.test.creditcard.service.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.FilterProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.ADMIN
import com.navid.test.creditcard.config.AppConstants.Security.Authority.Companion.USER
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import java.io.IOException
import java.util.HashSet

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */
object TestUtil {

    fun switchToAdmin() {
        val authentication = getAuthentication("admin", "admin", ADMIN)
        SecurityContextHolder.getContext().authentication = authentication
        ReactiveSecurityContextHolder.withAuthentication(authentication)
    }

    fun switchToUser() {
        val authentication = getAuthentication("user", "user", USER)
        SecurityContextHolder.getContext().authentication = authentication
        ReactiveSecurityContextHolder.withAuthentication(authentication)
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     */
    @Throws(IOException::class)
    fun convertObjectToJsonBytes(`object`: Any): ByteArray {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        val module = JavaTimeModule()
        mapper.registerModule(module)

        return mapper.writeValueAsBytes(`object`)
    }

    /**
     * Convert an value to JSON string.
     *
     * @param value the value to convert
     * @return the JSON string
     */
    @Throws(IOException::class)
    fun convertObjectToJsonString(value: Any, provider: FilterProvider?): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        if (provider != null) {
            mapper.setFilterProvider(provider)
        }

        val module = JavaTimeModule()
        mapper.registerModule(module)

        return mapper.writeValueAsString(value)
    }

    private fun getAuthentication(username: String, password: String, vararg authorities: String): Authentication {
        val grantedAuthorities = HashSet<GrantedAuthority>()
        for (authority in authorities) {
            grantedAuthorities.add(SimpleGrantedAuthority(authority))
        }
        return UsernamePasswordAuthenticationToken(username, password, grantedAuthorities)
    }
}
