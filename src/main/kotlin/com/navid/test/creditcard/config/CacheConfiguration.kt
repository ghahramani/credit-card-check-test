package com.navid.test.creditcard.config

import com.hazelcast.config.Config
import com.hazelcast.config.EvictionPolicy
import com.hazelcast.config.ManagementCenterConfig
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MaxSizeConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.spi.impl.eventservice.impl.Registration
import com.hazelcast.spring.cache.HazelcastCacheManager
import com.navid.test.creditcard.config.database.nosql.MongoConfiguration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import javax.annotation.PreDestroy

@Configuration
@EnableCaching
@AutoConfigureBefore(value = [MongoConfiguration::class])
class CacheConfiguration(
    private val env: Environment,
    private val context: ApplicationContext,
    private val serverProperties: ServerProperties,
    private val discoveryClient: DiscoveryClient,
    @Autowired(required = false)
    private var registration: Registration?
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PreDestroy
    fun destroy() {
        logger.info("Closing Cache Manager")
        Hazelcast.shutdownAll()
    }

    @Bean
    fun cacheManager(hazelcastInstance: HazelcastInstance): CacheManager {
        logger.debug("Starting HazelcastCacheManager")
        return HazelcastCacheManager(hazelcastInstance)
    }

    @Bean
    fun hazelcastInstance(properties: AppProperties): HazelcastInstance {
        logger.debug("Configuring Hazelcast")
        val hazelCastInstance = Hazelcast.getHazelcastInstanceByName(context.id)
        if (hazelCastInstance != null) {
            logger.debug("Hazelcast already initialized")
            return hazelCastInstance
        }
        val config = Config()
        config.instanceName = context.id
        config.networkConfig.join.multicastConfig.isEnabled = false

        if (this.registration == null) {
            logger.warn("No discovery service is set up, Hazelcast cannot create a cluster.")
        }

        this.registration?.let {
            // The serviceId is by default the application's name,
            // see the "spring.application.name" standard Spring property
            val serviceId = registration?.serviceName
            logger.debug("Configuring Hazelcast clustering for instanceId: {}", serviceId)
            // In development, everything goes through 127.0.0.1, with a different port
            if (env.acceptsProfiles(Profiles.of(AppConstants.General.PROFILE_DEV))) {
                logger.debug(
                    "\"Dev\" profile is running, Hazelcast cluster will only work with localhost instances"
                )

                System.setProperty("hazelcast.local.localAddress", "127.0.0.1")
                config.networkConfig.port = serverProperties.port!! + LOGSTASH_PORT
                config.networkConfig.join.tcpIpConfig.isEnabled = true
                for (instance in discoveryClient.getInstances(serviceId)) {
                    val clusterMember = "127.0.0.1:" + (instance.port + LOGSTASH_PORT)
                    logger.debug("Adding Hazelcast (dev) cluster member $clusterMember")
                    config.networkConfig.join.tcpIpConfig.addMember(clusterMember)
                }
            } else { // Production configuration, one host per instance all using port 5701
                config.networkConfig.port = LOGSTASH_PORT
                config.networkConfig.join.tcpIpConfig.isEnabled = true
                for (instance in discoveryClient.getInstances(serviceId)) {
                    val clusterMember = instance.host + ":$LOGSTASH_PORT"
                    logger.debug("Adding Hazelcast (prod) cluster member $clusterMember")
                    config.networkConfig.join.tcpIpConfig.addMember(clusterMember)
                }
            }
        }

        config.mapConfigs["default"] = initializeDefaultMapConfig(properties)

        // Full reference is available at: http://docs.hazelcast.org/docs/management-center/3.9/manual/html/Deploying_and_Starting.html
        config.managementCenterConfig = initializeDefaultManagementCenterConfig(properties)
        return Hazelcast.newHazelcastInstance(config)
    }

    private fun initializeDefaultManagementCenterConfig(properties: AppProperties): ManagementCenterConfig {
        val managementCenterConfig = ManagementCenterConfig()
        managementCenterConfig.isEnabled = properties.cache.hazelcast.managementCenter.enabled
        managementCenterConfig.url = properties.cache.hazelcast.managementCenter.url
        managementCenterConfig.updateInterval = properties.cache.hazelcast.managementCenter.updateInterval
        return managementCenterConfig
    }

    private fun initializeDefaultMapConfig(properties: AppProperties): MapConfig {
        val mapConfig = MapConfig()

        /*
        Number of backups. If 1 is set as the backup-count for example,
        then all entries of the map will be copied to another JVM for
        fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
        */
        mapConfig.backupCount = properties.cache.hazelcast.backupCount

        /*
        Valid values are:
        NONE (no eviction),
        LRU (Least Recently Used),
        LFU (Least Frequently Used).
        NONE is the default.
        */
        mapConfig.evictionPolicy = EvictionPolicy.LRU

        /*
        Maximum size of the map. When max size is reached,
        map is evicted based on the policy defined.
        Any integer between 0 and Integer.MAX_VALUE. 0 means
        Integer.MAX_VALUE. Default is 0.
        */
        mapConfig.maxSizeConfig = MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE)

        mapConfig.timeToLiveSeconds = properties.cache.hazelcast.timeToLiveSeconds

        return mapConfig
    }

    companion object {
        private const val LOGSTASH_PORT = 5701
    }
}
