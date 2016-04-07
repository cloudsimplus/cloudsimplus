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

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
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
    private static final long STORAGE = Consts.MILLION;
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final double MIPS = 1000;

    private HostDynamicWorkloadSimple host;
    private List<Pe> peList;

    @Before
    public void setUp() throws Exception {
        peList = new ArrayList<>();
        peList.add(new PeSimple(0, new PeProvisionerSimple(MIPS)));
        peList.add(new PeSimple(1, new PeProvisionerSimple(MIPS)));
        host = new HostDynamicWorkloadSimple(
                ID,
                new ResourceProvisionerSimple<>(new Ram(RAM)),
                new ResourceProvisionerSimple<>(new Bandwidth(BW)),
                STORAGE,
                peList,
                new VmSchedulerTimeShared(peList)
        );
    }

    @Test
    public void testGetMaxUtilizationOneVm() {
        Vm vm0 = VmSimpleTest.createVmWithOnePeAndHalfMips(0);

        assertTrue(peList.get(0).getPeProvisioner().allocateMipsForVm(vm0, MIPS / 3));
        assertEquals((MIPS / 3) / MIPS, host.getMaxUtilization(), 0.001);
    }

    @Test
    public void testGetMaxUtilization() {
        Vm vm0 = VmSimpleTest.createVmWithOnePeAndHalfMips(0);
        Vm vm1 = VmSimpleTest.createVmWithOnePeAndHalfMips(1);

        assertTrue(peList.get(0).getPeProvisioner().allocateMipsForVm(vm0, MIPS / 3));
        assertTrue(peList.get(1).getPeProvisioner().allocateMipsForVm(vm1, MIPS / 5));

        assertEquals((MIPS / 3) / MIPS, host.getMaxUtilization(), 0.001);
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

        assertTrue(peList.get(0).getPeProvisioner().allocateMipsForVm(vm0, MIPS / 3));
        assertTrue(peList.get(1).getPeProvisioner().allocateMipsForVm(vm1, MIPS / 5));

        assertEquals((MIPS / 3) / MIPS, host.getMaxUtilizationAmongVmsPes(vm0), 0.001);
        assertEquals((MIPS / 5) / MIPS, host.getMaxUtilizationAmongVmsPes(vm1), 0.001);
    }
}
