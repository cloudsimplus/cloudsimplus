/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerDynamicWorkload;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.listeners.EventListener;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class HostSimpleTest {
    private static final int ID = 0;
    private static final long STORAGE = Consts.MILLION;
    private static final long HALF_STORAGE = STORAGE / 2;
    private static final long A_QUARTER_STORAGE = STORAGE / 4;
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final double MIPS = 1000;

    private HostSimple host;

    public static HostSimple createHostSimple(final int hostId, final int numberOfPes) {
        return createHostSimple(hostId, numberOfPes, MIPS, RAM, BW, STORAGE);
    }

    public static HostSimple createHostSimple(
            final int hostId, final int numberOfPes,
            final double mips, final int ram, 
            final long bw, final long storage) {
        List<Pe> peList = createPes(numberOfPes, mips);

        return new HostSimple(hostId,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, peList, new VmSchedulerTimeShared(peList));
    }

    public static final List<Pe> createPes(final int numberOfPes, final double mips) {
        final List<Pe> peList = new ArrayList<>(numberOfPes);
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        return peList;
    }

    @Before
    public void setUp() throws Exception {
        host = createHostSimple(ID, 2);
    }

    @Test
    public void testAddMigratingInVm() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        VmSimple vm = VmSimpleTest.createVm(
                0, MIPS, numberOfPes, RAM, BW, STORAGE, 
                new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        try {
            host.addMigratingInVm(vm);
        } catch (Exception e) {
            fail("It was expected the VM to be added to the migratingIn list. An exception shouldn't be raised.");
        }

        try {
            //try to add the already added VM
            host.addMigratingInVm(vm);
        } catch (Exception e) {
            fail("An exception shouldn't be raised when trying to add the same VM already added to the migratinInList. In this case, the method has to just not perform any action.");
        }
        assertTrue(vm.isInMigration());
    }

    @Test(expected = RuntimeException.class)
    public void testAddMigratingInVm_lackOfRam() {
        final int numberOfPes = 2;
        Host host = createHostSimple(0, numberOfPes);
        Vm vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM * 2, BW, STORAGE, new CloudletSchedulerTimeShared());
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
    public void testReallocateMigratingInVms() {
        final int numberOfPes = 1;
        Host host = createHostSimple(0, numberOfPes);
        VmSimple vm = VmSimpleTest.createVm(0, MIPS, numberOfPes, RAM, BW, STORAGE, new CloudletSchedulerTimeShared());
        vm.setHost(Host.NULL);
        vm.setBeingInstantiated(true);
        assertEquals(MIPS, host.getAvailableMips(), 0);
        host.addMigratingInVm(vm);
        final double availableMips = MIPS
                - (MIPS * host.getVmScheduler().getCpuOverheadDueToVmMigration());
        assertEquals(availableMips, host.getAvailableMips(), 0);
        assertEquals(0, host.getAvailableStorage(), 0);
    }

    @Test
    public void testIsSuitableForVm() {
        VmSimple vm0 = VmSimpleTest.createVm(0, MIPS, 2, RAM, BW, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 2));
        VmSimple vm1 = VmSimpleTest.createVm(1, MIPS * 2, 1, RAM * 2, BW * 2, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS * 2, 2));

        assertTrue(host.isSuitableForVm(vm0));
        assertFalse(host.isSuitableForVm(vm1));
    }

    @Test
    public void testSetOnUpdateVmsProcessingListener() {
        Host host = createHostSimple(0, 1);
        host.setOnUpdateVmsProcessingListener(null);
        assertEquals(EventListener.NULL, host.getOnUpdateVmsProcessingListener());
        EventListener<Host, Double> listener = (t,h,nt)->{};
        host.setOnUpdateVmsProcessingListener(listener);
        assertEquals(listener, host.getOnUpdateVmsProcessingListener());
    }

    @Test
    public void testVmCreate() {
        VmSimple vm0 = VmSimpleTest.createVm(0, MIPS / 2, 1, RAM / 2, BW / 2, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS / 2, 1));
        VmSimple vm1 = VmSimpleTest.createVm(1, MIPS, 1, RAM, BW, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));
        VmSimple vm2 = VmSimpleTest.createVm(2, MIPS * 2, 1, RAM, BW, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS * 2, 1));
        VmSimple vm3 = VmSimpleTest.createVm(3, MIPS / 2, 2, RAM / 2, BW / 2, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS / 2, 2));

        assertTrue(host.vmCreate(vm0));
        assertFalse(host.vmCreate(vm1));
        assertFalse(host.vmCreate(vm2));
        assertTrue(host.vmCreate(vm3));
    }

    @Test
    public void testVmDestroy() {
        VmSimple vm = VmSimpleTest.createVm(0, MIPS, 1, RAM / 2, BW / 2, STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));

        assertTrue(host.vmCreate(vm));
        assertSame(vm, host.getVm(0, 0));
        assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

        host.vmDestroy(vm);
        assertNull(host.getVm(0, 0));
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }

    @Test
    public void testVmDestroyAll() {
        VmSimple vm0 = VmSimpleTest.createVm(0, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));
        VmSimple vm1 = VmSimpleTest.createVm(1, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));

        assertTrue(host.vmCreate(vm0));
        assertSame(vm0, host.getVm(0, 0));
        assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

        assertTrue(host.vmCreate(vm1));
        assertSame(vm1, host.getVm(1, 0));
        assertEquals(0, host.getVmScheduler().getAvailableMips(), 0);

        host.vmDestroyAll();
        assertNull(host.getVm(0, 0));
        assertNull(host.getVm(1, 0));
        assertEquals(0, host.getVmList().size());
        assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }
}
