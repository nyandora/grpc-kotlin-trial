import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm")
    id("com.google.protobuf")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    implementation("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.ext["protobufVersion"]}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.ext["grpcVersion"]}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${rootProject.ext["grpcKotlinVersion"]}:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

tasks.register("copyStubToClient") {
    delete (fileTree("../client/src/main/java") {
        include("io/grpc/examples/helloworld/grpc_stub/**")
    })
    delete (fileTree("../client/src/main/kotlin") {
        include("io/grpc/examples/helloworld/grpc_stub/**")
    })
    copy {
        from("./build/generated/source/proto/main/grpc")
        into("../client/src/main/java")
    }
    copy {
        from("./build/generated/source/proto/main/grpckt")
        into("../client/src/main/kotlin")
    }
    copy {
        from("./build/generated/source/proto/main/java")
        into("../client/src/main/java")
    }
}

tasks.register("copyStubToServer") {
    delete (fileTree("../server/src/main/java") {
        include("io/grpc/examples/helloworld/grpc_stub/**")
    })
    delete (fileTree("../server/src/main/kotlin") {
        include("io/grpc/examples/helloworld/grpc_stub/**")
    })
    copy {
        from("./build/generated/source/proto/main/grpc")
        into("../server/src/main/java")
    }
    copy {
        from("./build/generated/source/proto/main/grpckt")
        into("../server/src/main/kotlin")
    }
    copy {
        from("./build/generated/source/proto/main/java")
        into("../server/src/main/java")
    }
}