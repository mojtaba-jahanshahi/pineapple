package ir.jampions.pineapple;

import com.github.lalyos.jfiglet.FigletFont;
import io.grpc.Server;
import ir.jampions.pineapple.Command.Start;
import ir.jampions.pineapple.service.AutoClosableService;
import ir.jampions.pineapple.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Arrays;

import static picocli.CommandLine.*;

/**
 * Main class of the pineapple project.
 *
 * @author alirezapourtaghi
 */
public class App {
    private static Server server;
    private GitService gitService;

    public static void main(String[] args) {
        App app = new App();

        try {
            Start start = app.parseCommandLineArguments(args);
            if (start.help) {
                usage(start, System.out);
            } else {
                app.addShutdownHook();

                app.printBanner();
                app.gitService = app.initializeGitService(start);

                app.checkServices();
            }
        } catch (Exception e) {
            System.err.println(String.format("[ERROR]: %s", e.getMessage()));
        }
    }

    /**
     * Parses the command line arguments entered by user when starting the server.
     *
     * @param args - list of command line arguments including options and parameters
     * @return command instance that populated by args
     * @throws ParameterException - if error occurred while populating
     */
    private Start parseCommandLineArguments(String[] args) throws CommandLine.ParameterException {
        return populateCommand(new Start(), args);
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
        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f -> Arrays.stream(f.getType().getInterfaces()).forEach(i -> {
            if (i.getName().equals(AutoClosableService.class.getName())) {
                Arrays.stream(i.getMethods()).forEach(m -> {
                    if (m.getName().equals(Constant.AUTO_CLOSABLE_CLEANUP_METHOD_NAME.getValue())) {
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
     * Prints the provided text as a banner when starting the server.
     *
     * @throws IOException - if error occurred while printing
     */
    private void printBanner() throws IOException {
        System.out.println(FigletFont.convertOneLine(Constant.BANNER_TEXT.getValue()));
    }

    /**
     * GitService initialization method.
     *
     * @param start - start command instance that populated by args
     * @return newly created git service
     */
    private GitService initializeGitService(Start start) throws GitAPIException {
        GitService gitService = new GitService(start.uri, start.remote, start.branch, start.username, start.password);
        gitService.cloneRepository();
        return gitService;
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
}
