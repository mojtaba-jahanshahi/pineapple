package ir.jampions.pineapple;

/**
 * Some constant values that is used on this project.
 *
 * @author alirezapourtaghi
 */
public enum Constant {
    BANNER_TEXT("PINEAPPLE"),
    AUTO_CLOSABLE_CLEANUP_METHOD_NAME("close"),
    REPOSITORY_BASE_DIRECTORY("/tmp/"),
    GIT_FILE(".git");

    private String value;

    Constant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
