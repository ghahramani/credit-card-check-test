package com.navid.test.creditcard.config.serdes.deserializer

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.data.web.SortHandlerMethodArgumentResolver
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer
import java.util.Optional
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer as SortResolver

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 **/

@Configuration
class PageableSerializer {

    @Bean
    @ConditionalOnMissingBean
    fun pageableCustomizer(properties: SpringDataWebProperties): PageableHandlerMethodArgumentResolverCustomizer {
        return PageableHandlerMethodArgumentResolverCustomizer { resolver ->
            val pageable = properties.pageable

            resolver.setPageParameterName(pageable.pageParameter)
            resolver.setSizeParameterName(pageable.sizeParameter)
            resolver.setOneIndexedParameters(pageable.isOneIndexedParameters)
            resolver.setPrefix(pageable.prefix)
            resolver.setQualifierDelimiter(pageable.qualifierDelimiter)
            resolver.setFallbackPageable(PageRequest.of(0, pageable.defaultPageSize))
            resolver.setMaxPageSize(pageable.maxPageSize)

        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun sortCustomizer(properties: SpringDataWebProperties): SortResolver {
        return SortResolver { resolver ->
            resolver.setSortParameter(properties.sort.sortParameter)
        }
    }

    @Bean
    fun pageableHandler(
        pageableResolver: Optional<PageableHandlerMethodArgumentResolverCustomizer>,
        sortHandler: SortHandlerMethodArgumentResolver
    ): PageableHandlerMethodArgumentResolver {
        val handler = PageableHandlerMethodArgumentResolver(sortHandler)
        pageableResolver.ifPresent { it.customize(handler) }
        return handler
    }

    @Bean
    fun sortHandler(sortResolver: Optional<SortResolver>): SortHandlerMethodArgumentResolver {
        val handler = SortHandlerMethodArgumentResolver()
        sortResolver.ifPresent { it.customize(handler) }
        return handler
    }

}
