package org.cloudbus.cloudsim.schedulers;

import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.CloudletSimpleTest;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudSim.class}) //to intercept and mock static method calls
public class CloudletSchedulerSpaceSharedTest {
    private static final double SCHEDULER_MIPS = 1000;

    @Test
    public void testCloudletFinish_CheckCloudletWasSetToFinished() {
        final double clockMethodReturnValue = 0;
        final int expectedClockCalls = 3;
        CloudSimMocker.build(mocker -> mocker.clock(clockMethodReturnValue, expectedClockCalls));
        Cloudlet c = CloudletSimpleTest.createCloudlet(0, 1000, 1);
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(c);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.cloudletFinish(rcl);
        assertEquals(Cloudlet.Status.SUCCESS, c.getStatus());
        CloudSimMocker.verify();
    }

    @Test
    public void testCloudletFinish_CloudletMovedToFinishList() {
        final double clockMethodReturnValue = 0;
        final int expectedClockCalls = 3;
        CloudSimMocker.build(mocker -> mocker.clock(clockMethodReturnValue, expectedClockCalls));
        CloudletExecutionInfo rcl =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(0, 1000, 1));
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.cloudletFinish(rcl);
        assertTrue(instance.getCloudletFinishedList().contains(rcl));
        CloudSimMocker.verify();
    }

    @Test
    public void testRemoveCloudletFromExecList_NotInExecList() {
        CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(Cloudlet.NULL);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        assertFalse(instance.removeCloudletFromExecList(cloudlet));
    }

    @Test
    public void testRemoveCloudletFromExecList_InExecList() {
        CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(Cloudlet.NULL);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.addCloudletToExecList(cloudlet);
        assertTrue(instance.removeCloudletFromExecList(cloudlet));
    }

    @Test
    public void testCloudletResume_NotInPausedList() {
        final int cloudletId = 0;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        double expResult = 0.0;
        double result = instance.cloudletResume(cloudletId);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletResume_NotEnoughPesToResume() {
        final int cloudletId = 0;
        final int cloudletPes = 1;
        final int numberOfCloudlets = 2;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(numberOfCloudlets, SCHEDULER_MIPS));
        for(int i = 0; i <= numberOfCloudlets; i++){
            instance.addCloudletToExecList(
                new CloudletExecutionInfo(
                    CloudletSimpleTest.createCloudletWithOnePe(i)));
        }

        instance.getCloudletPausedList().add(
            new CloudletExecutionInfo(
                CloudletSimpleTest.createCloudlet(numberOfCloudlets, cloudletPes)));

        double expResult = 0.0;
        double result = instance.cloudletResume(cloudletId);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletSubmit_VerifyIfCloudletWasAddedToExecListByCheckingSubmitReturnValue() {
        final int pes = 1;
        final long cloudletLen = (long)SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        Cloudlet cloudlet = CloudletSimpleTest.createCloudlet0(cloudletLen, pes);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);

        //number of expected seconds to finish the cloudlet
        double expResult = 1.0;
        double result = instance.cloudletSubmit(cloudlet, fileTransferTime);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletSubmit_VerifyIfCloudletWasAddedToExecListByCheckingSuchList() {
        final int pes = 1;
        final long cloudletLen = (long)SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        Cloudlet cloudlet = CloudletSimpleTest.createCloudlet0(cloudletLen, pes);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);

        instance.cloudletSubmit(cloudlet, fileTransferTime);
        assertFalse(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testCloudletSubmit_WhenThereAreMoreCloudletsThanPes_VerifyThatSubmitedCloudletWasAddedToWaitListByCheckingSubmitReturnValue() {
        final int pes = 1;
        final long cloudletLen = (long)SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletLen, pes);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        Cloudlet cloudlet1 = CloudletSimpleTest.createCloudlet(1, cloudletLen, pes);
        double expResult = 0.0;
        double result = instance.cloudletSubmit(cloudlet1, fileTransferTime);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletSubmit_WhenThereAreMoreCloudletsThanPes_VerifyThatSubmitedCloudletWasAddedToWaitListByCheckingSuchList() {
        final int pes = 1;
        final long cloudletLen = (long)SCHEDULER_MIPS;
        final double fileTransferTime = 0.0;

        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletLen, pes);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<Double> mipsList = CloudletSchedulerUtil.createMipsList(pes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(mipsList);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        Cloudlet cloudlet1 = CloudletSimpleTest.createCloudlet(1, cloudletLen, pes);
        instance.cloudletSubmit(cloudlet1, fileTransferTime);
        final int expResult = 1;
        final int result = instance.getCloudletWaitingList().size();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_EmptyExecList() {
        CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(Cloudlet.NULL);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createUnitaryMipsList(SCHEDULER_MIPS));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereAreOnePesAndOneAlreadyRunningCloudlet() {
        final int cloudletPes = 1;
        final int fileTransferTime = 0;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createUnitaryMipsList(SCHEDULER_MIPS));
        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        CloudletExecutionInfo cloudlet1 =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(1, cloudletPes));
        assertFalse(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereAreTwoPesAndOneAlreadyRunningCloudlet() {
        final int cloudletPes = 1;
        final int schedulerPes = 2;
        final int fileTransferTime = 0;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));
        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        CloudletExecutionInfo cloudlet1 =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(1, cloudletPes));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereAreFourPesAndOneAlreadyRunningCloudletRequiringTwoPes() {
        final int cloudletPes = 2;
        final int schedulerPes = 4;
        final int fileTransferTime = 0;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));
        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0, fileTransferTime);

        CloudletExecutionInfo cloudlet1 =
                new CloudletExecutionInfo(CloudletSimpleTest.createCloudlet(1, cloudletPes));
        assertTrue(instance.isThereEnoughFreePesForCloudlet(cloudlet1));
    }

    @Test
    public void testGetCloudletToMigrate_EmptyExecList_ThenReturnCloudletNullObject() {
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        Cloudlet expResult = Cloudlet.NULL;
        Cloudlet result = instance.getCloudletToMigrate();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletToMigrate_WhenThereAreExecCloudlet_ReturnOne() {
        final int schedulerPes = 2;
        final int cloudletPes = 1;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));

        Cloudlet expResult = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(expResult);

        Cloudlet result = instance.getCloudletToMigrate();
        assertSame(expResult, result);
    }

    @Test
    public void testGetCloudletToMigrate_CheckExecListBecameEmpty() {
        final int schedulerPes = 2;
        final int cloudletPes = 1;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));

        Cloudlet expResult = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(expResult);

        instance.getCloudletToMigrate();
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetCloudletToMigrate_WhenTwoCloudletsAreRunning_AfterMigrationUsedPesReturnToOne() {
        final int schedulerPes = 2;
        final int cloudletPes = 1;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));

        assertEquals(0, instance.getUsedPes());
        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        instance.cloudletSubmit(cloudlet0);
        assertEquals(1, instance.getUsedPes());

        Cloudlet cloudlet1 = CloudletSimpleTest.createCloudlet(1, cloudletPes);
        instance.cloudletSubmit(cloudlet1);
        assertEquals(2, instance.getUsedPes());

        final int expResult = 1;
        instance.getCloudletToMigrate();
        assertEquals(expResult, instance.getUsedPes());
    }

    @Test @Ignore("See the todo inside the getCurrentRequestedMips method body")
    public void testGetCurrentRequestedMips() {
        final int schedulerPes = 1;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<Double> expResult = CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(expResult);
        List<Double> result = instance.getCurrentRequestedMips();
        assertEquals(expResult, result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCurrentRequestedMips_TryToChangeReturnedListThrowsException() {
        final int schedulerPes = 1;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<Double> expResult = CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS);
        instance.setCurrentMipsShare(expResult);
        instance.getCurrentRequestedMips().add(0.0);
    }

    @Test
    public void testGetCloudletExecList_ReturnEmptyList() {
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        List<CloudletExecutionInfo> result = instance.getCloudletExecList();
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetCloudletExecList_SubmitedCloudletIsInExecList() {
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        final int schedulerPes = 1;
        instance.setCurrentMipsShare(CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS));
        Cloudlet cloudlet = CloudletSimpleTest.createCloudletWithOnePe(0);
        instance.cloudletSubmit(cloudlet);
        List<CloudletExecutionInfo> result = instance.getCloudletExecList();

        assertTrue(
            instance.getCloudletExecList()
                    .stream()
                    .map(CloudletExecutionInfo::getCloudlet)
                    .filter(c->c.equals(cloudlet))
                    .findFirst().isPresent());
    }

    @Test
    public void testAddCloudletToExecList_CloudletInsertedIntoExecList() {
        CloudletExecutionInfo cloudlet =  new CloudletExecutionInfo(Cloudlet.NULL);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.addCloudletToExecList(cloudlet);
        assertTrue(
            instance.getCloudletExecList()
                    .stream()
                    .filter(c->c.equals(cloudlet))
                    .findFirst().isPresent());
    }

    @Test
    public void testAddCloudletToExecList_ExecListSizeIsOne() {
        CloudletExecutionInfo cloudlet =  new CloudletExecutionInfo(Cloudlet.NULL);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        instance.addCloudletToExecList(cloudlet);
        final int expResult = 1;
        final int result = instance.getCloudletExecList().size();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTotalCurrentAvailableMipsForCloudlet() {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        final int schedulerPes = 2;
        List<Double> mipsShare =
                CloudletSchedulerUtil.createMipsList(schedulerPes, SCHEDULER_MIPS);
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        double expResult = SCHEDULER_MIPS;
        double result = instance.getTotalCurrentAvailableMipsForCloudlet(rcl, mipsShare);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        double time = 0.0;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        double expResult = 0.0;
        double result = instance.getTotalCurrentAllocatedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        double time = 0.0;
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        double expResult = 0.0;
        double result = instance.getTotalCurrentRequestedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetCurrentRequestedUtilizationOfRam() {
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        double expResult = 0.0;
        double result = instance.getCurrentRequestedUtilizationOfRam();
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("See the todo inside the tested method")
    public void testGetCurrentRequestedUtilizationOfBw() {
        CloudletSchedulerSpaceShared instance = new CloudletSchedulerSpaceShared();
        double expResult = 0.0;
        double result = instance.getCurrentRequestedUtilizationOfBw();
        assertEquals(expResult, result, 0.0);
    }

}
