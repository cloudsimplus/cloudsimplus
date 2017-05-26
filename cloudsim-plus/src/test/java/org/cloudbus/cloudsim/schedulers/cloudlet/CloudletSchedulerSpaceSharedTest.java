package org.cloudbus.cloudsim.schedulers.cloudlet;

import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.cloudlets.CloudletSimpleTest;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerSpaceSharedTest {
    private static final long SCHEDULER_MIPS = 1000;

    @Test
    public void testCloudletFinish_CheckCloudletWasSetToFinished() {
        final double clockMethodReturnValue = 0;
        final int expectedClockCalls = 4;
        final CloudSim cloudsim = CloudSimMocker.createMock(
                mocker -> mocker.clock(clockMethodReturnValue).times(expectedClockCalls));
        final Cloudlet c = CloudletSimpleTest.createCloudlet(0, 1000, 1);
        c.setBroker(Mocks.createMockBroker(cloudsim));
        final CloudletExecutionInfo rcl = new CloudletExecutionInfo(c);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.cloudletFinish(rcl);
        assertEquals(Cloudlet.Status.SUCCESS, c.getStatus());
        CloudSimMocker.verify(cloudsim);
    }

    @Test
    public void testCloudletResume_CloudletLengthNotChangedAfterResumeAndMovingToWaitList(){
        final double mips = 1000;
        final long cloudletLen = 10000;
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(0).anyTimes();
            mocker.getMinTimeBetweenEvents(0).anyTimes();
        });

        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final Vm vm = new VmSimple(0, mips, 1);
        vm.setBroker(Mocks.createMockBroker(cloudsim));
        instance.setVm(vm);
        final List<Double> mipsList = Arrays.asList(mips);
        instance.setCurrentMipsShare(mipsList);

        final Cloudlet cloudlet = CloudletSimpleTest.createCloudlet(0, cloudletLen, 1);
        instance.cloudletSubmit(cloudlet);
        instance.updateProcessing(2, mipsList);
        assertEquals(cloudletLen, cloudlet.getLength());
        instance.cloudletPause(cloudlet.getId());
        instance.cloudletResume(cloudlet.getId());
        assertEquals(cloudletLen, cloudlet.getLength());
    }

    @Test
    public void testCloudletFinish_CloudletMovedToFinishList() {
        final double clockMethodReturnValue = 0;
        final int expectedClockCalls = 4;
        final CloudSim cloudsim = CloudSimMocker.createMock(
                mocker -> mocker.clock(clockMethodReturnValue).times(expectedClockCalls));
        final Cloudlet cloudlet = CloudletSimpleTest.createCloudlet(0, 1000, 1);
        cloudlet.setBroker(Mocks.createMockBroker(cloudsim));
        final CloudletExecutionInfo rcl = new CloudletExecutionInfo(cloudlet);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.cloudletFinish(rcl);
        assertTrue(instance.getCloudletFinishedList().contains(rcl));
        CloudSimMocker.verify(cloudsim);
    }

    @Test
    public void testRemoveCloudletFromExecList_NotInExecList() {
        final CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(CloudletSimpleTest.createCloudletWithOnePe(0));
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        assertSame(CloudletExecutionInfo.NULL, instance.removeCloudletFromExecList(cloudlet));
    }

    @Test
    public void testRemoveCloudletFromExecList_InExecList() {
        final CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(CloudletSimpleTest.createCloudletWithOnePe(0));
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.addCloudletToExecList(cloudlet);
        assertSame(cloudlet, instance.removeCloudletFromExecList(cloudlet));
    }

    @Test
    public void testCloudletResume_NotInPausedList() {
        final int cloudletId = 0;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final double expResult = 0.0;
        final double result = instance.cloudletResume(cloudletId);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletResume_NotEnoughPesToResume() {
        final int cloudletId = 0;
        final int cloudletPes = 1;
        final int numberOfCloudlets = 2;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(numberOfCloudlets, SCHEDULER_MIPS));
        for(int i = 0; i <= numberOfCloudlets; i++){
            instance.addCloudletToExecList(
                new CloudletExecutionInfo(
                    CloudletSimpleTest.createCloudletWithOnePe(i)));
        }

        instance.getCloudletPausedList().add(
            new CloudletExecutionInfo(
                CloudletSimpleTest.createCloudlet(numberOfCloudlets, cloudletPes)));

        final double expResult = 0.0;
        final double result = instance.cloudletResume(cloudletId);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletSubmit_VerifyIfCloudletWasAddedToExecListByCheckingSubmitReturnValue() {
        final int pes = 1;
        final long cloudletLen = SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        final Cloudlet cloudlet = CloudletSimpleTest.createCloudlet0(cloudletLen, pes);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);

        //number of expected seconds to finish the cloudlet
        final double expResult = 1.0;
        final double result = instance.cloudletSubmit(cloudlet, fileTransferTime);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletSubmit_VerifyIfCloudletWasAddedToExecListByCheckingSuchList() {
        final int pes = 1;
        final long cloudletLen = SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        final Cloudlet cloudlet = CloudletSimpleTest.createCloudlet0(cloudletLen, pes);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);

        instance.cloudletSubmit(cloudlet, fileTransferTime);
        assertFalse(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testCloudletSubmit_WhenThereAreMoreCloudletsThanPes_VerifyThatSubmitedCloudletWasAddedToWaitListByCheckingSubmitReturnValue() {
        final int pes = 1;
        final long cloudletLen = SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletLen, pes);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final Cloudlet cloudlet1 = CloudletSimpleTest.createCloudlet(1, cloudletLen, pes);
        final double expResult = 0.0;
        final double result = instance.cloudletSubmit(cloudlet1, fileTransferTime);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletSubmit_WhenThereAreMoreCloudletsThanPes_VerifyThatSubmitedCloudletWasAddedToWaitListByCheckingSuchList() {
        final int pes = 1;
        final long cloudletLen = SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletLen, pes);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final Cloudlet cloudlet1 = CloudletSimpleTest.createCloudlet(1, cloudletLen, pes);
        instance.cloudletSubmit(cloudlet1, fileTransferTime);
        final int expResult = 1;
        final int result = instance.getCloudletWaitingList().size();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_EmptyExecList() {
        final CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(Cloudlet.NULL);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createUnitaryMipsList(SCHEDULER_MIPS));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereAreOnePesAndOneAlreadyRunningCloudlet() {
        final int cloudletPes = 1;
        final int fileTransferTime = 0;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createUnitaryMipsList(SCHEDULER_MIPS));
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final CloudletExecutionInfo cloudlet1 =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(1, cloudletPes));
        assertFalse(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereAreTwoPesAndOneAlreadyRunningCloudlet() {
        final int cloudletPes = 1;
        final int schedulerPes = 2;
        final int fileTransferTime = 0;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final CloudletExecutionInfo cloudlet1 =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(1, cloudletPes));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereAreFourPesAndOneAlreadyRunningCloudletRequiringTwoPes() {
        final int cloudletPes = 2;
        final int schedulerPes = 4;
        final int fileTransferTime = 0;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        final CloudletExecutionInfo cloudlet1 =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(1, cloudletPes));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testGetCloudletToMigrate_EmptyExecList_ThenReturnCloudletNullObject() {
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final Cloudlet expResult = Cloudlet.NULL;
        final Cloudlet result = instance.getCloudletToMigrate();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletToMigrate_WhenThereAreExecCloudlet_ReturnOne() {
        final int schedulerPes = 2;
        final int cloudletPes = 1;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));

        final Cloudlet expResult = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(expResult);

        final Cloudlet result = instance.getCloudletToMigrate();
        assertSame(expResult, result);
    }

    @Test
    public void testGetCloudletToMigrate_CheckExecListBecameEmpty() {
        final int schedulerPes = 2;
        final int cloudletPes = 1;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));

        final Cloudlet expResult = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(expResult);

        instance.getCloudletToMigrate();
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetCloudletToMigrate_WhenTwoCloudletsAreRunning_AfterMigrationUsedPesReturnToOne() {
        final int schedulerPes = 2;
        final int cloudletPes = 1;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));

        assertEquals(0, instance.getUsedPes());
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0);
        assertEquals(1, instance.getUsedPes());

        final Cloudlet cloudlet1 = CloudletSimpleTest.createCloudlet(1, cloudletPes);
        instance.cloudletSubmit(cloudlet1);
        assertEquals(2, instance.getUsedPes());

        final int expResult = 1;
        instance.getCloudletToMigrate();
        assertEquals(expResult, instance.getUsedPes());
    }

    @Test
    public void testGetCloudletExecList_ReturnEmptyList() {
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final List<CloudletExecutionInfo> result = instance.getCloudletExecList();
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetCloudletExecList_SubmitedCloudletIsInExecList() {
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final int schedulerPes = 1;
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));
        final Cloudlet cloudlet = CloudletSimpleTest.createCloudletWithOnePe(0);
        instance.cloudletSubmit(cloudlet);
        final List<CloudletExecutionInfo> result = instance.getCloudletExecList();

        assertTrue(
            instance.getCloudletExecList()
                    .stream()
                    .map(CloudletExecutionInfo::getCloudlet)
                    .anyMatch(c->c.equals(cloudlet)));
    }

    @Test
    public void testAddCloudletToExecList_CloudletInsertedIntoExecList() {
        final CloudletExecutionInfo cloudlet =  new CloudletExecutionInfo(Cloudlet.NULL);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.addCloudletToExecList(cloudlet);
        assertTrue(
            instance.getCloudletExecList()
                    .stream()
                    .anyMatch(c->c.equals(cloudlet)));
    }

    @Test
    public void testAddCloudletToExecList_ExecListSizeIsOne() {
        final CloudletExecutionInfo cloudlet =  new CloudletExecutionInfo(Cloudlet.NULL);
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.addCloudletToExecList(cloudlet);
        final int expResult = 1;
        final int result = instance.getCloudletExecList().size();
        assertEquals(expResult, result);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        final double time = 0.0;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        final double time = 0.0;
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetCurrentRequestedUtilizationOfRam() {
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final double expResult = 0.0;
        final double result = instance.getCurrentRequestedRamPercentUtilization();
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetCurrentRequestedUtilizationOfBw() {
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final double expResult = 0.0;
        final double result = instance.getCurrentRequestedBwPercentUtilization();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Submits 4 cloudlets that require one PE each one,
     * but since there is just 2 VM PEs, just 2 cloudlets
     * will be added to exec list.
     */
    @Test
    public void testGetTotalUtilizationOfCpu_MoreCloudletsThanPes() {
        final long mips = 1000;
        final int numberOfPes = 2;
        final int numberOfCloudlets = 4;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_OnePeForEachCloudlet() {
        final long mips = 1000;
        final int numberOfPes = 2;
        final int numberOfCloudlets = numberOfPes;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_LessCloudletsThanPesHalfUsage() {
        final long mips = 1000;
        final int numberOfPes = 4;
        final int numberOfCloudlets = 2;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);
        final double expected = 0.5;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_LessCloudletsThanPesThreeThirdUsage() {
        final long mips = 1000;
        final int numberOfPes = 4;
        final int numberOfCloudlets = 3;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);
        final double expected = 0.75;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_LessCloudletsThanPesNotFullUsage() {
        final long mips = 1000;
        final int numberOfPes = 5;
        final int numberOfCloudlets = 4;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);
        final double expected = 0.8;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_DualPesCloudlets_FullUsage() {
        final long mips = 1000;
        final int numberOfVmPes = 4;
        final int numberOfCloudletPes = 2;
        final int numberOfCloudlets = 2;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    /**
     * Submits 3 cloudlets that require 2 PEs each one, totalling 6 required PES.
     * However, since there is just 4 VM PEs, just 2 cloudlets
     * will be added to exec list.
     */
    @Test
    public void testGetTotalUtilizationOfCpu_DualPesCloudlets() {
        final long mips = 1000;
        final int numberOfVmPes = 4;
        final int numberOfCloudlets = 3;
        final int numberOfCloudletPes = 2;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);
        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    /**
     * Submits 3 cloudlets that require 2 PEs each one, totalling 6 required PES.
     * However, since there is just 3 VM PEs, just 1 Cloudlet
     * will be added to exec list, that will use 2 of the 3 VM PEs.
     */
    @Test
    public void testGetTotalUtilizationOfCpu_DualPesCloudlets_NotEnoughVmPes() {
        final long mips = 1000;
        final int numberOfVmPes = 3;
        final int numberOfCloudlets = 3;
        final int numberOfCloudletPes = 2;

        final CloudletSchedulerSpaceShared instance = newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);
        final double expected = 0.666;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0.001);
    }

    private CloudletSchedulerSpaceShared createCloudletSchedulerWithMipsList(int numberOfPes, long mipsOfEachPe) {
        final CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(numberOfPes, mipsOfEachPe);
        instance.setCurrentMipsShare(mipsList);
        instance.setVm(new VmSimple(0, mipsOfEachPe, numberOfPes));
        return instance;
    }

    /**
     * Creates a scheduler with a list of running cloudlets, where each Cloudlet has just one PE.
     *
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param numberOfVmPes number of PEs of the VM's scheduler
     * @param numberOfCloudlets number of Cloudlets to create
     * @return the new scheduler
     */
    private CloudletSchedulerSpaceShared newSchedulerWithSingleCoreRunningCloudlets(long mips, int numberOfVmPes, int numberOfCloudlets) {
        return newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, 1);
    }

    /**
     * Creates a scheduler with a list of running cloudlets.
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param numberOfVmPes number of PEs of the VM's scheduler
     * @param numberOfCloudlets number of Cloudlets to create
     * @param numberOfCloudletPes the number of PEs for each Cloudlet
     * @return the new scheduler
     */
    private CloudletSchedulerSpaceShared newSchedulerWithRunningCloudlets(long mips, int numberOfVmPes, int numberOfCloudlets, int numberOfCloudletPes) {
        final CloudletSchedulerSpaceShared instance = createCloudletSchedulerWithMipsList(numberOfVmPes, mips);

        for(int i = 0; i < numberOfCloudlets; i++) {
            final Cloudlet c = CloudletSimpleTest.createCloudlet(i, mips, numberOfCloudletPes);
            c.assignToDatacenter(Datacenter.NULL);
            instance.cloudletSubmit(c);
        }

        return instance;
    }
}
