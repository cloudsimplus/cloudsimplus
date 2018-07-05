/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.HostSimpleTest;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.*;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSimpleTest {

    private static final int BROKER_ID = 0;
    private static final int ID = 1;
    private static final double MIPS = 1000;
    private static final int PES_NUMBER = 2;
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final long SIZE = 1000;
    private static final String VMM = "Xen";
    private CloudletSchedulerTimeShared cloudletScheduler;
    private VmSimple vm;

    @Before
    public void setUp() throws Exception {
        cloudletScheduler = new CloudletSchedulerTimeShared();
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        final DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        vm = VmSimpleTest.createVm(cloudletScheduler);
        vm.setBroker(broker);
    }

    /**
     * Creates a VM with the 1 PE and half mips capacity defined in
     * {@link #MIPS}.
     *
     * @param vmId the id of the VM
     * @return
     */
    public static Vm createVmWithOnePeAndHalfMips(final int vmId) {
        return createVm(vmId, MIPS / 2, 1, RAM, BW, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with 1 PE and the total mips capacity defined in
     * {@link #MIPS}.
     *
     * @param vmId the id of the VM
     * @return
     */
    public static Vm createVmWithOnePeAndTotalMips(final int vmId) {
        return createVm(vmId, MIPS, 1, RAM, BW, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with the given numberOfPes and default configuration for
     * HOST_MIPS, HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVm(final int vmId, final int numberOfPes) {
        return createVm(vmId, MIPS, numberOfPes);
    }

    /**
     * Creates a VM with the default configuration defined in the Test Class'
     * constants.
     *
     * @param cloudletScheduler
     * @return
     */
    public static VmSimple createVm(CloudletScheduler cloudletScheduler) {
        return createVm(ID, MIPS, PES_NUMBER, RAM, BW, SIZE, cloudletScheduler);
    }

    /**
     * Creates a VM with the given mips and numberOfPes and default
     * configuration for HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param mips
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVm(final int vmId, final double mips, final int numberOfPes) {
        return createVm(vmId, mips, numberOfPes, RAM, BW, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with 1 PE.
     *
     * @param vmId id of the VM
     * @param capacity a capacity that will be set to all resources, such as CPU, HOST_RAM, HOST_BW, etc.
     * @return
     */
    public static VmSimple createVm(final int vmId, long capacity) {
        return createVm(vmId, capacity, 1, capacity, capacity, capacity, CloudletScheduler.NULL);
    }

    public static VmSimple createVm(final int vmId,
            final double mips, final int numberOfPes,
            final long ram, final long bw, final long storage)
    {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        final DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        final VmSimple vm = new VmSimple(vmId, mips, numberOfPes);
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(CloudletScheduler.NULL)
                .setBroker(broker);
        return vm;
    }

    public static VmSimple createVm(final int vmId,
            final double mips, final int numberOfPes,
            final long ram, final long bw, final long storage,
            final CloudletScheduler scheduler)
    {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        final DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        final VmSimple vm = new VmSimple(vmId, mips, numberOfPes);
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(scheduler)
                .setBroker(broker);
        return vm;
    }


    /**
     * Creates a VM with the given numberOfPes for a given user and default
     * configuration for HOST_MIPS, HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param broker
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVmWithSpecificNumberOfPEsForSpecificUser(
        final int vmId, final DatacenterBroker broker, final int numberOfPes) {
        final VmSimple vm = createVm(vmId, MIPS, numberOfPes, RAM, BW, SIZE, CloudletScheduler.NULL);
        vm.setBroker(broker);
        return vm;
    }

    @Test
    public void testGetMips() {
        assertEquals(MIPS, vm.getMips(), 0);
    }

    @Test
    public void testToString() {
        assertEquals(String.format("Vm %d/Broker %d", vm.getId(), vm.getBroker().getId()), vm.toString());
    }

    @Test
    public void testSetMips() {
        vm.setMips(MIPS / 2);
        assertEquals(MIPS / 2, vm.getMips(), 0);
    }

    @Test
    public void testSetRam() {
        vm.setRam(RAM / 2);
        assertEquals(RAM / 2, vm.getRam().getCapacity(), 0);
    }

    @Test
    public void testAddStateHistoryEntry_addEntryToEmptyList(){
        final double time=0;
        final double allocatedMips=1000;
        final double requestedMips=100;
        final boolean inMigration = false;
        final VmStateHistoryEntry entry =
                new VmStateHistoryEntry(time, allocatedMips, requestedMips, inMigration);
        vm.addStateHistoryEntry(entry);
        assertFalse(vm.getStateHistory().isEmpty());
    }

    @Test
    public void testAddStateHistoryEntry_checkAddedEntryValues(){
        final VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        assertEquals(entry, vm.getStateHistory().get(vm.getStateHistory().size()-1));
    }

    @Test
    public void testAddStateHistoryEntry_tryToAddEntryWithSameTime(){
        final VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        vm.addStateHistoryEntry(entry);
        assertEquals(1, vm.getStateHistory().size());
    }

    @Test
    public void testAddStateHistoryEntry_changeAddedEntry(){
        final VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        entry.setInMigration(true);
        vm.addStateHistoryEntry(entry);
        assertEquals(entry, vm.getStateHistory().get(vm.getStateHistory().size()-1));
    }

    @Test
    public void testSetBw() {
        vm.setBw(BW / 2);
        assertEquals(BW / 2, vm.getBw().getCapacity(), 0);
    }

    @Test
    public void testRemoveOnHostAllocationListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnHostAllocationListener(listener);
        assertTrue(vm.removeOnHostAllocationListener(listener));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveOnHostAllocationListener_Null() {
        vm.removeOnHostAllocationListener(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveOnHostDeallocationListener_Null() {
        vm.removeOnHostDeallocationListener(null);
    }

    @Test
    public void testRemoveOnHostDeallocationListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnHostDeallocationListener(listener);
        assertTrue(vm.removeOnHostDeallocationListener(listener));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveOnVmCreationFailureListener_Null() {
        vm.removeOnCreationFailureListener(null);
    }

    @Test
    public void testRemoveOnVmCreationFailureListener() {
        final EventListener<VmDatacenterEventInfo> listener = (info) -> {};
        vm.addOnCreationFailureListener(listener);
        assertTrue(vm.removeOnCreationFailureListener(listener));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveOnUpdateVmProcessingListener_Null() {
        vm.removeOnUpdateProcessingListener(null);
    }

    @Test
    public void testRemoveOnUpdateVmProcessingListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnUpdateProcessingListener(listener);
        assertTrue(vm.removeOnUpdateProcessingListener(listener));
    }

    @Test
    public void testGetNumberOfPes() {
        assertEquals(PES_NUMBER, vm.getNumberOfPes());
    }

    @Test
    public void testGetRam() {
        assertEquals(RAM, vm.getRam().getCapacity());
    }

    @Test
    public void testGetBw() {
        assertEquals(BW, vm.getBw().getCapacity());
    }

    @Test
    public void testGetSize() {
        assertEquals(SIZE, vm.getStorage().getCapacity());
    }

    @Test
    public void testGetVmm() {
        assertEquals(VMM, vm.getVmm());
    }

    @Test
    public void testGetHost() {
        final HostSimple host = HostSimpleTest.createHostSimple(0, 1);
        vm.setHost(host);
        assertEquals(host, vm.getHost());
    }

    @Test
    public void testIsInMigration() {
        vm.setInMigration(true);
        assertTrue(vm.isInMigration());
    }

    @Test
    public void testGetTotalUtilization() {
        assertEquals(0, vm.getCpuPercentUsage(0), 0);
    }

    @Test
    public void testGetTotalUtilizationMips() {
        assertEquals(0, vm.getTotalCpuMipsUsage(0), 0);
    }

    @Test
    public void testGetUid() {
        assertEquals(BROKER_ID + "-" + ID, vm.getUid());
    }

    @Test
    public void testUpdateVmProcessing() {
        final List<Double> mipsShare1 = new ArrayList<>(1);
        final List<Double> mipsShare2 = new ArrayList<>(1);
        mipsShare1.add(1.0);
        mipsShare2.add(1.0);
        final double expectedNextCompletionTime = cloudletScheduler.updateProcessing(0, mipsShare1);
        final double actualNextCompletionTime = vm.updateProcessing(0, mipsShare2);
        assertEquals(expectedNextCompletionTime, actualNextCompletionTime, 0);
    }

    @Test
    public void testIsCreated() {
        vm.setCreated(true);
        assertTrue(vm.isCreated());
    }

    @Test
    public void testGetCurrentRequestedBw_WhenVmWasCreatedInsideHost() {
        final double currentBwUsagePercent = 0.5;

        final CloudletScheduler cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.expect(cloudletScheduler.getCurrentRequestedBwPercentUtilization())
                .andReturn(currentBwUsagePercent);
        EasyMock.replay(cloudletScheduler);

        final Vm vm0 = VmSimpleTest.createVm(cloudletScheduler);
        vm0.setCreated(true);

        final long expectedCurrentBwUtilization = (long)(currentBwUsagePercent*BW);
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
        EasyMock.verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedBw_WhenVmWasNotCreatedInsideHost() {
        final Vm vm0 = VmSimpleTest.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentBwUsage = BW;
        assertEquals(expectedCurrentBwUsage, vm0.getCurrentRequestedBw());
    }

    @Test
    public void testGetCurrentRequestedRam_WhenVmWasCreatedInsideHost() {
        final double currentRamUsagePercent = 0.5;
        final long expectedCurrentRamUsage = (long)(currentRamUsagePercent*RAM);

        final CloudletScheduler  cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.expect(cloudletScheduler.getCurrentRequestedRamPercentUtilization())
                .andReturn(currentRamUsagePercent);
        EasyMock.replay(cloudletScheduler);

        final Vm vm0 = VmSimpleTest.createVm(cloudletScheduler);
        vm0.setCreated(true);
        assertEquals(expectedCurrentRamUsage, vm0.getCurrentRequestedRam());
        EasyMock.verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedRam_WhenVmWasNotCreatedInsideHost() {
        final Vm vm0 = VmSimpleTest.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentRamUsage = RAM;
        assertEquals(expectedCurrentRamUsage, vm0.getCurrentRequestedRam());
    }

    @Test
    public void testGetCurrentRequestedMips_ForTimeSharedScheduler_WhenVmWasCreatedInsideHost() {
        final CloudletScheduler cloudletScheduler = new CloudletSchedulerTimeShared();
        final Vm vm = VmSimpleTest.createVm(cloudletScheduler);
        vm.setCreated(true);

        assertTrue(vm.getCurrentRequestedMips().isEmpty());
    }
}
