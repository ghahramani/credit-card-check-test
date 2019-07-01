package com.navid.test.creditcard.config.database.util

import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

data class SearchCriteria constructor(

    @NotEmpty
    val field: String,

    @Valid
    @NotNull
    val query: SearchCriteriaQuery

) {

    data class SearchCriteriaQuery(

        @NotEmpty
        val values: Set<String>,

        @NotEmpty
        val exclusions: Set<String>

    )

}
