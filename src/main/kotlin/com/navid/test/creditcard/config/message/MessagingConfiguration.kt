package com.navid.test.creditcard.config.message

import com.rabbitmq.client.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.amqp.RabbitProperties
import org.springframework.boot.context.properties.PropertyMapper
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Bean
import java.time.Duration

/**
 * Configures Spring Cloud Stream support.
 *
 *
 * See http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/
 * for more information.
 *
 *
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */
@EnableBinding(QueueChannel::class)
class MessagingConfiguration {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun rabbitAdmin(template: RabbitTemplate) = RabbitAdmin(template)

    @Bean
    // This is a workaround to assure spring cloud is connected to RabbitMQ servers as it does this check only when pushing messages or changing declarations
    fun checkRabbitMQConnectivity(factory: CachingConnectionFactory): CommandLineRunner = CommandLineRunner {
        val connection = factory.createConnection()
        logger.info("Rabbitmq is connected")
        connection.close()
    }

    @Bean
    @Throws(Exception::class)
    fun extendedConnectionFactory(
        properties: RabbitProperties,
        extendedProperties: ExtendedRabbitProperties,
        connectionNameStrategy: ObjectProvider<ConnectionNameStrategy>
    ): CachingConnectionFactory {

        val map = PropertyMapper.get()
        val factory = CachingConnectionFactory(getFactoryBean(properties, extendedProperties).getObject())

        map.from { extendedProperties.determineAddresses() }.to { factory.setAddresses(it) }
        map.from { properties.isPublisherConfirms }.to { factory.isPublisherConfirms = it }
        map.from { properties.isPublisherReturns }.to { factory.isPublisherReturns = it }

        val channel = properties.cache.channel
        map.from<Int> { channel.size }.whenNonNull().to { factory.channelCacheSize = it }

        map.from<Duration> { channel.checkoutTimeout }.whenNonNull().`as`<Long> { it.toMillis() }
            .to { factory.setChannelCheckoutTimeout(it) }

        val connection = properties.cache.connection
        map.from<CachingConnectionFactory.CacheMode> { connection.mode }.whenNonNull().to { factory.cacheMode = it }
        map.from<Int> { connection.size }.whenNonNull().to { factory.connectionCacheSize = it }
        map.from<ConnectionNameStrategy> { connectionNameStrategy.ifUnique }.whenNonNull()
            .to { factory.setConnectionNameStrategy(it) }

        return factory
    }

    @Bean
    fun factory(factory: CachingConnectionFactory): ConnectionFactory = factory.rabbitConnectionFactory

    private fun getFactoryBean(
        props: RabbitProperties,
        exProps: ExtendedRabbitProperties
    ): RabbitConnectionFactoryBean {
        val map = PropertyMapper.get()

        val factory = RabbitConnectionFactoryBean()
        map.from<String> { exProps.determineHost() }.whenNonNull().to { factory.setHost(it) }
        map.from { exProps.determinePort() }.to { factory.setPort(it) }
        map.from { exProps.determineUsername() }.whenNonNull().to { factory.setUsername(it) }
        map.from { exProps.determinePassword() }.whenNonNull().to { factory.setPassword(it) }
        map.from { exProps.determineVirtualHost() }.whenNonNull().to { factory.setVirtualHost(it) }
        map.from<Duration> { props.requestedHeartbeat }.whenNonNull().asInt<Long> { it.seconds }
            .to { factory.setRequestedHeartbeat(it) }

        if (exProps.determineSSL()) {
            val ssl = props.ssl
            factory.setUseSSL(true)
            map.from<String> { ssl.algorithm }.whenNonNull().to { factory.setSslAlgorithm(it) }
            map.from<String> { ssl.keyStoreType }.to { factory.setKeyStoreType(it) }
            map.from<String> { ssl.keyStore }.to { factory.setKeyStore(it) }
            map.from<String> { ssl.keyStorePassword }.to { factory.setKeyStorePassphrase(it) }
            map.from<String> { ssl.trustStoreType }.to { factory.setTrustStoreType(it) }
            map.from<String> { ssl.trustStore }.to { factory.setTrustStore(it) }
            map.from<String> { ssl.trustStorePassword }.to { factory.setTrustStorePassphrase(it) }
            map.from { ssl.isValidateServerCertificate }
                .to { validate -> factory.isSkipServerCertificateValidation = !validate }
            map.from { ssl.verifyHostname }.to { factory.setEnableHostnameVerification(it) }
        }

        map.from<Duration> { props.connectionTimeout }.whenNonNull().asInt<Long> { it.toMillis() }
            .to { factory.setConnectionTimeout(it) }
        factory.afterPropertiesSet()
        return factory
    }

}
