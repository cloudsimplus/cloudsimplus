package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmTest {
    @Test
    public void testVmNullObject(){
        final Vm instance = Vm.NULL;
        assertAll(
            () -> assertTrue(instance.getStateHistory().isEmpty()),
            () -> assertEquals(CloudletScheduler.NULL, instance.getCloudletScheduler()),
            () -> assertFalse(instance.isInMigration()),
            () -> assertFalse(instance.isCreated()),
            () -> assertEquals(Host.NULL, instance.getHost()),
            () -> assertEquals("", instance.getUid()),
            () -> assertEquals(-1, instance.getId()),
            () -> assertSame(DatacenterBroker.NULL, instance.getBroker()),
            () -> assertEquals("", instance.getVmm()),
            () -> assertEquals(0, instance.updateProcessing(0, Collections.EMPTY_LIST))
        );
    }

    @Test
    public void testVmNullObjectListeners(){
        final Vm instance = Vm.NULL;
        instance.addOnHostAllocationListener(null);
        assertFalse(instance.removeOnHostAllocationListener(null));

        instance.addOnHostDeallocationListener(null);
        assertFalse(instance.removeOnHostDeallocationListener(null));

        instance.addOnCreationFailureListener(null);
        assertFalse(instance.removeOnCreationFailureListener(null));

        instance.addOnUpdateProcessingListener(null);
        assertFalse(instance.removeOnUpdateProcessingListener(null));
    }

    @Test
    public void testVmNullObjectResources(){
        final Vm instance = Vm.NULL;
        assertAll(
            () -> assertEquals(0, instance.getBw().getAllocatedResource()),
            () -> assertEquals(0, instance.getCurrentRequestedMaxMips()),
            () -> assertEquals(0, instance.getCurrentRequestedTotalMips()),
            () -> assertEquals(0, instance.getMips()),
            () -> assertEquals(0, instance.getNumberOfPes()),
            () -> assertEquals(0, instance.getCpuPercentUtilization(0)),
            () -> assertEquals(0, instance.getTotalCpuMipsUtilization(0)),
            () -> assertEquals(0, instance.getBw().getCapacity()),
            () -> assertEquals(0, instance.getBw().getAllocatedResource()),
            () -> assertEquals(0, instance.getCurrentRequestedBw()),
            () -> assertTrue(instance.getCurrentRequestedMips().isEmpty()),
            () -> assertEquals(0, instance.getCurrentRequestedRam()),
            () -> assertEquals(0, instance.getRam().getCapacity()),
            () -> assertEquals(0, instance.getRam().getAllocatedResource()),
            () -> assertEquals(0, instance.getStorage().getCapacity())
        );
    }

}
