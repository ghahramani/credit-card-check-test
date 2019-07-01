package com.navid.test.creditcard.web.rest.util

import com.navid.test.creditcard.config.database.util.SearchCriteria
import java.net.URLDecoder
import java.util.HashSet
import java.util.Optional

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

object ResourceUtil {

    private const val NAME_INDEX = 0
    private const val VALUE_INDEX = 1
    private const val EXCLUSION_INDEX = 2

    fun convertCriteria(criteria: Optional<String>): Set<SearchCriteria> {
        val fields: HashSet<SearchCriteria> = hashSetOf()

        criteria.map { condition ->
            if (condition.isEmpty()) {
                return@map
            }

            // TODO: needs to add (and &) and (or | already added) for values (needs and) and exclusions (needs or)
            val pattern = URLDecoder.decode(condition, Charsets.UTF_8.displayName()).split("/")

            for (item in pattern) {
                if (item.isEmpty()) {
                    continue
                }
                val fieldDef = item.split("::")
                val name = fieldDef[NAME_INDEX]
                var value = setOf<String>()
                var exclusion = setOf<String>()

                val size = fieldDef.size
                if (size > VALUE_INDEX) {
                    value = fieldDef[VALUE_INDEX].split("|").toSet()

                    if (size > EXCLUSION_INDEX) {
                        val temp = fieldDef[EXCLUSION_INDEX]

                        if (temp.isNotEmpty()) {
                            exclusion = temp.split("&").toSet()
                        }
                    }
                }

                fields.add(SearchCriteria(name, SearchCriteria.SearchCriteriaQuery(value, exclusion)))
            }
        }

        return fields
    }
}
