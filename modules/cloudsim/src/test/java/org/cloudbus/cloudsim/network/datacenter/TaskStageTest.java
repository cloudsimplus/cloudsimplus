package org.cloudbus.cloudsim.network.datacenter;

import org.cloudbus.cloudsim.network.datacenter.TaskStage.Stage;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class TaskStageTest {
    private static final Stage STAGE = Stage.WAIT_RECV;
    private static final int ID = 1;
    private static final int DATA_LENGTH = 2;
    private static final int TIME = 3;
    private static final int MEMORY = 5;
    private static final int VM_ID = 6;
    private static final int CLOUDLET_ID = 7;
    private TaskStage instance;
    
    @Before
    public void setUp(){
        instance = new TaskStage(ID, STAGE, DATA_LENGTH, TIME, MEMORY, VM_ID, CLOUDLET_ID);
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
    public void testSetTime() {
        double time = 103;
        assertEquals(TIME, instance.getExecutionTime(), 0);
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
