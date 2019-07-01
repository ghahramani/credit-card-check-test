package com.navid.test.creditcard.config.web

import com.navid.test.creditcard.config.serdes.deserializer.PageableResolver
import com.navid.test.creditcard.config.serdes.serializer.SearchCriteriaResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.server.WebFilter

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Configuration
class WebConfiguration(
    private val pageableResolver: PageableResolver,
    private val searchCriteriaResolver: SearchCriteriaResolver
) : WebFluxConfigurer {

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(pageableResolver)
        configurer.addCustomResolver(searchCriteriaResolver)
    }

    @Bean
    fun indexHtmlFilter(): WebFilter {
        return WebFilter { exchange, chain ->
            var filter = chain.filter(exchange)

            if (exchange.request.uri.path == "/") {
                filter = chain.filter(
                    exchange
                        .mutate()
                        .request(exchange.request.mutate().path("/index.html").build())
                        .build()
                )
            }

            return@WebFilter filter
        }
    }

}
