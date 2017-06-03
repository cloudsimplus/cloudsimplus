/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class HostDynamicWorkloadTest {
    private static final int ID = 0;
    private static final long STORAGE = Conversion.MILLION;
    private static final long RAM = 1024;
    private static final long BW = 10000;
    private static final double MIPS = 1000;

    private static final long ONE_THIRD_MIPS = (long)(MIPS / 3.0);
    private static final long ONE_FIFTH_MIPS = (long)(MIPS / 5.0);

    private HostDynamicWorkloadSimple host;
    private List<Pe> peList;

    @Before
    public void setUp() throws Exception {
        peList = new ArrayList<>();
        peList.add(new PeSimple(MIPS, new PeProvisionerSimple()));
        peList.add(new PeSimple(MIPS, new PeProvisionerSimple()));

        host = new HostDynamicWorkloadSimple(RAM, BW, STORAGE, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    @Test
    public void testGetMaxUtilizationOneVm() {
        Vm vm0 = VmSimpleTest.createVmWithOnePeAndHalfMips(0);

        assertTrue(peList.get(0).getPeProvisioner().allocateResourceForVm(vm0, ONE_THIRD_MIPS));
        assertEquals((ONE_THIRD_MIPS) / MIPS, host.getMaxUtilization(), 0.001);
    }

    @Test
    public void testGetMaxUtilization() {
        Vm vm0 = VmSimpleTest.createVmWithOnePeAndHalfMips(0);
        Vm vm1 = VmSimpleTest.createVmWithOnePeAndHalfMips(1);

        assertTrue(peList.get(0).getPeProvisioner().allocateResourceForVm(vm0, ONE_THIRD_MIPS));
        assertTrue(peList.get(1).getPeProvisioner().allocateResourceForVm(vm1, ONE_FIFTH_MIPS));

        assertEquals(ONE_THIRD_MIPS / MIPS, host.getMaxUtilization(), 0.001);
    }

    @Test
    public void testGetUtilizationOfCPU() {
        assertEquals(0.0, host.getUtilizationOfCpu(), 0);
    }

    @Test
    public void testGetUtilizationOfCPUMips() {
        assertEquals(0.0, host.getUtilizationOfCpuMips(), 0);
    }

    @Test
    public void testGetUtilizationOfRam() {
        assertEquals(0, host.getUtilizationOfRam());
    }

    @Test
    public void testGetUtilizationOfBW() {
        final long usedBw = host.getUtilizationOfBw();
        assertEquals(0L, usedBw);
    }

    @Test
    public void testGetMaxUtilizationAmongVmsPes() {
        Vm vm0 = VmSimpleTest.createVmWithOnePeAndHalfMips(0);
        Vm vm1 = VmSimpleTest.createVmWithOnePeAndHalfMips(1);

        assertTrue(peList.get(0).getPeProvisioner().allocateResourceForVm(vm0, ONE_THIRD_MIPS));
        assertTrue(peList.get(1).getPeProvisioner().allocateResourceForVm(vm1, ONE_FIFTH_MIPS));

        assertEquals((ONE_THIRD_MIPS) / MIPS, host.getMaxUtilizationAmongVmsPes(vm0), 0.001);
        assertEquals((ONE_FIFTH_MIPS) / MIPS, host.getMaxUtilizationAmongVmsPes(vm1), 0.001);
    }
}
