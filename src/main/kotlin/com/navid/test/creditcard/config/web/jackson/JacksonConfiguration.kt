package com.navid.test.creditcard.config.web.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Configuration
class JacksonConfiguration {

    /*
     * Jackson Afterburner module to speed up serialization/deserialization.
     */
    @Bean
    fun afterburnerModule(): AfterburnerModule {
        return AfterburnerModule()
    }

    @Bean
    @Primary
    fun mapper(module: AfterburnerModule): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(module)
        mapper.registerModule(KotlinModule())
        mapper.registerModule(JavaTimeModule())
        mapper.findAndRegisterModules()
        return mapper
    }

}
