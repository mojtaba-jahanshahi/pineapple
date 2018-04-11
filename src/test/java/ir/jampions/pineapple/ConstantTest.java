package ir.jampions.pineapple;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstantTest {

    @Test
    public void testDefaultValues() {
        assertEquals("PINEAPPLE", Constant.BANNER_TEXT.getValue());
        assertEquals("close", Constant.AUTO_CLOSABLE_CLEANUP_METHOD_NAME.getValue());
        assertEquals("/tmp/", Constant.REPOSITORY_BASE_DIRECTORY.getValue());
        assertEquals(".git", Constant.GIT_FILE_EXTENSION.getValue());
        assertEquals("1", Constant.SCHEDULER_POOL_SIZE.getValue());
        assertEquals("5", Constant.SCHEDULER_INITIAL_DELAY_IN_MINUTES.getValue());
        assertEquals("5", Constant.SCHEDULER_PERIOD_IN_MINUTES.getValue());
        assertEquals("rpcAccessKey", Constant.RPC_ACCESS_KEY_VALUE.getValue());
    }

    @Test
    public void testTypeSafety() {
        assertEquals(new Integer(1), Integer.valueOf(Constant.SCHEDULER_POOL_SIZE.getValue()));
        assertEquals(new Integer(5), Integer.valueOf(Constant.SCHEDULER_INITIAL_DELAY_IN_MINUTES.getValue()));
        assertEquals(new Integer(5), Integer.valueOf(Constant.SCHEDULER_PERIOD_IN_MINUTES.getValue()));
    }
}
