package ir.poolito.pineapple.rpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * Utility methods for use in rpc services.
 *
 * @author Alireza Pourtaghi
 */
final class Util {

    /**
     * Creates a status exception to notify clients (stubs) that the request is not authenticated.
     *
     * @return a runtime status exception of gRPC framework
     */
    static StatusRuntimeException unauthenticatedError() {
        return Status.UNAUTHENTICATED.asRuntimeException();
    }

    /**
     * Creates a status exception to notify clients (stubs) that requested resource or entity not found.
     *
     * @return a runtime status exception of gRPC framework
     */
    static StatusRuntimeException notFoundError() {
        return Status.NOT_FOUND.asRuntimeException();
    }
}
