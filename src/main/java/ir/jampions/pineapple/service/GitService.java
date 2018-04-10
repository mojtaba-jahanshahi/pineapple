package ir.jampions.pineapple.service;

import ir.jampions.pineapple.Constant;
import ir.jampions.pineapple.model.Application;
import ir.jampions.pineapple.model.Property;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Git service implementation that provides access to remote git repository and other useful functionality.
 *
 * @author Alireza Pourtaghi
 */
public class GitService implements AutoClosableService {
    private Git git;

    private final String uri;
    private final String remote;
    private final String branch;
    private final String username;
    private final String password;

    private final ConcurrentHashMap<Application, HashSet<Property>> applications;

    public GitService(String uri, String remote, String branch, String username, String password) {
        this.uri = uri;
        this.remote = remote;
        this.branch = branch;
        this.username = username;
        this.password = password;

        this.applications = new ConcurrentHashMap<>();
    }

    Git getGit() {
        return git;
    }

    String getUri() {
        return uri;
    }

    String getRemote() {
        return remote;
    }

    String getBranch() {
        return branch;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    public ConcurrentHashMap<Application, HashSet<Property>> getApplications() {
        return applications;
    }

    /**
     * Clones a remote git repository and extracts all available files to read application specific properties.
     *
     * @throws GitAPIException - if exception occurred while cloning repository
     */
    public void start() throws GitAPIException {
        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(uri)
                .setRemote(remote)
                .setBranch(branch)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setDirectory(new File(Constant.REPOSITORY_BASE_DIRECTORY.getValue() + System.currentTimeMillis()));
        git = cloneCommand.call();
        File[] files = git.getRepository().getWorkTree().listFiles();
        if (files != null) {
            Arrays.stream(files)
                    .filter(file -> !file.getName().equals(Constant.GIT_FILE_EXTENSION.getValue()))
                    .forEach(file -> applications.putIfAbsent(new Application(file.getName()), Util.extractProperties(file)));
        }
    }

    @Override
    public void close() {
        System.out.println("[INFO]: closing git service ...");
        if (git != null) {
            git.close();
        }
    }
}
