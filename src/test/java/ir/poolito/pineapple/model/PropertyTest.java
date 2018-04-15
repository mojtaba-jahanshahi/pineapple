package ir.poolito.pineapple.model;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class PropertyTest {

    @Test
    public void testEqualsAndHashCode() {
        Property first = new Property("key", "value");
        Property second = new Property("key", "anotherValue");
        Property third = new Property("anotherKey", "value");
        Property fourth = new Property("anotherKey", "anotherValue");

        HashSet<Property> hashSet = new HashSet<>();
        assertTrue(hashSet.add(first));
        assertFalse(hashSet.add(second));
        assertTrue(hashSet.add(third));
        assertFalse(hashSet.add(fourth));

        assertEquals(2, hashSet.size());
    }

    @Test
    public void testParse() {
        assertTrue(Property.parse("key=value").isPresent());
        assertEquals("key", Property.parse("key=value").get().getKey());
        assertEquals("value", Property.parse("key=value").get().getValue());

        assertTrue(Property.parse("key =value").isPresent());
        assertEquals("key", Property.parse("key =value").get().getKey());
        assertEquals("value", Property.parse("key =value").get().getValue());

        assertTrue(Property.parse("key= value").isPresent());
        assertEquals("key", Property.parse("key= value").get().getKey());
        assertEquals("value", Property.parse("key= value").get().getValue());

        assertTrue(Property.parse("key = value").isPresent());
        assertEquals("key", Property.parse("key = value").get().getKey());
        assertEquals("value", Property.parse("key = value").get().getValue());

        assertTrue(Property.parse("key    =value").isPresent());
        assertEquals("key", Property.parse("key    =value").get().getKey());
        assertEquals("value", Property.parse("key    =value").get().getValue());

        assertTrue(Property.parse("key=    value").isPresent());
        assertEquals("key", Property.parse("key=    value").get().getKey());
        assertEquals("value", Property.parse("key=    value").get().getValue());

        assertTrue(Property.parse(" key = value ").isPresent());
        assertEquals("key", Property.parse(" key = value ").get().getKey());
        assertEquals("value", Property.parse(" key = value ").get().getValue());

        assertTrue(Property.parse("key = \"value~`!@#$%^&*()_+\"").isPresent());
        assertEquals("key", Property.parse("key = \"value~`!@#$%^&*()_+\"").get().getKey());
        assertEquals("\"value~`!@#$%^&*()_+\"", Property.parse("key = \"value~`!@#$%^&*()_+\"").get().getValue());

        assertFalse(Property.parse("keyvalue").isPresent());
        assertFalse(Property.parse("key value").isPresent());
        assertFalse(Property.parse("key    value").isPresent());
        assertFalse(Property.parse("key:value").isPresent());
        assertFalse(Property.parse("key==value").isPresent());
    }
}
