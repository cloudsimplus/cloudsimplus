/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power.models;

/**
 * Implements a power model where the power consumption is linear to resource usage.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:</p>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 * @TODO the tree first attributes are being repeated among several classes.
 * Thus, a better class hierarchy should be provided, such as an abstract class
 * implementing the PowerModel interface.
 */
public class PowerModelLinear  extends PowerModelAbstract {

	/** @see #getMaxPower()  */
	private double maxPower;

	/** @see #getConstant()  */
	private double constant;

	/**
     * The static power consumption that is not dependent of resource usage.
     * It is the amount of energy consumed even when the host is idle.
     */
	private double staticPower;

	/**
	 * Instantiates a new linear power model.
	 *
	 * @param maxPower the max power that can be consumed (in Watts/second).
	 * @param staticPowerPercent the static power percent
	 */
	public PowerModelLinear(final double maxPower, final double staticPowerPercent) {
		setMaxPower(maxPower);
		setStaticPower(staticPowerPercent * maxPower);
		setConstant((maxPower - getStaticPower()) / 100.0);
	}

	@Override
	protected double getPowerInternal(final double utilization) throws IllegalArgumentException {
		if (utilization == 0) {
			return 0;
		}
		return getStaticPower() + getConstant() * utilization * 100;
	}

	/**
	 * Gets The max power that can be consumed.
	 *
	 * @return the max power
	 */
	protected double getMaxPower() {
		return maxPower;
	}

	/**
	 * Sets The max power that can be consumed.
	 *
	 * @param maxPower the new max power
	 */
	protected final void setMaxPower(double maxPower) {
		this.maxPower = maxPower;
	}

	/**
	 * Gets the constant which represents the power consumption
     * for each fraction of resource used.
	 *
	 * @return the constant
	 */
	protected double getConstant() {
		return constant;
	}

	/**
	 * Sets the constant which represents the power consumption
     * for each fraction of resource used.
	 *
	 * @param constant the new constant
	 */
	protected final void setConstant(double constant) {
		this.constant = constant;
	}

	/**
	 * Gets the static power.
	 *
	 * @return the static power
	 */
	protected final double getStaticPower() {
		return staticPower;
	}

	/**
	 * Sets the static power.
	 *
	 * @param staticPower the new static power
	 */
	protected final void setStaticPower(double staticPower) {
		this.staticPower = staticPower;
	}

}
