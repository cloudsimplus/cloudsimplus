/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
        List<Pe> peList = createPes(numberOfPes, mips);

        HostSimple host = new HostSimple(hostId, storage, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    public static HostSimple createHostSimple(final int hostId,
            final int numberOfPes, VmScheduler vmScheduler) {
        List<Pe> peList = createPes(numberOfPes, MIPS);

        HostSimple host = new HostSimple(hostId, STORAGE, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple(new Ram(RAM)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(RAM)))
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
        Host host = createHostSimple(0, numberOfVms);

        List<Vm> vms = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> {
            Vm vm = VmSimpleTest.createVm(
                    i, MIPS/numberOfVms, 1, RAM/numberOfVms, BW/numberOfVms, STORAGE/numberOfVms,
                    new CloudletSchedulerTimeShared());
            vm.setHost(Host.NULL);
            host.addMigratingInVm(vm);
            vms.add(vm);
        });

        host.reallocateMigratingInVms();
        assertEquals(vms, host.getVmList());
    }

    @Test
    public void testReallocateMigratingInVms_oneVmAlreadyAllocatedToTheHost() {
        final int numberOfVms = 4;
        Host host = createHostSimple(0, numberOfVms);

        List<Vm> vms = new ArrayList<>();
        IntStream.range(0, numberOfVms).forEach(i -> {
            Vm vm = VmSimpleTest.createVm(
                    i, MIPS/numberOfVms, 1, RAM/numberOfVms, BW/numberOfVms, STORAGE/numberOfVms,
                    CloudletScheduler.NULL);
            vm.setHost(Host.NULL);
            if(i == 0){
                /*considers that one of the migrating in VMs already was placed at the host,
                thus, it will not be added again to the host vm list.
                By this way, the vms on the host list will be the same
                added to migration list*/
                host.getVmList().add(vm);
            }
            host.addMigratingInVm(vm);
            vms.add(vm);
        });


        host.reallocateMigratingInVms();
        assertEquals(vms, host.getVmList());
    }

    @Test
    public void testAddMigratingInVm_checkVmWasChangedToInMigration() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        VmSimple vm = VmSimpleTest.createVm(
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
        Host host = createHostSimple(0, numberOfPes);
        VmSimple vm = VmSimpleTest.createVm(
            0, MIPS, numberOfPes, RAM, BW, STORAGE,
            new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        assertEquals(MIPS, host.getAvailableMips(), 0);
        host.addMigratingInVm(vm);
        final double availableMips = MIPS
                - (MIPS * host.getVmScheduler().getCpuOverheadDueToVmMigration());
        assertEquals(availableMips, host.getAvailableMips(), 0);
        assertEquals(0, host.getAvailableStorage(), 0);
    }

    @Test(expected = RuntimeException.class)
    public void testAddMigratingInVm_lackOfRam() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        Vm vm = VmSimpleTest.createVm(
            0, MIPS, numberOfPes, RAM * 2,
            BW, STORAGE, new CloudletSchedulerTimeShared());
        host.addMigratingInVm(vm);
    }

    @Test(expected = RuntimeException.class)
    public void testAddMigratingInVm_lackOfStorage() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        Vm vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE * 2, new CloudletSchedulerTimeShared());
        host.addMigratingInVm(vm);
    }

    @Test(expected = RuntimeException.class)
    public void testAddMigratingInVm_lackOfBw() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        Vm vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW * 2, STORAGE, new CloudletSchedulerTimeShared());
        host.addMigratingInVm(vm);
        assertFalse(vm.isInMigration());
    }

    @Test(expected = RuntimeException.class)
    public void testAddMigratingInVm_lackOfMips() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        Vm vm = VmSimpleTest.createVm(0, MIPS * 2, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        host.addMigratingInVm(vm);
    }

    @Test
    public void testRemoveMigratingInVm() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        VmSimple vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        host.addMigratingInVm(vm);
        host.removeMigratingInVm(vm);
        assertFalse(vm.isInMigration());
        assertFalse(host.getVmsMigratingIn().contains(vm));
    }

    @Test
    public void testIsSuitableForVm() {
        VmSimple vm0 = VmSimpleTest.createVm(0, MIPS, 2, RAM, BW, HALF_STORAGE, new CloudletSchedulerTimeShared());
        VmSimple vm1 = VmSimpleTest.createVm(1, MIPS * 2, 1, RAM * 2, BW * 2, HALF_STORAGE, new CloudletSchedulerTimeShared());

        assertTrue(host.isSuitableForVm(vm0));
        assertFalse(host.isSuitableForVm(vm1));
    }

    @Test
    public void testUpdateVmProcessing() {
        final int numberOfVms = 4;
        List<Vm> vmList = new ArrayList<>(numberOfVms);

        final List<Double> mipsShare = new ArrayList<>(1);
        mipsShare.add(MIPS / numberOfVms);
        final double time = 0;

        IntStream.range(0, numberOfVms).forEach(i -> {
            double nextCloudletCompletionTimeOfCurrentVm = i+1;

            Vm vm = EasyMock.createMock(Vm.class);
            EasyMock.expect(vm.updateVmProcessing(time, mipsShare))
                    .andReturn(nextCloudletCompletionTimeOfCurrentVm)
                    .times(1);
            EasyMock.replay(vm);

            vmList.add(vm);
        });

        VmScheduler vmScheduler = EasyMock.createMock(VmScheduler.class);
        EasyMock.expect(vmScheduler.getAllocatedMipsForVm(EasyMock.anyObject()))
                .andReturn(mipsShare)
                .times(numberOfVms);
        EasyMock.expect(vmScheduler.setHost(EasyMock.anyObject()))
            .andReturn(vmScheduler)
            .once();
        EasyMock.replay(vmScheduler);

        Host host = createHostSimple(0, numberOfVms, vmScheduler);
        host.getVmList().addAll(vmList);

        final int i = 0;
        Vm vm = vmList.get(i);
        final double nextCloudletCompletionTimeOfCurrentVm = i+1;
        assertEquals(
                nextCloudletCompletionTimeOfCurrentVm,
                host.updateVmsProcessing(time), 0);
        EasyMock.verify(vm);

        EasyMock.verify(vmScheduler);
    }

    @Test
    public void testSetOnUpdateVmsProcessingListener() {
        Host host = createHostSimple(0, 1);
        host.setOnUpdateVmsProcessingListener(null);
        assertEquals(EventListener.NULL, host.getOnUpdateVmsProcessingListener());
        EventListener<HostUpdatesVmsProcessingEventInfo> listener = (evt)->{};
        host.setOnUpdateVmsProcessingListener(listener);
        assertEquals(listener, host.getOnUpdateVmsProcessingListener());
    }

    @Test
    public void testVmCreate() {
        VmSimple vm0 = VmSimpleTest.createVm(0, MIPS / 2, 1, RAM / 2, BW / 2,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        VmSimple vm1 = VmSimpleTest.createVm(1, MIPS, 1, RAM, BW,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        VmSimple vm2 = VmSimpleTest.createVm(2, MIPS * 2, 1, RAM, BW,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());
        VmSimple vm3 = VmSimpleTest.createVm(3, MIPS / 2, 2, RAM / 2, BW / 2,
                A_QUARTER_STORAGE, new CloudletSchedulerTimeShared());

        assertTrue(host.vmCreate(vm0));
        assertFalse(host.vmCreate(vm1));
        assertFalse(host.vmCreate(vm2));
        assertTrue(host.vmCreate(vm3));
    }

    @Test
    public void testVmCreate_unavailableStorageSpace() {
        Host host = createHostSimple(0, 1);
        VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS, 1, RAM, BW, STORAGE*2,
                        CloudletScheduler.NULL);
        assertFalse(host.vmCreate(vm));
    }

    @Test
    public void testVmCreate_unavailableBw() {
        Host host = createHostSimple(0, 1);
        VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS, 1, RAM, BW*2, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.vmCreate(vm));
    }

    @Test
    public void testGetNumberOfPes() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
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
        Host host = createHostSimple(0, numberOfPes);
        List<Double> mipsShare = new ArrayList<>(1);
        mipsShare.add(MIPS);
        final Vm vm = Vm.NULL;
        assertTrue(host.allocatePesForVm(vm, mipsShare));
        assertEquals(mipsShare, host.getAllocatedMipsForVm(vm));
        host.deallocatePesForVm(vm);
        assertTrue(host.getAllocatedMipsForVm(vm).isEmpty());
    }

    @Test
    public void testGetTotalAllocatedMipsForVm() {
        final int numberOfPes = 4;
        Host host = createHostSimple(0, numberOfPes);
        List<Double> mipsShare = new ArrayList<>(2);
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        final Vm vm = Vm.NULL;
        host.allocatePesForVm(vm, mipsShare);
        assertEquals(MIPS*mipsShare.size(), host.getTotalAllocatedMipsForVm(vm), 0);
    }

    @Test
    public void testGetMaxAvailableMips() {
        final int numberOfPes = 3;
        Host host = createHostSimple(0, numberOfPes);
        List<Double> mipsShare = new ArrayList<>(2);
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        final Vm vm = Vm.NULL;
        host.allocatePesForVm(vm, mipsShare);
        assertEquals(MIPS, host.getMaxAvailableMips(), 0);
    }

    @Test
    public void testGetNumberOfFreePes() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        assertEquals(numberOfPes, host.getNumberOfFreePes());
    }

    @Test
    public void testGetNumberOfFreePes_oneBusyPes() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        host.getPeList().get(0).setStatus(Pe.Status.BUSY);
        assertEquals(numberOfPes-1, host.getNumberOfFreePes());
    }

    @Test
    public void testGetNumberOfFreePes_noFreePes() {
        final int numberOfPes = 4;
        Host host = createHostSimple(0, numberOfPes);

        host.getPeList().forEach(pe -> pe.setStatus(Pe.Status.BUSY));
        assertEquals(0, host.getNumberOfFreePes());
    }

    @Test
    public void testVmCreate_unavailableRam() {
        Host host = createHostSimple(0, 1);
        VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS, 1, RAM*2, BW, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.vmCreate(vm));
    }

    @Test
    public void testVmCreate_unavailableMips() {
        Host host = createHostSimple(0, 1);
        VmSimple vm =
                VmSimpleTest.createVm(
                        0, MIPS*2, 1, RAM, BW, STORAGE,
                        CloudletScheduler.NULL);
        assertFalse(host.vmCreate(vm));
    }

    @Test
    public void testVmDestroy() {
        CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).times(2));
        DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        VmSimple vm = VmSimpleTest.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, STORAGE,
                new CloudletSchedulerTimeShared());
        vm.setBroker(broker);

        assertTrue(host.vmCreate(vm));
        assertSame(vm, host.getVm(0, 0));
        assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

        host.destroyVm(vm);
        assertSame(Vm.NULL, host.getVm(0, 0));
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }

    @Test
    public void testVmDestroyAll() {
        CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).times(2));
        DatacenterBroker broker = Mocks.createMockBroker(cloudsim);
        VmSimple vm0 = VmSimpleTest.createVm(
                0, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE,
                new CloudletSchedulerTimeShared());
        vm0.setBroker(broker);
        VmSimple vm1 = VmSimpleTest.createVm(
                1, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE,
                new CloudletSchedulerTimeShared());
        vm1.setBroker(broker);

        assertTrue(host.vmCreate(vm0));
        assertSame(vm0, host.getVm(0, 0));
        assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

        assertTrue(host.vmCreate(vm1));
        assertSame(vm1, host.getVm(1, 0));
        assertEquals(0, host.getVmScheduler().getAvailableMips(), 0);

        host.destroyAllVms();
        assertSame(Vm.NULL, host.getVm(0, 0));
        assertSame(Vm.NULL, host.getVm(1, 0));
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }
}
