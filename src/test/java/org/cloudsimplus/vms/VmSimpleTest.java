/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.hosts.HostSimpleTest;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.mocks.MocksHelper;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerAbstract;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        vm = VmTestUtil.createVm(
            cloudletScheduler,
            b -> Mockito.when(b.requestIdleVmDestruction(Mockito.any())).thenReturn(b));
    }

    @Test
    public void testGetWaitTimeForNonCreateVmWithZeroArrivedTime() {
        assertEquals(-1, vm.getCreationWaitTime());
    }

    @Test
    public void testGetWaitTimeForNonCreateVmWithNonZeroClockAndZeroArrivedTime() {
        final int clock = 10;
        final int arrivedTime = 0;
        final Simulation simulation = CloudSimMocker.createMock(mock -> mock.clock(clock));
        final DatacenterBroker broker = MocksHelper.createMockBroker(simulation, b -> {});

        final Vm vm = new VmSimple(this.vm);
        vm.setBroker(broker);
        vm.setBrokerArrivalTime(arrivedTime);
        assertEquals(clock, vm.getCreationWaitTime());
    }

    @Test
    public void testGetWaitTimeForNonCreateVmWithNonZeroClockAndArrivedTime() {
        final int clock = 10;
        final int arrivedTime = 2;
        final Simulation simulation = CloudSimMocker.createMock(mock -> mock.clock(clock));
        final DatacenterBroker broker = MocksHelper.createMockBroker(simulation, b -> {});

        final Vm vm = new VmSimple(this.vm);
        vm.setBroker(broker);
        vm.setBrokerArrivalTime(arrivedTime);
        assertEquals(8, vm.getCreationWaitTime());
    }

    @Test
    public void testGetWaitTimeForCreateVmWithNonZeroClockAndZeroArrivedTime() {
        final int clock = 10;
        vm.setBrokerArrivalTime(0);
        vm.setCreated(true);
        vm.setCreationTime(clock);
        assertEquals(clock, vm.getCreationWaitTime());
    }

    @Test
    public void testGetWaitTimeForCreateVmWithNonZeroClockAndArrivedTime() {
        final int clock = 10;
        vm.setBrokerArrivalTime(2);
        vm.setCreated(true);
        vm.setCreationTime(clock);
        assertEquals(8, vm.getCreationWaitTime());
    }

    @Test
    public void testGetMips() {
        assertEquals(VmTestUtil.MIPS, vm.getMips());
    }

    @Test
    public void testToString() {
        assertEquals("Vm %d".formatted(vm.getId()), vm.toString());
    }

    @Test
    public void testSetMips() {
        vm.setMips(VmTestUtil.MIPS / 2);
        assertEquals(VmTestUtil.MIPS / 2, vm.getMips());
    }

    @Test
    public void testSetStartTimeValid0() {
        final int expected = 0;
        vm.setStartTime(expected);
        assertEquals(expected, vm.getStartTime());
    }

    @Test
    public void testSetStartTimeValid1() {
        final int expected = 1;
        vm.setStartTime(expected);
        assertEquals(expected, vm.getStartTime());
    }

    @Test
    public void testSetLastBusyTimeForCreatedVm() {
        final int expected = 1;
        vm.setStartTime(expected);
        assertEquals(expected, vm.getLastBusyTime());
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
        assertThrows(NullPointerException.class, () -> vm.removeOnHostAllocationListener(null));
    }

    @Test()
    public void testRemoveOnHostDeallocationListenerWhenNull() {
        assertThrows(NullPointerException.class, () -> vm.removeOnHostDeallocationListener(null));
    }

    @Test
    public void testRemoveOnHostDeallocationListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnHostDeallocationListener(listener);
        assertTrue(vm.removeOnHostDeallocationListener(listener));
    }

    @Test()
    public void testRemoveOnVmCreationFailureListenerWhenNull() {
        assertThrows(NullPointerException.class, () -> vm.removeOnCreationFailureListener(null));
    }

    @Test
    public void testRemoveOnVmCreationFailureListener() {
        final EventListener<VmDatacenterEventInfo> listener = (info) -> {};
        vm.addOnCreationFailureListener(listener);
        assertTrue(vm.removeOnCreationFailureListener(listener));
    }

    @Test()
    public void testRemoveOnUpdateVmProcessingListenerWhenNull() {
        assertThrows(NullPointerException.class, () -> vm.removeOnUpdateProcessingListener(null));
    }

    @Test
    public void testRemoveOnUpdateVmProcessingListener() {
        final EventListener<VmHostEventInfo> listener = (info) -> {};
        vm.addOnUpdateProcessingListener(listener);
        assertTrue(vm.removeOnUpdateProcessingListener(listener));
    }

    @Test
    public void testGetPesNumber() {
        assertEquals(VmTestUtil.PES_NUMBER, vm.getPesNumber());
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
        assertEquals(0, vm.getCpuPercentUtilization(0));
    }

    @Test
    public void testGetTotalUtilizationMips() {
        assertEquals(0, vm.getTotalCpuMipsUtilization(0));
    }

    @Test
    public void testGetUid() {
        assertEquals(BROKER_ID + "-" + VmTestUtil.ID, vm.getUid());
    }

    @Test
    public void testUpdateVmProcessing() {
        final MipsShare mipsShare1 = new MipsShare(1.0);
        final MipsShare mipsShare2 = new MipsShare(1.0);
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

        final CloudletScheduler scheduler = Mockito.mock(CloudletSchedulerAbstract.class);
        Mockito.doNothing().when(scheduler).setVm(Mockito.any());
        Mockito.when(scheduler.getCurrentRequestedBwPercentUtilization())
               .thenReturn(currentBwUsagePercent);

        final var vm0 = VmTestUtil.createVm(scheduler);
        vm0.setCreated(true);

        final long expectedCurrentBwUtilization = (long)(currentBwUsagePercent* VmTestUtil.BANDWIDTH);
        assertEquals(expectedCurrentBwUtilization, vm0.getCurrentRequestedBw());
    }

    @Test
    public void testGetCurrentRequestedBwWhenVmWasNotCreatedInsideHost() {
        final var vm0 = VmTestUtil.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentBwUsage = VmTestUtil.BANDWIDTH;
        assertEquals(expectedCurrentBwUsage, vm0.getCurrentRequestedBw());
    }

    @Test
    public void testGetCurrentRequestedRamWhenVmWasCreatedInsideHost() {
        final double currentRamUsagePercent = 0.5;
        final long expectedCurrentRamUsage = (long)(currentRamUsagePercent* VmTestUtil.RAM);

        final CloudletScheduler scheduler = Mockito.mock(CloudletSchedulerAbstract.class);
        Mockito.doNothing().when(scheduler).setVm(Mockito.any());
        Mockito.when(scheduler.getCurrentRequestedRamPercentUtilization())
                .thenReturn(currentRamUsagePercent);

        final var vm0 = VmTestUtil.createVm(scheduler);
        vm0.setCreated(true);
        assertEquals(expectedCurrentRamUsage, vm0.getCurrentRequestedRam());
        Mockito.verify(scheduler).getCurrentRequestedRamPercentUtilization();
    }

    @Test
    public void testGetCurrentRequestedRamWhenVmWasNotCreatedInsideHost() {
        final var vm0 = VmTestUtil.createVm(CloudletScheduler.NULL);
        vm0.setCreated(false);
        final long expectedCurrentRamUsage = VmTestUtil.RAM;
        assertEquals(expectedCurrentRamUsage, vm0.getCurrentRequestedRam());
    }

    @Test
    public void testGetCurrentRequestedMipsTimeSharedSchedulerWhenVmWasCreatedInsideHost() {
        final CloudletScheduler cloudletScheduler = new CloudletSchedulerTimeShared();
        final var vm = VmTestUtil.createVm(cloudletScheduler);
        vm.setCreated(true);

        assertTrue(vm.getCurrentRequestedMips().isEmpty());
    }
}
