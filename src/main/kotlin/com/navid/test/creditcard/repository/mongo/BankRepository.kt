package com.navid.test.creditcard.repository.mongo

import com.navid.test.creditcard.domain.Bank
import com.navid.test.creditcard.repository.util.BaseMongoRepository
import reactor.core.publisher.Mono

/**
 * Spring Data MongoDB repository for the Bank entity.
 */
interface BankRepository : BaseMongoRepository<Bank> {

    fun existsByName(name: String): Mono<Boolean>
    fun findOneByName(name: String): Mono<Bank>

}
