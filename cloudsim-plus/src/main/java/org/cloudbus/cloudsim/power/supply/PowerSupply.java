package org.cloudbus.cloudsim.power.supply;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * Provides data about a PM power consumption,
 * according to a defined {@link PowerModel}.
 * Power consumption data is just available if a {@link PowerModel}
 * is set to the PowerSupply.
 *
 * <p><b>It's required to set a {@link PowerModel} in order to get any power usage data
 * using the available methods. A {@link PowerModel} don't need to be set
 * you don't want to simulate power consumption.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @author Anton Beloglazov
 * @since CloudSim Plus 1.4
 */
public interface PowerSupply {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerSupply}
     * objects.
     */
    PowerSupply NULL = new PowerSupplyNull();

    /**
     * Gets the current power consumption of the host. For this moment, it only computes the power consumed by
     * {@link Pe}s.
     * <b>It's required to set a {@link PowerModel} in order to get power usage data.</b>
     *
     * @return the power consumption
     */
    double getPower();

    /**
     * Gets the amount of power the Host consumes considering a given
     * utilization percentage. For this moment it only computes the power consumed by PEs.
     * <b>It's required to set a {@link PowerModel} in order to get power usage data.</b>
     *
     * @param utilization the utilization percentage (between [0 and 1]) of a
     *                    resource that is critical for power consumption
     * @return the power consumption
     */
    double getPower(double utilization);

    /**
     * Gets the max power that can be consumed by the host.
     * <b>It's required to set a {@link PowerModel} in order to get power usage data.</b>
     *
     * @return the max consumption power
     */
    double getMaxPower();

    /**
     * Sets the power model.
     *
     * @param powerModel the new power model
     * @return
     */
    Host setPowerModel(PowerModel powerModel);

    /**
     * Gets the power model used by the host
     * to define how it consumes power.
     *
     * @return the power model
     */
    PowerModel getPowerModel();

    /**
     * Gets the energy consumption using linear interpolation of the utilization
     * change.
     * <b>It's required to set a {@link PowerModel} in order to get power usage data.</b>
     *
     * @param fromUtilization the initial utilization percentage
     * @param toUtilization   the final utilization percentage
     * @param time            the time
     * @return the energy
     */
    double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time);
}
