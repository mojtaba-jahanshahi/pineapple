package ir.poolito.pineapple.service;

import ir.poolito.pineapple.AppConstant;
import ir.poolito.pineapple.model.Application;
import ir.poolito.pineapple.model.Property;
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
                .setDirectory(new File(AppConstant.REPOSITORY_BASE_DIRECTORY.getValue() + System.currentTimeMillis()));
        git = cloneCommand.call();
        File[] files = git.getRepository().getWorkTree().listFiles();
        if (files != null) {
            addApplications(files);
        }
    }

    /**
     * First clears the list of current apps and then adds new work tree files to applications.
     */
    public void updateApplications() {
        File[] files = git.getRepository().getWorkTree().listFiles();
        if (files != null) {
            applications.clear();
            addApplications(files);
        }
    }

    /**
     * Adds work tree files to applications.
     *
     * @param files - list of work tree files
     */
    private void addApplications(File[] files) {
        Arrays.stream(files)
                .filter(file -> !file.getName().equals(AppConstant.GIT_FILE_EXTENSION.getValue()))
                .forEach(file -> applications.putIfAbsent(new Application(file.getName()), Util.extractProperties(file)));
    }

    /**
     * Loads all properties of a specific application.
     *
     * @param application - the application name
     * @return a set of all valid properties or null if application does not exists
     */
    public HashSet<Property> loadProperties(String application) {
        return applications.computeIfPresent(new Application(application), (k, v) -> v);
    }

    @Override
    public void close() {
        System.out.println("[INFO]: closing git service ...");
        if (git != null) {
            git.close();
        }
    }
}
