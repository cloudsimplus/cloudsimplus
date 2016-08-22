package org.cloudbus.cloudsim.network.datacenter;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletDataTaskTest {
    private static final int ID = 1;
    private static final int MEMORY = 5;
    private CloudletSendTask instance;
    
    @Before
    public void setUp(){
        instance = new CloudletSendTask(ID);
    }
    
    @Test
    public void testSetId() {
        int id = 90;
        assertEquals(ID, instance.getId());
        instance.setId(id);
        assertEquals(id, instance.getId());
    }

    @Test
    public void testSetMemory() {
        long memory = 105L;
        instance.setMemory(memory);
        assertEquals(memory, instance.getMemory());
    }

    
}
