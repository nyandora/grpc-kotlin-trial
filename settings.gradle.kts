rootProject.name = "grpc-kotlin-examples"

include("stub", "client","server")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
    }
}
