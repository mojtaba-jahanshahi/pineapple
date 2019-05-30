package ir.poolito.pineapple.internal;

import ir.poolito.pineapple.command.Start;

import java.io.File;

/**
 * Options that needed for configure server. This is Immutable.
 *
 * @author Mojtaba Jahanshahi
 */
public final class ServerOption {
    private final String host;
    private final int port;
    private final boolean ssl;
    private final File certFile;
    private final File privateKeyFile;
    private final int threads;


    private ServerOption(String host,
                         int port,
                         boolean ssl,
                         File certFile,
                         File privateKeyFile,
                         int threads) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
        this.certFile = certFile;
        this.privateKeyFile = privateKeyFile;
        this.threads = threads;
    }

    /**
     * Builder method. Create new instance using {@code Start} object.
     *
     * @param start - {@code Start} object
     * @return - newly-created instance
     */
    public static ServerOption from(Start start) {
        return new ServerOption(start.host,
                start.port,
                start.ssl,
                start.cert,
                start.privateKey,
                start.threads);
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public File getCertFile() {
        return certFile;
    }

    public File getPrivateKeyFile() {
        return privateKeyFile;
    }

    public int getThreads() {
        return threads;
    }
}
