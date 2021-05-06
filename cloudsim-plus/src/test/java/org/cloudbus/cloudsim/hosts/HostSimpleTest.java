/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.MocksHelper;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.MipsShare;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class HostSimpleTest {
    private static final int ID = 0;
    private static final long STORAGE = Conversion.MILLION;
    private static final long HALF_STORAGE = STORAGE / 2;
    private static final long A_QUARTER_STORAGE = STORAGE / 4;
    private static final long RAM = 1024;
    private static final long BW = 10000;

    private static final int PES = 2;
    private static final double MIPS = 2000;

    private HostSimple host;

    public static HostSimple createHostSimple(final int hostId, final int numberOfPes) {
        return createHostSimple(hostId, numberOfPes, MIPS, RAM, BW, STORAGE);
    }

    public static HostSimple createHostSimple(
            final int hostId, final int numberOfPes,
            final double mips, final long ram,
            final long bw, final long storage) {
        final List<Pe> peList = createPes(numberOfPes, mips);

        final HostSimple host = new HostSimple(ram, bw, storage, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared())
            .setId(hostId);
        return host;
    }

    private static HostSimple createHostSimple(final int numberOfPes, VmScheduler vmScheduler) {
        final List<Pe> peList = createPes(numberOfPes, MIPS);

        final HostSimple host = new HostSimple(RAM, BW, STORAGE, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(vmScheduler);
        return host;

    }

    private static List<Pe> createPes(final int numberOfPes, final double mips) {
        return IntStream.range(0, numberOfPes)
                        .mapToObj(id -> new PeSimple(mips, new PeProvisionerSimple()))
                        .collect(Collectors.toCollection(() -> new ArrayList<>(numberOfPes)));
    }

    @BeforeEach
    public void setUp() {
        host = createHostSimple(ID, PES);
    }

    @Test
    public void isSuitableForVm_WhenThereIsAvailableStorage(){
        final Vm vm = createVm(PES, MIPS, STORAGE);
        assertTrue(host.isSuitableForVm(vm));
    }

    @Test
    public void isSuitableForVm_WhenThereIsNotAvailableStorage(){
        final Vm vm = createVm(PES, MIPS, STORAGE * 2);
        assertFalse(host.isSuitableForVm(vm));
    }

    @Test
    public void isSuitableForVm_WhenThereIsEnoughPes(){
        host.setVmScheduler(new VmSchedulerSpaceShared());
        final Vm vm = createVm(PES, MIPS, STORAGE);
        assertTrue(host.isSuitableForVm(vm));
    }

    /**
     * If the total HOST_MIPS a VM is requesting is lower or equal to the total available HOST_MIPS
     * from the physical PEs but the number of available PEs is lower then the
     * PEs requested, the test must fail at least for a {@link VmSchedulerSpaceShared}.
     */
    @Test
    public void isSuitableForVm_WhenThereIsNotEnoughPes(){
        host.setVmScheduler(new VmSchedulerSpaceShared());
        final Vm vm = createVm(4, 500, STORAGE);
        assertFalse(host.isSuitableForVm(vm));
    }

    private Vm createVm(final int pes, final double mips, final long storage) {
        final Vm vm = new VmSimple(mips, pes);
        vm.setRam(RAM);
        vm.setBw(BW);
        vm.setSize(storage);
        return vm;
    }

    @Test
    public void testReallocateMigratingInVmsWhenAllVmsAllocatedToTheHost() {
        final int numberOfVms = 4;
        final Host host = createHostSimple(0, numberOfVms);

        final List<Vm> vms = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> {
            final Vm vm = VmTestUtil.createVm(
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

        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(0).anyTimes();
            mocker.getNumEntities().anyTimes();
            mocker.isTerminationTimeSet().once();
            mocker.addEntity().anyTimes();
            mocker.clockStr().anyTimes();
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
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final VmSimple vm = VmTestUtil.createVm(
                0, MIPS, numberOfPes, RAM, BW, STORAGE,
                new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        host.addMigratingInVm(vm);

        //try to add the already added VM
        host.addMigratingInVm(vm);
        assertTrue(vm.isInMigration());
    }

    @Test
    public void testAddMigratingInVmAndCheckAvailableMipsAndStorage() {
        final int numberOfPes = 1;
        final Host targetHost = createHostSimple(0, numberOfPes);
        final double VM_MIPS = 500;
        final VmSimple vm = VmTestUtil.createVm(
            0, VM_MIPS, numberOfPes, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared());
        assertEquals(MIPS, targetHost.getTotalAvailableMips());
        assertTrue(targetHost.addMigratingInVm(vm));
        final double availableMips = MIPS - VM_MIPS;
        assertEquals(availableMips, targetHost.getTotalAvailableMips());
        assertEquals(0, targetHost.getAvailableStorage());
    }

    @Test
    public void testAddMigratingInVmAndCheckAllocatedMips() {
        final int numberOfPes = 1;
        final Host targetHost = createHostSimple(0, numberOfPes);
        final double VM_MIPS = 500;
        final VmSimple vm = VmTestUtil.createVm(
            0, VM_MIPS, numberOfPes, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared());
        targetHost.addMigratingInVm(vm);
        //During migration, just  10% of capacity is allocated (it's the migration overhead)
        final double allocatedMips = 50;
        assertEquals(allocatedMips, targetHost.getTotalAllocatedMipsForVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackRam() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmTestUtil.createVm(
            0, MIPS, numberOfPes, RAM * 2,
            BW, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackStorage() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmTestUtil.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE * 2, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackBw() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmTestUtil.createVm(0, MIPS, numberOfPes, RAM, BW * 2, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testAddMigratingInVmWhenLackMips() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmTestUtil.createVm(0, MIPS * 2, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testRemoveMigratingInVm() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final VmSimple vm = VmTestUtil.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
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

        final MipsShare mipsShare = new MipsShare(MIPS / numberOfVms);
        final double time = 0;

        final List<Vm> vmList = createListOfMockVms(numberOfVms, mipsShare, time);

        final VmScheduler vmScheduler = EasyMock.createMock(VmScheduler.class);
        final HostSimple host = createHostSimple(numberOfVms, VmScheduler.NULL);

        EasyMock.expect(vmScheduler.getAllocatedMips(EasyMock.anyObject()))
                .andReturn(mipsShare)
                .times(numberOfVms);
        EasyMock.expect(vmScheduler.setHost(EasyMock.anyObject()))
            .andReturn(vmScheduler)
            .once();
        EasyMock.expect(vmScheduler.getHost()).andReturn(host).anyTimes();

        EasyMock.replay(vmScheduler);
        host.setVmScheduler(vmScheduler);

        vmList.forEach(host::addVmToList);

        final int idx = 0;
        final Vm vm = vmList.get(idx);
        final double nextCloudletCompletionTimeOfCurrentVm = idx+1;
        assertEquals(
                nextCloudletCompletionTimeOfCurrentVm,
                host.updateProcessing(time));
        EasyMock.verify(vm);

        EasyMock.verify(vmScheduler);
    }

    private List<Vm> createListOfMockVms(
        final int numberOfVms, final MipsShare mipsShare,
        final double simulationClock)
    {
        final List<Vm> vmList = new ArrayList<>(numberOfVms);
        for(int i = 0; i < numberOfVms; i++) {
            final double nextCloudletCompletionTimeOfCurrentVm = i+1;

            final Vm vm = EasyMock.createMock(Vm.class);
            EasyMock
                    .expect(vm.updateProcessing(simulationClock, mipsShare))
                    .andReturn(nextCloudletCompletionTimeOfCurrentVm)
                    .times(1);
            EasyMock.replay(vm);

            vmList.add(vm);
        }

        return vmList;
    }

    @Test
    public void testSetOnUpdateVmsProcessingListener() {
        final Host host = createHostSimple(0, 1);

        final EventListener<HostUpdatesVmsProcessingEventInfo> updateVmsProcessing = e -> {};
        host.addOnUpdateProcessingListener(updateVmsProcessing);
        assertTrue(host.removeOnUpdateProcessingListener(updateVmsProcessing));

        host.addOnUpdateProcessingListener(e -> {});
        assertFalse(host.removeOnUpdateProcessingListener(null));
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
    public void testGetNumberOfPes() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        assertEquals(numberOfPes, host.getNumberOfPes());
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
    public void testGetNumberOfFreePes() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        assertEquals(numberOfPes, host.getFreePesNumber());
    }

    @Test
    public void testGetNumberOfFreePesWhenOneBusyPes() {
        final int numberOfPes = 2;
        final HostSimple host = createHostSimple(0, numberOfPes);
        host.setPeStatus(host.getPeList().subList(0,1), Pe.Status.BUSY);
        assertEquals(numberOfPes-1, host.getFreePesNumber());
    }

    @Test
    public void testGetNumberOfFreePesWhenNoFreePes() {
        final int numberOfPes = 4;
        final HostSimple host = createHostSimple(0, numberOfPes);

        host.setPeStatus(host.getPeList(), Pe.Status.BUSY);
        assertEquals(0, host.getFreePesNumber());
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
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).times(2));
        final VmSimple vm = VmTestUtil.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, STORAGE,
                new CloudletSchedulerTimeShared());
        final List<Vm> vmExecList = new ArrayList<>(1);
        vmExecList.add(vm);
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim, b -> EasyMock.expect(b.getVmExecList()).andReturn(vmExecList));
        vm.setBroker(broker);

        assertTrue(host.createVm(vm).fully());
        assertEquals(MIPS, host.getVmScheduler().getTotalAvailableMips());

        host.destroyVm(vm);
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getTotalAvailableMips());
    }

    @Test
    public void testVmDestroyAll() {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).times(2));
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
        final CloudSim cloudsim = new CloudSim();

        final DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);
        final Vm vm = VmTestUtil.createVm(0, 2);
        vm.setBroker(broker);
        host.createVm(vm);
        vm.setCreated(true);
        broker.getVmExecList().add(vm);
        host.destroyVm(vm);
        assertFalse(
            broker.getVmExecList().contains(vm),
            vm + " was destroyed into the Host but was not removed from the broker's VM exec List.");
    }

    @Test
    public void testGetRelativeRamUtilization() {
        final Host host = createHostSimple(0, 2);
        //Considers the VM has half of the host RAM as capacity and is using half of that capacity
        final Vm vm = createMockVm(new Ram(RAM/2), Vm::getRam, RAM/4);

        //This way, the VM is using a quarter of the Host capacity
        final double expected = 0.25;
        final double actual = host.getRelativeRamUtilization(vm);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetRelativeBwUtilization() {
        final Host host = createHostSimple(0, 2);
        //Considers the VM has half of the host BW as capacity and is using half of that capacity
        final Vm vm = createMockVm(new Bandwidth(BW/2), Vm::getBw, BW/4);

        //This way, the VM is using a quarter of the Host capacity
        final double expected = 0.25;
        final double actual = host.getRelativeBwUtilization(vm);
        assertEquals(expected, actual);
    }

    /**
     * Creates a mock VM to test the utilization of some resource
     * @param vmResource a new instance of a resource to be attached to the VM and to be tested
     * @param vmResourceFunction the VM function that is able to get the provided resource stored on the VM
     * @param usedResource the amount of resource to be allocated from the total capacity
     * @return
     */
    private Vm createMockVm(final ResourceManageable vmResource, final Function<Vm, Resource> vmResourceFunction, final long usedResource){
        final Vm vm = EasyMock.createMock(Vm.class);
        vmResource.allocateResource(usedResource);

        /* When the given VM resource function is called during tests,
        * it must return the provided resource instance. */
        EasyMock.expect(vmResourceFunction.apply(vm)).andReturn(vmResource).once();
        EasyMock.expect(vm.getResources()).andReturn(Collections.singletonList(vmResource)).once();
        EasyMock.replay(vm);

        return vm;
    }

}

