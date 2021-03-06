# Simple example of gRPC-Kotlin.   

## How to build and run.
Build `client` project and `server` project. 
```
./gradlew installDist
```

Start gRPC server.
```
./server/build/install/server/bin/hello-world-server
```

Send a request to the server from the client.
```
./client/build/install/client/bin/hello-world-client
```

## How to generate gRPC stub codes and use them by client/server project.
Modify `hello_world.proto`. This file locates at `stub/src/main/proto/io/grpc/examples/helloworld/hello_world.proto`.

Then run `protoc` and kotlin codegen plugin by executing the command below.
```
./gradlew :stub:build
```

You can find stub codes at locations below.
* stub/build/generated/source/proto/main/grpc
* stub/build/generated/source/proto/main/grpckt
* stub/build/generated/source/proto/main/java

> Note: For some reason, gRPC Message types are generated as Java Class (not Kotlin class). See [this page](https://github.com/grpc/grpc-kotlin).

Copy generated codes to `src/main/grpc-stub` of `client`/`server` project by executing the command below.

```
./gradlew :stub:copyStubToClient
./gradlew :stub:copyStubToServer
```

In other authors' examples, directories of generated codes are added to main source one  
so that client codes and server codes can use stub codes immediately.
This example does not select this convenience because in actual scenes 
we generate stub codes and then copy to client project or/and server project. 

## The Example elements inside this repository.
### Error Handling

This repository follows ["Error Model"](https://cloud.google.com/apis/design/errors) guided by Google.

Specifically, when the server application gets a validation error, 
the application responses `INVALID_ARGUMENT` status code 
with `com.google.rpc.BadRequest` message for describing error information.

Then the client application extract this message from a caught exception.