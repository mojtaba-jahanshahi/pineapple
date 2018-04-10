package ir.jampions.pineapple;

/**
 * Some constant values that is used on this project.
 *
 * @author Alireza Pourtaghi
 */
public enum Constant {
    BANNER_TEXT("PINEAPPLE"),
    AUTO_CLOSABLE_CLEANUP_METHOD_NAME("close"),
    REPOSITORY_BASE_DIRECTORY("/tmp/"),
    GIT_FILE_EXTENSION(".git"),
    SCHEDULER_POOL_SIZE("1"),
    SCHEDULER_INITIAL_DELAY_IN_MINUTES("5"),
    SCHEDULER_PERIOD_IN_MINUTES("5");

    private String value;

    Constant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
