package com.navid.test.creditcard.web.rest

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.navid.test.creditcard.web.rest.vm.LoggerVM
import io.micrometer.core.annotation.Timed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

/**
 * Controller for view and managing Log Level at runtime.
 *
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@RestController
@RequestMapping("/actuator/logs")
class LogsResource {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Timed
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeLevel(@RequestBody vm: LoggerVM): Mono<Void> {
        logger.debug("REST request to change log level")
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        context.getLogger(vm.name).level = Level.valueOf(vm.level)
        return Mono.empty()
    }

    @Timed
    @GetMapping
    fun list(): Flux<LoggerVM> {
        logger.debug("REST request to retrieving all logs definitions")
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        return context.loggerList
            .stream()
            .map(::LoggerVM)
            .toFlux()
    }
}
