/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Conversion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class HostPowerTest {
    private static final long RAM = 1024;
    private static final long BANDWIDTH = 10000;
    private static final long MIPS = 1000;
    private static final double MAX_POWER = 200;
    private static final double STATIC_POWER_PERCENT = 0.3;
    private static final double TIME = 10;
    private static final long STORAGE = Conversion.MILLION;

    private HostSimple host;

    /* default */ static HostSimple createPowerHost(final int numberOfPes) {
        final List<Pe> peList =
                    IntStream.range(0, numberOfPes)
                             .mapToObj(id -> new PeSimple(MIPS, new PeProvisionerSimple()))
                             .collect(Collectors.toList());

        final HostSimple host = new HostSimple(RAM, BANDWIDTH, STORAGE, peList);
        host.setPowerModel(new PowerModelLinear(MAX_POWER, STATIC_POWER_PERCENT))
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());

        return host;
    }

    @BeforeEach
    public void setUp() {
        host = createPowerHost(1);
    }

    @Test
    public void testGetMaxPower() {
        assertEquals(MAX_POWER, host.getPowerModel().getMaxPower());
    }

    @Test
    public void testGetEnergyUtilization() {
        final PowerModel model = host.getPowerModel();
        final double expected = 1370;
        assertEquals(expected, model.getEnergyLinearInterpolation(0.2, 0.9, TIME));
    }

    @Test
    public void testGetEnergyUtilizationZero() {
        final PowerModel model = host.getPowerModel();
        final double expected = 600;
        assertEquals(expected, model.getEnergyLinearInterpolation(0, 0, TIME));
    }

}
