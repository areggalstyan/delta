plugins {
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.2")
}

gradlePlugin {
    plugins {
        register("deltaPlugin") {
            id = "$group.plugin"
            implementationClass = "$id.DeltaPlugin"
        }
    }
}
