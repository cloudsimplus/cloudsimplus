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
package org.cloudsimplus.traces.google;

import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.provisioners.ResourceProvisioner;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.schedulers.vm.VmScheduler;

import java.util.function.Function;

/// A data class to store the attributes to create a [Host],
/// according to the data read from a line inside a "machine events" trace file.
/// Instances of this class are created by the [GoogleMachineEventsTraceReader]
/// and provided to the user's simulation.
///
/// The [GoogleMachineEventsTraceReader] requires the developer to provide a [Function]
/// that creates Hosts according to the developer needs.
///
/// The [GoogleMachineEventsTraceReader] cannot create Hosts itself
/// by hard-coding some simulation-specific parameters such as the [VmScheduler]
/// or [ResourceProvisioner]. This way, it requests a [Function] implemented
/// by the developer using the [GoogleMachineEventsTraceReader] class
/// that has the custom logic to create Hosts.
/// However, this developer's [Function] needs to receive
/// the host parameters read from the trace file.
/// To avoid passing so many parameters to the developer's
/// Function, an instance of this class that wraps all these
/// parameters is used instead.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
@Getter @Setter
public final class MachineEvent extends MachineDataBase {
    /**
     * the time the event happened (converted to seconds).
     * @see MachineEventField#TIMESTAMP
     */
    private double timestamp;

    /**
     * The actual RAM capacity to be assigned to a Host,
     * according the {@link GoogleMachineEventsTraceReader#getMaxRamCapacity()}.
     * @see MachineEventField#RAM_CAPACITY
     */
    private long ram;

    /**
     * The actual number of {@link Pe}s (CPU cores) to be assigned to a Host,
     * according the {@link GoogleMachineEventsTraceReader#getMaxCpuCores()}.
     *
     * @see MachineEventField#CPU_CAPACITY
     */
    private int cpuCores;

    /** Constructor just to generate the Builder inner class. */
    @lombok.Builder
    private MachineEvent(double timestamp, long ram, int cpuCores, long machineId) {
        super(machineId);
        this.timestamp = timestamp;
        this.ram = ram;
        this.cpuCores = cpuCores;
    }
}
