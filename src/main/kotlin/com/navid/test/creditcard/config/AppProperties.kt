package com.navid.test.creditcard.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.cors.CorsConfiguration

/**
 * @author Navid Ghahremani (ghahramani.navid@gmail.com)
 */

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
class AppProperties {

    val async = Async()
    val cache = Cache()
    val cors = CorsConfiguration()
    val mail = Mail()
    val security = Security()

    class Async {

        var corePoolSize = CORE_POOL_SIZE
        var maxPoolSize = MAX_POOL_SIZE
        var queueCapacity = QUEUE_COUNT

        companion object {
            private const val CORE_POOL_SIZE = 2
            private const val MAX_POOL_SIZE = 50
            private const val QUEUE_COUNT = 10000
        }

        override fun toString(): String {
            return "Async(corePoolSize=$corePoolSize, maxPoolSize=$maxPoolSize, queueCapacity=$queueCapacity)"
        }

    }

    class Cache {

        val hazelcast = Hazelcast()

        class Hazelcast {

            var timeToLiveSeconds: Int = HOUR
            var backupCount: Int = 1
            val managementCenter: ManagementCenter = ManagementCenter()

            class ManagementCenter {

                var enabled: Boolean = true
                var updateInterval: Int = INTERVAL_COUNT
                var url: String = "http://hazelcast-management:10000"

                companion object {
                    private const val INTERVAL_COUNT = 3
                }

                override fun toString(): String {
                    return "ManagementCenter(enabled=$enabled, updateInterval=$updateInterval, url='$url')"
                }

            }

            companion object {
                private const val HOUR = 3600
            }

            override fun toString(): String {
                return """
                    Hazelcast(
                    timeToLiveSeconds=$timeToLiveSeconds,
                    backupCount=$backupCount,
                    managementCenter=$managementCenter
                    )""".trimIndent()
            }

        }

        override fun toString(): String {
            return "Cache(hazelcast=$hazelcast)"
        }

    }

    class Mail {

        var from = ""
        var baseUrl = ""

        override fun toString(): String {
            return "Mail(from='$from', baseUrl='$baseUrl')"
        }

    }

    class Security {

        val authentication = Authentication()

        class Authentication {

            val jwt = Jwt()

            class Jwt {

                lateinit var secret: String

                var tokenValidityInSeconds = DAY
                var tokenValidityInSecondsForRememberMe = MONTH

                companion object {
                    private const val DAY = 3600 * 24L
                    private const val MONTH = DAY * 30L
                }

                override fun toString(): String {
                    return """
                        Jwt(
                        tokenValidityInSeconds=$tokenValidityInSeconds,
                        tokenValidityInSecondsForRememberMe=$tokenValidityInSecondsForRememberMe
                        )
                    """.trimIndent()
                }

            }

            override fun toString(): String {
                return "Authentication(jwt=$jwt)"
            }

        }

        override fun toString(): String {
            return "Security(authentication=$authentication)"
        }
    }

    override fun toString(): String {
        return "AppProperties(async=$async, cache=$cache, cors=$cors, mail=$mail, security=$security)"
    }

}
