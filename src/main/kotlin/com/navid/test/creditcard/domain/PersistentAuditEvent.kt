package com.navid.test.creditcard.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.Instant
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * Persist AuditEvent managed by the Spring Boot actuator.
 *
 * @see org.springframework.boot.actuate.audit.AuditEvent
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Document(collection = "persistent_audit_events")
data class PersistentAuditEvent(

    @Id
    var id: String? = null,

    @NotNull
    var auditEventDate: Instant = Instant.now(),

    @NotEmpty
    var auditEventType: String,

    val data: HashMap<String, String> = HashMap(),

    @NotEmpty
    var principal: String? = null

) : Serializable
