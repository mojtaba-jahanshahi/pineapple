package ir.poolito.pineapple.model;

import java.util.Objects;
import java.util.Optional;

/**
 * A property of an application instance (or configuration file).
 *
 * @author Alireza Pourtaghi
 */
public final class Property {
    private final String key;
    private final String value;

    public Property(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    /**
     * Parses a string to Property instance.
     *
     * @param line - string representation of a property
     * @return a Property instance if line is valid otherwise empty
     */
    public static Optional<Property> parse(String line) {
        String[] tokens = line.split("=", -1);
        if (tokens.length == 2) {
            return Optional.of(new Property(tokens[0].trim(), tokens[1].trim()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(key, property.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Property{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
