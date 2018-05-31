/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.migration;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;

import java.util.List;
import java.util.Map;

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
     * An attribute that implements the Null Object Design Pattern for {@link VmAllocationPolicyMigration}
     * objects.
     */
    VmAllocationPolicyMigration NULL = new VmAllocationPolicyMigrationNull();

    /**
     * Gets a <b>read-only</b> map of the utilization history for each Host.
     *
     * @return the utilization history
     */
    Map<Host, List<Double>> getUtilizationHistory();

    /**
     * Gets a <b>read-only</b> map of metric history.
     *
     * @TODO the map stores different data. Sometimes it stores the upper
     * threshold, other times it stores utilization threshold or predicted
     * utilization. That is very confusing.
     *
     * @return the metric history
     */
    Map<Host, List<Double>> getMetricHistory();

    /**
     * Gets a <b>read-only</b> map of times when entries in each history list was added for each Host.
     * All history lists are updated at the same time.
     *
     * @return the time history
     */
    Map<Host, List<Double>> getTimeHistory();

    /**
     * Checks if host is currently under utilized, according the the
     * conditions defined by the Allocation Policy.
     *
     * @param host the host
     * @return true, if the host is under utilized; false otherwise
     */
    boolean isHostUnderloaded(Host host);

    /**
     * Checks if host is currently over utilized, according the the
     * conditions defined by the Allocation Policy.
     *
     * @param host the host to check
     * @return true, if the host is over utilized; false otherwise
     */
    boolean isHostOverloaded(Host host);

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
}
