package ir.jampions.pineapple.service;

import ir.jampions.pineapple.Constant;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A service to check changes on remote git repository in periodically.
 *
 * @author Alireza Pourtaghi
 */
public class SchedulerService implements AutoClosableService {
    private final ScheduledExecutorService scheduledExecutorService;
    private final GitService gitService;

    public SchedulerService(GitService gitService) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        this.gitService = gitService;
    }

    /**
     * Initializes the service for periodically check of remote git repository.
     */
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
                        if (gitService.getGit() != null) {
                            System.out.println("[INFO]: pulling to check new changes ...");
                            gitService.getGit()
                                    .pull()
                                    .setRemote(gitService.getRemote())
                                    .setRemoteBranchName(gitService.getBranch())
                                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitService.getUsername(), gitService.getPassword()))
                                    .call();
                        } else {
                            System.err.println("[ERROR]: git repository has been closed");
                        }
                    } catch (Exception e) {
                        System.err.println(String.format("[ERROR]: %s", e.getMessage()));
                    }
                },
                Integer.valueOf(Constant.SCHEDULER_INITIAL_DELAY_IN_MINUTES.getValue()),
                Integer.valueOf(Constant.SCHEDULER_PERIOD_IN_MINUTES.getValue()),
                TimeUnit.MINUTES
        );
    }

    @Override
    public void close() {
        System.out.println("[INFO]: closing scheduler service ...");
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }
}
