package com.navid.test.creditcard.domain

import org.springframework.data.annotation.Id
import java.io.Serializable
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size
import org.springframework.data.mongodb.core.mapping.Document as MongoDocument

/**
 * An authority (a security role) used by Spring Security.
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@MongoDocument(collection = "authorities")
data class Authority(

    @Id
    @NotEmpty
    @Size(min = 1, max = 50)
    var name: String

) : Serializable
