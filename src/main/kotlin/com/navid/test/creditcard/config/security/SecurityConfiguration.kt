package com.navid.test.creditcard.config.security

import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.config.AppProperties
import com.navid.test.creditcard.config.error.ExceptionTranslator
import com.navid.test.creditcard.config.error.ProblemHandler
import com.navid.test.creditcard.config.security.jwt.SecurityContextRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.util.pattern.PathPatternParser

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
class SecurityConfiguration {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun filter(
        http: ServerHttpSecurity,
        repo: SecurityContextRepository,
        manager: AuthenticationManager,
        problem: ProblemHandler
    ): SecurityWebFilterChain {

        return http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()

            .authorizeExchange()

            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            .pathMatchers("/app/**/*.js").permitAll()
            .pathMatchers("/app/**/*.html").permitAll()
            .pathMatchers("/i18n/**/*.json").permitAll()
            .pathMatchers("/assets/**").permitAll()
            .pathMatchers("/v2/api-docs/**").permitAll()
            .pathMatchers("/swagger-resources/configuration/ui").permitAll()
            .pathMatchers("/swagger-ui/index.html").hasAuthority(with(AppConstants.Security.Authority) { PREFIX + ACTUATOR })

            .pathMatchers(HttpMethod.POST, "/api/account/authenticate").permitAll()
            .pathMatchers("/api/account/reset-password/init").permitAll()
            .pathMatchers("/api/account/reset-password/finish").permitAll()
            .pathMatchers("/api/**").authenticated()
            .pathMatchers("/actuator/prometheus").permitAll()
            .pathMatchers("/actuator/health").permitAll()
            .pathMatchers("/actuator/**").hasAuthority(with(AppConstants.Security.Authority) { PREFIX + ACTUATOR })

            .anyExchange().permitAll()

            .and()

            .exceptionHandling()
            .authenticationEntryPoint(problem)
            .accessDeniedHandler(ExceptionTranslator())
            .and()

            .securityContextRepository(repo)
            .authenticationManager(manager)

            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(PASSWORD_STRENGTH)
    }

    @Bean
    @ConditionalOnProperty(name = ["app.cors.allowed-origins"])
    fun corsWebFilter(properties: AppProperties): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource(PathPatternParser())
        logger.debug("Registering CORS filter")
        source.registerCorsConfiguration("/api/**", properties.cors)
        source.registerCorsConfiguration("/actuator/**", properties.cors)
        source.registerCorsConfiguration("/v2/api-docs", properties.cors)
        return CorsWebFilter(source)
    }

    companion object {
        private const val PASSWORD_STRENGTH = 10
    }
}
