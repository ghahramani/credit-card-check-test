package com.navid.test.creditcard.config.async

import com.navid.test.creditcard.config.AppProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@EnableAsync
@Configuration
class AsyncConfiguration(private val properties: AppProperties) : AsyncConfigurer {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Bean(name = ["taskExecutor"])
    override fun getAsyncExecutor(): Executor {
        logger.debug("Creating Async Task Executor")
        val executor = ThreadPoolTaskExecutor()
        with(executor) {
            corePoolSize = properties.async.corePoolSize
            maxPoolSize = properties.async.maxPoolSize
            setThreadNamePrefix("async-Executor-")
            setQueueCapacity(properties.async.queueCapacity)
        }
        return ExceptionHandlingAsyncTaskExecutor(executor)
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return SimpleAsyncUncaughtExceptionHandler()
    }
}
