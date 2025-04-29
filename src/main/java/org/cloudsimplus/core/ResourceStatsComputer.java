/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.core;

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.ResourceStats;
import org.cloudsimplus.vms.Vm;

/**
 * An interface that enables machines ({@link Vm}s or {@link Host}s)
 * to enable the calculation of statistics for its resource utilization.
 * Since that may be computationally complex and increase memory consumption,
 * you have to explicitly enable that by calling {@link #enableUtilizationStats()}.
 *
 * @param <T> the class in which resource utilization will be computed and stored
 * @author Manoel Camops da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public interface ResourceStatsComputer<T extends ResourceStats> {
    /**
     * Gets machine's CPU utilization percentage statistics (between [0 and 1]).
     * <p><b>WARNING:</b> You need to enable the data collection and computation of statistics
     * by calling {@link #enableUtilizationStats()}.</p>
     *
     * <p>The time interval in which utilization is collected is defined
     * by the {@link Datacenter#getSchedulingInterval()}.</p>
     *
     * @return an object containing the statistics
     */
    T getCpuUtilizationStats();

    /**
     * Enables the data collection and computation of utilization statistics.
     * @see #getCpuUtilizationStats()
     */
    void enableUtilizationStats();
}
