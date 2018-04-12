package ir.poolito.pineapple.service;

/**
 * An automatic service cleanup interface.
 * <p>
 * If your services need to close/cleanup resource(s) when the server is shutting down they must implement this interface to support automatic cleanup.
 * <p/>
 *
 * @author Alireza Pourtaghi
 */
public interface AutoClosableService {

    /**
     * Method to close/cleanup resource(s) and the service itself.
     */
    void close();
}
