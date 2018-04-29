package org.cloudbus.cloudsim.util;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.*;
import org.powermock.api.easymock.PowerMock;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manoel Campos da Silva Filho
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ExecutionTimeMeasurer.class, ExecutionTimeMeasurerTest.class})
public class ExecutionTimeMeasurerTest {
    private static final String ENTRY_NAME = "testStart";
    private static final long START_TIME = 1000;
    private static final long FINISH_TIME = 3000;

    @Before
    public void setUp(){
        PowerMock.mockStatic(System.class);
        expectCurrentTimeMillis(START_TIME);
    }

    private void expectCurrentTimeMillis(final long timeMillis) {
        EasyMock.expect(System.currentTimeMillis()).andReturn(timeMillis);
    }

    @Test
    public void testGetExecutionStartTimes_HasOneEntry(){
        start(true);
        final int entries = 1;
        assertEquals(entries, ExecutionTimeMeasurer.getExecutionStartTimes().size());
    }

    @Test
    public void testGetExecutionStartTime(){
        start(true);
        assertEquals(START_TIME, ExecutionTimeMeasurer.getExecutionStartTime(ENTRY_NAME), 0);
    }

    private void start(boolean replyAndVerifyAll) {
        if(replyAndVerifyAll){
            PowerMock.replayAll();
        }

        ExecutionTimeMeasurer.start(ENTRY_NAME);

        if(replyAndVerifyAll) {
            PowerMock.verifyAll();
        }
    }

    @Test
    public void testEnd(){
        expectCurrentTimeMillis(FINISH_TIME);
        PowerMock.replayAll();
        start(false);
        final double expectedExecutionTime = 2;
        assertEquals(expectedExecutionTime, ExecutionTimeMeasurer.end(ENTRY_NAME), 0);

        PowerMock.verifyAll();
    }

    @Test
    public void testGetExecutionStartTimes(){
        final Map<String, Long> map = new HashMap<>(1);
        map.put(ENTRY_NAME, START_TIME);
        start(true);
        assertEquals(map, ExecutionTimeMeasurer.getExecutionStartTimes());

    }

}
