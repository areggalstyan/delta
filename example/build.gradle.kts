plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.aregcraft.delta.plugin") version "1.0.0"
}

repositories {
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    implementation("com.aregcraft.delta:api:1.0.0")
}

pluginDescription {
    main.set("$group.example.Example")
    apiVersion.set("1.19")
}
