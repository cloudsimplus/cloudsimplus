/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;

import java.util.function.Function;

/**
 * A data class to store the attributes to create a {@link Host},
 * according to the data read from a line inside a "machine events" trace file.
 * Instance of this class are created by the {@link GoogleMachineEventsTraceReader}
 * and provided to the user's simulation.
 *
 * <p>In order to create such Hosts, the {@link GoogleMachineEventsTraceReader} requires
 * the developer to provide a {@link Function}
 * that creates Hosts according to the developer needs.</p>
 *
 * <p>The {@link GoogleMachineEventsTraceReader} cannot create the Hosts itself
 * by hardcoding some simulation specific parameters such as the {@link VmScheduler}
 * or {@link ResourceProvisioner}. This way, it request a {@link Function} implemented
 * by the developer using the {@link GoogleMachineEventsTraceReader} class
 * that has the custom logic to create Hosts.
 * However, this developer's {@link Function} needs to receive
 * the host parameters read from the trace file.
 * To avoid passing so many parameters to the developer's
 * Function, an instance of this class that wraps all these
 * parameters is used instead.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public final class MachineEvent extends MachineDataAbstract {
    private double timestamp;
    private long ram;
    private int cpuCores;

    /**
     * Gets the actual RAM capacity to be assigned to a Host,
     * according the {@link GoogleMachineEventsTraceReader#getMaxRamCapacity()}.
     *
     * @return
     * @see GoogleMachineEventsTraceReader.FieldIndex#RAM_CAPACITY
     */
    public long getRam(){
        return ram;
    }

    protected MachineEvent setRam(final long ram){
        this.ram = ram;
        return this;
    }

    /**
     * Gets the actual number of {@link Pe}s (CPU cores) to be assigned to a Host,
     * according the {@link GoogleMachineEventsTraceReader#getMaxCpuCores()}.
     *
     * @return
     * @see GoogleMachineEventsTraceReader.FieldIndex#CPU_CAPACITY
     */
    public int getCpuCores() {
        return cpuCores;
    }

    /* default */ MachineEvent setCpuCores(final int cpuCores) {
        this.cpuCores = cpuCores;
        return this;
    }

    /**
     * Gets the time the event happened (converted to seconds).
     *
     * @return
     * @see GoogleMachineEventsTraceReader.FieldIndex#TIMESTAMP
     */
    public double getTimestamp() {
        return timestamp;
    }

    /* default */ MachineEvent setTimestamp(final double timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
