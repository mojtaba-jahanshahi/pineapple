package ir.poolito.pineapple.service;

import ir.poolito.pineapple.AppConstant;
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
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.gitService = gitService;
    }

    /**
     * Initializes the service for periodically checking of remote git repository changes.
     */
    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
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
                Integer.valueOf(AppConstant.SCHEDULER_INITIAL_DELAY_IN_MINUTES.getValue()),
                Integer.valueOf(AppConstant.SCHEDULER_PERIOD_IN_MINUTES.getValue()),
                TimeUnit.MINUTES
        );
    }

    @Override
    public void close() {
        if (scheduledExecutorService != null) {
            System.out.println("[INFO]: closing scheduler service ...");
            scheduledExecutorService.shutdown();
        }
    }
}
