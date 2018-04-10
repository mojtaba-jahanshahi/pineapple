package ir.jampions.pineapple.service;

import ir.jampions.pineapple.Constant;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A service to check changes on remote git repository in periodic order.
 *
 * @author Alireza Pourtaghi
 */
public class SchedulerService implements AutoClosableService {
    private final ScheduledExecutorService scheduledExecutorService;
    private final GitService gitService;

    public SchedulerService(GitService gitService) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(Integer.valueOf(Constant.SCHEDULER_POOL_SIZE.getValue()));
        this.gitService = gitService;
    }

    /**
     * Initializes the service for periodically check of remote git repository.
     */
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
                        System.out.println("[INFO]: pulling to check new updates ...");
                        PullResult pullResult = gitService.getGit()
                                .pull()
                                .setRemote(gitService.getRemote())
                                .setRemoteBranchName(gitService.getBranch())
                                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitService.getUsername(), gitService.getPassword()))
                                .call();
                        if (pullResult.isSuccessful()) {
                            gitService.updateApplications();
                        } else {
                            System.err.println("[ERROR]: git pull was not successful");
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
