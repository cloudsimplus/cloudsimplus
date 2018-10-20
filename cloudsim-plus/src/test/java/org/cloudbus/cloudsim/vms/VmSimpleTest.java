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
import org.cloudbus.cloudsim.mocks.MocksHelper;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSimpleTest {

    private static final int BROKER_ID = 0;
    private CloudletSchedulerTimeShared cloudletScheduler;
    private VmSimple vm;

    @BeforeEach
    public void setUp() {
        cloudletScheduler = new CloudletSchedulerTimeShared();
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim);
        vm = VmTestUtil.createVm(cloudletScheduler);
        vm.setBroker(broker);
    }


    @Test
    public void testGetMips() {
        assertEquals(VmTestUtil.MIPS, vm.getMips());
    }

    @Test
    public void testToString() {
        assertEquals(String.format("Vm %d/Broker %d", vm.getId(), vm.getBroker().getId()), vm.toString());
    }

    @Test
    public void testSetMips() {
        vm.setMips(VmTestUtil.MIPS / 2);
        assertEquals(VmTestUtil.MIPS / 2, vm.getMips());
    }

    @Test
    public void testSetRam() {
        vm.setRam(VmTestUtil.RAM / 2);
        assertEquals(VmTestUtil.RAM / 2, vm.getRam().getCapacity());
    }

    @Test
    public void testAddStateHistoryEntryWhenAddEntryToEmptyList(){
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
    public void testAddStateHistoryEntryCheckAddedEntryValues(){
        final VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        assertEquals(entry, vm.getStateHistory().get(vm.getStateHistory().size()-1));
    }

    @Test
    public void testAddStateHistoryEntryWhenAddEntryWithSameTime(){
        final VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        vm.addStateHistoryEntry(entry);
        assertEquals(1, vm.getStateHistory().size());
    }

    @Test
    public void testAddStateHistoryEntryWhenChangeAddedEntry(){
        final VmStateHistoryEntry entry = new VmStateHistoryEntry(0, 1000, 100, false);
        vm.addStateHistoryEntry(entry);
        entry.setInMigration(true);
        vm.addStateHistoryEntry(entry);
        assertEquals(entry, vm.getStateHistory().get(vm.getStateHistory().size()-1));
    }

    @Test
    public void testSetBw() {
        vm.setBw(VmTestUtil.BANDWIDTH / 2);
        assertEquals(VmTestUtil.BANDWIDTH / 2, vm.getBw().getCapacity());
    }

    @Test
    public void testRemoveOnHostAllocationListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnHostAllocationListener(listener);
        assertTrue(vm.removeOnHostAllocationListener(listener));
    }

    @Test()
    public void testRemoveOnHostAllocationListenerWhenNull() {
        Assertions.assertThrows(NullPointerException.class, () -> vm.removeOnHostAllocationListener(null));
    }

    @Test()
    public void testRemoveOnHostDeallocationListenerWhenNull() {
        Assertions.assertThrows(NullPointerException.class, () -> vm.removeOnHostDeallocationListener(null));
    }

    @Test
    public void testRemoveOnHostDeallocationListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnHostDeallocationListener(listener);
        assertTrue(vm.removeOnHostDeallocationListener(listener));
    }

    @Test()
    public void testRemoveOnVmCreationFailureListenerWhenNull() {
        Assertions.assertThrows(NullPointerException.class, () -> vm.removeOnCreationFailureListener(null));
    }

    @Test
    public void testRemoveOnVmCreationFailureListener() {
        final EventListener<VmDatacenterEventInfo> listener = (info) -> {};
        vm.addOnCreationFailureListener(listener);
        assertTrue(vm.removeOnCreationFailureListener(listener));
    }

    @Test()
    public void testRemoveOnUpdateVmProcessingListenerWhenNull() {
        Assertions.assertThrows(NullPointerException.class, () -> vm.removeOnUpdateProcessingListener(null));
    }

    @Test
    public void testRemoveOnUpdateVmProcessingListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnUpdateProcessingListener(listener);
        assertTrue(vm.removeOnUpdateProcessingListener(listener));
    }

    @Test
    public void testGetNumberOfPes() {
        assertEquals(VmTestUtil.PES_NUMBER, vm.getNumberOfPes());
    }

    @Test
    public void testGetRam() {
        assertEquals(VmTestUtil.RAM, vm.getRam().getCapacity());
    }

    @Test
    public void testGetBw() {
        assertEquals(VmTestUtil.BANDWIDTH, vm.getBw().getCapacity());
    }

    @Test
    public void testGetSize() {
        assertEquals(VmTestUtil.SIZE, vm.getStorage().getCapacity());
    }

    @Test
    public void testGetVmm() {
        assertEquals(VmTestUtil.VMM, vm.getVmm());
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
        assertEquals(0, vm.getCpuPercentUsage(0));
    }

    @Test
    public void testGetTotalUtilizationMips() {
        assertEquals(0, vm.getTotalCpuMipsUsage(0));
    }

    @Test
    public void testGetUid() {
        assertEquals(BROKER_ID + "-" + VmTestUtil.ID, vm.getUid());
    }

    @Test
    public void testUpdateVmProcessing() {
        final List<Double> mipsShare1 = new ArrayList<>(1);
        final List<Double> mipsShare2 = new ArrayList<>(1);
        mipsShare1.add(1.0);
        mipsShare2.add(1.0);
        final double expectedNextCompletionTime = cloudletScheduler.updateProcessing(0, mipsShare1);
        final double actualNextCompletionTime = vm.updateProcessing(0, mipsShare2);
        assertEquals(expectedNextCompletionTime, actualNextCompletionTime);
    }

    @Test
    public void testIsCreated() {
        vm.setCreated(true);
        assertTrue(vm.isCreated());
    }

    @Test
    public void testGetCurrentRequestedBwWhenVmWasCreatedInsideHost() {
        final double currentBwUsagePercent = 0.5;

        final CloudletScheduler cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.expect(cloudletScheduler.getCurrentRequestedBwPercentUtilization())
                .andReturn(currentBwUsagePercent);
        EasyMock.replay(cloudletScheduler);

        final Vm vm0 = VmTestUtil.createVm(cloudletScheduler);
        vm0.setCreated(true);

        final long expectedCurrentBwUtilization = (long)(currentBwUsagePercent* VmTestUtil.BANDWIDTH);
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
        EasyMock.verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedBwWhenVmWasNotCreatedInsideHost() {
        final Vm vm0 = VmTestUtil.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentBwUsage = VmTestUtil.BANDWIDTH;
        assertEquals(expectedCurrentBwUsage, vm0.getCurrentRequestedBw());
    }

    @Test
    public void testGetCurrentRequestedRamWhenVmWasCreatedInsideHost() {
        final double currentRamUsagePercent = 0.5;
        final long expectedCurrentRamUsage = (long)(currentRamUsagePercent* VmTestUtil.RAM);

        final CloudletScheduler  cloudletScheduler = createMock(CloudletScheduler.class);
        cloudletScheduler.setVm(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.expect(cloudletScheduler.getCurrentRequestedRamPercentUtilization())
                .andReturn(currentRamUsagePercent);
        EasyMock.replay(cloudletScheduler);

        final Vm vm0 = VmTestUtil.createVm(cloudletScheduler);
        vm0.setCreated(true);
        assertEquals(expectedCurrentRamUsage, vm0.getCurrentRequestedRam());
        EasyMock.verify(cloudletScheduler);
    }

    @Test
    public void testGetCurrentRequestedRamWhenVmWasNotCreatedInsideHost() {
        final Vm vm0 = VmTestUtil.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentRamUsage = VmTestUtil.RAM;
        assertEquals(expectedCurrentRamUsage, vm0.getCurrentRequestedRam());
    }

    @Test
    public void testGetCurrentRequestedMipsTimeSharedSchedulerWhenVmWasCreatedInsideHost() {
        final CloudletScheduler cloudletScheduler = new CloudletSchedulerTimeShared();
        final Vm vm = VmTestUtil.createVm(cloudletScheduler);
        vm.setCreated(true);

        assertTrue(vm.getCurrentRequestedMips().isEmpty());
    }
}
