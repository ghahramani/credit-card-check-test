package com.navid.test.creditcard.repository.mongo.extended.impl

import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.config.audit.PersistentAuditEventConverter
import com.navid.test.creditcard.domain.PersistentAuditEvent
import com.navid.test.creditcard.repository.mongo.PersistenceAuditEventRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.HashMap

/**
 * An implementation of Spring Boot's AuditEventRepository.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

open class AuditEventRepositoryImpl(
    private val repository: PersistenceAuditEventRepository,
    private val converter: PersistentAuditEventConverter
) : AuditEventRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun add(event: AuditEvent) {
        if (AUTHORIZATION_FAILURE != event.type && AppConstants.Security.Authority.ANONYMOUS != event.principal) {
            val entity = PersistentAuditEvent(auditEventType = event.type)
            entity.principal = event.principal
            entity.auditEventDate = event.timestamp
            val eventData = converter.convertDataToStrings(event.data)

            entity.data.clear()
            entity.data.putAll(truncate(eventData))
            repository.save(entity).subscribe()
        }
    }

    override fun find(principal: String, after: Instant, type: String): List<AuditEvent> {
        val persistentAuditEvents = repository.findByPrincipalAndAuditEventDateAfterAndAuditEventType(
            principal,
            after,
            type
        )
        return converter.convertToAuditEvent(persistentAuditEvents)
    }

    /**
     * Truncate event data that might exceed column length.
     */
    private fun truncate(data: Map<String, String>): Map<String, String> {
        val results = HashMap<String, String>()

        for (entry in data.entries) {
            var value: String = entry.value
            val length = value.length
            if (length > EVENT_DATA_COLUMN_MAX_LENGTH) {
                value = value.substring(0, EVENT_DATA_COLUMN_MAX_LENGTH)
                logger.warn(
                    "Event data for {} too long ({}) has been truncated to {}. Consider increasing column width.",
                    entry.key,
                    length,
                    EVENT_DATA_COLUMN_MAX_LENGTH
                )
            }
            results[entry.key] = value
        }
        return results
    }

    companion object {
        /**
         * Should be the same as in Liquibase migration.
         */
        private const val EVENT_DATA_COLUMN_MAX_LENGTH = 255
        private const val AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE"
    }
}
