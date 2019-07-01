package com.navid.test.creditcard.config.database.util

import org.springframework.data.mongodb.core.query.Criteria
import java.util.regex.Pattern
import org.springframework.data.mongodb.core.query.Query as MongoQuery

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */
object SearchUtil {

    fun convertToMongoQuery(criteria: Set<SearchCriteria>): MongoQuery {
        val builder = MongoQuery()
        val conditions = arrayListOf<Criteria>()

        for (item in criteria) {

            val where = Criteria.where(item.field)
            val hasValue = item.query.values.isNotEmpty()
            if (hasValue) {
                val regex = arrayListOf<Pattern>()
                for (value in item.query.values) {
                    regex.add(Pattern.compile(".*$value.*", Pattern.CASE_INSENSITIVE))
                }
                where.`in`(regex)
            }

            val hasExclusion = item.query.exclusions.isNotEmpty()
            if (hasExclusion) {
                where.nin(item.query.exclusions)
            }

            if (hasValue || hasExclusion) {
                conditions.add(where)
            }
        }

        if (conditions.isNotEmpty()) {
            builder.addCriteria(Criteria().andOperator(*conditions.toTypedArray()))
        }

        return builder
    }

}
