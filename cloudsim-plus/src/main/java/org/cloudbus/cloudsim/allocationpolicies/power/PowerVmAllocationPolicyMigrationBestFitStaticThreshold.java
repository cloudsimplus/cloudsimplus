/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link VmAllocationPolicy} that uses a Static CPU utilization Threshold (THR) to
 * detect host {@link #getUnderUtilizationThreshold() under} and
 * {@link #getOverUtilizationThreshold(PowerHost)} over} utilization.
 *
 * <p>It's a Best Fit policy which selects the Host having the least used amount of CPU
 * MIPS to place a given VM, <b>disregarding energy consumption</b>.</p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class PowerVmAllocationPolicyMigrationBestFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold {
    public PowerVmAllocationPolicyMigrationBestFitStaticThreshold(
            PowerVmSelectionPolicy vmSelectionPolicy,
            double overUtilizationThreshold)
    {
        super(vmSelectionPolicy, overUtilizationThreshold);
    }

    /**
     * Gets the Host having the least available MIPS capacity (max used MIPS).
     *
     * <p>This method is ignoring the additional filtering performed by the super class.
     * This way, Host selection is performed ignoring energy consumption.
     * However, all the basic filters defined in the super class are ensured, since
     * this method is called just after they are applied.
     * </p>
     *
     * @param vm {@inheritDoc}
     * @param hostStream {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected Optional<PowerHost> findHostForVmInternal(Vm vm, Stream<PowerHost> hostStream) {
        /*It's ignoring the super class to intentionally avoid the additional filtering performed there
        * and to apply a different method to select the Host to place the VM.*/
        return hostStream.max(Comparator.comparingDouble(PowerHost::getUtilizationOfCpuMips));
    }
}
