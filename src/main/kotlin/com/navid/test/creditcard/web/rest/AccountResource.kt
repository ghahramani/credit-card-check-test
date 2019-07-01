package com.navid.test.creditcard.web.rest

import com.navid.test.creditcard.config.AppConstants.Security
import com.navid.test.creditcard.config.security.jwt.AuthenticationToken
import com.navid.test.creditcard.service.UserService
import com.navid.test.creditcard.service.dto.UserDTO
import com.navid.test.creditcard.service.mapper.UserMapper
import com.navid.test.creditcard.service.util.error.AuthenticationException
import com.navid.test.creditcard.web.rest.error.FieldRequiredException
import com.navid.test.creditcard.web.rest.error.UserNotFoundException
import com.navid.test.creditcard.web.rest.error.UsernameNotFoundException
import com.navid.test.creditcard.web.rest.vm.LoginVM
import io.micrometer.core.annotation.Timed
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

/**
 * REST controller for managing the current user's account.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */
@RestController
@RequestMapping("/api/account")
class AccountResource(private val service: UserService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var mapper: UserMapper

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the true if the user is authenticated, otherwise false
     */
    @Timed
    @GetMapping
    fun isAuthenticated(request: Mono<AuthenticationToken>): Mono<UserDTO> {
        logger.debug("REST request to get current user")
        return request.flatMap { auth ->
            service
                .findByUsername(auth.principal.toString())
                .switchIfEmpty(Mono.error(AuthenticationException()))
                .map(mapper::userToUserDTO)
        }
    }

    @Timed
    @PostMapping("/authenticate")
    fun authenticate(@Valid @RequestBody vm: LoginVM, response: ServerHttpResponse): Mono<JWTTokenVM> {
        logger.debug("REST request to authenticating")

        return service
            .login(vm.username, vm.password, vm.remember)
            .map { jwt ->
                val httpHeaders = HttpHeaders()
                httpHeaders.add(Security.Jwt.HEADER_STRING, Security.Jwt.TOKEN_PREFIX + jwt)
                response.headers.addAll(httpHeaders)
                JWTTokenVM(jwt)
            }
    }

    /**
     * POST  / : update the current user information.
     *
     * @param vm the current user information
     * @throws UsernameNotFoundException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if the user login wasn't found
     */
    @Timed
    @PostMapping
    fun saveAccount(request: ServerHttpRequest, @Valid @RequestBody vm: UserDTO): Mono<UserDTO> {
        logger.debug("REST request to save current user")

        if (vm.username.isNullOrEmpty()) {
            throw FieldRequiredException("username")
        }

        return service
            .existsByUsername(vm.username as String)
            .switchIfEmpty(Mono.error(UsernameNotFoundException()))
            .map { exists ->
                if (!exists) {
                    throw UserNotFoundException()
                }
            }
            .flatMap { service.update(mapper.userDTOToUser(vm)) }
            .map(mapper::userToUserDTO)
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    inner class JWTTokenVM(val token: String)
}
