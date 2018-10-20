/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.utilizationmodels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class UtilizationModelStochasticTest {

    private UtilizationModelStochastic utilizationModel;

    @BeforeEach
    public void setUp() {
        utilizationModel = new UtilizationModelStochastic();
    }

    @Test
    public void testGetUtilization() {
        final double utilization0 = utilizationModel.getUtilization(0);
        final double utilization1 = utilizationModel.getUtilization(1);
        assertAll(
            () -> assertNotNull(utilization0),
            () -> assertNotNull(utilization1),
            () -> assertNotSame(utilization0, utilization1),
            () -> assertEquals(utilization0, utilizationModel.getUtilization(0)),
            () -> assertEquals(utilization1, utilizationModel.getUtilization(1))
        );
    }

}
