/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSchedulerTimeSharedOverSubscriptionTest {
    private static final int    HOST_PES = 2;
    private static final double MIPS = 1000;
    private static final long   RAM = 2048;
    private static final long   BANDWIDTH = 20000;
    private static final long   STORAGE = 2000;

    private VmSchedulerTimeSharedOverSubscription vmScheduler;
    private Vm vm1;
    private Vm vm2;

    @BeforeEach
    public void setUp() throws Exception {
        vmScheduler = createVmScheduler(MIPS, HOST_PES);
        vm1 = VmTestUtil.createVm(0, MIPS / 4, 1);
        vm2 = VmTestUtil.createVm(1, MIPS / 2, 2);
    }

    private VmSchedulerTimeSharedOverSubscription createVmScheduler(double mips, int hostPesNumber) {
        final VmSchedulerTimeSharedOverSubscription scheduler = new VmSchedulerTimeSharedOverSubscription();
        final List<Pe> peList = LongStream.range(0, hostPesNumber)
                                          .mapToObj(idx -> new PeSimple(mips, new PeProvisionerSimple()))
                                          .collect(toList());
        final Host host = new HostSimple(RAM, BANDWIDTH, STORAGE, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(scheduler);
        host.setId(0);
        return scheduler;
    }

    @Test
    public void testInit() {
        final List<Pe> peList = vmScheduler.getHost().getWorkingPeList();
        assertEquals(peList, vmScheduler.getWorkingPeList());
        assertEquals(2000, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocatePesForVm() {
        final List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(250.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        assertEquals(1750, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm1));

        final List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(500.0);
        mipsShare2.add(125.0);

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));

        assertEquals(1125.0, vmScheduler.getAvailableMips());
        assertEquals(875.0, vmScheduler.getMaxAvailableMips());
        assertEquals(625.0, vmScheduler.getTotalAllocatedMipsForVm(vm2));

        vmScheduler.deallocatePesForAllVms();

        assertEquals(2000, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm2));
    }

    @Test
    public void testAllocatePesForVmInMigration() {
        vmScheduler.getHost().addMigratingInVm(vm1);
        vmScheduler.getHost().addMigratingInVm(vm2);
        vm1.setInMigration(true);
        vm2.setInMigration(true);

        assertEquals(750.0, vmScheduler.getAvailableMips());
        //During migration, just  10% of capacity is allocated (it's the migration overhead)
        assertEquals(25.0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
        assertEquals(100.0, vmScheduler.getTotalAllocatedMipsForVm(vm2));

        vmScheduler.deallocatePesForAllVms();

        assertEquals(2000, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm2));
    }

    @Test
    public void testAllocatePesForVmShortageEqualsToAllocatedMips() {
        final VmScheduler vmScheduler = createVmScheduler(3500, 1);
        final Vm vm1 = VmTestUtil.createVm(0, 170, 1);
        final Vm vm2 = VmTestUtil.createVm(1, 2000, 1);
        final Vm vm3 = VmTestUtil.createVm(2, 10, 1);
        final Vm vm4 = VmTestUtil.createVm(3, 2000, 1);

        final List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(170.0);

        final List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(2000.0);

        final List<Double> mipsShare3 = new ArrayList<>();
        mipsShare3.add(10.0);

        final List<Double> mipsShare4 = new ArrayList<>();
        mipsShare4.add(2000.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        assertEquals(3330, vmScheduler.getAvailableMips());
        assertEquals(170, vmScheduler.getTotalAllocatedMipsForVm(vm1));

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));
        assertEquals(1330, vmScheduler.getAvailableMips());
        assertEquals(2000, vmScheduler.getTotalAllocatedMipsForVm(vm2));

        assertTrue(vmScheduler.allocatePesForVm(vm3, mipsShare3));
        assertEquals(1320, vmScheduler.getAvailableMips());
        assertEquals(10, vmScheduler.getTotalAllocatedMipsForVm(vm3));

        assertTrue(vmScheduler.allocatePesForVm(vm4, mipsShare4));
        assertEquals(0, vmScheduler.getAvailableMips());
        assertEquals(1674, vmScheduler.getTotalAllocatedMipsForVm(vm4), 0.7);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(3500, vmScheduler.getAvailableMips());
        assertEquals(3500, vmScheduler.getMaxAvailableMips());
    }

    @Test
    public void testAllocatePesForSameSizedVmsOversubscribed() {
        final VmScheduler vmScheduler = createVmScheduler(MIPS, 1);
        final VmSimple vm1 = VmTestUtil.createVm(0, 1500, 1);
        final VmSimple vm2 = VmTestUtil.createVm(1, 1000, 1);
        final VmSimple vm3 = VmTestUtil.createVm(2, 1000, 1);

        final List<Double> mipsShare1 = new ArrayList<>(1);
        mipsShare1.add(1500.0);

        final List<Double> mipsShare2 = new ArrayList<>(1);
        mipsShare2.add(1000.0);

        final List<Double> mipsShare3 = new ArrayList<>(1);
        mipsShare3.add(1000.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare1));
        assertEquals(0, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getTotalAllocatedMipsForVm(vm1));

        assertTrue(vmScheduler.allocatePesForVm(vm2, mipsShare2));
        assertEquals(0, vmScheduler.getAvailableMips());
        assertEquals(500, vmScheduler.getTotalAllocatedMipsForVm(vm1));
        assertEquals(500, vmScheduler.getTotalAllocatedMipsForVm(vm2));

        assertTrue(vmScheduler.allocatePesForVm(vm3, mipsShare3));
        assertEquals(0, vmScheduler.getAvailableMips());
        assertEquals(333, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0.4);
        assertEquals(333, vmScheduler.getTotalAllocatedMipsForVm(vm2), 0.4);
        assertEquals(333, vmScheduler.getTotalAllocatedMipsForVm(vm3), 0.4);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(1000, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
    }
}
