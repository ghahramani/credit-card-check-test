package com.navid.test.creditcard.service.dto

import com.navid.test.creditcard.config.AppConstants.Security.Companion.USERNAME_PATTERN
import com.navid.test.creditcard.domain.Authority
import com.navid.test.creditcard.domain.User
import java.io.Serializable
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * A DTO representing a user, with his authorities.
 */
class UserDTO : Serializable {

    var id: String? = null

    @Pattern(regexp = USERNAME_PATTERN)
    @Size(min = User.USERNAME_MIN_LENGTH, max = User.USERNAME_MAX_LENGTH)
    var username: String? = null

    @Size(min = 2, max = 5)
    var langKey: String? = null

    var authorities: Set<String>? = null

    constructor() {
        // Empty constructor needed for MapStruct.
    }

    constructor(user: User) : this(
        user.id,
        user.username,
        user.langKey,
        user.authorities.map(Authority::name).toSet()
    )

    constructor(id: String?, username: String?, langKey: String?, authorities: Set<String>?) {
        this.id = id
        this.username = username
        this.langKey = langKey
        this.authorities = authorities
    }

    companion object {
        private const val serialVersionUID = 9092925377899439397L
    }
}
