package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.PowerMeasurement;


/**
 * Simple power model defining the power consumption of a data center.
 * It's power usage is the aggregated power usage of all hosts times the
 * <a href="https://en.wikipedia.org/wiki/Power_usage_effectiveness">Power Usage Effectiveness (PUE)</a>.
 * @since CloudSim Plus 6.0.0
 */
public class PowerModelDatacenterSimple extends PowerModelDatacenter {
    /** @see #getPowerUsageEffectiveness() */
    private double powerUsageEffectiveness = 1;

    /**
     * Instantiates a PowerModelDatacenterSimple for a given Datacenter,
     * defining the Power Usage Effectiveness (PUE) as 1 (100%).
     * @param datacenter the Datacenter for which the power model will be defined
     */
    public PowerModelDatacenterSimple(final Datacenter datacenter) {
        super();
        setDatacenter(datacenter);
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        final PowerMeasurement measurement = getDatacenter().getHostList().stream()
            .map(Host::getPowerModel)
            .map(PowerModelHost::getPowerMeasurement)
            .reduce(PowerMeasurement::add)
            .orElse(new PowerMeasurement());
        return measurement.multiply(powerUsageEffectiveness);
    }

    /**
     * Gets the Power Usage Effectiveness (PUE) for this Power Model,
     * defining how effective power usage is.
     * @return a percentage value between [0 and 1]
     */
    public double getPowerUsageEffectiveness() {
        return powerUsageEffectiveness;
    }

    /**
     * Sets the Power Usage Effectiveness (PUE) for this Power Model,
     * defining how effective power usage is.
     * @param powerUsageEffectiveness a percentage value between [0 and 1]
     */
    public void setPowerUsageEffectiveness(final double powerUsageEffectiveness) {
        if(powerUsageEffectiveness < 0 || powerUsageEffectiveness > 1){
            throw new IllegalArgumentException("powerUsageEffectiveness must be between [0 and 1].");
        }

        this.powerUsageEffectiveness = powerUsageEffectiveness;
    }
}
