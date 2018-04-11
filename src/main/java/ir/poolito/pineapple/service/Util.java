package ir.poolito.pineapple.service;

import ir.poolito.pineapple.model.Property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility methods for use in services.
 *
 * @author Alireza Pourtaghi
 */
final class Util {

    /**
     * Extracts all defined properties that are in specified file.
     *
     * @param file - the file that contains properties, one at a line
     * @return set containing all properties extracted from a file
     */
    static HashSet<Property> extractProperties(File file) {
        HashSet<Property> properties = new HashSet<>();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            lines.filter(line -> !line.isEmpty())
                    .map(Property::parse)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(properties::add);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
