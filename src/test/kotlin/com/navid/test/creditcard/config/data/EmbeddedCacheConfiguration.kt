package com.navid.test.creditcard.config.data

import com.hazelcast.config.Config
import com.hazelcast.config.EvictionPolicy
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MaxSizeConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.navid.test.creditcard.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class EmbeddedCacheConfiguration(private val properties: AppProperties) {

    private val logger = LoggerFactory.getLogger(EmbeddedCacheConfiguration::class.java)

    @Bean
    @Primary
    fun embeddedHazelcastInstance(): HazelcastInstance {
        logger.debug("Configuring Hazelcast")
        val hazelCastInstance = Hazelcast.getHazelcastInstanceByName("test")
        if (hazelCastInstance != null) {
            logger.debug("Hazelcast already initialized")
            return hazelCastInstance
        }

        val config = Config()
        config.instanceName = "test"
        config.setProperty("hazelcast.shutdownhook.enabled", "false")
        config.networkConfig.join.tcpIpConfig.isEnabled = false
        config.networkConfig.join.multicastConfig.isEnabled = false
        config.mapConfigs["default"] = initializeDefaultMapConfig()
        config.managementCenterConfig.isEnabled = false

        logger.debug("Finishing hazelcast configuration")

        return Hazelcast.newHazelcastInstance(config)
    }

    private fun initializeDefaultMapConfig(): MapConfig {
        val mapConfig = MapConfig()

        /*
        Number of backups. If 1 is set as the backup-count for example,
        then all entries of the map will be copied to another JVM for
        fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
     */
        mapConfig.backupCount = 0

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
}
