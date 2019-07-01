package com.navid.test.creditcard.repository.mongo

import com.navid.test.creditcard.domain.Authority
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
interface AuthorityRepository : ReactiveMongoRepository<Authority, String>
