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

import java.util.function.Function;

/**
 * Computes resource utilization statistics for a specific resource on a given {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public class VmResourceStats extends ResourceStats<Vm> {
    public static final VmResourceStats NULL = new VmResourceStats(Vm.NULL, vm -> 0.0) { @Override public boolean add(double time) { return false; }};

    /**
     * Creates a VmResourceStats to collect resource utilization statistics for a VM.
     * @param machine the VM where the statistics will be collected
     * @param resourceUtilizationFunction a {@link Function} that receives a VM
     *                                    and returns the current resource utilization for that VM
     */
    public VmResourceStats(final Vm machine, final Function<Vm, Double> resourceUtilizationFunction) {
        super(machine, resourceUtilizationFunction);
    }
}
