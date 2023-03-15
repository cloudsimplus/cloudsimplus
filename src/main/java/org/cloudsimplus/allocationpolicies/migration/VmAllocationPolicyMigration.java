/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies.migration;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;

/**
 * An interface to be implemented by a VM allocation policy
 * that detects {@link Host} under and over CPU utilization.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface VmAllocationPolicyMigration extends VmAllocationPolicy {
    /**
     * An attribute that implements the Null Object Design Pattern for
     * {@link VmAllocationPolicyMigration} objects.
     */
    VmAllocationPolicyMigration NULL = new VmAllocationPolicyMigrationNull();

    /**
     * Checks if there is any underloaded Host.
     * @return
     */
    boolean isUnderloaded();

    /**
     * Checks if host is currently under utilized, according the
     * conditions defined by the Allocation Policy.
     *
     * @param host the host
     * @return true, if the host is under utilized; false otherwise
     */
    boolean isUnderloaded(Host host);

    /**
     * Checks if there is any overloaded Host.
     * @return
     */
    boolean isOverloaded();

    /**
     * Checks if host is currently over utilized, according the
     * conditions defined by the Allocation Policy.
     *
     * @param host the host to check
     * @return true, if the host is over utilized; false otherwise
     */
    boolean isOverloaded(Host host);

    /**
     * Gets the host CPU utilization threshold to detect over utilization.
     * It is a percentage value from 0 to 1.
     * Whether it is a static or dynamically defined threshold depends on each implementing class.
     *
     * @param host the host to get the over utilization threshold
     * @return the over utilization threshold
     */
    double getOverUtilizationThreshold(Host host);

    /**
     * Sets the policy that defines how VMs are selected for migration.
     *
     * @param vmSelectionPolicy the new vm selection policy
     */
    VmAllocationPolicy setVmSelectionPolicy(VmSelectionPolicy vmSelectionPolicy);

    /**
     * Gets the policy that defines how VMs are selected for migration.
     *
     * @return the {@link VmSelectionPolicy}.
     */
    VmSelectionPolicy getVmSelectionPolicy();

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
     * Checks if there is any under or overloaded Host.
     * @return
     */
    default boolean isUnderOrOverloaded() {
        return isUnderloaded() || isOverloaded();
    }
}
