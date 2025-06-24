/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies.migration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link VmAllocationPolicy} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getOverUtilizationThreshold(Host) over utilization}.
 *
 * <p>It's a <b>First Fit policy</b> which selects the first Host having enough capacity for a given VM,
 * <b>disregarding energy consumption</b>.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.2.0
 */
public class VmAllocationPolicyMigrationFirstFitStaticThreshold extends VmAllocationPolicyMigrationStaticThreshold {
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private int lastHostIndex;

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
    public VmAllocationPolicyMigrationFirstFitStaticThreshold(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, DEF_OVER_UTILIZATION_THRESHOLD);
    }

    /**
     * Creates a VmAllocationPolicy with a given over utilization threshold.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     * @param overUtilizationThreshold {@link #setOverUtilizationThreshold(double) the over utilization threshold percent (between 0 and 1)}
     * @see #setUnderUtilizationThreshold(double)
     * @see #setOverUtilizationThreshold(double)
     */
    public VmAllocationPolicyMigrationFirstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        this(vmSelectionPolicy, overUtilizationThreshold, null);
    }

    /**
     * Creates a VmAllocationPolicy, changing the {@link Function} to select a Host for a Vm.
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     * @param overUtilizationThreshold {@link #setOverUtilizationThreshold(double) the over utilization threshold percent (between 0 and 1)}
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     *                              Passing null makes the Function to be set as the default {@link #findHostForVm(Vm)}.
     * @see VmAllocationPolicy#setFindHostForVmFunction(BiFunction)
     */
    public VmAllocationPolicyMigrationFirstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, overUtilizationThreshold, findHostForVmFunction);
    }

    /**
     * Gets the first Host having enough capacity to place the VM.
     *
     * <p>This method is ignoring the additional filtering performed by the super class.
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
        /*It's ignoring the super class intentionally to avoid the additional filtering performed there
        * and to apply a different method to select the Host to place the VM.*/

        final List<Host> hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final Host host = hostList.get(lastHostIndex);
            if (predicate.test(host)) {
                return Optional.of(host);
            }

            lastHostIndex = ++lastHostIndex % getHostList().size();
        }

        return Optional.empty();
    }
}
