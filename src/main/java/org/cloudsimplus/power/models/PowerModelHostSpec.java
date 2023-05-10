/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.power.models;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.power.PowerMeasurement;
import org.cloudsimplus.traces.FileReader;

import java.util.Arrays;

/**
 * A power model created based on data from
 * <a href="http://www.spec.org/power_ssj2008/">SPEC power benchmark</a>.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.0
 */
public class PowerModelHostSpec extends PowerModelHostAbstract {
    /**
     * Since {@link #powerSpecs} represents the power consumption data
     * according to CPU utilization, as shorter the size of such a List,
     * less accurate is the power consumption according to CPU utilization.
     * Check mentioned attribute for details.
     * Less than 2 entries doesn't make any sense,
     * since for any CPU utilization, the power consumption will be the same.
     */
    public static final int MIN_POWER_CONSUMPTION_DATA_SIZE = 2;

    /**
     * An array where each element represents the power consumption (in Watts)
     * of the entity for specific CPU utilization percentage.
     *
     * <p>Item at position 0 indicates the power consumption when the Host is completely idle.
     * The number of remaining items indicate the power consumption according the percentage of CPU utilization.
     * For instance, if after the first element there are additionals:
     * <ul>
     *     <li>10 elements, each value indicates the power consumption for 10%, 20% ... 100% of CPU utilization.</li>
     *     <li>100 elements, each value indicates the power consumption for 1%,  2% ... 100% of CPU utilization.</li>
     * </ul>
     * </p>
     * @see #getInstance(String)
     */
    @Getter
    private final double[] powerSpecs;

    /**
     * Instantiates a PowerModelHostSpec providing
     * the power consumption data of the entity for different
     * CPU utilization percentages.
     *
     * @param powerSpecs an array where each element represents the power consumption (in Watts) of the entity for specific
     * CPU utilization percentage. Check {@link #getPowerSpecs()} for more details about this data.
     * @see #getInstance(String)
     */
    public PowerModelHostSpec(@NonNull final double[] powerSpecs) {
        super();
        if (powerSpecs.length >= MIN_POWER_CONSUMPTION_DATA_SIZE) {
            this.powerSpecs = powerSpecs;
            return;
        }

        final var msg =
            "powerSpec has to contain at least %d elements (representing utilization at 0%% and 100%% load, respectively)"
            .formatted(MIN_POWER_CONSUMPTION_DATA_SIZE);
        throw new IllegalArgumentException(msg);
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        final double utilizationFraction = getHost().getCpuMipsUtilization() / getHost().getTotalMipsCapacity();
        final int utilizationIndex = (int) Math.round(utilizationFraction * powerSpecs.length);
        final double powerUsage = powerSpecs[utilizationIndex];
        final double staticPower = powerSpecs[0];
        return new PowerMeasurement(staticPower, powerUsage - staticPower);
    }

    @Override
    protected double getPowerInternal(final double utilizationFraction) {
        final int utilizationIndex = (int) Math.round(utilizationFraction * powerSpecs.length);
        return powerSpecs[utilizationIndex];
    }

    /**
     * Creates a PowerModelHostSpec instance reading the power consumption
     * data (in Watts) from a file with a single line, where each value is separated by space.
     * The content for this file is usually obtained from <a href="https://www.spec.org">https://www.spec.org</a>.
     *
     * <p>The <a href="https://github.com/cloudsimplus/cloudsimplus-examples/tree/master/src/main/resources/power-specs">power-specs</a>
     * directory in the examples project contains sample files that can be used here.
     * Check {@link #getPowerSpecs()} for more details about the data of this file.</p>
     *
     * @param powerSpecFilePath path to the power spec file
     * @return a new PowerModelHostSpec instance
     */
    public static PowerModelHostSpec getInstance(final String powerSpecFilePath) {
        final String[] values = FileReader.getSingleLineReader(powerSpecFilePath).readFile();
        final double[] powerSpecs = Arrays.stream(values).mapToDouble(Double::valueOf).toArray();
        return new PowerModelHostSpec(powerSpecs);
    }
}
