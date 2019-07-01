package com.navid.test.creditcard.domain.util

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import java.io.Serializable
import java.time.Instant

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@JsonIgnoreProperties(value = ["createdAt", "createdBy", "modifiedAt", "modifiedBy"], allowGetters = true)
abstract class AbstractAuditing : Serializable {

    @CreatedBy
    @JsonIgnore
    var createdBy: String? = null

    @Indexed
    @CreatedDate
    var createdAt: Instant = Instant.now()

    @JsonIgnore
    @LastModifiedBy
    var modifiedBy: String? = null

    @Indexed
    @LastModifiedDate
    var modifiedAt: Instant = Instant.now()

    companion object {
        private const val serialVersionUID = 1L
    }
}
