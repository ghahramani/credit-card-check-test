package com.navid.test.creditcard.repository.util

import com.navid.test.creditcard.config.database.nosql.BaseGenericReactiveMongoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/

@NoRepositoryBean
interface BaseMongoRepository<T> : BaseGenericReactiveMongoRepository<T> {

    @Query("{ id: { \$exists: true }}")
    fun findAll(pageable: Pageable): Flux<T>

    fun findAllByIdIn(ids: Set<String>, pageable: Pageable): Flux<T>

}
