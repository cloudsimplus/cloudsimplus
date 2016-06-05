/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;
import org.junit.Before;
import org.junit.Test;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class PowerHostTest {
	
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final double MIPS = 1000;
    private static final double MAX_POWER = 200;
    private static final double STATIC_POWER_PERCENT = 0.3;
    private static final double TIME = 10;
    private static final long STORAGE = Consts.MILLION;
    

    private PowerHostSimple host;

    public static PowerHostSimple createPowerHost(final int hostId, final int numberOfPes) {
        final List<Pe> peList = new ArrayList<>(numberOfPes);
        for(int i = 0; i < numberOfPes; i++)
            peList.add(new PeSimple(i, new PeProvisionerSimple(MIPS)));
        
        return new PowerHostSimple(hostId, 
                new ResourceProvisionerSimple<>(new Ram(RAM)), 
                new ResourceProvisionerSimple<>(new Bandwidth(BW)), 
                STORAGE, peList, new VmSchedulerTimeShared(peList),
                new PowerModelLinear(MAX_POWER, STATIC_POWER_PERCENT)
        );
    }
    
    @Before
    public void setUp() throws Exception {
        host = createPowerHost(0, 1);
    }

    @Test
    public void testGetMaxPower() {
        assertEquals(MAX_POWER, host.getMaxPower(), 0);		
    }

    @Test
    public void testGetEnergy() {
        assertEquals(0, host.getEnergyLinearInterpolation(0, 0, TIME), 0);
        double expectedEnergy = 0;
        expectedEnergy = (host.getPowerModel().getPower(0.2) + (host.getPowerModel().getPower(0.9) - host.getPowerModel().getPower(0.2)) / 2) * TIME;
        assertEquals(expectedEnergy, host.getEnergyLinearInterpolation(0.2, 0.9, TIME), 0);
    }

}
