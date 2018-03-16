/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts.power;

import org.cloudbus.cloudsim.hosts.HostDynamicWorkload;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * An interface to be implemented by power-aware Host classes,
 * defining power consumption based on a {@link PowerModel}.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface PowerHost extends HostDynamicWorkload {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerHost}
     * objects.
     */
    PowerHost NULL = new PowerHostNull();

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
     * Gets the current power consumption of the host. For this moment, it only computes the power consumed by
     * {@link Pe}s.
     *
     * @return the power consumption
     */
    double getPower();

    /**
     * Gets the power model used by the host
     * to define how it consumes power.
     *
     * @return the power model
     */
    PowerModel getPowerModel();

    /**
     * Sets the power model.
     *
     * @param powerModel the new power model
     * @return
     */
    PowerHost setPowerModel(PowerModel powerModel);
}
