package ir.poolito.pineapple.rpc;

import io.grpc.stub.StreamObserver;
import ir.poolito.pineapple.AppConstant;
import ir.poolito.pineapple.model.Property;
import ir.poolito.pineapple.service.GitService;

import java.util.HashSet;
import java.util.Optional;

/**
 * Basic functionality for clients of this config server.
 *
 * @author Alireza Pourtaghi
 */
public class ClientRpc extends ClientGrpc.ClientImplBase {
    private final GitService gitService;

    public ClientRpc(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public void echo(Base.Message request, StreamObserver<Base.Message> responseObserver) {
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void loadContext(ClientService.LoadContextRequest request, StreamObserver<ClientService.LoadContextResponse> responseObserver) {
        HashSet<Property> properties = gitService.loadProperties(request.getName());
        if (properties != null) {
            Optional<Property> rpcAccessKey = properties.stream().filter(property -> property.getKey().equals(AppConstant.RPC_ACCESS_KEY_VALUE.getValue())).findFirst();
            if (rpcAccessKey.isPresent()) {
                if (rpcAccessKey.get().getValue().equals(request.getAccessKey())) {
                    responseObserver.onNext(buildLoadContext(properties));
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(Util.unauthenticatedError());
                }
            } else {
                responseObserver.onNext(buildLoadContext(properties));
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
     * @return a newly created and loaded context
     */
    private ClientService.LoadContextResponse buildLoadContext(HashSet<Property> properties) {
        ClientService.LoadContextResponse.Builder responseBuilder = ClientService.LoadContextResponse.newBuilder();
        properties.forEach(property -> responseBuilder.addProperty(
                ClientService.Property.newBuilder()
                        .setKey(property.getKey())
                        .setValue(property.getValue())
                        .build()
        ));
        return responseBuilder.build();
    }
}
