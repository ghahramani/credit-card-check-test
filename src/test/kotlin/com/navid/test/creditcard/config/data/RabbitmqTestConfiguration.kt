package com.navid.test.creditcard.config.data

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class RabbitmqTestConfiguration {

    @Bean
    @Primary
    fun connectionFactory(): ConnectionFactory = CachingConnectionFactory(MockConnectionFactory())

    @Bean
    @Primary
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate = RabbitTemplate(connectionFactory)

}
