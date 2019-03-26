/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.selectionpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.UtilizationHistory;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.cloudbus.cloudsim.util.MathUtil.correlationCoefficients;

/**
 * A VM selection policy that selects for migration the VM with the Maximum Correlation Coefficient (MCC) among
 * a list of migratable VMs.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a></li>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class VmSelectionPolicyMaximumCorrelation implements VmSelectionPolicy {

    /** @see #getFallbackPolicy() */
    private VmSelectionPolicy fallbackPolicy;

    /**
     * Instantiates a new PowerVmSelectionPolicyMaximumCorrelation.
     *
     * @param fallbackPolicy the fallback policy
     */
    public VmSelectionPolicyMaximumCorrelation(final VmSelectionPolicy fallbackPolicy) {
        super();
        setFallbackPolicy(fallbackPolicy);
    }

    @Override
    public Vm getVmToMigrate(final Host host) {
        final List<Vm> migratableVms = host.getMigratableVms();
        if (migratableVms.isEmpty()) {
            return Vm.NULL;
        }

        try {
            final List<Double> metrics = correlationCoefficients(getUtilizationMatrix(migratableVms));
            double maxMetric = Double.MIN_VALUE;
            int maxIndex = 0;
            for (int i = 0; i < metrics.size(); i++) {
                final double metric = metrics.get(i);
                if (metric > maxMetric) {
                    maxMetric = metric;
                    maxIndex = i;
                }
            }

            return migratableVms.get(maxIndex);
        } catch (IllegalArgumentException e) { // the degrees of freedom must be greater than zero
            return getFallbackPolicy().getVmToMigrate(host);
        }
    }

    /**
     * Gets the CPU utilization percentage matrix for a given list of VMs.
     *
     * @param vmList the VM list
     * @return the CPU utilization percentage matrix, where each line i
     * is a VM and each column j is a CPU utilization percentage history for that VM.
     */
    private double[][] getUtilizationMatrix(final List<Vm> vmList) {
        final int numberVms = vmList.size();
        final int minHistorySize = getMinUtilizationHistorySize(vmList);
        final double[][] utilization = new double[numberVms][minHistorySize];

        for (int i = 0; i < numberVms; i++) {
            final double[] vmUtilization = vmList.get(i).getUtilizationHistory().getHistory().values().stream().mapToDouble(v -> v).toArray();
            if (minHistorySize >= 0) {
                System.arraycopy(vmUtilization, 0, utilization[i], 0, minHistorySize);
            }
        }
        return utilization;
    }

    /**
     * Gets the min CPU utilization percentage history size between a list of VMs.
     *
     * @param vmList the VM list
     * @return the min CPU utilization percentage history size of the VM list
     */
    private int getMinUtilizationHistorySize(final List<Vm> vmList) {
        return vmList.stream()
            .map(Vm::getUtilizationHistory)
            .map(UtilizationHistory::getHistory)
            .mapToInt(Map::size)
            .min().orElse(0);
    }

    /**
     * Gets the fallback VM selection policy to be used when
     * the Maximum Correlation policy doesn't have data to be computed.
     *
     * @return the fallback policy
     */
    public VmSelectionPolicy getFallbackPolicy() {
        return fallbackPolicy;
    }

    /**
     * Sets the fallback VM selection policy to be used when
     * the Maximum Correlation policy doesn't have data to be computed.
     *
     * @param fallbackPolicy the new fallback policy
     */
    public final void setFallbackPolicy(final VmSelectionPolicy fallbackPolicy) {
        this.fallbackPolicy = Objects.requireNonNull(fallbackPolicy);
    }

}
