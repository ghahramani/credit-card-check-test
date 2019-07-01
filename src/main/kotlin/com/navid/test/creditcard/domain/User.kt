package com.navid.test.creditcard.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.domain.util.BaseModelWithId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * A user.
 */
@Document(collection = "users")
data class User(

    @NotEmpty
    @Indexed(unique = true)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    @Pattern(regexp = AppConstants.Security.USERNAME_PATTERN)
    val username: String? = null,

    @NotEmpty
    @JsonIgnore
    @Size(min = 60, max = 60)
    //We should keep the password out from front-end and we will change it from UserDTO
    //We store hashed password in db for security reason
    val password: String? = null,

    @JsonIgnore
    //The authorities should keep out from front-end to prevent exposing the permissions
    val authorities: HashSet<Authority> = hashSetOf(),

    @Size(min = 2, max = 5)
    val langKey: String? = null

) : BaseModelWithId() {

    companion object {
        const val USERNAME_MAX_LENGTH = 50
        const val USERNAME_MIN_LENGTH = 1
        private const val serialVersionUID = 1422425627594240748L
    }

}
