/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.power.PowerHost;

/**
 * An interface to be implemented by VM allocation policy for power-aware VMs
 * that detects {@link PowerHost} under and over CPU utilization.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface PowerVmAllocationPolicyMigration extends PowerVmAllocationPolicy {
    /**
     * Checks if host is over utilized.
     *
     * @param host the host
     * @return true, if the host is over utilized; false otherwise
     */
    boolean isHostOverUtilized(PowerHost host);

    /**
     * Gets the host CPU utilization threshold to detect over utilization.
     * It is a percentage value from 0 to 1.
     * Whether it is a static or dynamically defined threshold depends on each implementing class.
     *
     * @param host the host to get the over utilization threshold
     * @return the over utilization threshold
     */
    double getOverUtilizationThreshold(PowerHost host);

    /**
     * Checks if host is under utilized.
     *
     * @param host the host
     * @return true, if the host is under utilized; false otherwise
     */
    boolean isHostUnderUtilized(PowerHost host);

    /**
     * Gets the percentage of total CPU utilization
     * to indicate that a host is under used and its VMs have to be migrated.
     *
     * @return the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)
     */
    double getUnderUtilizationThreshold();

    /**
     * Sets the percentage of total CPU utilization
     * to indicate that a host is under used and its VMs have to be migrated.
     *
     * @param underUtilizationThreshold the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)
     */
    void setUnderUtilizationThreshold(double underUtilizationThreshold);

    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerVmAllocationPolicyMigration}
     * objects.
     */
    PowerVmAllocationPolicyMigration NULL = new PowerVmAllocationPolicyMigrationNull();

}
