package org.cloudbus.cloudsim.power.supply;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.models.PowerModel;

/**
 * A class that implements the Null Object Design Pattern for {@link PowerSupply}
 * objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.4
 * @see PowerSupply#NULL
 */
final class PowerSupplyNull implements PowerSupply {
    @Override public double getPower() {
        return 0;
    }
    @Override public double getPower(double utilization) { return 0; }
    @Override public double getMaxPower() {
        return 0;
    }
    @Override public Host setPowerModel(PowerModel powerModel) {
        return null;
    }
    @Override public PowerModel getPowerModel() {
        return null;
    }
    @Override public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) { return 0; }
}
