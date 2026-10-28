/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.hosts;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.mocks.MocksHelper;
import org.cloudsimplus.allocators.ResourceAllocator;
import org.cloudsimplus.allocators.ResourceAllocatorSimple;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerAbstract;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.util.Conversion;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.cloudsimplus.resources.Processor.totalMips;

/// @author Anton Beloglazov
/// @since CloudSim Toolkit 2.0
public class HostSimpleTest {
    private static final int ID = 0;
    private static final long STORAGE = Conversion.MILLION;
    private static final long HALF_STORAGE = STORAGE / 2;
    private static final long A_QUARTER_STORAGE = STORAGE / 4;
    private static final long RAM = 1024;
    private static final long BW = 10000;

    private static final int CORES = 2;
    private static final double MIPS = 2000;

    private HostSimple host;

    public static HostSimple createHostSimple(final int hostId, final int cores) {
        return createHostSimple(hostId, cores, true);
    }

    public static HostSimple createHostSimple(final int hostId, final int cores, final boolean activate) {
        return createHostSimple(hostId, cores, MIPS, RAM, BW, STORAGE, activate);
    }

    public static HostSimple createHostSimple(
        final int hostId, final int cores,
        final double mips, final long ram,
        final long bw, final long storage)
    {
        return createHostSimple(hostId, cores, mips, ram, bw, storage, true);
    }

    public static HostSimple createHostSimple(
        final int hostId, final int cores,
        final double mips, final long ram,
        final long bw, final long storage, final boolean activate)
    {
        final var host = new HostSimple(ram, bw, storage, new CoreCapacity(cores, mips), activate);
        host.setRamAllocator(new ResourceAllocatorSimple())
            .setBwAllocator(new ResourceAllocatorSimple())
            .setVmScheduler(new VmSchedulerTimeShared())
            .setId(hostId);
        return host;
    }

    private static HostSimple createHostSimple(final int cores, VmScheduler vmScheduler) {
        final var host = new HostSimple(RAM, BW, STORAGE, new CoreCapacity(cores, MIPS));
        host.setRamAllocator(new ResourceAllocatorSimple())
            .setBwAllocator(new ResourceAllocatorSimple())
            .setVmScheduler(vmScheduler);
        return host;

    }

    @Test
    public void testConstructorWithZeroCores() {
        assertThrows(IllegalArgumentException.class, () -> new HostSimple(RAM, BW, STORAGE, new CoreCapacity(0, MIPS)));
    }

    @Test
    public void testTotalMipsCapacityExcludesFailedCores() {
        final HostSimple host = createHostSimple(0, 4);
        host.getProcessor().moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertEquals(3 * MIPS, totalMips(host));
    }

    @Test
    public void testSetFailedFailsAllCoresAndRecoverFreesThem() {
        final int cores = 4;
        final HostSimple host = createHostSimple(0, cores);
        host.getProcessor().moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);

        host.setFailed(true);
        assertAll(
            () -> assertEquals(cores, host.getProcessor().getFailedCores()),
            () -> assertEquals(0, host.getProcessor().getWorkingCores()),
            () -> assertEquals(0, host.getProcessor().getBusyCores()),
            () -> assertEquals(0, totalMips(host))
        );

