package com.navid.test.creditcard.config.audit

import com.navid.test.creditcard.domain.PersistentAuditEvent
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import java.util.ArrayList
import java.util.Date
import java.util.HashMap

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Component
class PersistentAuditEventConverter {

    /**
     * Internal conversion. This is needed to support the current SpringBoot actuator AuditEventRepository interface
     *
     * @param data the data to convert
     * @return a map of String, Object
     */
    fun convertDataToObjects(data: Map<String, String>): Map<String, Any> {
        val results = HashMap<String, Any>()
        data.forEach { results[it.key] = it.value }
        return results
    }

    /**
     * Internal conversion. This method will allow to save additional data.
     * By default, it will save the object as string
     *
     * @param data the data to convert
     * @return a map of String, String
     */
    fun convertDataToStrings(data: Map<String, Any?>?): Map<String, String> {
        val results = HashMap<String, String>()

        if (data != null) {
            for ((key, obj) in data) {

                // Extract the data that will be saved.
                when {
                    obj is WebAuthenticationDetails -> {
                        results["remoteAddress"] = obj.remoteAddress
                        results["sessionId"] = obj.sessionId
                    }

                    obj != null -> results[key] = obj.toString()
                    else -> results[key] = "null"
                }
            }
        }

        return results
    }

    /**
     * Convert a list of PersistentAuditEvent to a list of AuditEvent
     *
     * @param events the list to convert
     * @return the converted list.
     */
    fun convertToAuditEvent(events: Iterable<PersistentAuditEvent>?): List<AuditEvent> {
        if (events == null) {
            return emptyList()
        }
        val auditEvents = ArrayList<AuditEvent>()
        for (item in events) {
            convertToAuditEvent(item)?.let { auditEvents.add(it) }
        }
        return auditEvents
    }

    /**
     * Convert a PersistentAuditEvent to an AuditEvent
     *
     * @param entity the event to convert
     * @return the converted list.
     */
    fun convertToAuditEvent(entity: PersistentAuditEvent): AuditEvent? {
        return AuditEvent(
            Date.from(entity.auditEventDate).toInstant(),
            entity.principal,
            entity.auditEventType,
            convertDataToObjects(entity.data)
        )

    }
}
