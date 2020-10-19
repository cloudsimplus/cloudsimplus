package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;

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

    private final List<Double> powerSpec;

    public PowerModelHostSpec(final List<Double> powerSpec) {
        if (powerSpec.size() >= 2) {
            throw new IllegalArgumentException("powerSpec has to contain at least 2 elements " +
                "(utilizazion at 0% and 100% load)");
        }
        this.powerSpec = powerSpec;
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        double utilizationFraction = getHost().getCpuMipsUtilization() / getHost().getTotalMipsCapacity();
        int utilizationIndex = (int) Math.round(utilizationFraction * powerSpec.size());
        double powerUsage = powerSpec.get(utilizationIndex);
        return new PowerMeasurement(powerSpec.get(0), powerUsage - powerSpec.get(0));
    }

    @Override
    public double getPower(double utilizationFraction) throws IllegalArgumentException {
        int utilizationIndex = (int) Math.round(utilizationFraction * powerSpec.size());
        return powerSpec.get(utilizationIndex);
    }
}
