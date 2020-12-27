# Simple example of gRPC-Kotlin to help you try some experiments.   

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

## How to generate gRPC stub codes and use them by client project and server project.
Modify `hello_world.proto`. 

This file locates at `stub/src/main/proto/io/grpc/examples/helloworld/hello_world.proto`.

Then run `protoc` and kotlin codegen plugin.
```
./gradlew :stub:build
```

You can find stub codes at locations below.
* stub/build/generated/source/proto/main/grpc
* stub/build/generated/source/proto/main/grpckt
* stub/build/generated/source/proto/main/java

Copy generated codes to `src/main` of `client` project and `server` project.

> note
>
> For some reason, gRPC Message types are generated as Java Class (not Kotlin class). See [this page](https://github.com/grpc/grpc-kotlin).
>

> note
>
> In other authors' examples, directories of generated codes are added to main source one  
> so that client codes and server codes can use stub codes immediately.
> This example does not select this convenience because in actual scenes 
> we generate stub codes and then copy to client project or/and server project. 

