rootProject.name = "grpc-kotlin-trial"

include("stub", "client","server")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
    }
}
