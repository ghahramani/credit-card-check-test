package com.navid.test.creditcard.repository.mongo

import com.navid.test.creditcard.domain.User
import com.navid.test.creditcard.repository.util.BaseMongoRepository
import reactor.core.publisher.Mono

/**
 * Spring Data MongoDB repository for the User entity.
 */
interface UserRepository : BaseMongoRepository<User> {

    fun findOneByUsername(username: String): Mono<User>
    fun existsByUsername(username: String): Mono<Boolean>

}
