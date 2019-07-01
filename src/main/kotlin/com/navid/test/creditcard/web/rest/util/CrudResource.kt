package com.navid.test.creditcard.web.rest.util

import com.navid.test.creditcard.config.database.util.SearchCriteria
import com.navid.test.creditcard.domain.util.BaseModelWithId
import com.navid.test.creditcard.repository.util.BaseMongoRepository
import com.navid.test.creditcard.service.util.BaseMongoService
import com.navid.test.creditcard.service.util.error.BadRequestException
import com.navid.test.creditcard.web.rest.error.CreateIdExistsException
import com.navid.test.creditcard.web.rest.error.EntityNotFoundException
import com.navid.test.creditcard.web.rest.error.NoEmptyFieldPatchException
import com.navid.test.creditcard.web.rest.error.UpdateIdNotEqualException
import io.micrometer.core.annotation.Timed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */
abstract class CrudResource<
    ENTITY : BaseModelWithId,
    out REPOSITORY : BaseMongoRepository<ENTITY>,
    out SERVICE : BaseMongoService<ENTITY, REPOSITORY>>(protected open val service: SERVICE) {

    protected open val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * GET / : get all entities.
     *
     * @return the ResponseEntity with status 200 (OK) and with body all entities
     */
    @Timed
    @GetMapping
    open fun all(pageable: Pageable): Flux<ENTITY> {
        logger.debug("REST request to retrieving entities")
        return service.all(pageable)
    }

    /**
     * GET /count : number of persisted records of entity.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the number
     */
    @Timed
    @GetMapping(value = ["/count/**", "/count"])
    open fun count(criteria: Set<SearchCriteria>): Mono<Long> {
        logger.debug("REST request to retrieving entities count")
        return service.count(criteria)
    }

    /**
     * GET /:id : get the entity by id.
     *
     * @param id the id of the entity to find
     * @return the ResponseEntity with status 200 (OK) and with body the entity, or with status 404 (Not Found)
     */
    @Timed
    @GetMapping("/{id}")
    open fun get(@PathVariable id: String): Mono<ENTITY> {
        logger.debug("REST request to retrieving entity with id: $id")
        return service.get(id).switchIfEmpty(Mono.error(EntityNotFoundException(entityNameFromServiceType())))
    }

    /**
     * DELETE /:id : delete the entity by id.
     *
     * @param id the id of the entity to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Timed
    @DeleteMapping("/{id}")
    open fun delete(@PathVariable id: String): Mono<Boolean> {
        logger.debug("REST request to deleting entity with id: $id")
        return service.delete(id).map { true }.switchIfEmpty(Mono.just(false))
    }

    /**
     * POST  /  : Creates a new entity.
     *
     * Creates a new entity
     *
     * @param entity the entity to create
     * @return the ResponseEntity with status 201 (Created) and with body the new entity,
     * or with status 400 (Bad Request) if the id is set
     *
     * @throws BadRequestException 400 (Bad Request) if the id is set
     */
    @Timed
    @PostMapping
    open fun create(@Valid @RequestBody entity: ENTITY): Mono<ENTITY> {
        logger.debug("REST request to saving entity with entity: $entity")
        if (entity.id !== null) {
            throw CreateIdExistsException()
        }
        return service.create(entity)
    }

    /**
     * PUT /:id : Replace entire an existing entity.
     *
     * @param entity the entity to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user
     * @throws UpdateIdNotEqualException 400 (Bad Request) if the id is not equal with passed id in url
     */
    @Timed
    @PutMapping("/{id}")
    open fun update(@PathVariable id: String, @Valid @RequestBody entity: ENTITY): Mono<ENTITY> {
        logger.debug("REST request to saving entity with entity: $entity")
        entity.id = id
        return service
            .exists(id)
            .map { exists ->
                if (!exists) {
                    throw EntityNotFoundException(entity.javaClass.simpleName.toLowerCase())
                }
            }
            .then(service.update(entity))
    }

    /**
     * PATCH /:id : Updates an existing entity partially
     *
     * @param fields key=>value for fields that should be updated, key as name of field, value as updated value,
     * note that, audit fields and id will be ignored
     *
     * @return the ResponseEntity with status 200 (OK) and with body the updated entity
     * @throws NoEmptyFieldPatchException 400 (Bad Request) if the fields are empty
     */
    @Timed
    @PatchMapping("/{id}")
    open fun patch(@PathVariable id: String, @RequestBody fields: Map<String, Any>): Mono<ENTITY> {
        logger.debug("REST request to patch id $id with fields: $fields")
        if (fields.isEmpty()) {
            throw NoEmptyFieldPatchException()
        }
        return service.exists(id).map { exists ->
            if (!exists) {
                throw EntityNotFoundException(entityNameFromServiceType())
            }
        }.then(service.patch(id, fields))
    }

    @Timed
    @PostMapping("/{type}/{query:[a-zA-Z0-9 ]+}")
    open fun downloadAll(@PathVariable type: DownloadType, @PathVariable query: String, sort: Sort): Mono<ENTITY> {
        logger.debug("REST request to patch id $type")
        throw NotImplementedError()
    }

    @Timed
    @PostMapping("/{type}")
    open fun downloadSelected(@PathVariable type: DownloadType, @RequestBody ids: Set<String>): Mono<ENTITY> {
        logger.debug("REST request to patch id $type")
        throw NotImplementedError()
    }

    /**
     * SEARCH /_search/field1,field2/{field}:{type}:{value}[/...] : search for the User corresponding
     * to the query.
     *
     * @param ** the query to search e.x: {field}:{type}:{value}/{field2}:{type2}:{value2}. Note: Only field is required
     * @return the result of the search
     */
    @Timed
    @GetMapping(value = ["/_search/**", "/_search"])
    open fun search(pageable: Pageable, criteria: Set<SearchCriteria>): Flux<ENTITY> {
        return service.search(criteria, setOf(), pageable)
    }

    private fun entityNameFromServiceType() = service
        .javaClass
        .simpleName
        .replace("Service", "")
        .toLowerCase()

}
