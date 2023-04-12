package org.cloudsimplus.power.models;

import lombok.NonNull;
import org.cloudsimplus.power.PowerMeasurement;

import java.util.List;

/**
 * A power model created based on data from
 * <a href="http://www.spec.org/power_ssj2008/">SPEC power benchmark</a>.
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
 * @since CloudSim Plus 6.0.0
 */
public class PowerModelHostSpec extends PowerModelHost {
    /**
     * Since {@link #powerSpec} represents the power consumption data
     * according to CPU utilization, as shorter the size of such a List,
     * less accurate is the power consumption according to CPU utilization.
     * Check mentioned attribute for details.
     * Less than 2 entries doesn't make any sense,
     * since for any CPU utilization, the power consumption will be the same.
     */
    public static final int MIN_POWER_CONSUMPTION_DATA_SIZE = 2;

    /**
     * An array where each element represents the
     * power consumption (in Watts) of the entity for specific
     * CPU utilization percentage.
     * Item at position 0 indicates the power consumption when the Host is completely idle.
     * The number of remaining items indicate the power consumption according the percentage of CPU utilization.
     * For instance, if after the first element there are additionals:
     * <ul>
     *     <li>10 elements, each value indicates the power consumption for 10%, 20% ... 100% of CPU utilization.</li>
     *     <li>100 elements, each value indicates the power consumption for 1%,  2% ... 100% of CPU utilization.</li>
     * </ul>
     */
    private final double[] powerSpec;

    /**
     * Instantiates a PowerModelHostSpec providing
     * the power consumption data of the entity for different
     * CPU utilization percentages.
     *
     * @param powerSpec an array where each element represents the power consumption (in Watts) of the entity for specific
     * CPU utilization percentage.
     * Item at position 0 indicates the power consumption when the Host is completely idle.
     * The number of remaining items indicate the power consumption according the percentage of CPU utilization.
     * For instance, if after the first element there are additionals:
     * <ul>
     *     <li>10 elements, each value indicates the power consumption for 10%, 20% ... 100% of CPU utilization.</li>
     *     <li>100 elements, each value indicates the power consumption for 1%,  2% ... 100% of CPU utilization.</li>
     * </ul>
     */
    public PowerModelHostSpec(@NonNull final double[] powerSpec) {
        super();
        if (powerSpec.length >= MIN_POWER_CONSUMPTION_DATA_SIZE) {
            this.powerSpec = powerSpec;
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
        final int utilizationIndex = (int) Math.round(utilizationFraction * powerSpec.length);
        final double powerUsage = powerSpec[utilizationIndex];
        final double staticPower = powerSpec[0];
        return new PowerMeasurement(staticPower, powerUsage - staticPower);
    }

    @Override
    public double getPowerInternal(final double utilizationFraction) {
        final int utilizationIndex = (int) Math.round(utilizationFraction * powerSpec.length);
        return powerSpec[utilizationIndex];
    }
}
