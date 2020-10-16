package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.PowerMeasurement;


/**
 * Simple power model for data centers.
 *
 * It's power usage is the aggregated power usage of all hosts times the Power Usage Effectiveness (PUE).
 */
public class PowerModelDatacenterSimple extends PowerModelDatacenter {

    private double powerUsageEffectiveness = 1;

    public PowerModelDatacenterSimple(Datacenter datacenter) {
        setDatacenter(datacenter);
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        PowerMeasurement measurement = getDatacenter().getHostList().stream()
            .map(Host::getPowerModel)
            .map(PowerModelHost::getPowerMeasurement)
            .reduce(PowerMeasurement::add)
            .orElse(new PowerMeasurement());
        return measurement.multiply(powerUsageEffectiveness);
    }

    public double getPowerUsageEffectiveness() {
        return powerUsageEffectiveness;
    }

    public void setPowerUsageEffectiveness(double powerUsageEffectiveness) {
        this.powerUsageEffectiveness = powerUsageEffectiveness;
    }

}
