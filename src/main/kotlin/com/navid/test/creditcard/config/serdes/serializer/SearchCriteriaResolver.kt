package com.navid.test.creditcard.config.serdes.serializer

import com.navid.test.creditcard.config.database.util.SearchCriteria
import com.navid.test.creditcard.web.rest.util.ResourceUtil
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.pattern.PathPattern
import reactor.core.publisher.Mono
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.util.Optional

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/

@Component
class SearchCriteriaResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return try {

            if (parameter.genericParameterType !is ParameterizedTypeImpl) {
                return false
            }

            val typeImpl = parameter.genericParameterType as ParameterizedTypeImpl
            val arguments = typeImpl.actualTypeArguments

            if (arguments.isEmpty()) {
                return false
            }

            arguments[0] == SearchCriteria::class.java
        } catch (ignore: Exception) {
            return false
        }
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        val path = exchange.getAttribute<PathPattern>(ATTRIBUTE_PATTERN)
        val pattern = path?.patternString.orEmpty()

        if (pattern.isEmpty()) {
            return Mono.just(setOf<SearchCriteria>())
        }

        val criteria = AntPathMatcher().extractPathWithinPattern(pattern, exchange.request.path.value())

        if (criteria.isEmpty()) {
            return Mono.just(setOf<SearchCriteria>())
        }

        val fields = ResourceUtil.convertCriteria(Optional.ofNullable(criteria))
        return Mono.justOrEmpty(fields)
    }

    companion object {
        const val ATTRIBUTE_PATTERN = "org.springframework.web.reactive.HandlerMapping.bestMatchingPattern"
    }

}
