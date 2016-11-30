/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.HostSimpleTest;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerDynamicWorkload;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudsimplus.listeners.DatacenterToVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.easymock.EasyMock;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSimpleTest {

    private static final int ID = 1;
    private static final int USER_ID = 1;
    private static final double MIPS = 1000;
    private static final int PES_NUMBER = 2;
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final long SIZE = 1000;
    private static final String VMM = "Xen";
    private CloudletSchedulerDynamicWorkload vmScheduler;
    private VmSimple vm;

    @Before
    public void setUp() throws Exception {
        vmScheduler = new CloudletSchedulerDynamicWorkload(MIPS, PES_NUMBER);
        DatacenterBroker broker = Mocks.createMockBroker(USER_ID, 2);
        vm = VmSimpleTest.createVm(vmScheduler);
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
     * Creates a VM with the given configuration
     *
     * @param vmId
     * @param mips
     * @param numberOfPes
     * @param ram
     * @param bw
     * @param storage
     * @param scheduler
     * @return
     */
    public static VmSimple createVm(final int vmId,
            final double mips, final int numberOfPes,
            final long ram, final long bw, final long storage,
            final CloudletScheduler scheduler)
    {
        CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        VmSimple vm = new VmSimple(vmId, mips, numberOfPes);
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(scheduler)
                .setSimulation(cloudsim);
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
        VmSimple vm = createVm(vmId, MIPS, numberOfPes, RAM, BW, SIZE, CloudletScheduler.NULL);
        vm.setBroker(broker);
        return vm;
    }

    @Test
    public void testGetMips() {
        assertEquals(MIPS, vm.getMips(), 0);
    }

    @Test
    public void testToString() {
        assertEquals(vm.getUid(), vm.toString());
    }

    @Test
    public void testSetMips() {
        vm.setMips(MIPS / 2);
        assertEquals(MIPS / 2, vm.getMips(), 0);
    }

    @Test
    public void testSetRam() {
        vm.setRam(RAM / 2);
        assertEquals(RAM / 2, vm.getRam(), 0);
    }

    @Test
    public void testAddStateHistoryEntry_addEntryToEmptyList(){
        Vm vm = VmSimpleTest.createVm(vmScheduler);
        double time=0, allocatedMips=1000, requestedMips=100;
        boolean inMigration = false;
        assertTrue(vm.getStateHistory().isEmpty());
        VmStateHistoryEntry entry =
                new VmStateHistoryEntry(time, allocatedMips, requestedMips, inMigration);
        vm.addStateHistoryEntry(entry);
        assertFalse(vm.getStateHistory().isEmpty());
    }

    @Test
    public void testAddStateHistoryEntry_checkAddedEntryValues(){
        Vm vm = VmSimpleTest.createVm(vmScheduler);
        VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        assertEquals(entry, vm.getStateHistory().get(vm.getStateHistory().size()-1));
    }

    @Test
    public void testAddStateHistoryEntry_tryToAddEntryWithSameTime(){
        Vm vm = VmSimpleTest.createVm(vmScheduler);
        VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        assertEquals(1, vm.getStateHistory().size());
        vm.addStateHistoryEntry(entry);
        assertEquals(1, vm.getStateHistory().size());
    }

    @Test
    public void testAddStateHistoryEntry_changeAddedEntry(){
        Vm vm = VmSimpleTest.createVm(vmScheduler);
        VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        entry.setInMigration(true);
        vm.addStateHistoryEntry(entry);
        assertEquals(entry, vm.getStateHistory().get(vm.getStateHistory().size()-1));
    }

    @Test
    public void testSetBw() {
        vm.setBw(BW / 2);
        assertEquals(BW / 2, vm.getBw(), 0);
    }

    @Test
    public void testSetOnHostAllocationListener() {
        vm.setOnHostAllocationListener(null);
        assertEquals(EventListener.NULL, vm.getOnHostAllocationListener());
        EventListener<HostToVmEventInfo> listener = (evt) -> {};
        vm.setOnHostAllocationListener(listener);
        assertEquals(listener, vm.getOnHostAllocationListener());
    }

    @Test
    public void testSetOnHostDeallocationListener() {
        vm.setOnHostDeallocationListener(null);
        assertEquals(EventListener.NULL, vm.getOnHostDeallocationListener());
        EventListener<HostToVmEventInfo> listener = (evt) -> {};
        vm.setOnHostDeallocationListener(listener);
        assertEquals(listener, vm.getOnHostDeallocationListener());
    }

    @Test
    public void testSetOnVmCreationFailureListener() {
        vm.setOnVmCreationFailureListener(null);
        assertEquals(EventListener.NULL, vm.getOnVmCreationFailureListener());
        EventListener<DatacenterToVmEventInfo> listener = (evt) -> {};
        vm.setOnVmCreationFailureListener(listener);
        assertEquals(listener, vm.getOnVmCreationFailureListener());
    }

    @Test
    public void testSetOnUpdateVmProcessingListener() {
        vm.setOnUpdateVmProcessingListener(null);
        assertEquals(EventListener.NULL, vm.getOnUpdateVmProcessingListener());
        EventListener<HostToVmEventInfo> listener = (evt) -> {};
        vm.setOnUpdateVmProcessingListener(listener);
        assertEquals(listener, vm.getOnUpdateVmProcessingListener());
    }

    @Test
    public void testGetNumberOfPes() {
        assertEquals(PES_NUMBER, vm.getNumberOfPes());
    }

    @Test
    public void testGetRam() {
        assertEquals(RAM, vm.getRam());
    }

    @Test
    public void testGetBw() {
        assertEquals(BW, vm.getBw());
    }

    @Test
    public void testGetSize() {
        assertEquals(SIZE, vm.getSize());
    }

    @Test
    public void testGetVmm() {
        assertEquals(VMM, vm.getVmm());
    }

    @Test
    public void testGetHost() {
        assertEquals(Host.NULL, vm.getHost());
        HostSimple host = HostSimpleTest.createHostSimple(0, 1);
        vm.setHost(host);
        assertEquals(host, vm.getHost());
    }

    @Test
    public void testIsInMigration() {
        assertFalse(vm.isInMigration());
        vm.setInMigration(true);
        assertTrue(vm.isInMigration());
    }

    @Test
    public void testGetTotalUtilization() {
        assertEquals(0, vm.getTotalUtilizationOfCpu(0), 0);
    }

    @Test
    public void testGetTotalUtilizationMips() {
        assertEquals(0, vm.getTotalUtilizationOfCpuMips(0), 0);
    }

    @Test
    public void testGetUid() {
        assertEquals(USER_ID + "-" + ID, vm.getUid());
    }

    @Test
    public void testUpdateVmProcessing() {
        assertEquals(Double.MAX_VALUE, vm.updateVmProcessing(0, null), 0);
        List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(1.0);
        List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(1.0);
        assertEquals(vmScheduler.updateVmProcessing(0, mipsShare1), vm.updateVmProcessing(0, mipsShare2), 0);
    }

    @Test
    public void testIsCreated() {
        assertFalse(vm.isCreated());
        vm.setCreated(true);
        assertTrue(vm.isCreated());
    }

    @Test
    public void testGetCurrentRequestedMips_WhenVmWasCreatedInsideHost() {
        List<Double> expectedCurrentMips = new ArrayList<>();
        expectedCurrentMips.add(MIPS / 2);
        expectedCurrentMips.add(MIPS / 2);

        CloudletScheduler  cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedMips()).andReturn(expectedCurrentMips);
        replay(cloudletScheduler);

        Vm vm = VmSimpleTest.createVm(cloudletScheduler);
        vm.setCreated(true);
        assertEquals(expectedCurrentMips, vm.getCurrentRequestedMips());

        verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedBw_WhenVmWasCreatedInsideHost() {
        final double currentBwUtilizationPercentage = 0.5;

        CloudletScheduler cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedUtilizationOfBw())
                .andReturn(currentBwUtilizationPercentage);
        replay(cloudletScheduler);

        Vm vm0 = VmSimpleTest.createVm(cloudletScheduler);
        vm0.setCreated(true);

        final long expectedCurrentBwUtilization = (long)(currentBwUtilizationPercentage*BW);
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
        verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedBw_WhenVmWasNotCreatedInsideHost() {
        Vm vm0 = VmSimpleTest.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentBwUtilization = BW;
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
    }

    @Test
    public void testGetCurrentRequestedRam_WhenVmWasCreatedInsideHost() {
        final double currentRamUtilizationPercentage = 0.5;
        final long expectedCurrentRamUtilization = (long)(currentRamUtilizationPercentage*RAM);

        CloudletScheduler  cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedUtilizationOfRam())
                .andReturn(currentRamUtilizationPercentage);
        replay(cloudletScheduler);

        Vm vm0 = VmSimpleTest.createVm(cloudletScheduler);
        vm0.setCreated(true);
        assertEquals(expectedCurrentRamUtilization, vm0.getCurrentRequestedRam());
        verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedRam_WhenVmWasNotCreatedInsideHost() {
        Vm vm0 = VmSimpleTest.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentRamUtilization = RAM;
        assertEquals(expectedCurrentRamUtilization, vm0.getCurrentRequestedRam());
    }

    @Test
    public void testGetCurrentRequestedMips_ForTimeSharedScheduler_WhenVmWasCreatedInsideHost() {
        CloudletScheduler cloudletScheduler = new CloudletSchedulerTimeShared();
        Vm vm = VmSimpleTest.createVm(cloudletScheduler);
        vm.setCreated(true);

        assertTrue(vm.getCurrentRequestedMips().isEmpty());
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

    @Test
    public void testGetCurrentRequestedTotalMips() {
        List<Double> currentMips = new ArrayList<>();
        currentMips.add(MIPS);
        currentMips.add(MIPS);

        CloudletSchedulerAbstract cloudletScheduler = createMock(CloudletSchedulerAbstract.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedMips()).andReturn(currentMips);
        replay(cloudletScheduler);

        Vm vm = VmSimpleTest.createVm(cloudletScheduler);
        assertEquals(MIPS * 2, vm.getCurrentRequestedTotalMips(), 0);
        verify(cloudletScheduler);
    }

}
