package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.CloudletSimpleTest;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelArithmeticProgression;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
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
public class CloudletSchedulerTimeSharedTest {
    private static final double CPU_UTILIZATION_INCREMENT = 0;
    private static final double CPU_INITIAL_UTILIZATION = 0.5;
    private UtilizationModel um;

    @Before
    public void setUp(){
        um = new UtilizationModelArithmeticProgression(
                CPU_UTILIZATION_INCREMENT, CPU_INITIAL_UTILIZATION);
    }

    /**
     * Creates a mock CloudletExecutionInfo.
     * @param id Cloudlet id
     * @return the created mock Cloudlet
     */
    private CloudletExecutionInfo createCloudletExecInfo(int id){
        CloudletExecutionInfo cloudlet = EasyMock.createMock(CloudletExecutionInfo.class);
        EasyMock.expect(cloudlet.getCloudletId()).andReturn(id).anyTimes();
        EasyMock.replay(cloudlet);
        return cloudlet;
    }

    @Test
    public void testGetCloudletWaitingList_Empty() {
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        List<CloudletExecutionInfo> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCloudletWaitingList_EmptyAfterResumingCloudlet() {
        final long cloudletLength = 1000;
        final double mips = cloudletLength;
        CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(1, mips);
        final int cloudletId = 0;
        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        instance.cloudletResume(cloudletId);
        List<CloudletExecutionInfo> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCloudletResume_EmptyPausedList() {
        int cloudletId = 0;
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        double expResult = 0.0;
        double result = instance.cloudletResume(cloudletId);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletResume_CloudletNotInPausedList() {
        int cloudletIdInTheList = 1;
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        instance.getCloudletPausedList().add(createCloudletExecInfo(cloudletIdInTheList));
        double expResult = 0.0;
        int cloudletIdSearched = 2;
        double result = instance.cloudletResume(cloudletIdSearched);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletResume_CloudletInPausedList() {
        final int cloudletId = 1;
        final int schedulerPes = 1;
        final double mips = 1000;
        final long cloudletLength = 10000;
        CloudletSchedulerTimeShared instance =
                createCloudletSchedulerWithMipsList(schedulerPes, mips);

        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        double expResult = 10;
        double result = instance.cloudletResume(cloudletId);

        assertEquals(expResult, result, 0.0);
    }

    private CloudletSchedulerTimeShared createCloudletSchedulerWithMipsList(int numberOfPes, double mipsOfEachPe) {
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        List<Double> mipsList = CloudletSchedulerUtil.createMipsList(numberOfPes, mipsOfEachPe);
        instance.setCurrentMipsShare(mipsList);
        return instance;
    }


    private void createCloudletAndAddItToPausedList(CloudletSchedulerTimeShared instance, int cloudletId, long cloudletLength) {
        CloudletSimple cloudlet = CloudletSimpleTest.createCloudlet(cloudletId, cloudletLength, 1);
        cloudlet.setCloudletStatus(Cloudlet.Status.PAUSED);
        instance.getCloudletPausedList().add(new CloudletExecutionInfo(cloudlet));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_EmptyList() {
        final int cloudletPes = 1;
        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        //Vm processing was never updated, so the processing capacity is unknow yet
        assertFalse(instance.isThereEnoughFreePesForCloudlet(new CloudletExecutionInfo(cloudlet0)));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereIsOneRunningCloudlet() {
        final int cloudletPes = 1;
        final int schedulerPes = 2;
        final double schedulerMips = 1000;
        Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(schedulerPes, schedulerMips);
        instance.cloudletSubmit(cloudlet0);
        final double time0 = 0;
        instance.updateVmProcessing(time0, instance.getCurrentMipsShare());
        assertTrue(instance.isThereEnoughFreePesForCloudlet(new CloudletExecutionInfo(cloudlet0)));
    }

    @Test @Ignore("The test is being ignored because the tested method, in fact, is always returning an empty list. It doesn't have an actual implementation.")
    public void testGetCurrentRequestedMips() {
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        List<Double> result = instance.getCurrentRequestedMips();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetTotalCurrentAvailableMipsForCloudlet_OneCloudlet() {
        final double mips = 1000.0;
        final long cloudletLen = (long)mips;
        final int cloudletPes = 2;
        final int schedulerPes = 4;
        CloudletExecutionInfo cloudlet =
                new CloudletExecutionInfo(
                        CloudletSimpleTest.createCloudlet0(cloudletLen, cloudletPes));
        CloudletSchedulerTimeShared instance =
                createCloudletSchedulerWithMipsList(schedulerPes, mips);
        List<Double> mipsList = instance.getCurrentMipsShare();

        double expResult = mips;
        double result = instance.getTotalCurrentAvailableMipsForCloudlet(cloudlet, mipsList);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        double time = 0.0;
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        double expResult = 0.0;
        double result = instance.getTotalCurrentAllocatedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        double time = 0.0;
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        double expResult = 0.0;
        double result = instance.getTotalCurrentRequestedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfRam() {
        final int schedulerPes = 2;
        CloudletSchedulerTimeShared instance =
                createCloudletSchedulerWithListOfExecCloudlets(schedulerPes);

        double expResult = 1.0;
        double result = instance.getCurrentRequestedUtilizationOfRam();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfBw() {
        final int schedulerPes = 2;
        CloudletSchedulerTimeShared instance =
                createCloudletSchedulerWithListOfExecCloudlets(schedulerPes);

        double expResult = 1.0;
        double result = instance.getCurrentRequestedUtilizationOfBw();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Creates a CloudletScheduler and a given number of cloudlets,
     * adding them to the executing list.
     * All created Cloudlets will use the UtilizationModel
     * {@link #um}.
     *
     * @param numbeOfCloudlets number of cloudlets to create
     * @return the created CloudletScheduler with the given number of Cloudlets to execute
     */
    private CloudletSchedulerTimeShared createCloudletSchedulerWithListOfExecCloudlets(int numbeOfCloudlets) {
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        for(int i = 0; i < numbeOfCloudlets; i++){
            instance.addCloudletToExecList(
                    new CloudletExecutionInfo(
                            CloudletSimpleTest.createCloudlet(i, um)));
        }

        return instance;
    }

    @Test
    public void testGetCloudletExecList_Empty() {
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        List<CloudletExecutionInfo> result = instance.getCloudletExecList();
        assertTrue(result.isEmpty());
    }

    /**
     * This test runs 2 cloudlets requiring just 1 PE each one.
     * These cloudlets are run in a CloudletScheduler having 2 PEs,
     * one for each cloudlet. The cloudlet length is equals to the capacity of
     * each PE, meaning that each cloudlet will finish in just one second.
     */
    @Test
    public void testGetCloudletExecList_EmptyAfterFinishedCloudletsForTwoSchedulerPes() {
        final long mips = 1000;
        final int numberOfCloudlets = 2;

        CloudletSchedulerTimeShared instance =
            createCloudletSchedulerWithMipsList(numberOfCloudlets, mips);
        for(int i = 0; i < numberOfCloudlets; i++) {
            Cloudlet c = CloudletSimpleTest.createCloudletWithOnePe(i, mips);
            c.assignCloudletToDatacenter(0, 0, 0);
            instance.addCloudletToExecList(new CloudletExecutionInfo(c));
        }

        final double time0 = 0.5;
        instance.updateVmProcessing(time0, instance.getCurrentMipsShare());
        assertEquals(2, instance.getCloudletExecList().size());

        final double time1 = 1.0;
        instance.updateVmProcessing(time1, instance.getCurrentMipsShare());
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    /**
     * This test runs 2 cloudlets requiring just 1 PE each one.
     * These cloudlets are run in a CloudletScheduler having just 1 PE
     * that is shared between the cloudlets.
     * The cloudlet length is equals to the capacity of
     * each PE, meaning that each cloudlet will finish in just 2 second
     * because there is just 1 PE.
     */
    @Test
    public void testGetCloudletExecList_EmptyAfterFinishedCloudletsForOneSchedulerPe() {
        final long mips = 1000;
        final int numberOfCloudlets = 2;

        CloudletSchedulerTimeShared instance =
            createCloudletSchedulerWithMipsList(1, mips);
        for(int i = 0; i < numberOfCloudlets; i++) {
            Cloudlet c = CloudletSimpleTest.createCloudletWithOnePe(i, mips);
            c.assignCloudletToDatacenter(0, 0, 0);
            instance.addCloudletToExecList(new CloudletExecutionInfo(c));
        }

        final double time1 = 1;
        instance.updateVmProcessing(time1, instance.getCurrentMipsShare());
        assertEquals(2, instance.getCloudletExecList().size());

        final double time2 = 2;
        instance.updateVmProcessing(time2, instance.getCurrentMipsShare());
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testRemoveCloudletFromExecList() {
        CloudletExecutionInfo c = new CloudletExecutionInfo(Cloudlet.NULL);
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        instance.addCloudletToExecList(c);
        assertTrue(instance.removeCloudletFromExecList(c));
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testRemoveCloudletFromExecList_CloudletNoFound() {
        CloudletExecutionInfo cloudletNotAdded = new CloudletExecutionInfo(CloudletSimpleTest.createCloudletWithOnePe(0));
        CloudletExecutionInfo cloudletAdded = new CloudletExecutionInfo(CloudletSimpleTest.createCloudletWithOnePe(1));
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        List<CloudletExecutionInfo> list = new ArrayList<>();
        list.add(cloudletAdded);
        instance.addCloudletToExecList(cloudletAdded);
        assertFalse(instance.removeCloudletFromExecList(cloudletNotAdded));
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }

    @Test
    public void testAddCloudletToExecList() {
        CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(Cloudlet.NULL);
        List<CloudletExecutionInfo> list = new ArrayList<>();
        list.add(cloudlet);
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        instance.addCloudletToExecList(cloudlet);
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }

}
