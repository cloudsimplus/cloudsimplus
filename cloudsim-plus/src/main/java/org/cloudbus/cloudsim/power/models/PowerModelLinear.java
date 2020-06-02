/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power.models;

/**
 * A power model where the power consumption is linear to resource usage.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:</p>
 *
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a></li>
 * </ul>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 2.0
 */
public class PowerModelLinear  extends PowerModelSimple {
    /**
	 * Instantiates a linear power model.
	 *
	 * @param maxPower the max power that can be consumed in Watt-Second (Ws).
	 * @param staticPowerPercent the static power usage percentage between 0 and 1.
	 */
	public PowerModelLinear(final double maxPower, final double staticPowerPercent) {
	    /** Calls the super constructor passing a {@link #powerFunction}
         * that indicates the base power consumption is linear to CPU utilization.*/
	    super(maxPower, staticPowerPercent, utilizationPercent -> utilizationPercent);
	}
}
