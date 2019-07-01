import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage

buildscript {
    repositories {
        maven { setUrl("https://nexus.avantiplc.net/repository/maven-public/") }
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.bmuschko:gradle-docker-plugin:4.10.0")
    }
}

apply(plugin = "com.bmuschko.gradle.docker.DockerRemoteApiPlugin")

tasks {

    val copyDockerFiles by registering(Copy::class) {
        description = "Copy Dockerfile and required data to build directory"
        from("src/main/docker", "${project.buildDir}/libs")
        into("${project.buildDir}/docker")
        include("*")
    }

    val dockerImageBuild by registering(DockerBuildImage::class) {
        dependsOn(copyDockerFiles)
        remove.set(true)
        inputDir.set(project.file("${project.buildDir}/docker"))
        tags.add("${project.utils.determineDockerRegistryHost().plus(project.name)}:${project.utils.determineTagPrefix() + project.version}")
        tags.add("${project.utils.determineDockerRegistryHost().plus(project.name)}:${project.utils.determineTagPrefix()}latest")
        registryCredentials.url.set(project.utils.determineDockerRegistryURL())
    }

    val pushDockerImageWithVersion by registering(DockerPushImage::class){
        imageName.set(project.utils.determineDockerRegistryHost().plus(project.name))
        tag.set(project.utils.determineTagPrefix() + project.version)
        registryCredentials.url.set(project.utils.determineDockerRegistryURL())
        enabled = project.utils.determineDockerRegistryHost().isEmpty().not()

        val username = project.utils.determineDockerUsername()
        if (username.isEmpty().not()) {
            registryCredentials.username.set( username)
        }

        val password = project.utils.determineDockerPassword()
        if (password.isEmpty().not()) {
            registryCredentials.password.set(password)
        }
    }

    val pushDockerImageWithLatest by registering(DockerPushImage::class){
        imageName.set(project.utils.determineDockerRegistryHost().plus(project.name))
        tag.set("${project.utils.determineTagPrefix()}latest")
        registryCredentials.url.set(project.utils.determineDockerRegistryURL())
        enabled = project.utils.determineDockerRegistryHost().isEmpty().not()

        val username = project.utils.determineDockerUsername()
        if (username.isEmpty().not()) {
            registryCredentials.username.set( username)
        }

        val password = project.utils.determineDockerPassword()
        if (password.isEmpty().not()) {
            registryCredentials.password.set(password)
        }
    }

}

/*

task pushDockerImageWithLatest(type: DockerPushImage) {
    imageName = project.utils.determineDockerRegistryHost().plus(project.name)
    tag = "${project.utils.determineTagPrefix()}latest".toString()
    registryCredentials.url = project.utils.determineDockerRegistryURL()
    enabled = !project.utils.determineDockerRegistryHost().isEmpty()

    def username = project.utils.determineDockerUsername()
    if (!username.isEmpty()) {
        registryCredentials.username = username
    }

    def password = project.utils.determineDockerPassword()
    if (!password.isEmpty()) {
        registryCredentials.password = password
    }
}

task buildDocker(dependsOn: [dockerImageBuild, pushDockerImageWithVersion, pushDockerImageWithLatest])

if (project.utils.isBuildingEnabled()) {
    copyDockerFiles.dependsOn 'stage'
}
*/
