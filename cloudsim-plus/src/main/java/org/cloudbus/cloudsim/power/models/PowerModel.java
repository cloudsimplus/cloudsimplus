/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;

/**
 * Provides a model for power consumption of hosts, depending on utilization of a critical system
 * component, such as CPU.
 * <b>This is the fundamental class to enable power-aware Hosts.
 * However, a Host just provides power usage data if a PowerModel is set using the
 * {@link Host#setPowerModel(PowerModel)}.</b>
 * The power consumption data is return in Watt-Second (Ws),
 * which is just in a different scale than the usual Kilowatt-Hour (kWh).
 *
 * <p>The interface implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link PowerModel#NULL} object instead of attributing {@code null} to
 * {@link PowerModel} variables.</p>
 *
 * <p>If you are using any algorithms, policies or workload included in the
 * power package please cite the following paper:</p>
 *
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley and Sons, Ltd, New York, USA, 2012</a></li>
 * </ul>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 2.0
 */
public interface PowerModel extends PowerAware {
    /**
     * A property that implements the Null Object Design Pattern for {@link Host}
     * objects.
     */
    PowerModel NULL = new PowerModel() {
        @Override public Host getHost() { return Host.NULL; }
        @Override public void setHost(Host host) {}
        @Override public double getMaxPower() { return 0; }
        @Override public double getPower() { return 0; }
        @Override public double getPower(double utilization) throws IllegalArgumentException { return 0; }
        @Override public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) { return 0; }
    };

    Host getHost();

    void setHost(Host host);

    /**
     * Gets the max power that can be supplied by the host in Watts (W).
     *
     * @return the max power supply in Watts (W)
     */
    double getMaxPower();

    /**
     * Gets the power supply in Watts (W), according to the utilization
     * percentage of a critical resource, such as CPU
     * (which is currently the only resource considered).
     *
     * @param utilization the utilization percentage (between [0 and 1]) of a
     * resource that impacts power supply.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException when the utilization percentage is not
     * between [0 and 1]
     */
    double getPower(double utilization) throws IllegalArgumentException;

    /**
     * Gets an <b>estimation</b> of energy consumption using linear interpolation of the utilization
     * change for a given time interval.
     * <b>It's required to set a {@link PowerModel} in order to get power usage data.</b>
     *
     * @param fromUtilization the initial utilization percentage
     * @param toUtilization   the final utilization percentage
     * @param time            the time span (in seconds) between the initial and final utilization to compute the energy consumption
     * @return the <b>estimated</b> energy consumption in Watts-sec (Ws)
     */
    double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time);
}
