package com.navid.test.creditcard

import com.navid.test.creditcard.config.AppConstants
import com.navid.test.creditcard.config.AppProperties
import com.navid.test.creditcard.config.message.ExtendedRabbitProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.core.env.Environment
import java.net.InetAddress
import javax.annotation.PostConstruct

@EnableConfigurationProperties(
    ExtendedRabbitProperties::class,
    AppProperties::class,
    SpringDataWebProperties::class
)
@RefreshScope
@SpringBootApplication
class CreditCardApplication(private val env: Environment) {

    /**
     * Initializes test.
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     */
    @PostConstruct
    fun initApplication() {
        if (env.activeProfiles.contains(AppConstants.General.PROFILE_DEV) &&
            env.activeProfiles.contains(AppConstants.General.PROFILE_PROD)
        ) {
            logger.error("You are not allowed to run dev and prod profile at the same time")
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CreditCardApplication::class.java)
    }

}

fun main(args: Array<String>) {
    val logger: Logger = LoggerFactory.getLogger(CreditCardApplication::class.java)
    val app = SpringApplication(CreditCardApplication::class.java)

    val map = hashMapOf<String, Any>()
    map["spring.profiles.default"] = AppConstants.General.PROFILE_DEV
    app.setDefaultProperties(map)

    val context = app.run(*args)
    val env = context.environment

    var protocol = "http"

    if (env.getProperty("server.ssl.key-store") != null) {
        protocol = "https"
    }

    logger.info(
        """
-----------------------------------------------------------------------
	Application '{}' is running! Access URLs:
	Local: 		{}://localhost:{}
	External: 	{}://{}:{}
	Profile(s): 	{}
-----------------------------------------------------------------------""".trimIndent(),
        env.getProperty("spring.application.name"),
        protocol,
        env.getProperty("server.port"),
        protocol,
        InetAddress.getLocalHost().hostAddress,
        env.getProperty("server.port"),
        env.activeProfiles
    )
}
