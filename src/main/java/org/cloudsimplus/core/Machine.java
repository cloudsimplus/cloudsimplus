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

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.Resourceful;
import org.cloudsimplus.vms.Vm;

/**
 * Represents either a: (i) Physical Machine (PM) which implements the {@link Host} interface;
 * or (ii) Virtual Machine (VM), which implements the {@link Vm} interface.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 5.1.4
 * @param <T> the type of the storage device for the machine
 */
public sealed interface Machine<T extends Resource>
    extends ChangeableId, Resourceful, ExecDelayable
    permits Vm, PhysicalMachine, MachineNull
{
    /**
     * An attribute that implements the Null Object Design Pattern for {@link Machine} objects.
     */
    Machine NULL = new MachineNull();

    /**
     * @return a resource representing the machine bandwidth (bw) in Megabits/s.
     */
    Resource getBw();

    /**
     * @return a resource representing the machine memory in Megabytes.
     */
    Resource getRam();

    /**
     * @return the storage device of the machine with capacity in Megabytes.
     */
    T getStorage();

    /**
     * @return the overall number of {@link Pe}s the machine has,
     *         that include PEs of all statuses, including failed PEs.
     */
    long getPesNumber();

    /**
     * @return the individual MIPS capacity of any machine's {@link Pe},
     *         considering that all PEs have the same capacity.
     */
    double getMips();

    /**
     * @return total MIPS capacity across all PEs of the machine.
     */
    double getTotalMipsCapacity();

    /**
     * @return the {@link CloudSimPlus} instance that represents the simulation the Entity belongs to.
     */
    Simulation getSimulation();

    /**
     * Checks if the Machine has been idle for a given amount of time (in seconds).
     * @param time the time interval to check if the Machine has been idle (in seconds).
     *             If time is zero, it will be checked if the Machine is currently idle.
     *             If it's negative, even if the Machine is idle, it's considered
     *             that it isn't idle enough. This is useful if you don't want to perform
     *             any operation when the machine becomes idle (for instance,
     *             if idle machines might be shut down and a negative value is given,
     *             they won't).
     * @return true if the Machine has been idle as long as the given time;
     *         false if it's active or isn't idle long enough
     */
    default boolean isIdleEnough(final double time) {
        if(time < 0) {
            return false;
        }

        return getIdleInterval() >= time;
    }

    /**
     * Gets the time interval the Machine has been idle.
     * @return the idle time interval (in seconds) or 0 if the Machine is not idle
     */
    default double getIdleInterval() {
        final double clock = getSimulation().clock();

        /* If the busy time is negative, the machine was never busy.
        * This way, get the current clock instead, so that the difference will be zero,
        * indicating there is no idle time. */
        final double lastBusyTime = getLastBusyTime() > NOT_ASSIGNED ? getLastBusyTime() : clock;
        return clock - lastBusyTime;
    }

    /**
     * @return the last time the Machine was running some process (in seconds).
     */
    double getLastBusyTime();

    /**
     * @return true if the Machine is currently idle, false otherwise
     */
    default boolean isIdle(){
        return getIdleInterval() > 0;
    }

    /**
     * Validates a capacity for a machine resource.
     * @param capacity the capacity to check
     * @throws IllegalArgumentException if the given capacity is zero or negative
     */
    static void validateCapacity(final double capacity){
        if(capacity <= 0){
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
    }
}
