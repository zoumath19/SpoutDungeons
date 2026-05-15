plugins {
    java
}

version = "1.0.0-SNAPSHOT"

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.zoumath19.Spout:spout-api:spout-upstream-fixes-SNAPSHOT")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "apiversion" to "\"${providers.gradleProperty("apiVersion").get()}\"",
    )
    inputs.properties(props)
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

