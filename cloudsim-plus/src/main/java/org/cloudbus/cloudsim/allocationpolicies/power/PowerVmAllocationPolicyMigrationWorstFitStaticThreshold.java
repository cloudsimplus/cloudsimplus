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
package org.cloudbus.cloudsim.allocationpolicies.power;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;

/**
 * A {@link VmAllocationPolicy} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getOverUtilizationThreshold(PowerHost)} over} utilization.
 * It selects as the host to place a VM, that one having the least used amount of CPU
 * MIPS (Worst Fit policy), <b>disregarding energy consumption</b>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class PowerVmAllocationPolicyMigrationWorstFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold {

    public PowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
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
        super.<PowerHost>getHostList().sort(this::compareHosts);
        return (List<T>) super.<PowerHostSimple>getHostList();
    }

    /**
     * Compares two hosts. The host with the most available CPU MIPS
     * is considered to be greater than the other one. Thus, in a sort operation,
     * the host will be sorted in increasing order of available CPU MIPS.
     *
     * @param host1 the first host to be compared
     * @param host2 the second host to be compared
     * @return
     */
    private int compareHosts(PowerHost host1, PowerHost host2) {
        return Double.compare(getUtilizationOfCpuMips(host1), getUtilizationOfCpuMips(host2));
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
    public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
        return this.<PowerHost>getHostList().stream()
            .filter(host -> !excludedHosts.contains(host))
            .filter(host -> host.isSuitableForVm(vm))
            .filter(host -> !isHostOverUtilizedAfterAllocation(host, vm))
            .findFirst()
            .orElse(PowerHost.NULL);
    }

    /**
     * Gets the first under utilized host based on the {@link #getUnderUtilizationThreshold()}.
     * @param excludedHosts
     * @return the first under utilized host or null if there isn't any one
     */
    @Override
    protected PowerHost getUnderUtilizedHost(Set<? extends Host> excludedHosts) {
        /*@todo there is duplication with the method in the PowerVmAllocationPolicyMigrationAbstract class.
        * The only difference is that here is defined an underUtilizationThreshold.
        * Maybe the super class could defined an abstract Predicate (boolean method)
        * that performs the additional check to validate
        * */
        return this.<PowerHost>getHostList().stream()
            .filter(h -> !excludedHosts.contains(h))
            .filter(h -> h.getUtilizationOfCpu() > 0)
            .filter(h -> h.getUtilizationOfCpu() < getUnderUtilizationThreshold())
            .filter(h -> !allVmsAreMigratingOutOrThereAreVmsMigratingIn(h))
            .min((h1, h2) -> Double.compare(h1.getUtilizationOfCpu(), h2.getUtilizationOfCpu()))
            .orElse(PowerHost.NULL);
    }

}
