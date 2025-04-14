/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies.migration;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Comparator.comparingDouble;

/**
 * A {@link VmAllocationPolicy} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getOverUtilizationThreshold(Host) over} utilization.
 *
 * <p>It's a <b>Worst-Fit policy</b> which selects the Host having the least used amount of CPU
 * MIPS to place a given VM, <b>disregarding energy consumption</b>.</p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class VmAllocationPolicyMigrationWorstFitStaticThreshold extends VmAllocationPolicyMigrationStaticThreshold {

    /**
     * Creates a new VmAllocationPolicy, changing the {@link Function} to select a Host for a Vm.
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @param overUtilizationThreshold {@link #setOverUtilizationThreshold(double) the over utilization threshold percent (between 0 and 1)}
     */
    public VmAllocationPolicyMigrationWorstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        super(vmSelectionPolicy, overUtilizationThreshold);
    }

    /**
     * Creates a new VmAllocationPolicy, changing the {@link Function} to select a Host for a Vm.
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @param overUtilizationThreshold {@link #setOverUtilizationThreshold(double) the over utilization threshold percent (between 0 and 1)}
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     *                              Passing null makes the Function to be set as the default {@link #findHostForVm(Vm)}.
     * @see VmAllocationPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
     */
    public VmAllocationPolicyMigrationWorstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, overUtilizationThreshold, findHostForVmFunction);
    }

    /**
     * Gets the Host having the most available MIPS capacity (min used MIPS).
     *
     * <p>This method is ignoring the additional filtering performed by the super class.
     * This way, Host selection is performed ignoring energy consumption.
     * However, all the basic filters defined in the super class are ensured, since
     * this method is called just after they are applied.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate) {
        /*It's ignoring the super class to intentionally avoid the additional filtering performed there
        * and to apply a different method to select the Host to place the VM.*/
        return getHostList().stream().filter(predicate).min(comparingDouble(Host::getCpuMipsUtilization));
    }
}
