package com.navid.test.creditcard.service

import com.navid.test.creditcard.config.audit.PersistentAuditEventConverter
import com.navid.test.creditcard.repository.mongo.PersistenceAuditEventRepository
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Instant

/**
 * Service for managing audit events.
 *
 *
 * This is the default implementation to support SpringBoot Actuator AuditEventRepository
 *
 */
@Service
class AuditEventService(
    private val persistenceAuditEventRepository: PersistenceAuditEventRepository,
    private val auditEventConverter: PersistentAuditEventConverter
) {

    fun find(id: String) =
        persistenceAuditEventRepository
            .findOneById(id)
            .map { event -> auditEventConverter.convertToAuditEvent(event) }

    fun findAll(pageable: Pageable) =
        persistenceAuditEventRepository
            .findAll(pageable)
            .map { event -> auditEventConverter.convertToAuditEvent(event) }

    fun findByDates(fromDate: Instant, toDate: Instant, pageable: Pageable): Flux<AuditEvent> =
        persistenceAuditEventRepository
            .findAllByAuditEventDateBetween(fromDate, toDate, pageable)
            .map { event -> auditEventConverter.convertToAuditEvent(event) }
}
