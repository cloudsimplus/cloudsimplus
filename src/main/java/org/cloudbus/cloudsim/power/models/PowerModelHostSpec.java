package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;

import java.util.List;
import java.util.Objects;

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
     * A List where each element represents the
     * power consumption (in Watts) of the entity for specific
     * CPU utilization percentage.
     * If the list has 10 items, each element represents
     * the power consumption for 10% of CPU utilization.
     * This way, the 1st item represents the power consumption for 10% of CPU utilization,
     * the 2nd for 20% of CPU utilization and so on.
     * If the list has 100 itens, each item represents the power consumption for 1% of
     * CPU utilization and so no.
     */
    private final List<Double> powerSpec;

    /**
     * Instantiates a PowerModelHostSpec providing
     * the power consumption data of the entity for different
     * CPU utilization percentages.
     *
     * @param powerSpec a list where each element represents the
     *                  power consumption (in Watts) of the entity for specific
     *                  CPU utilization percentage.
     *                  If the list has 10 items, each element represents
     *                  the power consumption for 10% of CPU utilization.
     *                  This way, the 1st item represents the power consumption for 10% of CPU utilization,
     *                  the 2nd for 20% of CPU utilization and so on.
     *                  If the list has 100 itens, each item represents the power consumption for 1% of
     *                  CPU utilization and so no.
     *
     * <p>If there are 100 elements in this list,
     * element at position 1 represents the power consumption
     * for 1% of CPU utilization, where element 100,</p>
     * represents power consumption for 100% of CPU utilization.
     *
     * <p>If there are only 10 elements in this list,
     * each element represents the power consumption
     * when the CPU utilization is between an interval between [p .. p+10],
     * where p is the CPU utilization percentage.
     * For instance, element 0 represents the power consumption
     * when CPU utilization is between [0 .. 10%].</p>
     */
    public PowerModelHostSpec(final List<Double> powerSpec) {
        super();
        Objects.requireNonNull(powerSpec, "powerSpec cannot be null");
        if (powerSpec.size() >= MIN_POWER_CONSUMPTION_DATA_SIZE) {
            this.powerSpec = powerSpec;
            return;
        }

        final var msg =
            String.format("powerSpec has to contain at least %d elements (representing utilization at 0%% and 100%% load, respectively)",
                MIN_POWER_CONSUMPTION_DATA_SIZE);
        throw new IllegalArgumentException(msg);
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        final double utilizationFraction = getHost().getCpuMipsUtilization() / getHost().getTotalMipsCapacity();
        final int utilizationIndex = (int) Math.round(utilizationFraction * powerSpec.size());
        final double powerUsage = powerSpec.get(utilizationIndex);
        return new PowerMeasurement(powerSpec.get(0), powerUsage - powerSpec.get(0));
    }

    @Override
    public double getPower(final double utilizationFraction) throws IllegalArgumentException {
        final int utilizationIndex = (int) Math.round(utilizationFraction * powerSpec.size());
        return powerSpec.get(utilizationIndex);
    }
}
