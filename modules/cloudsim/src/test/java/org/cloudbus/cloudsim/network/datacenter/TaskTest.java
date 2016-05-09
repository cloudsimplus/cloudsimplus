package org.cloudbus.cloudsim.network.datacenter;

import org.cloudbus.cloudsim.network.datacenter.Task.Stage;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class TaskTest {
    private static final Stage STAGE = Stage.WAIT_RECV;
    private static final int ID = 1;
    private static final int DATA_LENGTH = 2;
    private static final int EXECUTION_LENGTH = 3;
    private static final int TIME = 4;
    private static final int MEMORY = 5;
    private static final int VM_ID = 6;
    private static final int CLOUDLET_ID = 7;
    private Task instance;
    
    @Before
    public void setUp(){
        instance = new Task(ID, STAGE, DATA_LENGTH, EXECUTION_LENGTH, MEMORY, VM_ID, CLOUDLET_ID);
    }
    
    @Test
    public void testSetId() {
        int id = 90;
        assertEquals(ID, instance.getId());
        instance.setId(id);
        assertEquals(id, instance.getId());
    }

    @Test
    public void testSetCloudletId() {
        int cloudletId = 100;
        assertEquals(CLOUDLET_ID, instance.getCloudletId());
        instance.setCloudletId(cloudletId);
        assertEquals(cloudletId, instance.getCloudletId());
    }
    
    @Test
    public void testSetType() {
        Stage stageId = Stage.FINISH;
        assertEquals(STAGE, instance.getStage());
        instance.setStage(stageId);
        assertEquals(stageId, instance.getStage());
    }

    @Test
    public void testSetDataLenght() {
        double dataLenght = 102;
        assertEquals(DATA_LENGTH, instance.getDataLenght(), 0);
        instance.setDataLenght(dataLenght);
        assertEquals(dataLenght, instance.getDataLenght(), 0);
    }

    @Test
    public void testSetExecutionTime() {
        double time = 103;
        assertEquals(0, instance.getExecutionTime(), 0);
        instance.setExecutionTime(time);
        assertEquals(time, instance.getExecutionTime(), 0);
    }

    @Test
    public void testSetMemory() {
        long memory = 105L;
        assertEquals(MEMORY, instance.getMemory());
        instance.setMemory(memory);
        assertEquals(memory, instance.getMemory());
    }

    @Test
    public void testSetVmId() {
        int vmId = 106;
        assertEquals(VM_ID, instance.getVmId());
        instance.setVmId(vmId);
        assertEquals(vmId, instance.getVmId());
    }
    
}
