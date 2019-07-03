package com.navid.test.creditcard.web.rest.vm

import org.apache.commons.lang3.builder.ToStringBuilder
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

/**
 * View Model object for storing a user's credentials.
 */

data class LoginVM @JvmOverloads constructor(

    @NotEmpty
    @Size(min = 1, max = 50)
    val username: String,

    @NotEmpty
    @Size(min = 4, max = 100)
    val password: String,

    val remember: Boolean = false

) {

    override fun toString(): String {
        return ToStringBuilder(this)
            .append("username", username)
            .append("remember", remember)
            .toString()
    }

}
