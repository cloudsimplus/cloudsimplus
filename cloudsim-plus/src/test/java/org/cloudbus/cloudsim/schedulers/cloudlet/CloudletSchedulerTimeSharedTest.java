package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.cloudlets.CloudletTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeSharedTestUtil.newSchedulerWithSingleCoreRunningCloudlets;
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
        final List<CloudletExecution> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testTimeSpanIntegerTime(){
        final CloudletExecution cloudlet = new CloudletExecution(Cloudlet.NULL);
        cloudlet.setLastProcessingTime(0);
        final double expected = 10;
        assertEquals(expected, instance.timeSpan(cloudlet, expected), 0.01);
    }

    @Test
    public void testTimeSpanLessThan1(){
        final CloudletExecution cloudlet = new CloudletExecution(Cloudlet.NULL);
        cloudlet.setLastProcessingTime(0);
        final double expected = 0.6;
        assertEquals(expected, instance.timeSpan(cloudlet, expected), 0.01);
    }

    @Test
    public void testTimeSpanGreaterThan1AndLessThan2(){
        final CloudletExecution cloudlet = new CloudletExecution(Cloudlet.NULL);
        cloudlet.setLastProcessingTime(0);
        final double expected = 1.7;
        assertEquals(expected, instance.timeSpan(cloudlet, expected), 0.01);
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
        final CloudletSchedulerTimeShared instance = CloudletSchedulerTimeSharedTestUtil.createCloudletSchedulerWithMipsList(1, cloudletLength);
        final int cloudletId = 0;
        CloudletSchedulerTimeSharedTestUtil.createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        final List<CloudletExecution> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCloudletResumeWhenCloudletNotInPausedList() {
        final int cloudletIdInTheList = 1;
        instance.getCloudletPausedList().add(CloudletSchedulerTimeSharedTestUtil.createCloudletExecInfo(cloudletIdInTheList));
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
        final CloudletSchedulerTimeShared instance =
            CloudletSchedulerTimeSharedTestUtil.createCloudletSchedulerWithMipsList(schedulerPes, mips);

        CloudletSchedulerTimeSharedTestUtil.createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
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
        final Cloudlet cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletPes);
        final CloudletSchedulerTimeShared instance = CloudletSchedulerTimeSharedTestUtil.createCloudletSchedulerWithMipsList(schedulerPes, schedulerMips);
        instance.cloudletSubmit(cloudlet0);
        final double time0 = 0;
        instance.updateProcessing(time0, instance.getCurrentMipsShare());
        assertTrue(instance.isThereEnoughFreePesForCloudlet(new CloudletExecution(cloudlet0)));
    }

    @Test @Disabled("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final CloudletExecution ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(ce, time);
        assertEquals(expResult, result);
    }

    @Test @Disabled("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final CloudletExecution ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(ce, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfRam() {
        final int cloudlets = 2;
        final CloudletSchedulerTimeShared instance = CloudletSchedulerTimeSharedTestUtil.newSchedulerWithRunningCloudlets(1000, 2, cloudlets, 1);

        final double expResult = 2.0; //200% of RAM usage
        final double result = instance.getCurrentRequestedRamPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfBw() {
        final long mips = 1000;
        final int numberOfVmPes = 1;
        final int numberOfCloudlets = numberOfVmPes;
        final int numberOfCloudletPes = 1;
        final CloudletSchedulerTimeShared instance = CloudletSchedulerTimeSharedTestUtil.newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);

        final double expResult = 1.0;
        final double result = instance.getCurrentRequestedBwPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletExecListWhenEmpty() {
        final List<CloudletExecution> result = instance.getCloudletExecList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenMoreCloudletsThanPes() {
        final long mips = 1000;
        final int numberOfPes = 2;
        final int numberOfCloudlets = 4;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 1.0;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenOnePeForEachCloudlet() {
        final long mips = 1000;
        final int numberOfPes = 2;
        final int numberOfCloudlets = numberOfPes;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesHalfUsage() {
        final long mips = 1000;
        final int numberOfPes = 4;
        final int numberOfCloudlets = 2;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 0.5;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesThreeThirdUsage() {
        final long mips = 1000;
        final int numberOfPes = 4;
        final int numberOfCloudlets = 3;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 0.75;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesNotFullUsage() {
        final long mips = 1000;
        final int numberOfPes = 5;
        final int numberOfCloudlets = 4;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 0.8;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenDualPesCloudlets() {
        final long mips = 1000;
        final int vmPes = 4;
        final int cloudlets = 2;
        final int cloudletPes = 2;

        final CloudletSchedulerTimeShared instance = CloudletSchedulerTimeSharedTestUtil.newSchedulerWithRunningCloudlets(mips, vmPes, cloudlets, cloudletPes);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0));
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
        final int numberOfCloudlets = 2;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfCloudlets, numberOfCloudlets);

        final double time = 1.0;
        instance.updateProcessing(time, instance.getCurrentMipsShare());
        assertTrue(instance.getCloudletExecList().isEmpty());
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

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, vmPes, cloudlets);

        final double time = 2.0;
        instance.updateProcessing(time, instance.getCurrentMipsShare());
        assertTrue(instance.getCloudletExecList().isEmpty());

    }

    @Test
    public void testRemoveCloudletFromExecList() {
        final CloudletExecution cle = new CloudletExecution(Cloudlet.NULL);
        instance.addCloudletToExecList(cle);
        assertSame(cle, instance.removeCloudletFromExecList(cle));
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testRemoveCloudletFromExecListWhenCloudletNoFound() {
        final CloudletExecution cloudletNotAdded = new CloudletExecution(CloudletTestUtil.createCloudletWithOnePe(0));
        final CloudletExecution cloudletAdded = new CloudletExecution(CloudletTestUtil.createCloudletWithOnePe(1));
        final List<CloudletExecution> list = new ArrayList<>();
        list.add(cloudletAdded);
        instance.addCloudletToExecList(cloudletAdded);
        assertSame(CloudletExecution.NULL, instance.removeCloudletFromExecList(cloudletNotAdded));
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }

    @Test
    public void testAddCloudletToExecList() {
        final CloudletExecution cloudlet = new CloudletExecution(Cloudlet.NULL);
        final List<CloudletExecution> list = new ArrayList<>();
        list.add(cloudlet);
        instance.addCloudletToExecList(cloudlet);
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }
}
