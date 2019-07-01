package com.navid.test.creditcard.web.rest.error

import com.navid.test.creditcard.config.AppConstants.Error.Companion.Url.Companion.UI_TYPE
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.http.HttpStatus

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

abstract class ParametricException @JvmOverloads constructor(
    url: String = UI_TYPE,
    msg: String,
    status: HttpStatus = HttpStatus.BAD_REQUEST,
    val params: MutableMap<String, Any> = HashMap()
) : BaseException(url, msg, status) {

    fun putParam(key: String, value: Any): Map<String, Any> {
        params[key] = value
        return params
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("params", params)
            .toString()
    }
}
