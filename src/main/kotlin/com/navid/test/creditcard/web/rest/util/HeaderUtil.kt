package com.navid.test.creditcard.web.rest.util

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

/**
 * Utility class for HTTP headers creation.
 */
object HeaderUtil {

    private val log = LoggerFactory.getLogger(HeaderUtil::class.java)

    private const val APPLICATION_NAME = "credit-card"

    fun createAlert(message: String, param: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("x-credit-card-alert", message)
        headers.add("x-credit-card-params", param)
        return headers
    }

    fun createEntityCreationAlert(entityName: String, param: String): HttpHeaders {
        return createAlert("$APPLICATION_NAME.$entityName.created", param)
    }

    fun createEntityUpdateAlert(entityName: String, param: String): HttpHeaders {
        return createAlert("$APPLICATION_NAME.$entityName.updated", param)
    }

    fun createEntityDeletionAlert(entityName: String, param: String): HttpHeaders {
        return createAlert("$APPLICATION_NAME.$entityName.deleted", param)
    }

    fun createFailureAlert(entityName: String, errorKey: String, defaultMessage: String): HttpHeaders {
        log.error("Entity processing failed, {}", defaultMessage)
        val headers = HttpHeaders()
        headers.add("x-credit-card-error", "error.$errorKey")
        headers.add("x-credit-card-params", entityName)
        return headers
    }
}
