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
package org.cloudsimplus.vms;

import org.cloudsimplus.hosts.Host;

import java.util.function.Function;

/**
 * Computes resource utilization statistics for a specific resource on a given {@link Host}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public class HostResourceStats extends ResourceStats<Host> {
    public static final HostResourceStats NULL = new HostResourceStats(Host.NULL, host -> 0.0) { @Override public boolean add(double time) { return false; }};

    /**
     * Creates a HostResourceStats to collect resource utilization statistics for a {@link Host}.
     * @param host the Host where the statistics will be collected
     * @param resourceUtilizationFunction a {@link Function} that receives a Host
     *                                    and returns the current resource utilization for that Host
     */
    public HostResourceStats(final Host host, final Function<Host, Double> resourceUtilizationFunction) {
        super(host, resourceUtilizationFunction);
    }

    /**
     * {@inheritDoc}.
     * The method is automatically called when the {@link Host} processing is updated.
     * @param time {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean add(final double time) {
        return super.add(time) && getMachine().isActive();
    }
}
