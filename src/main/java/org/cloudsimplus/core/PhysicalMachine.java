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
import org.cloudsimplus.resources.FileStorage;
import org.cloudsimplus.vms.Vm;

/**
 * An interface to be implemented by different kinds of Physical Machines (PMs),
 * such as {@link Host}s.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public sealed interface PhysicalMachine extends Machine<FileStorage> permits Host
{
    /**
     * Computes the current relative percentage of the CPU a VM is using from the PM's total MIPS capacity.
     * If the capacity is 1000 MIPS and the VM is using 250 MIPS, it's equivalent to 25%
     * of the PM's capacity.
     *
     * @return the relative VM CPU usage percent (from 0 to 1)
     */
    default double getRelativeCpuUtilization(final Vm vm) {
        return getExpectedRelativeCpuUtilization(vm, vm.getCpuPercentUtilization());
    }

    /**
     * Computes what would be the relative percentage of the CPU a VM is using from a PM's total MIPS capacity,
     * considering that the VM's CPU load is at a given percentage.
     *
     * @param vm the VM to get its relative percentage of CPU utilization
     * @param vmCpuUtilizationPercent the VM's CPU utilization percentage for a given time
     * @return the relative VM CPU usage percent (from 0 to 1)
     */
    default double getExpectedRelativeCpuUtilization(final Vm vm, final double vmCpuUtilizationPercent){
        return vmCpuUtilizationPercent * getRelativeMipsCapacityPercent(vm);
    }

    /**
     * @return the percentage of the MIPS capacity a VM represents from the total PM's MIPS capacity.
     */
    default double getRelativeMipsCapacityPercent(final Vm vm) {
        return vm.getTotalMipsCapacity() / this.getTotalMipsCapacity();
    }

    /**
     * Computes the relative percentage of the RAM a VM is using from a PM's total capacity
     * for the current simulation time.
     *
     * @param vm the {@link Vm} to compute the relative utilization of RAM from this PM
     * @return the relative VM RAM usage percent (from 0 to 1)
     */
    default double getRelativeRamUtilization(final Vm vm){
        return vm.getRam().getAllocatedResource() / (double)this.getRam().getCapacity();
    }

    /**
     * Computes the relative percentage of the BW a VM is using from a PM's total capacity
     * for the current simulation time.
     *
     * @param vm the {@link Vm} to compute the relative utilization of BW from this PM
     * @return the relative VM BW usage percent (from 0 to 1)
     */
    default double getRelativeBwUtilization(final Vm vm){
        return vm.getBw().getAllocatedResource() / (double)this.getBw().getCapacity();
    }
}
