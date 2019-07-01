package com.navid.test.creditcard.web.rest.vm

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

class ProblemVM(
    val url: String,
    val message: String,
    val status: Int,
    val params: MutableMap<String, Any> = HashMap()
) {

    fun with(key: String, value: Any): ProblemVM {
        params[key] = value
        return this
    }

    fun with(values: HashMap<String, Any>): ProblemVM {
        params.clear()
        params.putAll(values)
        return this
    }
}
