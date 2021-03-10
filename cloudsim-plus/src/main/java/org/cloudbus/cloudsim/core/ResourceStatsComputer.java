package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.AbstractResourceStats;

/**
 * An interface that enables machines (VMs or Hosts) to enable the computation of statistics for its resource utilization.
 * Since that computation may be computationally complex and increase memory consumption,
 * you have to explicitly enable that by calling {@link #enableUtilizationStats()}.
 *
 * @param <T> the class in which resource utilization will be computed and stored
 * @author Manoel Camops da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public interface ResourceStatsComputer<T extends AbstractResourceStats> {
    /**
     * Gets machine's CPU utilization percentage statistics (between [0 and 1]).
     * <p><b>WARNING:</b> You need to enable the data collection and computation of statistics
     * by calling {@link #enableUtilizationStats()}.</p>
     *
     * <p>The time interval in which utilization is collected is defined
     * by the {@link Datacenter#getSchedulingInterval()}.</p>
     *
     * @return
     */
    T getCpuUtilizationStats();

    /**
     * Enables the data collection and computation of utilization statistics.
     * @see #getCpuUtilizationStats()
     */
    void enableUtilizationStats();
}
