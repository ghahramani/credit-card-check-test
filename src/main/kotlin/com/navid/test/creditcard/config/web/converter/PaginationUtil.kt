package com.navid.test.creditcard.config.web.converter

import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import java.util.ArrayList

object PaginationUtil {

    fun toHttpHeaders(pageable: Pageable): HttpHeaders {
        val map = HttpHeaders()

        map.add("page", pageable.pageNumber.toString())
        map.add("size", pageable.pageSize.toString())

        val sort = ArrayList<String>()

        for (next in pageable.sort) {
            sort.add(next.property + "," + next.direction.name.toLowerCase())
        }

        if (sort.isNotEmpty()) {
            map["sort"] = sort
        }

        return map
    }

}
