/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.power;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkload;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;

/**
 * A {@link VmAllocationPolicy} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getOverUtilizationThreshold(PowerHost)} over} utilization.
 * It selects as the host to place a VM, that one having the least used amount of CPU
 * MIPS (Worst Fit policy), <b>disregarding energy consumption</b>.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class PowerVmAllocationPolicyMigrationWorstFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold {

    public PowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
            PowerVmSelectionPolicy vmSelectionPolicy,
            double overUtilizationThreshold)
    {
        super(vmSelectionPolicy, overUtilizationThreshold);
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
        super.<PowerHost>getHostList().sort(Comparator.comparingDouble(this::getUtilizationOfCpuMips));
        return (List<T>) super.<PowerHostSimple>getHostList();
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
            .filter(host -> isHostNotOverusedAfterAllocation(host, vm))
            .findFirst()
            .orElse(PowerHost.NULL);
    }

    /**
     * Gets the first under utilized host based on the {@link #getUnderUtilizationThreshold()}.
     * @param excludedHosts the list of hosts to ignore
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
            .filter(h -> isNotAllVmsMigratingOutNeitherAreVmsMigratingIn(h))
            .min(Comparator.comparingDouble(HostDynamicWorkload::getUtilizationOfCpu))
            .orElse(PowerHost.NULL);
    }

}
