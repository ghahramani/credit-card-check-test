package com.navid.test.creditcard.service.util

import com.navid.test.creditcard.config.database.util.SearchCriteria
import com.navid.test.creditcard.domain.util.BaseModelWithId
import com.navid.test.creditcard.repository.util.BaseMongoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */
open class BaseMongoService<ENTITY, out REPOSITORY>(protected open val repository: REPOSITORY)
    where ENTITY : BaseModelWithId, REPOSITORY : BaseMongoRepository<ENTITY> {

    protected open val logger: Logger = LoggerFactory.getLogger(javaClass)

    open fun all(pageable: Pageable): Flux<ENTITY> {
        logger.debug("Request to get all entities")
        return repository.findAll(pageable)
    }

    open fun findByIds(ids: Set<String>, pageable: Pageable): Flux<ENTITY> {
        logger.debug("Request to get all entities")
        return repository.findAllByIdIn(ids, pageable)
    }

    open fun count(criteria: Set<SearchCriteria>): Mono<Long> {
        logger.debug("Request for count of entities")
        return repository.count(criteria)
    }

    open fun create(model: ENTITY): Mono<ENTITY> {
        logger.debug("Request to create entity: $model")
        return repository.save(model)
    }

    open fun delete(id: String): Mono<Void> {
        logger.debug("Request to delete entity: $id")
        return repository.deleteById(id)
    }

    open fun exists(id: String): Mono<Boolean> {
        logger.debug("Request to check existence entity: $id")
        return repository.existsById(id)
    }

    open fun get(id: String): Mono<ENTITY> {
        logger.debug("Request to check entity with id: $id")
        return repository.findById(id)
    }

    open fun update(model: ENTITY): Mono<ENTITY> {
        logger.debug("Request to update entity: $model")
        return repository.save(model)
    }

    open fun patch(id: String, fields: Map<String, Any>): Mono<ENTITY> {
        logger.debug("Request to patch for id: $id with fields: $fields")
        return repository.patch(id, fields)
    }

    open fun search(criteria: Set<SearchCriteria>, returnFields: Set<String>, pageable: Pageable): Flux<ENTITY> {
        logger.debug("Request searching in entities")
        return repository.search(criteria, returnFields, pageable)
    }
}
