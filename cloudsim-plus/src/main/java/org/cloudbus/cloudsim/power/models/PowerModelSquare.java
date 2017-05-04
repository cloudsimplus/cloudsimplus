/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power.models;

/**
 * Implements a power model where the power consumption is the square of the resource usage.
 * <p>
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * </p>
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerModelSquare extends PowerModelAbstract {

    /**
     * The max power that can be consumed.
     */
    private double maxPower;

    /**
     * The constant that represents the power consumption
     * for each fraction of resource used.
     */
    private double constant;

    /**
     * The static power consumption that is not dependent of resource usage.
     * It is the amount of energy consumed even when the host is idle.
     */
    private double staticPower;

    /**
     * Instantiates a new power model square.
     *
     * @param maxPower           the max power
     * @param staticPowerPercent the static power percent
     */
    public PowerModelSquare(double maxPower, double staticPowerPercent) {
        setMaxPower(maxPower);
        setStaticPower(staticPowerPercent * maxPower);
        setConstant((maxPower - getStaticPower()) / Math.pow(100, 2));
    }

    @Override
    protected double getPowerInternal(double utilization) throws IllegalArgumentException {
        if (utilization == 0) {
            return 0;
        }
        return getStaticPower() + getConstant() * Math.pow(utilization * 100, 2);
    }

    /**
     * Gets the max power.
     *
     * @return the max power
     */
    protected double getMaxPower() {
        return maxPower;
    }

    /**
     * Sets the max power.
     *
     * @param maxPower the new max power
     */
    protected final void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }

    /**
     * Gets the constant.
     *
     * @return the constant
     */
    protected double getConstant() {
        return constant;
    }

    /**
     * Sets the constant.
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
