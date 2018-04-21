/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.supply.PowerSupply;

/**
 * Provides a model for power consumption of hosts, depending on utilization of a critical system
 * component, such as CPU.
 * <b>This is the fundamental class to enable power-aware Hosts.</b> Despite all Hosts have a {@link PowerSupply} attribute,
 * it just provides power usage data if a PowerModel is set using the {@link PowerSupply#setPowerModel(PowerModel)}.
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
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a></li>
 * </ul>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 2.0
 */
public interface PowerModel {
    /**
     * A property that implements the Null Object Design Pattern for {@link Host}
     * objects.
     */
    PowerModel NULL = new PowerModel() {
        @Override public Host getHost() { return Host.NULL; }
        @Override public void setHost(Host host) {}
        @Override public double getPower(double utilization) throws IllegalArgumentException { return 0; }
    };

    Host getHost();

    void setHost(Host host);

    /**
     * Gets power consumption (in Watts/Second) of the Power Model, according to the utilization
     * percentage of a critical resource, such as CPU.
     *
     * @param utilization the utilization percentage (between [0 and 1]) of a
     * resource that is critical for power consumption.
     * @return the power consumption (in Watts/Second)
     * @throws IllegalArgumentException when the utilization percentage is not
     * between [0 and 1]
     */
    double getPower(double utilization) throws IllegalArgumentException;
}
