package ir.poolito.pineapple.service;

import ir.poolito.pineapple.AppConstant;
import ir.poolito.pineapple.model.Application;
import ir.poolito.pineapple.model.Property;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Git service that provides access to remote git repository and other useful functionality.
 *
 * @author Alireza Pourtaghi
 */
public class GitService implements AutoClosableService {
    private final String uri;
    private final String remote;
    private final String branch;
    private final String username;
    private final String password;
    private final ConcurrentHashMap<Application, HashSet<Property>> applications;
    private Git git;

    public GitService(String uri, String remote, String branch, String username, String password) {
        this.uri = uri;
        this.remote = remote;
        this.branch = branch;
        this.username = username;
        this.password = password;
        this.applications = new ConcurrentHashMap<>();
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

    Git getGit() {
        return git;
    }

    /**
     * Clones a remote git repository and extracts all available files to read application specific properties.
     *
     * @throws Exception - if exception occurred while cloning repository
     */
    public void start() throws Exception {
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
     * Loads all properties of a specific application.
     *
     * @param application - the application name
     * @return a set of all valid properties or null if application does not exists
     */
    public HashSet<Property> loadProperties(String application) {
        return applications.computeIfPresent(new Application(application), (k, v) -> v);
    }

    /**
     * First clears the list of current applications and then adds new work tree files to applications.
     */
    void updateApplications() {
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

    @Override
    public void close() {
        if (git != null) {
            System.out.println("[INFO]: closing git service ...");
            git.close();
        }
    }
}
