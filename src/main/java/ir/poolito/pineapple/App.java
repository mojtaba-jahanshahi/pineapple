package ir.poolito.pineapple;

import com.github.lalyos.jfiglet.FigletFont;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import ir.poolito.pineapple.command.Start;
import ir.poolito.pineapple.rpc.PineappleRpc;
import ir.poolito.pineapple.service.AutoClosableService;
import ir.poolito.pineapple.service.GitService;
import ir.poolito.pineapple.service.SchedulerService;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executors;

import static picocli.CommandLine.populateCommand;
import static picocli.CommandLine.usage;

/**
 * Main class of the pineapple project.
 *
 * @author Alireza Pourtaghi
 */
public class App {
    private GitService gitService;
    private SchedulerService schedulerService;
    private Server server;

    public static void main(String[] args) {
        App app = new App();

        try {
            Start start = app.parseCommandLineArguments(args);
            if (start.help) {
                usage(start, System.out);
            } else {
                app.printBanner();
                app.gitService = app.initializeGitService(start.uri, start.remote, start.branch, start.username, start.password);
                app.schedulerService = app.initializeSchedulerService(app.gitService);
                app.checkServices();

                app.server = app.startServer(start.host, start.port, start.ssl, start.cert, start.privateKey);
                System.out.println("[INFO]: server started successfully");

                app.addShutdownHook();
                app.awaitTermination();
            }
        } catch (Exception e) {
            System.err.println(String.format("[ERROR]: %s", e.getMessage()));
            app.closeAutoClosableServices();
        }
    }

    /**
     * Parses the command line arguments entered by user when starting the server.
     *
     * @param args - list of command line arguments including options and parameters
     * @return command instance that populated by args
     */
    private Start parseCommandLineArguments(String[] args) {
        return populateCommand(new Start(), args);
    }

    /**
     * Prints the provided text as a banner when starting the server.
     *
     * @throws IOException - if error occurred while printing
     */
    private void printBanner() throws IOException {
        System.out.println(FigletFont.convertOneLine(AppConstant.APP_NAME.getValue().toUpperCase()));
    }

    /**
     * GitService initialization method.
     *
     * @param uri      - http(s) address of remote git repository
     * @param remote   - remote name to keep track of upstream repository
     * @param branch   - branch name to read configuration files from
     * @param username - username that has access to remote git repository
     * @param password - the password of provided username
     * @return newly created git service
     * @throws GitAPIException - if exception occurred while cloning repository
     */
    private GitService initializeGitService(String uri, String remote, String branch, String username, String password) throws GitAPIException {
        GitService gitService = new GitService(uri, remote, branch, username, password);
        gitService.start();
        return gitService;
    }

    /**
     * SchedulerService initialization method.
     *
     * @param gitService - initialized git service to use its methods on scheduler service
     * @return newly created scheduler service
     */
    private SchedulerService initializeSchedulerService(GitService gitService) {
        SchedulerService schedulerService = new SchedulerService(gitService);
        schedulerService.start();
        return schedulerService;
    }

    /**
     * Tries to detect and check all services initialized so not equal to null.
     */
    private void checkServices() {
        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f -> {
            try {
                String nameOfField = f.getName();
                if (nameOfField.contains("Service") && f.get(this) == null) {
                    throw new RuntimeException(String.format("%s not initialized", nameOfField));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * Builds and starts a new gRPC server instance ready for dispatching incoming calls.
     *
     * @param host           - host to listen on
     * @param port           - port number to listen on
     * @param ssl            - whether the server should start with ssl/tls or not
     * @param certChainFile  - certificate chain file
     * @param privateKeyFile - private key file in PEM format
     * @return gRPC server instance
     * @throws Exception - if start failed
     */
    private Server startServer(String host, int port, boolean ssl, File certChainFile, File privateKeyFile) throws Exception {
        if (ssl && certChainFile != null && privateKeyFile != null) {
            System.out.println("[INFO]: starting server with ssl/tls enabled ...");
            NettyServerBuilder nettyServerBuilder = NettyServerBuilder
                    .forAddress(new InetSocketAddress(host, port))
                    .useTransportSecurity(certChainFile, privateKeyFile)
                    .executor(Executors.newWorkStealingPool());
            addRpcServices(nettyServerBuilder);
            return nettyServerBuilder
                    .build()
                    .start();
        } else {
            System.err.println("[WARNING]: starting server without ssl/tls ...");
            NettyServerBuilder nettyServerBuilder = NettyServerBuilder
                    .forAddress(new InetSocketAddress(host, port))
                    .executor(Executors.newWorkStealingPool());
            addRpcServices(nettyServerBuilder);
            return nettyServerBuilder
                    .build()
                    .start();
        }
    }

    /**
     * Adds all needed rpc services to the gRPC server.
     *
     * @param nettyServerBuilder - the server builder
     */
    private void addRpcServices(NettyServerBuilder nettyServerBuilder) {
        nettyServerBuilder.addService(new PineappleRpc(gitService));
    }

    /**
     * Adds JVM shutdown hook for the app instance to clean up global main services, executors and AutoClosableServices.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closeAutoClosableServices();
            } catch (Exception e) {
                System.err.println(String.format("[ERROR]: %s", e.getMessage()));
            } finally {
                shutdownServer();
            }
        }));
    }

    /**
     * Tries to detect and close all AutoClosableServices when the server is shutting down.
     */
    private void closeAutoClosableServices() {
        Arrays.stream(this.getClass().getDeclaredFields()).sorted(Collections.reverseOrder()).forEach(f -> Arrays.stream(f.getType().getInterfaces()).forEach(i -> {
            if (i.getName().equals(AutoClosableService.class.getName())) {
                Arrays.stream(i.getMethods()).forEach(m -> {
                    if (m.getName().equals(AppConstant.AUTO_CLOSABLE_CLEANUP_METHOD_NAME.getValue())) {
                        try {
                            if (f.get(this) != null) {
                                m.invoke(f.get(this));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                });
            }
        }));
    }

    /**
     * Shutdowns the gRPC server instance.
     */
    private void shutdownServer() {
        if (server != null) {
            System.out.println("[INFO]: shutting down the server ...");
            server.shutdown();
        }
    }

    /**
     * Waits for the gRPC server to shutdown, so this method blocks the main thread.
     */
    private void awaitTermination() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
