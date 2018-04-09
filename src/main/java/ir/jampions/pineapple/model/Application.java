package ir.jampions.pineapple.model;

import java.util.Objects;

/**
 * An application instance mapped from a configuration file of remote git repository.
 *
 * @author Alireza Pourtaghi
 */
public class Application {
    private final String name;

    public Application(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
