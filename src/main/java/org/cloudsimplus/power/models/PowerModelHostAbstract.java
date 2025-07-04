package org.cloudsimplus.power.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeter;
import org.cloudsimplus.util.MathUtil;

/**
 * Abstract implementation of a {@link Host} {@link PowerModel}.
 *
 * @since CloudSim Plus 6.0.0
 * @see PowerMeter
 */
@Accessors(makeFinal = false) @Getter
public abstract non-sealed class PowerModelHostAbstract implements PowerModelHost {
    @Setter
    private Host host;
    private double startupPower;
    private double shutDownPower;
    private double totalStartupPower;
    private double totalShutDownPower;
    private double totalStartupTime;
    private double totalShutDownTime;
    private int totalStartups;

    /**
     * Checks if a power value (in Watts) is valid.
     * @param power the value to validate (in Watts)
     * @param fieldName the name of the field/variable storing the value
     * @return the given power if it's valid
     * @throws IllegalArgumentException when the value is smaller than 1
     */
    protected static double validatePower(final double power, final String fieldName) {
        MathUtil.nonNegative(power, fieldName);

        final var s = "%s must be in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.";
        if(power < 1){
            throw new IllegalArgumentException(s.formatted(fieldName));
        }

        return power;
    }

    @Override
    public final double getPower(final double utilizationFraction) {
        MathUtil.percentage(utilizationFraction, "utilizationFraction");
        return getPowerInternal(utilizationFraction);
    }

    /**
     * @see #getPower(double)
     */
    protected abstract double getPowerInternal(double utilizationFraction);

    @Override
    public PowerModelHost setStartupPower(final double power) {
        this.startupPower = validatePower(power, "Power");
        return this;
    }

    @Override
    public PowerModelHost setShutDownPower(final double power) {
        this.shutDownPower = validatePower(power, "Power");
        return this;
    }

    @Override
    public void addStartupTotals() {
        totalStartupPower += startupPower;
        totalStartupTime += host.getStartupDelay();
        totalStartups++;
    }

    @Override
    public void addShutDownTotals() {
        totalShutDownPower += shutDownPower;
        totalShutDownTime += host.getShutDownDelay();
    }
}
