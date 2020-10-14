package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.PowerMeasurement;


/**
 * Power model for data centers.
 *
 * It's power usage is the aggregated power usage of all hosts times the Power Usage Effectiveness (PUE).
 */
public class PowerModelDatacenter extends PowerModel {

    private Datacenter datacenter;
    private double powerUsageEffectiveness = 1;

    public static PowerModelDatacenter NULL = new PowerModelDatacenter(Datacenter.NULL) {
        @Override public PowerMeasurement getPowerMeasurement() { return new PowerMeasurement(); }
    };

    public PowerModelDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        PowerMeasurement measurement = datacenter.getHostList().stream()
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

    public Datacenter getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
