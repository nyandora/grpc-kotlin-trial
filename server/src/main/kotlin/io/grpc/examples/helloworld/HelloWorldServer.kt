/*
 * Copyright 2020 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.examples.helloworld

import com.google.rpc.BadRequest
import io.grpc.Metadata
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.examples.helloworld.grpc_stub.GreeterGrpcKt
import io.grpc.examples.helloworld.grpc_stub.HelloReply
import io.grpc.examples.helloworld.grpc_stub.HelloRequest
import io.grpc.protobuf.ProtoUtils

class HelloWorldServer(private val port: Int) {
    val server: Server = ServerBuilder
            .forPort(port)
            .addService(HelloWorldService())
            .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
                Thread {
                    println("*** shutting down gRPC server since JVM is shutting down")
                    this@HelloWorldServer.stop()
                    println("*** server shut down")
                }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class HelloWorldService : GreeterGrpcKt.GreeterCoroutineImplBase() {
        override suspend fun sayHello(request: HelloRequest): HelloReply {
            validate(request)
            return HelloReply.newBuilder().setMessage(request.name).build()
        }

        private fun validate(request: HelloRequest) {
            if (request.name.length >= 10) {
                val nameFieldError = BadRequest
                        .FieldViolation.newBuilder()
                        .setField("name")
                        .setDescription("More than 10 characters are not allowed.")
                        .build()

                val badRequestError = BadRequest
                        .newBuilder()
                        .addFieldViolations(nameFieldError)
                        .build()

                val errorDetail = Metadata()
                errorDetail.put(ProtoUtils.keyForProto(badRequestError), badRequestError)

                throw StatusException(Status.INVALID_ARGUMENT, errorDetail)
            }
        }

    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = HelloWorldServer(port)
    server.start()
    server.blockUntilShutdown()
}
