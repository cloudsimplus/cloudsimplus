package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeSharedTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTimeSharedTest {
    private CloudletSchedulerTimeShared instance;

    @BeforeEach
    public void setUp(){
        instance = new CloudletSchedulerTimeShared();
    }

    @Test
    public void testGetCloudletWaitingListWhenEmpty() {
        assertTrue(instance.getCloudletWaitingList().isEmpty());
    }

    @Test
    public void testCloudletResumeWhenEmptyPausedList() {
        final int cloudletId = 0;
        final double expResult = 0.0;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletWaitingListWhenEmptyAfterResumingCloudlet() {
        final long cloudletLength = 1000;
        final var instance = createCloudletSchedulerWithMipsList(1, cloudletLength);
        final int cloudletId = 0;
        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertTrue(instance.getCloudletWaitingList().isEmpty());
    }

    @Test
    public void testCloudletResumeWhenCloudletNotInPausedList() {
        final int cloudletIdInTheList = 1;
        instance.getCloudletPausedList().add(createCloudletExecInfo(cloudletIdInTheList));
        final double expResult = 0.0;
        final int cloudletIdSearched = 2;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletIdSearched, 1, 1));
        assertEquals(expResult, result);
    }

    @Test
    public void testCloudletResumeWhenCloudletInPausedList() {
        final int cloudletId = 1;
        final int schedulerPes = 1;
        final long mips = 1000;
        final long cloudletLength = 10000;
        final var instance = createCloudletSchedulerWithMipsList(schedulerPes, mips);

        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        final double expResult = 10;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));

        assertEquals(expResult, result);
    }

    @Test
    public void testIsThereEnoughFreePesForCloudletWhenEmptyList() {
        final int cloudletPes = 1;
        final Cloudlet cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletPes);
        //Vm processing was never updated, so the processing capacity is unknow yet
        assertFalse(instance.isThereEnoughFreePesForCloudlet(new CloudletExecution(cloudlet0)));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudletWhenThereIsOneRunningCloudlet() {
        final int cloudletPes = 1;
        final int schedulerPes = 2;
        final long schedulerMips = 1000;
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletPes);
        final var instance = createCloudletSchedulerWithMipsList(schedulerPes, schedulerMips);
        instance.cloudletSubmit(cloudlet0);
        final double time0 = 0;
        instance.updateProcessing(time0, instance.getCurrentMipsShare());
        assertTrue(instance.isThereEnoughFreePesForCloudlet(new CloudletExecution(cloudlet0)));
    }

    @Test
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final var ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(ce, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final var ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(ce, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfRam() {
        final int cloudlets = 2;
        final var model = new UtilizationModelFull();
        final var instance = newSchedulerWithRunningCloudlets(1000, 2, cloudlets, 1, model);

        final double expResult = 2.0; //200% of RAM usage
        final double result = instance.getCurrentRequestedRamPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfBw() {
        final long mips = 1000;
        final int vmPes = 1;
        final int cloudletsNumber = vmPes;
        final int cloudletPes = 1;
        final var instance = newSchedulerWithRunningCloudlets(mips, vmPes, cloudletsNumber, cloudletPes);

        final double expResult = 1.0;
        final double result = instance.getCurrentRequestedBwPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletExecListWhenEmpty() {
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenMoreCloudletsThanPes() {
        final long mips = 1000;
        final int pesNumber = 2;
        final int cloudletsNumber = 4;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, cloudletsNumber);

        final double expected = 1.0;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenOnePeForEachCloudlet() {
        final long mips = 1000;
        final int pesNumber = 2;
        final int cloudletsNumber = pesNumber;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, cloudletsNumber);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesHalfUsage() {
        final long mips = 1000;
        final int pesNumber = 4;
        final int cloudletsNumber = 2;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, cloudletsNumber);

        final double expected = 0.5;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesThreeThirdUsage() {
        final long mips = 1000;
        final int pesNumber = 4;
        final int cloudletsNumber = 3;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, cloudletsNumber);

        final double expected = 0.75;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesNotFullUsage() {
        final long mips = 1000;
        final int pesNumber = 5;
        final int cloudletsNumber = 4;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, cloudletsNumber);

        final double expected = 0.8;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenDualPesCloudlets() {
        final long mips = 1000;
        final int vmPes = 4;
        final int cloudlets = 2;
        final int cloudletPes = 2;

        final var instance = newSchedulerWithRunningCloudlets(mips, vmPes, cloudlets, cloudletPes);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    /**
     * Runs 2 cloudlets requiring just 1 PE each one.
     * These cloudlets are run in a CloudletScheduler having 2 PEs,
     * one for each cloudlet. The cloudlet length is equals to the capacity of
     * each PE, meaning that each cloudlet will finish in just one second.
     */
    @Test
    public void testGetCloudletExecListWhenEmptyAfterFinishedCloudletsForTwoSchedulerPes() {
        final long mips = 1000;
        final int cloudlets = 2;
        final int vmPes = cloudlets;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, vmPes, cloudlets);

        final double time = 1.0;
        instance.updateProcessing(time, instance.getCurrentMipsShare());
        assertEquals(0, instance.getCloudletExecList().size());
    }

    /**
     * Runs 2 cloudlets requiring just 1 PE each one.
     * These cloudlets are run in a CloudletScheduler having just 1 PE
     * that is shared between the cloudlets.
     * The cloudlet length is equals to the capacity of
     * each PE, meaning that each cloudlet will finish in 2 seconds
     * because there is just 1 PE.
     */
    @Test
    public void testGetCloudletExecListWhenEmptyAfterFinishedCloudletsForOneSchedulerPe() {
        final long mips = 1000;
        final int cloudlets = 2;
        final int vmPes = 1;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, vmPes, cloudlets);
        final var broker = createBroker();
        instance.getCloudletExecList().forEach(ce -> ce.getCloudlet().setBroker(broker));

        final double time = 2.0;
        instance.updateProcessing(time, instance.getCurrentMipsShare());
        assertEquals(0, instance.getCloudletExecList().size());
    }

    private static DatacenterBrokerSimple createBroker() {
        final var simulation = CloudSimMocker.createMock(cloudsim -> cloudsim.clock(2));
        final var broker = new DatacenterBrokerSimple(simulation);
        return broker;
    }

    @Test
    public void testRemoveCloudletFromExecList() {
        final var cle = new CloudletExecution(Cloudlet.NULL);
        instance.addCloudletToExecList(cle);
        assertSame(cle, instance.removeCloudletFromExecList(cle));
        assertEquals(0, instance.getCloudletExecList().size());
    }

    @Test
    public void testRemoveCloudletFromExecListWhenCloudletNoFound() {
        final var cloudletNotAdded = new CloudletExecution(CloudletTestUtil.createCloudletWithOnePe(0));
        final var cloudletAdded = new CloudletExecution(CloudletTestUtil.createCloudletWithOnePe(1));
        final var list = new ArrayList<CloudletExecution>();
        list.add(cloudletAdded);
        instance.addCloudletToExecList(cloudletAdded);
        assertSame(CloudletExecution.NULL, instance.removeCloudletFromExecList(cloudletNotAdded));
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }

    @Test
    public void testAddCloudletToExecList() {
        final var cloudlet = new CloudletExecution(Cloudlet.NULL);
        final var list = new ArrayList<CloudletExecution>();
        list.add(cloudlet);
        instance.addCloudletToExecList(cloudlet);
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }
}
