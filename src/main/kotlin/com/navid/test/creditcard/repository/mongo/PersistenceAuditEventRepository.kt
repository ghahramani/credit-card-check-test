package com.navid.test.creditcard.repository.mongo

import com.navid.test.creditcard.domain.PersistentAuditEvent
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Spring Data MongoDB repository for the PersistentAuditEvent entity.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */
interface PersistenceAuditEventRepository : ReactiveMongoRepository<PersistentAuditEvent, String> {

    fun findOneById(id: String): Mono<PersistentAuditEvent>

    @Query("{ id: { \$exists: true }}")
    fun findAll(pageable: Pageable): Flux<PersistentAuditEvent>

    fun findAllByAuditEventDateBetween(
        fromDate: Instant,
        toDate: Instant,
        pageable: Pageable
    ): Flux<PersistentAuditEvent>

    fun findByPrincipalAndAuditEventDateAfterAndAuditEventType(
        principle: String,
        after: Instant,
        type: String
    ): List<PersistentAuditEvent>
}
