package com.navid.test.creditcard.config.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.actuate.metrics.web.client.MetricsRestTemplateCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.data.web.ProjectingJackson2HttpMessageConverter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter.DEFAULT_CHARSET
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Configuration
class HttpClientConfiguration {

    @Bean
    @Primary
    fun restTemplate(
        mapper: ObjectMapper,
        builder: RestTemplateBuilder,
        customizer: MetricsRestTemplateCustomizer
    ): RestTemplate {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setBufferRequestBody(false)

        val template = builder
            .requestFactory { return@requestFactory requestFactory }
            .additionalMessageConverters(
                ProjectingJackson2HttpMessageConverter(),
                FormHttpMessageConverter()
            )
            .additionalCustomizers(customizer)
            .build()

        for (converter in template.messageConverters) {
            if (converter is MappingJackson2HttpMessageConverter) {
                converter.objectMapper = mapper
                converter.supportedMediaTypes = listOf(
                    MediaType("application", "hal+json", DEFAULT_CHARSET),
                    MediaType(MediaType.APPLICATION_JSON, DEFAULT_CHARSET),
                    MediaType("text", "javascript", DEFAULT_CHARSET)
                )
            }
        }

        return template
    }

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.build()
    }

    @Scope("prototype")
    @Bean("jsonHttpHeader")
    fun jsonHttpHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        return headers
    }
}
