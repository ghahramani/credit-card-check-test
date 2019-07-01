package com.navid.test.creditcard.config.data

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Configuration
class HttpClientTestConfiguration {

    @Bean
    fun webTestClient(context: ApplicationContext) =
        WebTestClient.bindToApplicationContext(context).build()

}
