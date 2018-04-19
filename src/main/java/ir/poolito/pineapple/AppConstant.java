package ir.poolito.pineapple;

/**
 * Some constant values that is used on this project.
 *
 * @author Alireza Pourtaghi
 */
public enum AppConstant {
    APP_NAME("PINEAPPLE"),
    AUTO_CLOSABLE_CLEANUP_METHOD_NAME("close"),
    REPOSITORY_BASE_DIRECTORY("/tmp/"),
    GIT_FILE_EXTENSION(".git"),
    SCHEDULER_POOL_SIZE("1"),
    SCHEDULER_INITIAL_DELAY_IN_MINUTES("5"),
    SCHEDULER_PERIOD_IN_MINUTES("5"),
    RPC_ACCESS_KEY_VALUE("rpcAccessKey");

    private String value;

    AppConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
