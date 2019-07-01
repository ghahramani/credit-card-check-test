package com.navid.test.creditcard.config.database.nosql

import com.navid.test.creditcard.config.database.util.SearchCriteria
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/

@NoRepositoryBean
interface BaseGenericReactiveMongoRepository<T> : ReactiveMongoRepository<T, String> {

    fun patch(id: String, fields: Map<String, Any>): Mono<T>

    fun search(criteria: Set<SearchCriteria>, returnFields: Set<String>, pageable: Pageable): Flux<T>

    fun count(criteria: Set<SearchCriteria>): Mono<Long>

}
