package ir.poolito.pineapple;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppConstantTest {

    @Test
    public void testDefaultValues() {
        assertEquals("PINEAPPLE", AppConstant.BANNER_TEXT.getValue());
        assertEquals("close", AppConstant.AUTO_CLOSABLE_CLEANUP_METHOD_NAME.getValue());
        assertEquals("/tmp/", AppConstant.REPOSITORY_BASE_DIRECTORY.getValue());
        assertEquals(".git", AppConstant.GIT_FILE_EXTENSION.getValue());
        assertEquals("1", AppConstant.SCHEDULER_POOL_SIZE.getValue());
        assertEquals("5", AppConstant.SCHEDULER_INITIAL_DELAY_IN_MINUTES.getValue());
        assertEquals("5", AppConstant.SCHEDULER_PERIOD_IN_MINUTES.getValue());
        assertEquals("rpcAccessKey", AppConstant.RPC_ACCESS_KEY_VALUE.getValue());
    }

    @Test
    public void testTypeSafety() {
        assertEquals(new Integer(1), Integer.valueOf(AppConstant.SCHEDULER_POOL_SIZE.getValue()));
        assertEquals(new Integer(5), Integer.valueOf(AppConstant.SCHEDULER_INITIAL_DELAY_IN_MINUTES.getValue()));
        assertEquals(new Integer(5), Integer.valueOf(AppConstant.SCHEDULER_PERIOD_IN_MINUTES.getValue()));
    }
}