        host.setFailed(false);
        assertAll(
            () -> assertEquals(0, host.getProcessor().getFailedCores()),
            () -> assertEquals(cores, host.getProcessor().getFreeCores()),
            () -> assertEquals(cores, host.getProcessor().getWorkingCores())
        );
    }

    @BeforeEach
    public void setUp() {
        host = createHostSimple(ID, CORES);
    }

    @Test
    public void isSuitableForVmWhenThereIsAvailableStorage(){
        final Vm vm = createVm(CORES, MIPS, STORAGE);
        assertTrue(host.isSuitableForVm(vm));
    }

    @Test
    public void isSuitableForVmWhenThereIsNotAvailableStorage(){
        final Vm vm = createVm(CORES, MIPS, STORAGE * 2);
        assertFalse(host.isSuitableForVm(vm));
    }

    @Test
    public void isSuitableForVmWhenThereIsEnoughCores(){
        host.setVmScheduler(new VmSchedulerSpaceShared());
        final Vm vm = createVm(CORES, MIPS, STORAGE);
        assertTrue(host.isSuitableForVm(vm));
    }

    /// If the total HOST_MIPS a VM is requesting is lower or equal to the total available HOST_MIPS
    /// from the physical cores but the number of available cores is lower then the
    /// cores requested, the test must fail at least for a [VmSchedulerSpaceShared].
    @Test
    public void isSuitableForVmWhenThereIsNotEnoughCores(){
        host.setVmScheduler(new VmSchedulerSpaceShared());
        final Vm vm = createVm(4, 500, STORAGE);
        assertFalse(host.isSuitableForVm(vm));
    }

    private Vm createVm(final int cores, final double mips, final long storage) {
        final Vm vm = VmSimple.of(new CoreCapacity(cores, mips));
        vm.setRam(RAM);
        vm.setBw(BW);
        vm.setSize(storage);
        return vm;
    }

    @Test
    public void testReallocateMigratingInVmsWhenAllVmsAllocatedToTheHost() {
        final int numberOfVms = 4;
        final var host = createHostSimple(0, numberOfVms);

        final List<Vm> vms = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> {
            final var vm = VmTestUtil.createVm(
                    i, MIPS /numberOfVms, 1, RAM/numberOfVms, BW/numberOfVms, STORAGE/numberOfVms,
                    new CloudletSchedulerTimeShared());
            vm.setHost(Host.NULL);
            host.addMigratingInVm(vm);
            vms.add(vm);
        });

        host.reallocateMigratingInVms();
        assertEquals(vms.size(), host.getVmList().size());
        assertTrue(host.getVmList().containsAll(vms));
    }

    @Test
    public void testReallocateMigratingInVmsWhenOneVmAlreadyAllocatedToTheHost() {
        final int numberOfVms = 4;
        final HostSimple host = createHostSimple(0, numberOfVms);

        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(0);
            mocker.getNumEntities();
            mocker.isTerminationTimeSet();
            mocker.addEntity();
            mocker.clockStr();
            //Broker shutdown event
            mocker.send();
        });

        final List<Vm> vms = new ArrayList<>(numberOfVms);
        for (int i = 0; i < numberOfVms; i++) {
            final Vm vm =
                VmTestUtil.createVm(
                    i, MIPS / numberOfVms, 1, RAM / numberOfVms,
                    BW / numberOfVms, STORAGE / numberOfVms, cloudsim);
            if (i == 0) {
                /*considers that one of the migrating in VMs already was placed at the host,
                thus, it will not be added again to the host vm list.
                By this way, the vms on the host list will be the same
                added to migration list*/
                host.addVmToList(vm);
            }
            host.addMigratingInVm(vm);
            vms.add(vm);
        }

        host.reallocateMigratingInVms();
        final List<Vm> result = host.getVmList();
        assertEquals(vms.size(), result.size());
        assertTrue(vms.containsAll(result));
    }

    @Test
    public void testAddMigratingInVmAndCheckVmWasChangedToInMigration() {
        final int cores = 2;
        final var host = createHostSimple(0, cores);
        final VmSimple vm = VmTestUtil.createVm(
                0, MIPS, cores, RAM, BW, STORAGE,
                new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        host.addMigratingInVm(vm);

        //try to add the already added VM
        host.addMigratingInVm(vm);
        assertTrue(vm.isInMigration());
    }

    @Test
    public void testAddMigratingInVmAndCheckAvailableMipsAndStorage() {
        final int cores = 1;
        final double VM_MIPS = 500;
        final VmSimple vm = VmTestUtil.createVm(
            0, VM_MIPS, cores, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared(),
            b -> Mockito.when(b.requestIdleVmDestruction(Mockito.any())).thenReturn(b));
        final var targetHost = createHostSimple(0, cores);

        assertEquals(MIPS, targetHost.getVmScheduler().getTotalAvailableMips());
        assertTrue(targetHost.addMigratingInVm(vm));
        final double availableMips = MIPS - VM_MIPS;
        assertEquals(availableMips, targetHost.getVmScheduler().getTotalAvailableMips());
        assertEquals(0, targetHost.getStorage().getFree());
    }

    /// During migration, by default, just 10% of capacity is allocated (it's the migration overhead).
    /// @see VmScheduler#DEF_VM_MIGRATION_CPU_OVERHEAD
    @Test
    public void testAddMigratingInVmAndCheckAllocatedMips() {
        final int cores = 1;
        final var targetHost = createHostSimple(0, cores);
        final double VM_MIPS = 500;
        final VmSimple vm = VmTestUtil.createVm(
            0, VM_MIPS, cores, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared());
        targetHost.addMigratingInVm(vm);
        final double allocatedMips = 50;
        assertEquals(allocatedMips, targetHost.getVmScheduler().getTotalAllocatedMipsForVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackRam() {
        final int cores = 2;
        final var host = createHostSimple(0, cores);
        final Vm vm = VmTestUtil.createVm(
            0, MIPS, cores, RAM * 2,
            BW, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackStorage() {
        final int cores = 2;
        final var host = createHostSimple(0, cores);
        final Vm vm = VmTestUtil.createVm(0, MIPS, cores, RAM, BW, STORAGE * 2, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackBw() {
        final int cores = 2;
        final var host = createHostSimple(0, cores);
        final Vm vm = VmTestUtil.createVm(0, MIPS, cores, RAM, BW * 2, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackMips() {
        final int cores = 2;
        final var host = createHostSimple(0, cores);
        final Vm vm = VmTestUtil.createVm(0, MIPS * 2, cores, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testRemoveMigratingInVm() {
        final int cores = 2;
        final var host = createHostSimple(0, cores);
        final VmSimple vm = VmTestUtil.createVm(0, MIPS, cores, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        host.addMigratingInVm(vm);
        host.removeMigratingInVm(vm);
        assertFalse(vm.isInMigration());
        assertFalse(host.getVmsMigratingIn().contains(vm));
    }

    @Test
    public void testIsSuitableForVm() {
        final VmSimple vm0 = VmTestUtil.createVm(0, MIPS, 2, RAM, BW, HALF_STORAGE, new CloudletSchedulerTimeShared());
        final VmSimple vm1 = VmTestUtil.createVm(1, MIPS * 2, 1, RAM * 2, BW * 2, HALF_STORAGE, new CloudletSchedulerTimeShared());

        assertTrue(host.isSuitableForVm(vm0));
        assertFalse(host.isSuitableForVm(vm1));
    }

    @Test
    public void testUpdateVmProcessing() {
        final int numberOfVms = 4;

        final var mipsShare = new CoreCapacity(MIPS / numberOfVms);
        final double time = 0;

        final List<Vm> vmList = createListOfMockVms(numberOfVms, mipsShare, time);

        final VmScheduler vmScheduler = Mockito.mock(VmSchedulerAbstract.class);
        final HostSimple host = createHostSimple(numberOfVms, VmScheduler.NULL);

        Mockito.when(vmScheduler.getAllocatedMips(Mockito.any())).thenReturn(mipsShare);
        Mockito.when(vmScheduler.setHost(Mockito.any())).thenReturn(vmScheduler);
        Mockito.when(vmScheduler.getHost()).thenReturn(host);

        host.setVmScheduler(vmScheduler);
        vmList.forEach(host::addVmToList);

        final int idx = 0;
        final double nextCloudletCompletionTime = idx+1;
        assertEquals(nextCloudletCompletionTime, host.updateProcessing(time));
    }

    private List<Vm> createListOfMockVms(
        final int vmsNumber, final CoreCapacity mipsShare,
        final double simulationClock)
    {
        final List<Vm> vmList = new ArrayList<>(vmsNumber);
        for(int i = 0; i < vmsNumber; i++) {
            final double nextCloudletCompletionTime = i+1;

            final VmAbstract vm = Mockito.mock(VmAbstract.class);
            Mockito
                .when(vm.updateProcessing(simulationClock, mipsShare))
                .thenReturn(nextCloudletCompletionTime);

            vmList.add(vm);
        }

        return vmList;
    }

    @Test
    public void testSetOnUpdateVmsProcessingListener() {
        final Host host = createHostSimple(0, 1);

        final EventListener<HostUpdatesVmsProcessingEventInfo> updateVmsProcessing = e -> {};
        host.listeners().addOnUpdateProcessing(updateVmsProcessing);
        assertTrue(host.listeners().removeOnUpdateProcessing(updateVmsProcessing));

        host.listeners().addOnUpdateProcessing(e -> {});
    }

    @Test
    public void testVmCreate() {
        final VmSimple vm0 = VmTestUtil.createVm(0, MIPS / 2, 1, RAM / 2, BW / 2,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertTrue(host.createVm(vm0).fully());

        final VmSimple vm1 = VmTestUtil.createVm(1, MIPS, 1, RAM, BW,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.createVm(vm1).fully());

        final VmSimple vm2 = VmTestUtil.createVm(2, MIPS * 2, 1, RAM, BW,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.createVm(vm2).fully());

        final VmSimple vm3 = VmTestUtil.createVm(3, MIPS / 2, 2, RAM / 2, BW / 2,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertTrue(host.createVm(vm3).fully());
    }

    /// Checks that provisioning a resource to a VM (such as in vertical scaling)
    /// changes the VM capacity and keeps the amount used by the VM, when it fits the new capacity.
    @Test
    public void testProvisionResourceForVmKeepsVmUtilizationThatFitsNewCapacity() {
        final Host host = createHostSimple(ID, CORES);
        final VmSimple vm = createVmForProvisioning();
        assertTrue(host.createVm(vm).fully());
        vm.getRam().occupy(RAM / 4);

        assertTrue(host.provisionResourceForVm(vm, Ram.class, RAM));
        assertEquals(RAM, vm.getRam().getCapacity());
        assertEquals(RAM, host.getRamAllocator().getTotalAllocatedResource());
        assertEquals(RAM / 4, vm.getRam().getOccupied());
    }

    /// Checks that downscaling a VM resource below the amount used by the VM
    /// clears such a usage, so that the new capacity can be set.
    @Test
    public void testProvisionResourceForVmClearsVmUtilizationHigherThanNewCapacity() {
        final Host host = createHostSimple(ID, CORES);
        final VmSimple vm = createVmForProvisioning();
        assertTrue(host.createVm(vm).fully());
        vm.getRam().occupy(RAM / 2);

        assertTrue(host.provisionResourceForVm(vm, Ram.class, RAM / 4));
        assertEquals(RAM / 4, vm.getRam().getCapacity());
        assertEquals(RAM / 4, host.getRamAllocator().getTotalAllocatedResource());
        assertEquals(0, vm.getRam().getOccupied());
    }

    @Test
    public void testProvisionResourceForVmWhenThereIsNoEnoughResource() {
        final Host host = createHostSimple(ID, CORES);
        final VmSimple vm = createVmForProvisioning();
        assertTrue(host.createVm(vm).fully());

        assertFalse(host.provisionResourceForVm(vm, Ram.class, RAM * 2));
        assertEquals(RAM / 2, vm.getRam().getCapacity());
        assertEquals(RAM / 2, host.getRamAllocator().getTotalAllocatedResource());
    }

    /// Checks that the Host RAM/BW utilization is what the VMs use,
    /// while the allocated amount is what was reserved for them.
    @Test
    public void testGetRamAndBwUtilizationAndAllocated() {
        final Host host = createHostSimple(ID, CORES);
        final VmSimple vm = createVmForProvisioning();
        assertTrue(host.createVm(vm).fully());
        vm.getRam().occupy(RAM / 4);
        vm.getBw().occupy(BW / 4);

        assertEquals(RAM / 2, host.getRamAllocator().getTotalAllocatedResource());
        assertEquals(BW / 2, host.getBwAllocator().getTotalAllocatedResource());
        assertEquals(RAM / 4, host.getRamUtilization());
        assertEquals(BW / 4, host.getBwUtilization());
    }

    private static VmSimple createVmForProvisioning() {
        return VmTestUtil.createVm(0, MIPS, 1, RAM / 2, BW / 2, A_QUARTER_STORAGE, CloudletScheduler.NULL);
    }

    @Test
    public void testStateHistoryIsNullUntilEnabled() {
        final Host host = createHostSimple(ID, CORES);
        assertSame(HostStateHistory.NULL, host.getStateHistory());
        assertFalse(host.getStateHistory().isEnabled());
        assertTrue(host.getStateHistory().getEntries().isEmpty());

        host.setStateHistoryEnabled(false);
        assertSame(HostStateHistory.NULL, host.getStateHistory());

        host.setStateHistoryEnabled(true);
        assertInstanceOf(HostStateHistorySimple.class, host.getStateHistory());
        assertTrue(host.getStateHistory().isEnabled());
        assertTrue(host.getStateHistory().getEntries().isEmpty());
    }

    /// Checks that disabling the history keeps the recorded entries and the same history object.
    @Test
    public void testDisableStateHistoryKeepsEntries() {
        final Host host = createHostSimple(ID, CORES);
        host.setStateHistoryEnabled(true);
        final HostStateHistory history = host.getStateHistory();
        history.add(new HostStateHistoryEntry(1, MIPS, MIPS, true));

        host.setStateHistoryEnabled(false);
        assertSame(history, host.getStateHistory());
        assertFalse(history.isEnabled());
        assertEquals(1, history.getEntries().size());

        host.setStateHistoryEnabled(true);
        assertSame(history, host.getStateHistory());
        assertTrue(history.isEnabled());
    }

    /// Checks that an entry for the same time as the last one replaces it.
    @Test
    public void testStateHistoryReplacesEntryForTheSameTime() {
        final var history = new HostStateHistorySimple();
        history.add(new HostStateHistoryEntry(1, MIPS, MIPS, true));
        final var lastEntry = new HostStateHistoryEntry(1, MIPS / 2, MIPS, true);
        history.add(lastEntry);
        history.add(new HostStateHistoryEntry(2, MIPS, MIPS, false));

        assertEquals(2, history.getEntries().size());
        assertEquals(lastEntry, history.getEntries().getFirst());
    }

    /// Checks that the NULL allocator can be set and that it doesn't keep a reference to the Host resources,
    /// since it's a shared object.
    @Test
    public void testSetNullAllocators() {
        final Host host = createHostSimple(ID, CORES);
        final var hostRam = host.getRam();
        assertDoesNotThrow(() -> host.setRamAllocator(ResourceAllocator.NULL).setBwAllocator(ResourceAllocator.NULL));
        assertSame(ResourceAllocator.NULL, host.getRamAllocator());
        assertNotSame(hostRam, ResourceAllocator.NULL.getPmResource());
        assertEquals(0, ResourceAllocator.NULL.getCapacity());
    }

    @Test
    public void testVmCreateWhenUnavailableStorageSpace() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmTestUtil.createVm(
                        0, MIPS, 1, RAM, BW, STORAGE*2,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm).fully());
    }

    @Test
    public void testVmCreateWhenUnavailableBw() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmTestUtil.createVm(
                        0, MIPS, 1, RAM, BW*2, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm).fully());
    }

    @Test
    public void testGetCoresNumber() {
        final int cores = 2;
        final Host host = createHostSimple(0, cores);
        assertEquals(cores, host.getProcessor().getCores());
    }

    @Test
    public void testGetBwCapacity() {
        assertEquals(BW, createHostSimple(0, 1).getBw().getCapacity());
    }

    @Test
    public void testGetRamCapacity() {
        assertEquals(RAM, createHostSimple(0, 1).getRam().getCapacity());
    }

    @Test
    public void testGetStorageCapacity() {
        assertEquals(STORAGE, createHostSimple(0, 1).getStorage().getCapacity());
    }

    @Test
    public void testGetNumberOfFreeCores() {
        final int cores = 2;
        final Host host = createHostSimple(0, cores);
        assertEquals(cores, host.getProcessor().getFreeCores());
    }

    @Test
    public void testGetNumberOfFreeCoresWhenOneBusyCore() {
        final int cores = 2;
        final HostSimple host = createHostSimple(0, cores);
        host.getProcessor().moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);
        assertAll(
            () -> assertEquals(1, host.getProcessor().getFreeCores()),
            () -> assertEquals(1, host.getProcessor().getBusyCores())
        );
    }

    @Test
    public void testActiveHostNumberInDatacenterWhenHostsAreAutoPoweredOn(){
        final List<Host> hostList = createHostList(true);
        final Datacenter dc = DatacenterSimple.of(Simulation.NULL, hostList);
        assertEquals(hostList.size(), dc.getActiveHostsNumber());
    }

    @Test
    public void testActiveHostNumberInDatacenterWhenHostsAreNotAutoPoweredOn(){
        final List<Host> hostList = createHostList(false);
        final Datacenter dc = DatacenterSimple.of(Simulation.NULL, hostList);
        assertEquals(0, dc.getActiveHostsNumber());
    }

    @Test
    public void testActiveHostNumberInDatacenterWhenHalfHostsAreAutoPoweredOn(){
        final int hostsToPowerOff = 2;
        final List<Host> hostList = createHostList(true);
        hostList.stream().skip(hostsToPowerOff).forEach(h -> h.setActive(false));
        final Datacenter dc = DatacenterSimple.of(Simulation.NULL, hostList);
        assertEquals(hostsToPowerOff, dc.getActiveHostsNumber());
    }

    @Test
    public void testActiveHostNumberInDatacenterWhenAllHostsArePoweredOffAndNewOneIsAdded(){
        final List<Host> hostList = createHostList(false);
        final Datacenter dc = DatacenterSimple.of(Simulation.NULL, hostList);
        dc.addHost(createHostSimple(hostList.size(), 1));
        assertEquals(1, dc.getActiveHostsNumber());
    }

    @Test
    public void testActiveHostNumberInDatacenterWhenAllHostsArePoweredOnAndNewOneIsAdded(){
        //List before adding a new Host after creating the datacenter
        final List<Host> originalHostList = createHostList(true);
        final Datacenter dc = DatacenterSimple.of(Simulation.NULL, new ArrayList<>(originalHostList));
        dc.addHost(createHostSimple(originalHostList.size(), 1));
        assertEquals(originalHostList.size()+1, dc.getActiveHostsNumber());
        assertEquals(dc.size(), dc.getActiveHostsNumber());
    }

    private List<Host> createHostList(final boolean activate) {
        final int hostCount = 4;
        return IntStream.range(0, hostCount)
                        .mapToObj(i -> createHostSimple(i, 1, activate))
                        .collect(Collectors.toList());
    }

    @Test
    public void testGetNumberOfWorkingCoresWhenOneIsFailed() {
        final int cores = 2;
        final HostSimple host = createHostSimple(0, cores);
        host.getProcessor().moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertAll(
            () -> assertEquals(1, host.getProcessor().getFreeCores()),
            () -> assertEquals(1, host.getProcessor().getWorkingCores()),
            () -> assertEquals(1, host.getProcessor().getFailedCores())
        );
    }

    @Test
    public void testGetNumberOfFreeCoresWhenNoFreeCores() {
        final int cores = 4;
        final HostSimple host = createHostSimple(0, cores);

        host.getProcessor().moveCores(CoreStatus.FREE, CoreStatus.BUSY, cores);
        assertEquals(0, host.getProcessor().getFreeCores());
    }

    @Test
    public void testVmCreateWhenUnavailableRam() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmTestUtil.createVm(
                        0, MIPS, 1, RAM*2, BW, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm).fully());
    }

    @Test
    public void testVmCreateWhenUnavailableMips() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmTestUtil.createVm(
                        0, MIPS *2, 1, RAM, BW, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm).fully());
    }

    @Test
    public void testVmDestroy() {
        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0));
        final VmSimple vm = VmTestUtil.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, STORAGE,
                new CloudletSchedulerTimeShared());
        final List<Vm> vmExecList = new ArrayList<>(1);
        vmExecList.add(vm);
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim, b -> Mockito.when(b.getVmExecList()).thenReturn(vmExecList));
        vm.setBroker(broker);

        assertTrue(host.createVm(vm).fully());
        assertEquals(MIPS, host.getVmScheduler().getTotalAvailableMips());

        host.destroyVm(vm);
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getTotalAvailableMips());
    }

    @Test
    public void testVmDestroyAll() {
        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0));
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim);
        final Vm vm0 = VmTestUtil.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE,
                new CloudletSchedulerTimeShared());
        vm0.setBroker(broker);
        final Vm vm1 = VmTestUtil.createVm(
                1, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE,
                new CloudletSchedulerTimeShared());
        vm1.setBroker(broker);

        assertTrue(host.createVm(vm0).fully());
        assertEquals(MIPS, host.getVmScheduler().getTotalAvailableMips());

        assertTrue(host.createVm(vm1).fully());
        assertEquals(0, host.getVmScheduler().getTotalAvailableMips());

        host.destroyAllVms();
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getTotalAvailableMips());
    }

    @Test
    public void testDestroyVmAndRemoveFromBrokerCreatedList() {
        final var cloudsim = new CloudSimPlus();

        final DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);
        final var vm = VmTestUtil.createVm(0, 2);
        vm.setBroker(broker);
        host.createVm(vm);
        vm.setCreated(true);
        broker.getVmExecList().add(vm);
        host.destroyVm(vm);
        assertFalse(
            broker.getVmExecList().contains(vm),
            vm + " was destroyed into the Host but was not removed from the broker's VM exec List.");
    }

    /// Checks the RAM a VM uses relative to its Host capacity:
    /// the VM has half of the Host RAM and uses half of it, so it uses a quarter of the Host RAM.
    @Test
    public void testVmGetHostRamUtilization() {
        final Host host = createHostSimple(ID, CORES);
        final VmSimple vm = createVmForProvisioning();
        assertTrue(host.createVm(vm).fully());
        vm.getRam().occupy(RAM / 4);

        assertEquals(0.25, vm.getHostRamUtilization());
    }

    /// Checks the BW a VM uses relative to its Host capacity:
    /// the VM has half of the Host BW and uses half of it, so it uses a quarter of the Host BW.
    @Test
    public void testVmGetHostBwUtilization() {
        final Host host = createHostSimple(ID, CORES);
        final VmSimple vm = createVmForProvisioning();
        assertTrue(host.createVm(vm).fully());
        vm.getBw().occupy(BW / 4);

        assertEquals(0.25, vm.getHostBwUtilization());
    }

    /// It is expected a mutable list since that list is sorted during VM migration process.
    @Test
    void testGetMigratableVmsReturnsMutableList() {
        final Host host = createHostSimple(0, 2);
        assertDoesNotThrow(() -> host.getMigratableVms().add(Vm.NULL));
    }
}

