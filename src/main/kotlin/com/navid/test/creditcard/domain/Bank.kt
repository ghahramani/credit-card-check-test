package com.navid.test.creditcard.domain

import com.navid.test.creditcard.domain.util.BaseModelWithId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotEmpty

/**
 * Developed by Navid Ghahremani (ghahramani.navid@gmail.com)
 */
@Document(collection = "banks")
data class Bank constructor(

    @NotEmpty
    @Indexed(unique = true)
    val name: String

) : BaseModelWithId() {

    companion object {
        private const val serialVersionUID = 7664699765208250792L
    }

    //Other fields are omitted because we don't need them in this test

}
