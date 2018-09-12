package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.cloudlets.CloudletSimpleTest;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTimeSharedTest {
    private CloudletSchedulerTimeShared instance;

    @Before
    public void setUp(){
        instance = new CloudletSchedulerTimeShared();
    }

    /**
     * Creates a mock CloudletExecutionInfo.
     * @param id Cloudlet id
     * @return the created mock Cloudlet
     */
    private CloudletExecution createCloudletExecInfo(int id){
        final CloudletExecution cloudlet = EasyMock.createMock(CloudletExecution.class);
        EasyMock.expect(cloudlet.getCloudletId()).andReturn(id).anyTimes();
        EasyMock.replay(cloudlet);
        return cloudlet;
    }

    @Test
    public void testGetCloudletWaitingList_Empty() {
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
    public void testCloudletResume_EmptyPausedList() {
        final int cloudletId = 0;
        final double expResult = 0.0;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetCloudletWaitingList_EmptyAfterResumingCloudlet() {
        final long cloudletLength = 1000;
        final CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(1, cloudletLength);
        final int cloudletId = 0;
        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        final List<CloudletExecution> result = instance.getCloudletWaitingList();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCloudletResume_CloudletNotInPausedList() {
        final int cloudletIdInTheList = 1;
        instance.getCloudletPausedList().add(createCloudletExecInfo(cloudletIdInTheList));
        final double expResult = 0.0;
        final int cloudletIdSearched = 2;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletIdSearched, 1, 1));
        assertEquals(expResult, result, 0.0);    }

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
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));

        assertEquals(expResult, result, 0.0);
    }

    private CloudletSchedulerTimeShared createCloudletSchedulerWithMipsList(int numberOfPes, long mipsOfEachPe) {
        final CloudletSchedulerTimeShared scheduler = new CloudletSchedulerTimeShared();
        final List<Double> mipsList = CloudletSchedulerUtil.createMipsList(numberOfPes, mipsOfEachPe);
        scheduler.setCurrentMipsShare(mipsList);
        scheduler.setVm(new VmSimple(0, mipsOfEachPe, numberOfPes));
        return scheduler;
    }

    private void createCloudletAndAddItToPausedList(CloudletSchedulerTimeShared instance, int cloudletId, long cloudletLength) {
        final CloudletSimple cloudlet = CloudletSimpleTest.createCloudlet(cloudletId, cloudletLength, 1);
        cloudlet.setStatus(Cloudlet.Status.PAUSED);
        instance.getCloudletPausedList().add(new CloudletExecution(cloudlet));
    }

    @Test
    public void testIsThereEnoughFreePesForCloudlet_EmptyList() {
        final int cloudletPes = 1;
        final Cloudlet cloudlet0 = CloudletSimpleTest.createCloudlet(0, cloudletPes);
        //Vm processing was never updated, so the processing capacity is unknow yet
        assertFalse(instance.isThereEnoughFreePesForCloudlet(new CloudletExecution(cloudlet0)));
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
        assertTrue(instance.isThereEnoughFreePesForCloudlet(new CloudletExecution(cloudlet0)));
    }

    @Test @Ignore("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final CloudletExecution ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(ce, time);
        assertEquals(expResult, result, 0.0);
    }

    @Test @Ignore("The test is being ignored because the tested method in fact is always returning zero. It doesn't have an actual implementation.")
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final CloudletExecution ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(ce, time);
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
        final List<CloudletExecution> result = instance.getCloudletExecList();
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
            final Cloudlet cloudlet = CloudletSimpleTest.createCloudlet(i, mips, numberOfCloudletPes);
            cloudlet.assignToDatacenter(Datacenter.NULL);
            instance.cloudletSubmit(cloudlet);
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
        final CloudletExecution cle = new CloudletExecution(Cloudlet.NULL);
        instance.addCloudletToExecList(cle);
        assertSame(cle, instance.removeCloudletFromExecList(cle));
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testRemoveCloudletFromExecList_CloudletNoFound() {
        final CloudletExecution cloudletNotAdded = new CloudletExecution(CloudletSimpleTest.createCloudletWithOnePe(0));
        final CloudletExecution cloudletAdded = new CloudletExecution(CloudletSimpleTest.createCloudletWithOnePe(1));
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
