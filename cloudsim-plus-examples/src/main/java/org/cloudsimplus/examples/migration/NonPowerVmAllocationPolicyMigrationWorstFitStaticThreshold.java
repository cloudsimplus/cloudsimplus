/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
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
package org.cloudsimplus.examples.migration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;

/**
 * A {@link VmAllocationPolicy} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getUtilizationThreshold() over} utilization.
 * It selects as the host to place a VM, that one having the least used amount of CPU
 * MIPS (Worst Fit policy), <b>disregarding energy consumption</b>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NonPowerVmAllocationPolicyMigrationWorstFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold {
    /**@see #getUnderUtilizationThreshold() */
    private double underUtilizationThreshold = 0.35;

    public NonPowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
            PowerVmSelectionPolicy vmSelectionPolicy,
            double utilizationThreshold)
    {
        super(vmSelectionPolicy, utilizationThreshold);
    }

    /**
     * Gets an ascending sorted list of hosts based on CPU utilization,
     * providing a Worst Fit host allocation policy for VMs.
     *
     * @param <T> The generic type.
     * @return The sorted list of hosts.
     * @see #findHostForVm(Vm, java.util.Set)
     */
    @Override
    public <T extends Host> List<T> getHostList() {
        final List<PowerHostSimple> list = super.<PowerHostSimple>getHostList();
        Collections.sort(list, new PowerHostComparator());
        return (List<T>) list;
    }

    /**
     * Gets the first PM that has enough resources to host a given
     * VM, which has the most available capacity and will not
     * be overloaded after the placement.
     *
     * @param vm The VM to find a host to
     * @param excludedHosts A list of hosts to be ignored
     * @return a PM to host the given VM or null if there isn't
     * any suitable one.
     */
    @Override
    public PowerHostSimple findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
        for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
            if (!excludedHosts.contains(host) && host.isSuitableForVm(vm)
                    && !isHostOverUtilizedAfterAllocation(host, vm)) {
                return host;
            }
        }

        return null;
    }

    /**
     * Gets the first under utilized host based on the {@link #getUnderUtilizationThreshold()}.
     * @param excludedHosts
     * @return the first under utilized host or null if there isn't any one
     */
    @Override
    protected PowerHostSimple getUnderUtilizedHost(Set<? extends Host> excludedHosts) {
        double minUtilization = 1;
        PowerHostSimple underUtilizedHost = null;
        for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
            if (excludedHosts.contains(host)) {
                continue;
            }

            double utilization = host.getUtilizationOfCpu();
            if (utilization > 0 && utilization < underUtilizationThreshold
            &&  utilization < minUtilization && !areAllVmsMigratingOutOrAnyVmMigratingIn(host)) {
                underUtilizedHost = host;
                minUtilization = utilization;
            }
        }
        return underUtilizedHost;
    }

    /**
     * Gets the percentage of total CPU utilization
     * to indicate that a host is under used and its VMs have to be migrated.
     *
     * @return the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)
     */
    public double getUnderUtilizationThreshold() {
        return underUtilizationThreshold;
    }

    /**
     * Sets the percentage of total CPU utilization
     * to indicate that a host is under used and its VMs have to be migrated.
     *
     * @param underUtilizationThreshold the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)
     */
    public void setUnderUtilizationThreshold(double underUtilizationThreshold) {
        this.underUtilizationThreshold = underUtilizationThreshold;
    }

    /**
     * A {@link PowerHost} {@link Comparator} class that compares hosts based on
     * their availability of CPU MIPS, considering the capacity
     * of all Host PEs. This Comparator is used to allow sorting
     * a list of VMs in methods such as {@link #getHostList()}.
     *
     * @see PowerHostComparator#compare(PowerHostSimple, PowerHostSimple)
     */
    class PowerHostComparator implements Comparator<PowerHostSimple> {
        public PowerHostComparator() {}

        /**
         * Compares two hosts in an operation similar to
         * {@code host1 > host2}. The host with the most available CPU MIPS
         * is considered to be greater than the other one. Thus, in a sort operation,
         * the host will be sorted in increasing order of available CPU MIPS.
         *
         * @param host1 the first host to be compared
         * @param host2 the second host to be compared
         * @return {@inheritDoc}
         */
        @Override
        public int compare(PowerHostSimple host1, PowerHostSimple host2) {
            return Double.compare(getUtilizationOfCpuMips(host1), getUtilizationOfCpuMips(host2));
        }
    }
}
