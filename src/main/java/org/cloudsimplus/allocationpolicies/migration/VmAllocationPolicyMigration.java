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
public sealed interface VmAllocationPolicyMigration extends VmAllocationPolicy
    permits VmAllocationPolicyMigrationDynamicUpperThreshold, VmAllocationPolicyMigrationAbstract, VmAllocationPolicyMigrationNull
{
    /**
     * An attribute that implements the Null Object Design Pattern for
     * {@link VmAllocationPolicyMigration} objects.
     */
    VmAllocationPolicyMigration NULL = new VmAllocationPolicyMigrationNull();

    /**
     * {@return true if there is any underloaded Host in the datacenter, false otherwise}
     */
    boolean isUnderloaded();

    /**
     * Checks if a host is currently under utilized, according the
     * conditions defined by the Allocation Policy implementation.
     *
     * @param host the host to check
     * @return true if the host is under utilized, false otherwise
     */
    boolean isUnderloaded(Host host);

    /**
     * {@return true if there is any overloaded Host in the datacenter, false otherwise}
     */
    boolean isOverloaded();

    /**
     * Checks if a host is currently over-utilized, according to the
     * conditions defined by the Allocation Policy.
     *
     * @param host the host to check
     * @return true if the host is overloaded, false otherwise
     */
    boolean isOverloaded(Host host);

    /**
     * Gets a host CPU utilization threshold to detect over utilization.
     * Whether it is a static or dynamically defined threshold depends on each allocation policy implementation.
     *
     * @param host the host to get the over utilization threshold
     * @return the over utilization threshold (a percentage value from 0 to 1)
     */
    double getOverUtilizationThreshold(Host host);

    /**
     * Sets the policy that defines how VMs are selected for migration.
     *
     * @param vmSelectionPolicy the new vm selection policy
     * @return this VmAllocationPolicy object
     */
    VmAllocationPolicy setVmSelectionPolicy(VmSelectionPolicy vmSelectionPolicy);

    /**
     * {@return the policy that defines how VMs are selected for migration}
     */
    VmSelectionPolicy getVmSelectionPolicy();

    /**
     * Gets the percentage of total Host CPU utilization
     * to indicate when a host is under used, so that its VMs can be migrated elsewhere.
     *
     * @return the under utilization threshold (a percentage value from 0 to 1)
     */
    double getUnderUtilizationThreshold();

    /**
     * Sets the percentage of total Host CPU utilization
     * to indicate when a host is under used, so that its VMs can be migrated elsewhere.
     *
     * @param underUtilizationThreshold the under utilization threshold (a percentage value from 0 to 1)
     */
    void setUnderUtilizationThreshold(double underUtilizationThreshold);

    /**
     * {@return true if there is any under or overloaded Host, false otherwise}
     */
    default boolean isUnderOrOverloaded() {
        return isUnderloaded() || isOverloaded();
    }
}
