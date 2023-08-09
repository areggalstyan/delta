pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "delta"
include("plugin")
include("api")
include("example")
include("meta")
