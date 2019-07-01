package com.navid.test.creditcard.config.message

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import java.util.ArrayList

// This class is just an extension for original RabbitProperties as it does not support AMQPS protocol and only supports AMQP
@ConfigurationProperties(prefix = "spring.rabbitmq")
class ExtendedRabbitProperties {

    /**
     * RabbitMQ host.
     */
    var host = "localhost"

    /**
     * RabbitMQ port.
     */
    var port = DEFAULT_PORT

    /**
     * Login user to authenticate to the broker.
     */
    var username = "guest"

    /**
     * Login to authenticate against the broker.
     */
    var password = "guest"

    /**
     * SSL configuration.
     */
    val ssl = Ssl()

    /**
     * Virtual host to use when connecting to the broker.
     */
    var virtualHost = "/"

    /**
     * Comma-separated list of addresses to which the client should connect.
     */
    var addresses: String? = null
        set(addresses) {
            field = addresses
            this.parsedAddresses = parseAddresses(addresses)
            this.ssl.isEnabled = determineSSL()
        }

    private var parsedAddresses: List<Address>? = null

    /**
     * Returns the host from the first address, or the configured host if no addresses
     * have been set.
     *
     * @return the host
     * @see .setAddresses
     * @see .getHost
     */
    fun determineHost(): String? = if (this.parsedAddresses.isNullOrEmpty()) {
        host
    } else this.parsedAddresses?.first()?.host

    /**
     * Returns the ssl availability from the first address, or the configured ssl if no addresses
     * have been set.
     *
     * @return the ssl availability
     * @see .setAddresses
     * @see .getSsl
     */
    fun determineSSL() =
        when {
            this.parsedAddresses.isNullOrEmpty() -> ssl.isEnabled
            else -> this.parsedAddresses?.first()?.secure ?: ssl.isEnabled
        }

    /**
     * Returns the port from the first address, or the configured port if no addresses
     * have been set.
     *
     * @return the port
     * @see .setAddresses
     * @see .getPort
     */
    fun determinePort() =
        when {
            this.parsedAddresses.isNullOrEmpty() -> port
            else -> {
                val address = this.parsedAddresses?.first()
                address?.port ?: port
            }
        }

    /**
     * Returns the comma-separated addresses or a single address (`host:port`)
     * created from the configured host and port if no addresses have been set.
     *
     * @return the addresses
     */
    fun determineAddresses(): String {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return this.host + ":" + this.port
        }
        val addressStrings = ArrayList<String>()
        for (parsedAddress in this.parsedAddresses!!) {
            addressStrings.add(parsedAddress.host + ":" + parsedAddress.port)
        }
        return StringUtils.collectionToCommaDelimitedString(addressStrings)
    }

    private fun parseAddresses(addresses: String?): List<Address> =
        when (addresses) {
            null -> listOf()
            else -> {
                val parsedAddresses = ArrayList<Address>()
                addresses.split(",").forEach { parsedAddresses.add(Address(it)) }
                parsedAddresses
            }
        }

    /**
     * If addresses have been set and the first address has a username it is returned.
     * Otherwise returns the result of calling `getUsername()`.
     *
     * @return the username
     * @see .setAddresses
     * @see .getUsername
     */
    fun determineUsername() =
        when {
            this.parsedAddresses.isNullOrEmpty() -> this.username
            else -> {
                val address = this.parsedAddresses?.first()
                address?.username ?: this.username
            }
        }

    /**
     * If addresses have been set and the first address has a password it is returned.
     * Otherwise returns the result of calling `getPassword()`.
     *
     * @return the password or `null`
     * @see .setAddresses
     * @see .getPassword
     */
    fun determinePassword() =
        when {
            this.parsedAddresses.isNullOrEmpty() -> password
            else -> {
                val address = this.parsedAddresses?.first()
                address?.password ?: password
            }
        }

    /**
     * If addresses have been set and the first address has a virtual host it is returned.
     * Otherwise returns the result of calling `getVirtualHost()`.
     *
     * @return the virtual host or `null`
     * @see .setAddresses
     * @see .getVirtualHost
     */
    fun determineVirtualHost() =
        when {
            this.parsedAddresses.isNullOrEmpty() -> virtualHost
            else -> {
                val address = this.parsedAddresses?.first()
                address?.virtualHost ?: virtualHost
            }
        }

    class Ssl {

        /**
         * Whether to enable SSL support.
         */
        var isEnabled: Boolean = false

    }

    private class Address constructor(var address: String) {

        var host: String? = null

        var port: Int = 0

        var secure: Boolean = false

        var username: String? = null

        var password: String? = null

        var virtualHost: String? = null

        init {
            address = address.trim { it <= ' ' }
            address = trimPrefix(address)
            address = parseUsernameAndPassword(address)
            address = parseVirtualHost(address)
            parseHostAndPort(address)
        }

        private fun trimPrefix(input: String) = when {
            input.startsWith(PREFIX_AMQP) -> input.substring(PREFIX_AMQP.length)
            input.startsWith(PREFIX_AMQPS) -> {
                secure = true
                input.substring(PREFIX_AMQPS.length)
            }
            else -> input
        }

        private fun parseUsernameAndPassword(input: String): String {
            var temp = input
            if (temp.contains("@")) {
                var split = temp.split("@")
                val credential = split[0]
                temp = split[1]
                split = credential.split(":")
                this.username = split[0]
                if (split.size > 1) {
                    this.password = split[1]
                }
            }
            return temp
        }

        private fun parseVirtualHost(input: String): String {
            val hostIndex = input.indexOf('/')
            if (hostIndex >= 0) {
                this.virtualHost = input.substring(hostIndex + 1)
                if (this.virtualHost.isNullOrEmpty()) {
                    this.virtualHost = "/"
                }
                return input.substring(0, hostIndex)
            }
            return input
        }

        private fun parseHostAndPort(input: String) {
            when (val portIndex = input.indexOf(':')) {
                -1 -> {
                    this.host = input
                    this.port = DEFAULT_PORT
                }
                else -> {
                    this.host = input.substring(0, portIndex)
                    this.port = Integer.valueOf(input.substring(portIndex + 1))
                }
            }
        }

    }

    companion object {

        private const val PREFIX_AMQP = "amqp://"
        private const val PREFIX_AMQPS = "amqps://"

        private const val DEFAULT_PORT = 5672
    }

}
