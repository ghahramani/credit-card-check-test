package com.navid.test.creditcard.service

import com.navid.test.creditcard.config.security.jwt.AuthenticationToken
import com.navid.test.creditcard.config.security.jwt.TokenProvider
import com.navid.test.creditcard.domain.User
import com.navid.test.creditcard.repository.mongo.UserRepository
import com.navid.test.creditcard.service.util.BaseMongoService
import com.navid.test.creditcard.service.util.error.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * Service class for managing users.
 */

@Service
class UserService(
    repository: UserRepository,
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder
) : BaseMongoService<User, UserRepository>(repository) {

    fun findByUsername(username: String): Mono<User> {
        logger.debug("Request to check enabled user with username: $username")
        return repository.findOneByUsername(username)
    }

    fun existsByUsername(username: String): Mono<Boolean> {
        logger.debug("Request to check enabled user with username: $username")
        return repository.existsByUsername(username)
    }

    fun login(username: String, password: String, remember: Boolean): Mono<String> =
        findByUsername(username)
            .map { user ->
                if (!passwordEncoder.matches(password, user.password)) {
                    throw AuthenticationException()
                }
                tokenProvider.createToken(
                    AuthenticationToken(
                        user.id,
                        username,
                        password,
                        user.authorities.map { role -> SimpleGrantedAuthority(role.name) }
                    ),
                    remember
                )
            }
            .switchIfEmpty(Mono.error(AuthenticationException()))

}
