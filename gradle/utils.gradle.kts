object Utils {
    fun determineTagPrefix(): String? {
        // This defaults to "default" as we don"t want the build to fail
        // but its good to know that somehow its not picking up the branch from the env.
        return ((checkValueAndReturn("tagPrefix") ?: "default") + "-")
    }

    fun determineDockerUsername(): String? {
        return checkValueAndReturn("dockerUsername")
    }

    fun determineDockerPassword(): String? {
        return checkValueAndReturn("dockerPassword")
    }

    fun determineDockerRegistryURL(): String? {
        return checkValueAndReturn("dockerRegistry")
    }

    fun determineDockerRegistryHost(): String? {
        val url = determineDockerRegistryURL() ?: ""

        if (url.isEmpty()) {
            return null
        }

        val uri = java.net.URL(url)
        return uri.host + when {
            uri.port >= 0 -> ":${uri.port}"
            else -> ""
        } + uri.path + "/"
    }

    fun determineReleaseVersion(): String? {
        return checkValueAndReturn("releaseVersion") ?: ""
    }

    fun isProd(): Boolean {
        return checkValueAndReturn("prod") == "true"
    }

    fun isSwaggerEnabled(): Boolean {
        return checkValueAndReturn("swagger") == "true"
    }

    fun isBuildingEnabled(): Boolean {
        return checkValueAndReturn("runBuild") == "true"
    }

    fun isEnvProfile(): Boolean {
        return checkValueAndReturn("env") == "true"
    }

    fun isTlsEnabled(): Boolean {
        return checkValueAndReturn("tls") == "true"
    }
}

fun checkValueAndReturn(key: String): String? {
    if (project.hasProperty(key) && project.findProperty(key).toString().isEmpty().not()) {
        return project.findProperty(key).toString().trim()
    }

    return ""
}
