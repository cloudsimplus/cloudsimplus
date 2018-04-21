package org.cloudbus.cloudsim.power.models;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A power model where the power consumption is defined by a {@link UnaryOperator} function
 * given as parameter to the constructor. This way, the user can define how the power consumption
 * increases along the time without requiring to create a new class for it.
 *
 * <p>However, specific classes that implement well known models
 * are provided, such as {@link PowerModelLinear},
 * {@link PowerModelSquare}, {@link PowerModelCubic}
 * and {@link PowerModelSqrt}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.1.0
 */
public class PowerModelSimple extends PowerModelAbstract {
    private final UnaryOperator<Double> powerIncrementFunction;

    /**
     * @see #getMaxPower()
     */
    private double maxPower;

    /**
     * @see #getStaticPowerPercent()
     */
    private double staticPowerPercent;

    /**
     * Instantiates a PowerModelSimple.
     *
     * @param maxPower the max power that can be consumed (in Watts/second).
     * @param staticPowerPercent the static power usage percentage between 0 and 1.
     * @param powerIncrementFunction a function that defines how the power consumption increases along the time.
     *                               This function receives the utilization percentage in scale from 0 to 100
     *                               and returns a factor representing how the power consumption will
     *                               increase for the given utilization percentage.
     *                               The function return is again a percentage value between [0 and 1].
     */
    public PowerModelSimple(
        final double maxPower,
        final double staticPowerPercent,
        final UnaryOperator<Double> powerIncrementFunction)
    {
        Objects.requireNonNull(powerIncrementFunction);
        this.powerIncrementFunction = powerIncrementFunction;
        setMaxPower(maxPower);
        setStaticPowerPercent(staticPowerPercent);
    }

    /**
     * Gets the max power that can be consumed (in Watts/second).
     *
     * @return the max power (in Watts/second)
     */
    public double getMaxPower() {
        return maxPower;
    }

    /**
     * Sets the max power that can be consumed (in Watts/second).
     *
     * @param maxPower the new max power (in Watts/second)
     */
    private void setMaxPower(final double maxPower) {
        if(maxPower < 0){
            throw new IllegalArgumentException("Maximum power consumption cannot be negative.");
        }

        this.maxPower = maxPower;
    }

    /**
     * Gets the static power consumption percentage (between 0 and 1) that is not dependent of resource usage.
     * It is the amount of energy consumed even when the host is idle.
     * @return the static power consumption percentage (between 0 and 1)
     */
    public double getStaticPowerPercent() {
        return staticPowerPercent;
    }

    /**
     * Sets the static power consumption percentage (between 0 and 1) that is not dependent of resource usage.
     * It is the amount of energy consumed even when the host is idle.
     *
     * @param staticPowerPercent the value to set (between 0 and 1)
     */
    private void setStaticPowerPercent(final double staticPowerPercent) {
        if(staticPowerPercent < 0 || staticPowerPercent > 1){
            throw new IllegalArgumentException("Static power percentage must be between 0 and 1.");
        }
        this.staticPowerPercent = staticPowerPercent;
    }

    /**
     * Gets the static power consumption (in Watts/second) that is not dependent of resource usage,
     * according to the {@link #getStaticPowerPercent()}.
     * It is the amount of energy consumed even when the host is idle.
     *
     * @return the static power usage (in Watts/second)
     */
    public final double getStaticPower() {
        return staticPowerPercent * maxPower;
    }

    /**
     * Gets the constant which represents the power consumption
     * for each fraction of resource used (in Watts/second).
     *
     * @return the power consumption constant (in Watts/second)
     */
    protected double getConstant() {
        return (maxPower - getStaticPower()) / powerIncrementFunction.apply(100.0);
    }

    @Override
    protected double getPowerInternal(final double utilization) throws IllegalArgumentException {
        return getStaticPower() + getConstant() * powerIncrementFunction.apply(utilization*100.0);
    }
}
