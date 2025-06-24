package org.cloudsimplus.power.models;

import lombok.Getter;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeasurement;
import org.cloudsimplus.util.MathUtil;


/// Simple power model defining the power consumption of a {@link Datacenter}.
/// It's power usage is the aggregated power usage of all hosts, multiplied by the
/// [Power Usage Effectiveness (PUE)](https://en.wikipedia.org/wiki/Power_usage_effectiveness).
///
/// @since CloudSim Plus 6.0.0
public class PowerModelDatacenterSimple extends PowerModelDatacenter {
    /**
     * The Power Usage Effectiveness (PUE) for this Power Model,
     * defining how effective power usage is.
     * It is a percentage value between [0 and 1].
     */
    @Getter
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
     * Sets the Power Usage Effectiveness (PUE) for this Power Model,
     * defining how effective power usage is.
     * @param powerUsageEffectiveness a percentage value between [0 and 1]
     */
    public void setPowerUsageEffectiveness(final double powerUsageEffectiveness) {
        this.powerUsageEffectiveness = MathUtil.percentage(powerUsageEffectiveness, "powerUsageEffectiveness");
    }
}
