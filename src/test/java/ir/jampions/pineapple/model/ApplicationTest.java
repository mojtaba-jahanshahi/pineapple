package ir.jampions.pineapple.model;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ApplicationTest {

    @Test
    public void testHashAndEquals() {
        Application first = new Application("value");
        Application second = new Application("value");
        Application third = new Application("anotherValue");

        ConcurrentHashMap<Application, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.putIfAbsent(first, "first");
        concurrentHashMap.putIfAbsent(second, "second");
        concurrentHashMap.putIfAbsent(third, "third");

        assertEquals(2, concurrentHashMap.size());
        assertEquals("first", concurrentHashMap.get(first));
        assertEquals("first", concurrentHashMap.get(second));
        assertEquals("third", concurrentHashMap.get(third));

        concurrentHashMap.remove(second);
        assertNull(concurrentHashMap.get(first));
        assertNull(concurrentHashMap.get(second));
        assertEquals("third", concurrentHashMap.get(third));
    }
}
