package com.navid.test.creditcard.domain.util

import org.springframework.data.annotation.Id

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

abstract class BaseModelWithId : AbstractAuditing() {

    @Id
    var id: String? = null
}
