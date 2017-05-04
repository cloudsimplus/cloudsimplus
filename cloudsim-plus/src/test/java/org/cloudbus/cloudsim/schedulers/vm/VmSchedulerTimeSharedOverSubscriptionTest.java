/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSchedulerTimeSharedOverSubscriptionTest {
    private static final int HOST_PES = 2;
    private static final double MIPS = 1000;
    private VmSchedulerTimeSharedOverSubscription vmScheduler;
    private Vm vm1;
    private Vm vm2;

    @Before
    public void setUp() throws Exception {
        vmScheduler = createVmScheduler(MIPS, HOST_PES);
        vm1 = VmSimpleTest.createVm(0, MIPS / 4, 1);
        vm2 = VmSimpleTest.createVm(1, MIPS / 2, 2);
    }

    private VmSchedulerTimeSharedOverSubscription createVmScheduler(double mips, int hostPesNumber) {
        final List<Pe> peList = new ArrayList<>(hostPesNumber);
        LongStream.range(0, hostPesNumber).forEach(i -> peList.add(new PeSimple(mips, new PeProvisionerSimple())));
        final Host host = new HostSimple(1000, 1000, 1000, peList);
        final VmSchedulerTimeSharedOverSubscription scheduler = new VmSchedulerTimeSharedOverSubscription();
        scheduler.setHost(host);
        return scheduler;
    }

    @Test
    public void testInit() {
        final List<Pe> peList = vmScheduler.getHost().getWorkingPeList();
        assertEquals(peList, vmScheduler.getWorkingPeList());
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
    }

    @Test
    public void testAllocatePesForVm() {
        final List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(MIPS / 4);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        final List<Pe> peList = vmScheduler.getHost().getPeList();
        assertEquals(PeList.getTotalMips(peList) - MIPS / 4, vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList) - MIPS / 4, vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

        final List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(MIPS / 2);
        mipsShare2.add(MIPS / 8);

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));

        assertEquals(
            PeList.getTotalMips(peList) - MIPS / 4 - MIPS / 2 - MIPS / 8,
            vmScheduler.getAvailableMips(),
            0);
        assertEquals(
            PeList.getTotalMips(peList) - MIPS / 4 - MIPS / 2 - MIPS / 8,
            vmScheduler.getMaxAvailableMips(),
            0);
        assertEquals(MIPS / 2 + MIPS / 8, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(PeList.getTotalMips(peList), vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0);
    }

    @Test
    public void testAllocatePesForVmInMigration() {
        vmScheduler.getHost().addMigratingInVm(vm1);
        vmScheduler.getHost().addMigratingInVm(vm2);
        vm1.setInMigration(true);
        vm2.setInMigration(true);

        final List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(250.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        final List<Pe> peList = vmScheduler.getHost().getPeList();
        assertEquals(1750.0, vmScheduler.getAvailableMips(), 0);
        assertEquals(1750.0, vmScheduler.getMaxAvailableMips(), 0);
        //-10% due to VM migration CPU overhead
        assertEquals(225.0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

        final List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(MIPS / 2);
        mipsShare2.add(MIPS / 8);

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));

        assertEquals(
            PeList.getTotalMips(peList) - 250.0 - MIPS / 2 - MIPS / 8,
            vmScheduler.getAvailableMips(),
            0);
        
        assertEquals(
            PeList.getTotalMips(peList) - 250.0 - MIPS / 2 - MIPS / 8,
            vmScheduler.getMaxAvailableMips(),
            0);
        
        assertEquals(0.9 * MIPS / 2 + 0.9 * MIPS / 8, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(PeList.getTotalMips(peList), vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0);
    }

    @Test
    public void testAllocatePesForVmShortageEqualsToAllocatedMips() {
        final VmSchedulerAbstract vmScheduler = createVmScheduler(3500, 1);
        final Vm vm1 = VmSimpleTest.createVm(0, 170, 1);
        final Vm vm2 = VmSimpleTest.createVm(1, 2000, 1);
        final Vm vm3 = VmSimpleTest.createVm(2, 10, 1);
        final Vm vm4 = VmSimpleTest.createVm(3, 2000, 1);

        final List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(170.0);

        final List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(2000.0);

        final List<Double> mipsShare3 = new ArrayList<>();
        mipsShare3.add(10.0);

        final List<Double> mipsShare4 = new ArrayList<>();
        mipsShare4.add(2000.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        assertEquals(3330, vmScheduler.getAvailableMips(), 0);
        assertEquals(170, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));
        assertEquals(1330, vmScheduler.getAvailableMips(), 0);
        assertEquals(2000, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0);

        assertTrue(vmScheduler.allocatePesForVm(vm3, mipsShare3));
        assertEquals(1320, vmScheduler.getAvailableMips(), 0);
        assertEquals(10, vmScheduler.getTotalAllocatedMipsForVm(vm3), 0);

        assertTrue(vmScheduler.allocatePesForVm(vm4, mipsShare4));
        assertEquals(0, vmScheduler.getAvailableMips(), 0);
        assertEquals(1674, vmScheduler.getTotalAllocatedMipsForVm(vm4), 0.7);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(3500, vmScheduler.getAvailableMips(), 0);
        assertEquals(3500, vmScheduler.getMaxAvailableMips(), 0);
    }

    @Test
    public void testAllocatePesForSameSizedVmsOversubscribed() {
        final VmSchedulerAbstract vmScheduler = createVmScheduler(MIPS, 1);
        final VmSimple vm1 = VmSimpleTest.createVm(0, 1500, 1);
        final VmSimple vm2 = VmSimpleTest.createVm(1, 1000, 1);
        final VmSimple vm3 = VmSimpleTest.createVm(2, 1000, 1);

        final List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(1500.0);

        final List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(1000.0);

        final List<Double> mipsShare3 = new ArrayList<>();
        mipsShare3.add(1000.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        assertEquals(0, vmScheduler.getAvailableMips(), 0);
        assertEquals(1000, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));
        assertEquals(0, vmScheduler.getAvailableMips(), 0);
        assertEquals(500, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
        assertEquals(500, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0);

        assertTrue(vmScheduler.allocatePesForVm(vm3, mipsShare3));
        assertEquals(0, vmScheduler.getAvailableMips(), 0);
        assertEquals(333, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0.4);
        assertEquals(333, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0.4);
        assertEquals(333, vmScheduler.getTotalAllocatedMipsForVm(vm3), 0.4);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(1000, vmScheduler.getAvailableMips(), 0);
        assertEquals(1000, vmScheduler.getMaxAvailableMips(), 0);
    }
}
