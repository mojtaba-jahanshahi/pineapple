package ir.jampions.pineapple.Command;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

/**
 * Command line options that may be provided by a user to start the server.
 *
 * @author alirezapourtaghi
 */
@Command(
        description = "starts the server with specified options",
        sortOptions = false,
        showDefaultValues = true,
        requiredOptionMarker = '*'
)
public class Start {
    @Option(
            names = {"--uri"},
            required = true,
            description = "the http(s) address of remote git repository"
    )
    public String uri;

    @Option(
            names = {"--remote"},
            description = "the remote name used to keep track of the upstream repository"
    )
    public String remote = "origin";

    @Option(
            names = {"--branch"},
            description = "the branch name to read configuration files from"
    )
    public String branch = "master";

    @Option(
            names = {"--username"},
            required = true,
            description = "the username that has access to remote git repository"
    )
    public String username;

    @Option(
            names = {"--password"},
            required = true,
            description = "the password of username that has access to remote git repository"
    )
    public String password;

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display usage help and exit"
    )
    public boolean help = false;
}
