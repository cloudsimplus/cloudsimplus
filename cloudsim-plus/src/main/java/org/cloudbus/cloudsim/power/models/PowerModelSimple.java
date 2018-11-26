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
    /**
     * A value representing one hundred percent.
     */
    private static final double ONE_HUNDRED = 100.0;

    /**
     * A function defining how the power consumption is computed based on the CPU utilization.
     * When called, this function receives the utilization percentage in scale from [0 to 100]
     * and must return the base power consumption for that CPU utilization.
     * The function is only accountable to compute the base energy consumption
     * because the total energy consumption depends on other factors such as
     * the {@link #getStaticPower() static power} consumed by the Host,
     * independent of its CPU usage.
     */
    private final UnaryOperator<Double> powerFunction;

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
     * @param maxPower           the max power that can be consumed in Watt-Second (Ws).
     * @param staticPowerPercent the static power usage percentage between [0 and 1].
     * @param powerFunction      A function defining how the power consumption is computed based on the CPU utilization.
     *                           When called, this function receives the utilization percentage in scale from [0 to 100]
     *                           and must return the base power consumption for that CPU utilization.
     *                           The function is only accountable to compute the base energy consumption
     *                           because the total energy consumption depends on other factors such as
     *                           the {@link #getStaticPower() static power} consumed by the Host,
     *                           independent of its CPU usage.
     */
    public PowerModelSimple(
        final double maxPower,
        final double staticPowerPercent,
        final UnaryOperator<Double> powerFunction)
    {
        super();
        this.powerFunction = Objects.requireNonNull(powerFunction);
        setMaxPower(maxPower);
        setStaticPowerPercent(staticPowerPercent);
    }

    @Override
    public double getMaxPower() {
        return maxPower;
    }

    /**
     * Sets the max power that can be consumed in Watt-Second (Ws).
     *
     * @param maxPower the new max power in Watt-Second (Ws)
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
     * Gets the static power consumption in Watt-Second (Ws) that is not dependent of resource usage,
     * according to the {@link #getStaticPowerPercent()}.
     * It is the amount of energy consumed even when the host is idle.
     *
     * @return the static power usage in Watt-Second (Ws)
     */
    public final double getStaticPower() {
        return staticPowerPercent * maxPower;
    }

    /**
     * Gets the constant which represents the power consumption
     * for each fraction of resource used in Watt-Second (Ws).
     *
     * @return the power consumption constant in Watt-Second (Ws)
     */
    protected double getConstant() {
        return (maxPower - getStaticPower()) / powerFunction.apply(ONE_HUNDRED);
    }

    @Override
    protected double getPowerInternal(final double utilization) throws IllegalArgumentException {
        return getStaticPower() + getConstant() * powerFunction.apply(utilization*ONE_HUNDRED);
    }
}
