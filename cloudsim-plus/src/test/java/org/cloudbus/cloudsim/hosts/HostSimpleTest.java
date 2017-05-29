/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

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
    private static final double MIPS = 1000;

    private HostSimple host;

    public static HostSimple createHostSimple(final int hostId, final int numberOfPes) {
        return createHostSimple(
                hostId, numberOfPes, MIPS, RAM, BW, STORAGE);
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

    public static HostSimple createHostSimple(final int hostId,
            final int numberOfPes, VmScheduler vmScheduler) {
        final List<Pe> peList = createPes(numberOfPes, MIPS);

        final HostSimple host = new HostSimple(RAM, BW, STORAGE, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(vmScheduler);
        return host;

    }

    public static final List<Pe> createPes(final int numberOfPes, final double mips) {
        final List<Pe> peList = new ArrayList<>(numberOfPes);
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return peList;
    }

    @Before
    public void setUp() throws Exception {
        host = createHostSimple(ID, 2);
    }

    @Test
    public void testReallocateMigratingInVms_allVmsAllocatedToTheHost() {
        final int numberOfVms = 4;
        final Host host = createHostSimple(0, numberOfVms);

        final List<Vm> vms = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> {
            Vm vm = VmSimpleTest.createVm(
                    i, MIPS/numberOfVms, 1, RAM/numberOfVms, BW/numberOfVms, STORAGE/numberOfVms,
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
    public void testReallocateMigratingInVms_oneVmAlreadyAllocatedToTheHost() {
        final int numberOfVms = 4;
        final HostSimple host = createHostSimple(0, numberOfVms);

        final List<Vm> vms = new ArrayList<>(numberOfVms);
        IntStream.range(0, numberOfVms).forEach(i -> {
            Vm vm = VmSimpleTest.createVm(
                    i, MIPS/numberOfVms, 1, RAM/numberOfVms, BW/numberOfVms, STORAGE/numberOfVms);
            if(i == 0){
                /*considers that one of the migrating in VMs already was placed at the host,
                thus, it will not be added again to the host vm list.
                By this way, the vms on the host list will be the same
                added to migration list*/
                host.addVmToList(vm);
            }
            host.addMigratingInVm(vm);
            vms.add(vm);
        });


        host.reallocateMigratingInVms();
        final List<Vm> result = host.getVmList();
        assertEquals(vms.size(), result.size());
        assertTrue(vms.containsAll(result));
    }

    @Test
    public void testAddMigratingInVm_checkVmWasChangedToInMigration() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final VmSimple vm = VmSimpleTest.createVm(
                0, MIPS, numberOfPes, RAM, BW, STORAGE,
                new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        host.addMigratingInVm(vm);

        //try to add the already added VM
        host.addMigratingInVm(vm);
        assertTrue(vm.isInMigration());
    }

    @Test
    public void testAddMigratingInVm_checkAvailableMipsAndStorage() {
        final int numberOfPes = 1;
        final Host targetHost = createHostSimple(0, numberOfPes);
        final double VM_MIPS = 500;
        final VmSimple vm = VmSimpleTest.createVm(
            0, VM_MIPS, numberOfPes, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared());
        assertEquals(MIPS, targetHost.getAvailableMips(), 0);
        assertTrue(targetHost.addMigratingInVm(vm));
        final double availableMips = VM_MIPS;
        assertEquals(availableMips, targetHost.getAvailableMips(), 0);
        assertEquals(0, targetHost.getAvailableStorage(), 0);
    }

    @Test
    public void testAddMigratingInVm_checkAllocatedMips() {
        final int numberOfPes = 1;
        final Host targetHost = createHostSimple(0, numberOfPes);
        final double VM_MIPS = 500;
        final VmSimple vm = VmSimpleTest.createVm(
            0, VM_MIPS, numberOfPes, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared());
        targetHost.addMigratingInVm(vm);
        //During migration, just  10% of capacity is allocated (it's the migration overhead)
        final double allocatedMips = 50;
        assertEquals(allocatedMips, targetHost.getTotalAllocatedMipsForVm(vm), 0);
    }

    public void testAddMigratingInVm_lackOfRam() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmSimpleTest.createVm(
            0, MIPS, numberOfPes, RAM * 2,
            BW, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    public void testAddMigratingInVm_lackOfStorage() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE * 2, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    public void testAddMigratingInVm_lackOfBw() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW * 2, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    public void testAddMigratingInVm_lackOfMips() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final Vm vm = VmSimpleTest.createVm(0, MIPS * 2, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.addMigratingInVm(vm));
    }

    @Test
    public void testRemoveMigratingInVm() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        final VmSimple vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        host.addMigratingInVm(vm);
        host.removeMigratingInVm(vm);
        assertFalse(vm.isInMigration());
        assertFalse(host.getVmsMigratingIn().contains(vm));
    }

    @Test
    public void testIsSuitableForVm() {
        final VmSimple vm0 = VmSimpleTest.createVm(0, MIPS, 2, RAM, BW, HALF_STORAGE, new CloudletSchedulerTimeShared());
        final VmSimple vm1 = VmSimpleTest.createVm(1, MIPS * 2, 1, RAM * 2, BW * 2, HALF_STORAGE, new CloudletSchedulerTimeShared());

        assertTrue(host.isSuitableForVm(vm0));
        assertFalse(host.isSuitableForVm(vm1));
    }

    @Test
    public void testUpdateVmProcessing() {
        final int numberOfVms = 4;
        final List<Vm> vmList = new ArrayList<>(numberOfVms);

        final List<Double> mipsShare = new ArrayList<>(1);
        mipsShare.add(MIPS / numberOfVms);
        final double time = 0;

        IntStream.range(0, numberOfVms).forEach(i -> {
            double nextCloudletCompletionTimeOfCurrentVm = i+1;

            Vm vm = EasyMock.createMock(Vm.class);
            EasyMock.expect(vm.updateProcessing(time, mipsShare))
                    .andReturn(nextCloudletCompletionTimeOfCurrentVm)
                    .times(1);
            EasyMock.replay(vm);

            vmList.add(vm);
        });

        final VmScheduler vmScheduler = EasyMock.createMock(VmScheduler.class);
        EasyMock.expect(vmScheduler.getAllocatedMips(EasyMock.anyObject()))
                .andReturn(mipsShare)
                .times(numberOfVms);
        EasyMock.expect(vmScheduler.setHost(EasyMock.anyObject()))
            .andReturn(vmScheduler)
            .once();
        EasyMock.replay(vmScheduler);

        final HostSimple host = createHostSimple(0, numberOfVms, vmScheduler);
        vmList.stream().forEach(host::addVmToList);

        final int i = 0;
        final Vm vm = vmList.get(i);
        final double nextCloudletCompletionTimeOfCurrentVm = i+1;
        assertEquals(
                nextCloudletCompletionTimeOfCurrentVm,
                host.updateProcessing(time), 0);
        EasyMock.verify(vm);

        EasyMock.verify(vmScheduler);
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
        final VmSimple vm0 = VmSimpleTest.createVm(0, MIPS / 2, 1, RAM / 2, BW / 2,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertTrue(host.createVm(vm0));

        final VmSimple vm1 = VmSimpleTest.createVm(1, MIPS, 1, RAM, BW,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.createVm(vm1));

        final VmSimple vm2 = VmSimpleTest.createVm(2, MIPS * 2, 1, RAM, BW,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertFalse(host.createVm(vm2));

        final VmSimple vm3 = VmSimpleTest.createVm(3, MIPS / 2, 2, RAM / 2, BW / 2,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        assertTrue(host.createVm(vm3));
    }

    @Test
    public void testVmCreate_unavailableStorageSpace() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS, 1, RAM, BW, STORAGE*2,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm));
    }

    @Test
    public void testVmCreate_unavailableBw() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS, 1, RAM, BW*2, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm));
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
    public void testAllocatePesForVm() {
        final int numberOfPes = 4;
        final Host host = createHostSimple(0, numberOfPes);
        final List<Double> mipsShare = new ArrayList<>(1);
        mipsShare.add(MIPS);
        final Vm vm = new VmSimple(1000, 1);
        assertTrue(host.allocatePesForVm(vm, mipsShare));
        assertEquals(mipsShare, host.getAllocatedMipsForVm(vm));
        host.deallocatePesForVm(vm);
        assertTrue(host.getAllocatedMipsForVm(vm).isEmpty());
    }

    @Test
    public void testGetTotalAllocatedMipsForVm() {
        final int numberOfPes = 4;
        final Host host = createHostSimple(0, numberOfPes);
        final List<Double> mipsShare = new ArrayList<>(2);
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        final Vm vm = Vm.NULL;
        host.allocatePesForVm(vm, mipsShare);
        assertEquals(MIPS*mipsShare.size(), host.getTotalAllocatedMipsForVm(vm), 0);
    }

    @Test
    public void testGetMaxAvailableMips() {
        final int numberOfPes = 3;
        final Host host = createHostSimple(0, numberOfPes);
        final List<Double> mipsShare = new ArrayList<>(2);
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        final Vm vm = Vm.NULL;
        host.allocatePesForVm(vm, mipsShare);
        assertEquals(MIPS, host.getMaxAvailableMips(), 0);
    }

    @Test
    public void testGetNumberOfFreePes() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        assertEquals(numberOfPes, host.getNumberOfFreePes());
    }

    @Test
    public void testGetNumberOfFreePes_oneBusyPes() {
        final int numberOfPes = 2;
        final Host host = createHostSimple(0, numberOfPes);
        host.getPeList().get(0).setStatus(Pe.Status.BUSY);
        assertEquals(numberOfPes-1, host.getNumberOfFreePes());
    }

    @Test
    public void testGetNumberOfFreePes_noFreePes() {
        final int numberOfPes = 4;
        final Host host = createHostSimple(0, numberOfPes);

        host.getPeList().forEach(pe -> pe.setStatus(Pe.Status.BUSY));
        assertEquals(0, host.getNumberOfFreePes());
    }

    @Test
    public void testVmCreate_unavailableRam() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS, 1, RAM*2, BW, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm));
    }

    @Test
    public void testVmCreate_unavailableMips() {
        final Host host = createHostSimple(0, 1);
        final VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS*2, 1, RAM, BW, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.createVm(vm));
    }

    @Test
    public void testVmDestroy() {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).times(2));
        final DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        final VmSimple vm = VmSimpleTest.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, STORAGE,
                new CloudletSchedulerTimeShared());
        vm.setBroker(broker);

        assertTrue(host.createVm(vm));
        assertSame(vm, host.getVm(0, 0));
        assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

        host.destroyVm(vm);
        assertSame(Vm.NULL, host.getVm(0, 0));
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }

    @Test
    public void testVmDestroyAll() {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).times(2));
        final DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        final VmSimple vm0 = VmSimpleTest.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE,
                new CloudletSchedulerTimeShared());
        vm0.setBroker(broker);
        VmSimple vm1 = VmSimpleTest.createVm(
                1, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE,
                new CloudletSchedulerTimeShared());
        vm1.setBroker(broker);

        assertTrue(host.createVm(vm0));
        assertSame(vm0, host.getVm(0, 0));
        assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

        assertTrue(host.createVm(vm1));
        assertSame(vm1, host.getVm(1, 0));
        assertEquals(0, host.getVmScheduler().getAvailableMips(), 0);

        host.destroyAllVms();
        assertSame(Vm.NULL, host.getVm(0, 0));
        assertSame(Vm.NULL, host.getVm(1, 0));
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }
}
