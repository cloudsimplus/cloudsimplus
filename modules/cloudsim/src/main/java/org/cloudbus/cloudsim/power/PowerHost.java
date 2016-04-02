package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.power.models.PowerModel;

/**
 * An interface to be implemented by power-aware Host classes.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface PowerHost extends HostDynamicWorkload {

    /**
     * Gets the energy consumption using linear interpolation of the utilization change.
     *
     * @param fromUtilization the initial utilization percentage
     * @param toUtilization the final utilization percentage
     * @param time the time
     * @return the energy
     */
    double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time);

    /**
     * Gets the max power that can be consumed by the host.
     *
     * @return the max power
     */
    double getMaxPower();

    /**
     * Gets the power. For this moment only consumed by all PEs.
     *
     * @return the power
     */
    double getPower();

    /**
     * Gets the power model.
     *
     * @return the power model
     */
    PowerModel getPowerModel();
    
}
