package ir.poolito.pineapple.rpc;

import io.grpc.stub.StreamObserver;
import ir.poolito.pineapple.AppConstant;
import ir.poolito.pineapple.model.Property;
import ir.poolito.pineapple.service.GitService;
import ir.poolito.rpc.Base;
import ir.poolito.rpc.PineappleGrpc;
import ir.poolito.rpc.PineappleService;

import java.util.HashSet;
import java.util.Optional;

/**
 * Basic functionality for clients of this config server.
 *
 * @author Alireza Pourtaghi
 */
public class PineappleRpc extends PineappleGrpc.PineappleImplBase {
    private final GitService gitService;

    public PineappleRpc(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public void echo(Base.Message request, StreamObserver<Base.Message> responseObserver) {
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void loadContext(PineappleService.LoadContextRequest request, StreamObserver<PineappleService.LoadContextResponse> responseObserver) {
        HashSet<Property> properties = gitService.loadProperties(request.getName());
        if (properties != null) {
            Optional<Property> optionalProperty = properties.stream().filter(property -> property.getKey().equals(AppConstant.RPC_ACCESS_KEY_VALUE.getValue())).findFirst();
            if (optionalProperty.isPresent()) {
                if (optionalProperty.get().getValue().equals(request.getAccessKey())) {
                    responseObserver.onNext(buildLoadContextResponse(properties));
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(Util.unauthenticatedError());
                }
            } else {
                responseObserver.onNext(buildLoadContextResponse(properties));
                responseObserver.onCompleted();
            }
        } else {
            responseObserver.onError(Util.notFoundError());
        }
    }

    /**
     * Builds a new LoadContextResponse.
     *
     * @param properties - properties of a context that should be loaded for response
     * @return a newly created and also loaded context response
     */
    private PineappleService.LoadContextResponse buildLoadContextResponse(HashSet<Property> properties) {
        PineappleService.LoadContextResponse.Builder responseBuilder = PineappleService.LoadContextResponse.newBuilder();
        properties.forEach(property -> responseBuilder.addProperty(
                PineappleService.Property.newBuilder()
                        .setKey(property.getKey())
                        .setValue(property.getValue())
                        .build()
        ));
        return responseBuilder.build();
    }
}
