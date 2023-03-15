package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.mocks.MocksHelper;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerSpaceSharedTest {

    @Test
    public void testCloudletFinishCheckCloudletWasSetToFinished() {
        final double clockMethodReturnValue = 0;
        final var cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(clockMethodReturnValue));
        final var cloudlet = CloudletTestUtil.createCloudlet(0, 1000, 1);
        cloudlet.setBroker(MocksHelper.createMockBroker(cloudsim));
        final var cle = new CloudletExecution(cloudlet);
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        instance.cloudletFinish(cle);
        assertEquals(Cloudlet.Status.SUCCESS, cloudlet.getStatus());
    }

    @Test
    public void testCloudletResumeWhenCloudletLengthNotChangedAfterResumeAndMovingToWaitList(){
        final double mips = 1000;
        final long cloudletLen = 10000;

        final Vm vm = new VmSimple(0, mips, 1);
        vm.setBroker(DatacenterBroker.NULL);
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(vm);
        final var mipsShare = new MipsShare(mips);
        instance.setCurrentMipsShare(mipsShare);

        final var cloudlet = CloudletTestUtil.createCloudlet(0, cloudletLen, 1);
        instance.cloudletSubmit(cloudlet);
        instance.updateProcessing(2, mipsShare);
        assertEquals(cloudletLen, cloudlet.getLength());
        instance.cloudletPause(cloudlet);
        instance.cloudletResume(cloudlet);
        assertEquals(cloudletLen, cloudlet.getLength());
    }

    @Test
    public void testCloudletFinishCheckCloudletMovedToFinishList() {
        final double clockMethodReturnValue = 0;
        final var cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(clockMethodReturnValue));
        final var cloudlet = CloudletTestUtil.createCloudlet(0, 1000, 1);
        cloudlet.setBroker(MocksHelper.createMockBroker(cloudsim));
        final CloudletExecution cle = new CloudletExecution(cloudlet);
        final CloudletSchedulerSpaceShared instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        instance.cloudletFinish(cle);
        assertTrue(instance.getCloudletFinishedList().contains(cle));
    }

    @Test
    public void testRemoveCloudletFromExecListWhenNotInExecList() {
        final var cloudlet = new CloudletExecution(CloudletTestUtil.createCloudletWithOnePe(0));
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        assertSame(CloudletExecution.NULL, instance.removeCloudletFromExecList(cloudlet));
    }

    @Test
    public void testRemoveCloudletFromExecListWhenInExecList() {
        final var cloudlet = new CloudletExecution(CloudletTestUtil.createCloudletWithOnePe(0));
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        instance.addCloudletToExecList(cloudlet);
        assertSame(cloudlet, instance.removeCloudletFromExecList(cloudlet));
    }

    @Test
    public void testCloudletResumeWhenNotInPausedList() {
        final int cloudletId = 0;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        final double expResult = 0.0;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertEquals(expResult, result);    }

    @Test
    public void testCloudletResumeWhenNotEnoughPesToResume() {
        final int cloudletId = 0;
        final int cloudletPes = 1;
        final int numberOfCloudlets = 2;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(numberOfCloudlets);
        for(int i = 0; i <= numberOfCloudlets; i++){
            instance.addCloudletToExecList(
                new CloudletExecution(
                    CloudletTestUtil.createCloudletWithOnePe(i)));
        }

        instance.getCloudletPausedList().add(
            new CloudletExecution(
                CloudletTestUtil.createCloudlet(numberOfCloudlets, cloudletPes)));

        final double expResult = 0.0;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertEquals(expResult, result);
    }

    @Test
    public void testCloudletSubmitVerifyIfCloudletWasAddedToExecListByCheckingSubmitReturnValue() {
        final int pes = 1;
        final double fileTransferTime = 0.0;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(pes);
        final var cloudlet = CloudletTestUtil.createCloudlet0(CloudletSchedulerSpaceSharedTestUtil.SCHEDULER_MIPS, pes);

        //number of expected seconds to finish the cloudlet
        final double expResult = 1.0;
        final double result = instance.cloudletSubmit(cloudlet, fileTransferTime);
        assertEquals(expResult, result);
    }

    @Test
    public void testCloudletSubmitVerifyIfCloudletWasAddedToExecListByCheckingSuchList() {
        final int pes = 1;
        final double fileTransferTime = 0.0;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(pes);
        final var cloudlet = CloudletTestUtil.createCloudlet0(CloudletSchedulerSpaceSharedTestUtil.SCHEDULER_MIPS, pes);

        instance.cloudletSubmit(cloudlet, fileTransferTime);
        assertFalse(instance.getCloudletExecList().isEmpty());
    }

    /**
     * Verify That Submitted Cloudlet Was Added To Wait List By Checking Submit Return Value
     */
    @Test
    public void testCloudletSubmitWhenThereAreMoreCloudletsThanPes1() {
        final int pes = 1;
        final long cloudletLen = CloudletSchedulerSpaceSharedTestUtil.SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(pes);
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletLen, pes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final var cloudlet1 = CloudletTestUtil.createCloudlet(1, cloudletLen, pes);
        final double expResult = 0.0;
        final double result = instance.cloudletSubmit(cloudlet1, fileTransferTime);
        assertEquals(expResult, result);
    }

    /**
     * Verify That Submitted Cloudlet Was Added To Wait List By Checking Such List
     */
    @Test
    public void testCloudletSubmitWhenThereAreMoreCloudletsThanPes2() {
        final int pes = 1;
        final long cloudletLen = CloudletSchedulerSpaceSharedTestUtil.SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(pes);
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletLen, pes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final var cloudlet1 = CloudletTestUtil.createCloudlet(1, cloudletLen, pes);
        instance.cloudletSubmit(cloudlet1, fileTransferTime);
        final int expResult = 1;
        final int result = instance.getCloudletWaitingList().size();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsThereEnoughFreePesForCloudletEmptyExecList() {
        final var cloudlet = new CloudletExecution(Cloudlet.NULL);
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        instance.setCurrentMipsShare(new MipsShare(CloudletSchedulerSpaceSharedTestUtil.SCHEDULER_MIPS));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet));
    }

    /**
     * When There Are One Pe And One Already Running Cloudlet
     */
    @Test
    public void testIsThereEnoughFreePesForCloudlet1() {
        final int cloudletPes = 1;
        final int fileTransferTime = 0;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(cloudletPes);
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final var cloudlet1 = new CloudletExecution(CloudletTestUtil.createCloudlet(1, cloudletPes));
        assertFalse(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    /**
     * When There Are Two Pes And One Already Running Cloudlet
     */
    @Test
    public void testIsThereEnoughFreePesForCloudlet2() {
        final int cloudletPes = 1;
        final int schedulerPes = 2;
        final int fileTransferTime = 0;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(schedulerPes);
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final var cloudlet1 = new CloudletExecution(CloudletTestUtil.createCloudlet(1, cloudletPes));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    /**
     * When There Are Four Pes And One Already Running Cloudlet Requiring Two Pes
     */
    @Test
    public void testIsThereEnoughFreePesForCloudlet3() {
        final int cloudletPes = 2;
        final int schedulerPes = 4;
        final int fileTransferTime = 0;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(schedulerPes);
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final CloudletExecution cloudlet1 =
                new CloudletExecution(CloudletTestUtil.createCloudlet(1, cloudletPes));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testGetCloudletExecListReturnEmptyList() {
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetCloudletExecListWhenSubmittedCloudletIsInExecList() {
        final int pes = 1;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler(pes);
        final Cloudlet cloudlet = CloudletTestUtil.createCloudletWithOnePe(0);
        instance.cloudletSubmit(cloudlet);

        assertTrue(
            instance.getCloudletExecList()
                    .stream()
                    .map(CloudletExecution::getCloudlet)
                    .anyMatch(c->c.equals(cloudlet)));
    }

    @Test
    public void testAddCloudletToExecListWhenCloudletInsertedIntoExecList() {
        final var cloudlet =  new CloudletExecution(Cloudlet.NULL);
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        instance.addCloudletToExecList(cloudlet);
        assertTrue(
            instance.getCloudletExecList()
                    .stream()
                    .anyMatch(c->c.equals(cloudlet)));
    }

    @Test
    public void testAddCloudletToExecListWhenExecListSizeIsOne() {
        final var cloudlet =  new CloudletExecution(Cloudlet.NULL);
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        instance.addCloudletToExecList(cloudlet);
        final int expResult = 1;
        final int result = instance.getCloudletExecList().size();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final var cle = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(cle, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final var cle = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(cle, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfRam() {
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        final double expResult = 0.0;
        final double result = instance.getCurrentRequestedRamPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfBw() {
        final var instance = CloudletSchedulerSpaceSharedTestUtil.createScheduler();
        final double expResult = 0.0;
        final double result = instance.getCurrentRequestedBwPercentUtilization();
        assertEquals(expResult, result);
    }

    /**
     * Submits 4 cloudlets that require one PE each one,
     * but since there is just 2 VM PEs, just 2 cloudlets
     * will be added to exec list.
     */
    @Test
    public void testGetTotalUtilizationOfCpuWhenMoreCloudletsThanPes() {
        final long mips = 1000;
        final int pesNumber = 2;
        final int numberOfCloudlets = 4;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, numberOfCloudlets);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenOnePeForEachCloudlet() {
        final long mips = 1000;
        final int pesNumber = 2;
        final int numberOfCloudlets = pesNumber;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, numberOfCloudlets);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesHalfUsage() {
        final long mips = 1000;
        final int pesNumber = 4;
        final int numberOfCloudlets = 2;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, numberOfCloudlets);
        final double expected = 0.5;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesThreeThirdUsage() {
        final long mips = 1000;
        final int pesNumber = 4;
        final int numberOfCloudlets = 3;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, numberOfCloudlets);
        final double expected = 0.75;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanPesNotFullUsage() {
        final long mips = 1000;
        final int pesNumber = 5;
        final int numberOfCloudlets = 4;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithSingleCoreRunningCloudlets(mips, pesNumber, numberOfCloudlets);
        final double expected = 0.8;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenDualPesCloudlets1() {
        final long mips = 1000;
        final int numberOfVmPes = 4;
        final int numberOfCloudletPes = 2;
        final int numberOfCloudlets = 2;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    /**
     * Submits 3 cloudlets that require 2 PEs each one, totalling 6 required PES.
     * However, since there is just 4 VM PEs, just 2 cloudlets
     * will be added to exec list.
     */
    @Test
    public void testGetTotalUtilizationOfCpuWhenDualPesCloudlets2() {
        final long mips = 1000;
        final int numberOfVmPes = 4;
        final int numberOfCloudlets = 3;
        final int numberOfCloudletPes = 2;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    /**
     * Submits 3 cloudlets that require 2 PEs each one, totalling 6 required PES.
     * However, since there is just 3 VM PEs, just 1 Cloudlet
     * will be added to exec list, that will use 2 of the 3 VM PEs.
     */
    @Test
    public void testGetTotalUtilizationOfCpuWhenDualPesCloudlets() {
        final long mips = 1000;
        final int numberOfVmPes = 3;
        final int numberOfCloudlets = 3;
        final int numberOfCloudletPes = 2;

        final var instance = CloudletSchedulerSpaceSharedTestUtil.newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);
        final double expected = 0.666;
        assertEquals(expected, instance.getRequestedCpuPercent(0), 0.001);
    }
}
