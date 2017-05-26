package org.cloudbus.cloudsim.schedulers.cloudlet;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.cloudlets.CloudletSimpleTest;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTimeSharedTest {
    private static final double CPU_INIT_USAGE = 0.5;
    private UtilizationModel um;

    @Before
    public void setUp(){
        //creates an utilization model that doesn't increment the usage along the time
        um = new UtilizationModelDynamic(CPU_INIT_USAGE);
    }

    /**
     * Creates a mock CloudletExecutionInfo.
     * @param id Cloudlet id
     * @return the created mock Cloudlet
     */
    private CloudletExecutionInfo createCloudletExecInfo(int id){
        final CloudletExecutionInfo cloudlet = EasyMock.createMock(CloudletExecutionInfo.class);
        EasyMock.expect(cloudlet.getCloudletId()).andReturn(id).anyTimes();
        EasyMock.replay(cloudlet);
        return cloudlet;
    }

    @Test
    public void testGetCloudletWaitingList_Empty() {
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final List<CloudletExecutionInfo> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCloudletResume_EmptyPausedList() {
        final int cloudletId = 0;
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final double expResult = 0.0;
        final double result = instance.cloudletResume(cloudletId);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCloudletWaitingList_EmptyAfterResumingCloudlet() {
        final long cloudletLength = 1000;
        final CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(1, cloudletLength);
        final int cloudletId = 0;
        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        instance.cloudletResume(cloudletId);
        final List<CloudletExecutionInfo> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCloudletResume_CloudletNotInPausedList() {
        final int cloudletIdInTheList = 1;
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        instance.getCloudletPausedList().add(createCloudletExecInfo(cloudletIdInTheList));
        final double expResult = 0.0;
        final int cloudletIdSearched = 2;
        final double result = instance.cloudletResume(cloudletIdSearched);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCloudletResume_CloudletInPausedList() {
        final int cloudletId = 1;
        final int schedulerPes = 1;
        final long mips = 1000;
        final long cloudletLength = 10000;
        final CloudletSchedulerTimeShared instance =
                createCloudletSchedulerWithMipsList(schedulerPes, mips);

        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        final double expResult = 10;
        final double result = instance.cloudletResume(cloudletId);

        assertEquals(expResult, result, 0.0);
    }

    private CloudletSchedulerTimeShared createCloudletSchedulerWithMipsList(int numberOfPes, long mipsOfEachPe) {
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(numberOfPes, mipsOfEachPe);
        instance.setCurrentMipsShare(mipsList);
        instance.setVm(new VmSimple(0, mipsOfEachPe, numberOfPes));
        return instance;
    }

    private void createCloudletAndAddItToPausedList(CloudletSchedulerTimeShared instance, int cloudletId, long cloudletLength) {
        final CloudletSimple cloudlet = CloudletSimpleTest.createCloudlet(cloudletId, cloudletLength, 1);
        cloudlet.setStatus(Cloudlet.Status.PAUSED);
        instance.getCloudletPausedList().add(new CloudletExecutionInfo(cloudlet));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_EmptyList() {
        final int cloudletPes = 1;
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        //Vm processing was never updated, so the processing capacity is unknow yet
        assertFalse(instance.isThereEnoughFreePesForCloudlet(new CloudletExecutionInfo(cloudlet0)));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_WhenThereIsOneRunningCloudlet() {
        final int cloudletPes = 1;
        final int schedulerPes = 2;
        final long schedulerMips = 1000;
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        final CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(schedulerPes, schedulerMips);
        instance.cloudletSubmit(cloudlet0);
        final double time0 = 0;
        instance.updateProcessing(time0, instance.getCurrentMipsShare());
        assertTrue(instance.isThereEnoughFreePesForCloudlet(new CloudletExecutionInfo(cloudlet0)));
    }

    @Test @Ignore("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        final double time = 0.0;
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final CloudletExecutionInfo rcl = new CloudletExecutionInfo(Cloudlet.NULL);
        final double time = 0.0;
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(rcl, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfRam() {
        final int cloudlets = 2;
        final CloudletSchedulerTimeShared instance = newSchedulerWithRunningCloudlets(1000, 2, cloudlets, 1);

        final double expResult = 2.0; //200% of RAM usage
        final double result = instance.getCurrentRequestedRamPercentUtilization();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfBw() {
        final long mips = 1000;
        final int numberOfVmPes = 1;
        final int numberOfCloudlets = numberOfVmPes;
        final int numberOfCloudletPes = 1;
        final CloudletSchedulerTimeShared instance = newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);

        final double expResult = 1.0;
        final double result = instance.getCurrentRequestedBwPercentUtilization();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCloudletExecList_Empty() {
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final List<CloudletExecutionInfo> result = instance.getCloudletExecList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetTotalUtilizationOfCpu_MoreCloudletsThanPes() {
        final long mips = 1000;
        final int numberOfPes = 2;
        final int numberOfCloudlets = 4;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 1.0;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_OnePeForEachCloudlet() {
        final long mips = 1000;
        final int numberOfPes = 2;
        final int numberOfCloudlets = numberOfPes;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_LessCloudletsThanPesHalfUsage() {
        final long mips = 1000;
        final int numberOfPes = 4;
        final int numberOfCloudlets = 2;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 0.5;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_LessCloudletsThanPesThreeThirdUsage() {
        final long mips = 1000;
        final int numberOfPes = 4;
        final int numberOfCloudlets = 3;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 0.75;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_LessCloudletsThanPesNotFullUsage() {
        final long mips = 1000;
        final int numberOfPes = 5;
        final int numberOfCloudlets = 4;

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfPes, numberOfCloudlets);

        final double expected = 0.8;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_DualPesCloudlets_FullUsage() {
        final long mips = 1000;
        final int numberOfVmPes = 4;
        final int numberOfCloudletPes = 2;
        final int numberOfCloudlets = 2;

        final CloudletSchedulerTimeShared instance = newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    @Test
    public void testGetTotalUtilizationOfCpu_DualPesCloudlets() {
        final long mips = 1000;
        final int numberOfVmPes = 4;
        final int numberOfCloudlets = 2;
        final int numberOfCloudletPes = 2;

        final CloudletSchedulerTimeShared instance = newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, numberOfCloudletPes);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercentUtilization(0), 0);
    }

    /**
     * Creates a scheduler with a list of running cloudlets, where each Cloudlet has just one PE.
     *
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param numberOfVmPes number of PEs of the VM's scheduler
     * @param numberOfCloudlets number of Cloudlets to create
     * @return the new scheduler
     */
    private CloudletSchedulerTimeShared newSchedulerWithSingleCoreRunningCloudlets(long mips, int numberOfVmPes, int numberOfCloudlets) {
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
    private CloudletSchedulerTimeShared newSchedulerWithRunningCloudlets(long mips, int numberOfVmPes, int numberOfCloudlets, int numberOfCloudletPes) {
        final CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(numberOfVmPes, mips);

        for(int i = 0; i < numberOfCloudlets; i++) {
            final Cloudlet c = CloudletSimpleTest.createCloudlet(i, mips, numberOfCloudletPes);
            c.assignToDatacenter(Datacenter.NULL);
            instance.cloudletSubmit(c);
        }

        return instance;
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

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, numberOfCloudlets, numberOfCloudlets);

        final double time0 = 0.5;
        instance.updateProcessing(time0, instance.getCurrentMipsShare());
        assertEquals(2, instance.getCloudletExecList().size());

        final double time1 = 1.0;
        instance.updateProcessing(time1, instance.getCurrentMipsShare());
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

        final CloudletSchedulerTimeShared instance = newSchedulerWithSingleCoreRunningCloudlets(mips, 1, numberOfCloudlets);

        final double time1 = 1;
        instance.updateProcessing(time1, instance.getCurrentMipsShare());
        assertEquals(2, instance.getCloudletExecList().size());

        final double time2 = 2;
        instance.updateProcessing(time2, instance.getCurrentMipsShare());
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testRemoveCloudletFromExecList() {
        final CloudletExecutionInfo c = new CloudletExecutionInfo(Cloudlet.NULL);
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        instance.addCloudletToExecList(c);
        assertSame(c, instance.removeCloudletFromExecList(c));
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testRemoveCloudletFromExecList_CloudletNoFound() {
        CloudletExecutionInfo cloudletNotAdded = new CloudletExecutionInfo(CloudletSimpleTest.createCloudletWithOnePe(0));
        CloudletExecutionInfo cloudletAdded = new CloudletExecutionInfo(CloudletSimpleTest.createCloudletWithOnePe(1));
        CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        final List<CloudletExecutionInfo> list = new ArrayList<>();
        list.add(cloudletAdded);
        instance.addCloudletToExecList(cloudletAdded);
        assertSame(CloudletExecutionInfo.NULL, instance.removeCloudletFromExecList(cloudletNotAdded));
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }

    @Test
    public void testAddCloudletToExecList() {
        final CloudletExecutionInfo cloudlet = new CloudletExecutionInfo(Cloudlet.NULL);
        final List<CloudletExecutionInfo> list = new ArrayList<>();
        list.add(cloudlet);
        final CloudletSchedulerTimeShared instance = new CloudletSchedulerTimeShared();
        instance.addCloudletToExecList(cloudlet);
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }
}
