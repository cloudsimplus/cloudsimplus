/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples.migration;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerHostSimple;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;

/**
 * A VM allocation policy that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and 
 * {@link #getUtilizationThreshold() over} utilization. 
 * It selects the host to place a VM, that one having the most remaining CPU 
 * which is enough to attend the VM (Worst Fit policy), 
 * <b>disregarding energy consumption</b>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NonPowerVmAllocationPolicyMigrationWorstFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold {
    /**@see #getUnderUtilizationThreshold() */
    private double underUtilizationThreshold = 0.35;
    
    public NonPowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
            List<PowerHost> hostList,
            PowerVmSelectionPolicy vmSelectionPolicy,
            double utilizationThreshold) {
        super(hostList, vmSelectionPolicy, utilizationThreshold);
    }

    /**
     * Gets a decreasing sorted host list based on CPU utilization in order to
     * provide a Best Fit host allocation for VMs.
     *
     * @param <T> The generic type.
     * @return The sorted list of hosts.
     * @see #findHostForVm(org.cloudbus.cloudsim.Vm, java.util.Set) 
     */
    @Override
    public <T extends Host> List<T> getHostList() {
        final List<PowerHostSimple> list = super.<PowerHostSimple>getHostList();
        list.sort(new PowerHostComparator());
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
                //Log.printConcatLine("\t#Host ", host.getId()," vm.getCurrentRequestedTotalMips: ", vm.getCurrentRequestedTotalMips()," host.getTotalMips: ", host.getTotalMips());
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
     * @see PowerHostComparator#compare(org.cloudbus.cloudsim.power.PowerHost,
     * org.cloudbus.cloudsim.power.PowerHost)
     */
    class PowerHostComparator implements Comparator<PowerHostSimple> {
        public PowerHostComparator() {}

        /**
         * Compares two hosts in an operation simulator to
         * {@code host1 &gt; host2}. The host with the most available CPU MIPS
         * is considered as greater than the other. Thus, in a sort operation,
         * the host will be sorted in increasing order of available CPU MIPS.
         *
         * @param host1 {@inheritDoc}
         * @param host2 {@inheritDoc}
         * @return {@inheritDoc }
         */
        @Override
        public int compare(PowerHostSimple host1, PowerHostSimple host2) {
            return Double.compare(getUtilizationOfCpuMips(host1), getUtilizationOfCpuMips(host2)); 
        }
    }
}
