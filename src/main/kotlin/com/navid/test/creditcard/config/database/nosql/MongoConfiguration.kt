package com.navid.test.creditcard.config.database.nosql

import com.github.mongobee.Mongobee
import com.mongodb.MongoClient
import com.navid.test.creditcard.config.database.util.serializer.MongoConverters
import com.navid.test.creditcard.domain.util.JSR310DateConverters
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@Configuration
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableReactiveMongoRepositories(
    basePackages = ["**.repository"],
    repositoryBaseClass = SimpleBaseGenericReactiveMongoRepository::class
)
class MongoConfiguration {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun customConversions(): MongoCustomConversions {
        val converters = arrayListOf<Any>()
        converters.add(MongoConverters.AuthorityConverter.INSTANCE)
        converters.add(JSR310DateConverters.DateToZonedDateTimeConverter.INSTANCE)
        converters.add(JSR310DateConverters.ZonedDateTimeToDateConverter.INSTANCE)
        converters.add(JSR310DateConverters.DateToLocalDateConverter.INSTANCE)
        converters.add(JSR310DateConverters.LocalDateToDateConverter.INSTANCE)
        converters.add(JSR310DateConverters.DateToLocalDateTimeConverter.INSTANCE)
        converters.add(JSR310DateConverters.LocalDateTimeToDateConverter.INSTANCE)
        converters.add(JSR310DateConverters.YearMonthToDateConverter.INSTANCE)
        converters.add(JSR310DateConverters.DateToYearMonthConverter.INSTANCE)
        return MongoCustomConversions(converters)
    }

    @Bean
    fun mongobee(mongoClient: MongoClient, mongoTemplate: MongoTemplate, mongoProperties: MongoProperties): Mongobee {
        logger.debug("Configuring Mongobee")
        val mongobee = Mongobee(mongoClient)
        mongobee.setDbName(mongoProperties.database)
        mongobee.setMongoTemplate(mongoTemplate)
        // package to scan for migrations
        mongobee.setChangeLogsScanPackage("com.navid.test.creditcard.repository.migrations")
        mongobee.isEnabled = true
        return mongobee
    }

}
