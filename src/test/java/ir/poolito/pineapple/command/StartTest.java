package ir.poolito.pineapple.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class StartTest {

    @Test
    public void testDefaultValues() {
        Start start = new Start();

        assertEquals("localhost", start.host);
        assertEquals(9091, start.port);

        assertFalse(start.ssl);
        assertNull(start.cert);
        assertNull(start.pKey);

        assertNull(start.uri);
        assertEquals("origin", start.remote);
        assertEquals("master", start.branch);

        assertNull(start.username);
        assertNull(start.password);

        assertFalse(start.help);
    }
}
