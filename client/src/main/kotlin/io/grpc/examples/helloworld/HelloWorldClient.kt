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
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.examples.helloworld.grpc_stub.GreeterGrpcKt
import io.grpc.examples.helloworld.grpc_stub.HelloRequest
import io.grpc.protobuf.ProtoUtils
import java.io.Closeable
import java.util.concurrent.TimeUnit

class HelloWorldClient(private val channel: ManagedChannel) : Closeable {
    private val stub: GreeterGrpcKt.GreeterCoroutineStub = GreeterGrpcKt.GreeterCoroutineStub(channel)

    suspend fun greet(name: String) {
        val request = HelloRequest.newBuilder().setName(name).build()
        try {
            val response = stub.sayHello(request)
            println("Received: ${response.message}")
        } catch (e: StatusException) {
            if (e.status == Status.INVALID_ARGUMENT) {
                val badReqErrDetail = e.trailers[ProtoUtils.keyForProto(BadRequest.getDefaultInstance())]
                badReqErrDetail?.fieldViolationsList?.forEach {
                    println("error occurred at ${it.field}")
                    println("error description: ${it.description}")
                }
            }
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
suspend fun main(args: Array<String>) {
    val port = 50051

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    val client = HelloWorldClient(channel)

    val user = args.singleOrNull() ?: "world"
    client.greet(user)
}
