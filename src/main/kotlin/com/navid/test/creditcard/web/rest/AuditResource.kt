package com.navid.test.creditcard.web.rest

import com.navid.test.creditcard.service.AuditEventService
import io.micrometer.core.annotation.Timed
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.time.ZoneId

/**
 * REST controller for getting the audit events.
 *
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@RestController
@RequestMapping("/actuator/audits")
class AuditResource(private val service: AuditEventService) {

    /**
     * GET /audits : get a page of AuditEvents.
     *
     * @return the Flux<AuditEvent> with status 200 (OK) and the list of AuditEvents in body
     */
    @Timed
    @GetMapping
    fun all(pageable: Pageable) = service.findAll(pageable)

    /**
     * GET  /audits/:id : get an AuditEvent by id.
     *
     * @param id the id of the entity to get
     * @return the Flux<AuditEvent> with status 200 (OK) and the AuditEvent in body, or status 404 (Not Found)
     */
    @Timed
    @GetMapping("/{id:.+}")
    fun byId(@PathVariable id: String) = service.find(id)

    /**
     * GET  /audits : get a page of AuditEvents between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of AuditEvents to get
     * @param toDate the end of the time period of AuditEvents to get
     * @return the Flux<AuditEvent> with status 200 (OK) and the list of AuditEvents in body
     */
    @Timed
    @GetMapping(params = ["fromDate", "toDate"])
    fun byDates(
        @RequestParam(value = "fromDate") fromDate: LocalDate,
        @RequestParam(value = "toDate") toDate: LocalDate,
        pageable: Pageable
    ): Flux<AuditEvent> {
        val zone = ZoneId.systemDefault()
        return service.findByDates(
            fromDate.atStartOfDay(zone).toInstant(),
            toDate.atStartOfDay(zone).plusDays(1).toInstant(),
            pageable
        )
    }
}
