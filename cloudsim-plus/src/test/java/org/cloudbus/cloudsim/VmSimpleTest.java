/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.schedulers.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerDynamicWorkload;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.easymock.EasyMock;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
        vm = VmSimpleTest.createVm(vmScheduler);
    }
    

    /**
     * Creates a VM with the 1 PE and half mips capacity defined in
     * {@link #MIPS}.
     *
     * @param vmId the id of the VM
     * @return
     */
    public static VmSimple createVmWithOnePeAndHalfMips(final int vmId) {
        return VmSimpleTest.createVm(vmId, MIPS / 2, 1, RAM, BW, SIZE, null);
    }

    /**
     * Creates a VM with 1 PE and the total mips capacity defined in
     * {@link #MIPS}.
     *
     * @param vmId the id of the VM
     * @return
     */
    public static VmSimple createVmWithOnePeAndTotalMips(final int vmId) {
        return VmSimpleTest.createVm(vmId, MIPS, 1, RAM, BW, SIZE, null);
    }

    /**
     * Creates a VM with the given mips and numberOfPes and default
     * configuration for RAM, BW and Storage.
     *
     * @param vmId
     * @param mips
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVm(final int vmId,
            final double mips, final int numberOfPes) {
        return new VmSimple(
                vmId, 0, mips, numberOfPes, RAM, BW, SIZE, 
                "", CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with the given numberOfPes and default configuration for
     * MIPS, RAM, BW and Storage.
     *
     * @param vmId
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVm(final int vmId, final int numberOfPes) {
        return new VmSimple(
                vmId, 0, MIPS, numberOfPes, RAM, BW, SIZE, 
                "", CloudletScheduler.NULL);
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
     * @param scheduler the cloudlet scheduler
     * @return
     */
    public static VmSimple createVm(final int vmId,
            final double mips, final int numberOfPes,
            final int ram, final long bw, final long storage,
            final CloudletScheduler scheduler) {
        return new VmSimple(vmId, 0, mips, numberOfPes, ram, bw, storage, "", scheduler);
    }

    /**
     * Creates a VM with the given numberOfPes for a given user and default
     * configuration for MIPS, RAM, BW and Storage.
     *
     * @param vmId
     * @param userId
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVmWithSpecificNumberOfPEsForSpecificUser(
            final int vmId, final int userId, final int numberOfPes) {
        return new VmSimple(
                vmId, userId, MIPS, numberOfPes, RAM, BW, SIZE, 
                "", CloudletScheduler.NULL);
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
        assertTrue(vm.setRam(RAM / 2));
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
        assertTrue(vm.setBw(BW / 2));
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
        assertEquals(null, vm.getHost());
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
    public void testGetCurrentAllocatedSize() {
        assertEquals(0, vm.getCurrentAllocatedSize());
        vm.setCurrentAllocatedSize(SIZE);
        assertEquals(SIZE, vm.getCurrentAllocatedSize());
    }

    @Test
    public void testGetCurrentAllocatedRam() {
        assertEquals(0, vm.getCurrentAllocatedRam());
        vm.setCurrentAllocatedRam(RAM);
        assertEquals(RAM, vm.getCurrentAllocatedRam());
    }

    @Test
    public void testGetCurrentAllocatedBw() {
        assertEquals(0, vm.getCurrentAllocatedBw());
        vm.setCurrentAllocatedBw(BW);
        assertEquals(BW, vm.getCurrentAllocatedBw());
    }

    @Test
    public void testGetCurrentAllocatedMips() {
        assertNull(vm.getCurrentAllocatedMips());
    }

    @Test
    public void testIsBeingInstantiated() {
        assertTrue(vm.isBeingInstantiated());
        vm.setBeingInstantiated(false);
        assertFalse(vm.isBeingInstantiated());
    }

    @Test
    public void testGetCurrentRequestedMips() {
        List<Double> expectedCurrentMips = new ArrayList<>();
        expectedCurrentMips.add(MIPS / 2);
        expectedCurrentMips.add(MIPS / 2);

        CloudletScheduler  cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedMips()).andReturn(expectedCurrentMips);
        replay(cloudletScheduler);

        VmSimple vm = VmSimpleTest.createVm(cloudletScheduler);
        vm.setBeingInstantiated(false);
        assertEquals(expectedCurrentMips, vm.getCurrentRequestedMips());

        verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedBwVmNotBeingInstantiated() {
        final double currentBwUtilizationPercentage = 0.5;

        CloudletScheduler cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedUtilizationOfBw())
                .andReturn(currentBwUtilizationPercentage);
        replay(cloudletScheduler);
        
        VmSimple vm0 = VmSimpleTest.createVm(cloudletScheduler);
        vm0.setBeingInstantiated(false);
        
        final long expectedCurrentBwUtilization = (long)(currentBwUtilizationPercentage*BW);
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
        verify(cloudletScheduler);
    }
    
    @Test
    public void testGetCurrentRequestedBwVmBeingInstantiated() {
        VmSimple vm0 = VmSimpleTest.createVm(CloudletScheduler.NULL);
        vm0.setBeingInstantiated(true);
        final long expectedCurrentBwUtilization = BW;
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
    }
    
    @Test
    public void testGetCurrentRequestedRamVmNotBeingInstantiated() {
        final double currentRamUtilizationPercentage = 0.5;
        final long expectedCurrentRamUtilization = (long)(currentRamUtilizationPercentage*RAM);

        CloudletScheduler  cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        expect(cloudletScheduler.getCurrentRequestedUtilizationOfRam())
                .andReturn(currentRamUtilizationPercentage);
        replay(cloudletScheduler);
        
        VmSimple vm0 = VmSimpleTest.createVm(cloudletScheduler);
        vm0.setBeingInstantiated(false);
        assertEquals(expectedCurrentRamUtilization, vm0.getCurrentRequestedRam());
        verify(cloudletScheduler);
    }
    
    @Test
    public void testGetCurrentRequestedRamVmBeingInstantiated() {
        VmSimple vm0 = VmSimpleTest.createVm(CloudletScheduler.NULL);
        vm0.setBeingInstantiated(true);
        final long expectedCurrentRamUtilization = RAM;
        assertEquals(expectedCurrentRamUtilization, vm0.getCurrentRequestedRam());
    }    

    @Test
    public void testGetCurrentRequestedMipsTimeSharedScheduler() {
        CloudletScheduler cloudletScheduler = new CloudletSchedulerTimeShared();
        VmSimple vm = VmSimpleTest.createVm(cloudletScheduler);
        vm.setBeingInstantiated(false);

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
        return new VmSimple(ID, USER_ID, MIPS, PES_NUMBER, RAM, BW, SIZE, VMM, cloudletScheduler);
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

        VmSimple vm = VmSimpleTest.createVm(cloudletScheduler);
        assertEquals(MIPS * 2, vm.getCurrentRequestedTotalMips(), 0);
        verify(cloudletScheduler);
    }

}
