/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power.models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.util.Conversion;
import org.junit.Before;
import org.junit.Test;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class HostPowerTest {
    private static final long RAM = 1024;
    private static final long BW = 10000;
    private static final long MIPS = 1000;
    private static final double MAX_POWER = 200;
    private static final double STATIC_POWER_PERCENT = 0.3;
    private static final double TIME = 10;
    private static final long STORAGE = Conversion.MILLION;

    private HostSimple host;

    public static HostSimple createPowerHost(final int numberOfPes) {
        final List<Pe> peList = new ArrayList<>(numberOfPes);
        for(int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(MIPS, new PeProvisionerSimple()));
        }

        final HostSimple host = new HostSimple(RAM, BW, STORAGE, peList);
        host.setPowerModel(new PowerModelLinear(MAX_POWER, STATIC_POWER_PERCENT))
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());

        return host;
    }

    @Before
    public void setUp() {
        host = createPowerHost(1);
    }

    @Test
    public void testGetMaxPower() {
        assertEquals(MAX_POWER, host.getPowerModel().getMaxPower(), 0);
    }

    @Test
    public void testGetEnergyUtilization() {
        final PowerModel pm = host.getPowerModel();
        final double expected = 1370;
        assertEquals(expected, pm.getEnergyLinearInterpolation(0.2, 0.9, TIME), 0);
    }

    @Test
    public void testGetEnergyUtilizationZero() {
        final PowerModel pm = host.getPowerModel();
        final double expected = 600;
        assertEquals(expected, pm.getEnergyLinearInterpolation(0, 0, TIME), 0);
    }

}
