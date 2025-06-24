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
 * A {@link VmAllocationPolicyMigration} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getOverUtilizationThreshold(Host) over} utilization.
 *
 * <p>It's a <b>Best Fit policy</b> which selects the Host having the most used amount of CPU
 * MIPS to place a given VM, <b>disregarding energy consumption</b>.</p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class VmAllocationPolicyMigrationBestFitStaticThreshold extends VmAllocationPolicyMigrationStaticThreshold {

    /**
     * Creates a VmAllocationPolicy.
     * It uses a {@link #DEF_OVER_UTILIZATION_THRESHOLD default over utilization threshold}
     * and a {@link #DEF_UNDER_UTILIZATION_THRESHOLD default under utilization threshold}.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @see #setUnderUtilizationThreshold(double)
     * @see #setOverUtilizationThreshold(double)
     */
    public VmAllocationPolicyMigrationBestFitStaticThreshold(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, DEF_OVER_UTILIZATION_THRESHOLD);
    }

    /**
     * Creates a VmAllocationPolicy with a given over utilization threshold.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @param overUtilizationThreshold {@link #setOverUtilizationThreshold(double) the over utilization threshold percent (between 0 and 1)}
     */
    public VmAllocationPolicyMigrationBestFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        this(vmSelectionPolicy, overUtilizationThreshold, null);
    }

    /**
     * Creates a new VmAllocationPolicy, changing the {@link Function} to select a Host for a Vm.
     * @param vmSelectionPolicy vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @param overUtilizationThreshold {@link #setOverUtilizationThreshold(double) the over utilization threshold percent (between 0 and 1)}
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     *                              Passing null makes the Function to be set as the default {@link #findHostForVm(Vm)}.
     * @see VmAllocationPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
     */
    public VmAllocationPolicyMigrationBestFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, overUtilizationThreshold, findHostForVmFunction);
    }

    /**
     * Gets the Host having the least available MIPS capacity (max used MIPS).
     *
     * <p>This method doesn't apply the additional filters from the super class.
     * This way, Host selection is performed ignoring energy consumption.
     * However, all the basic filters defined in the super class are ensured, since
     * this method is called just after they are applied.
     * </p>
     *
     * @param vm {@inheritDoc}
     * @param predicate {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate) {
        /* It's ignoring the super class intentionally to avoid the additional filtering performed there
         * and to apply a different method to select the Host to place the VM.*/
        return getHostList().stream().filter(predicate).max(comparingDouble(Host::getCpuMipsUtilization));
    }
}
