/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.migration;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.sla.readJsonFile.SlaMetric;
import org.cloudsimplus.sla.readJsonFile.SlaReader;

/**
 *
 * @author raysaoliveira
 */
public class ExemploVmAllocationPolicyMigrationWorstFitStaticThreshold extends PowerVmAllocationPolicyMigrationStaticThreshold {

    /**
     * @see #getUnderUtilizationThreshold()
     */
    private double underUtilizationThreshold;
    
    public ExemploVmAllocationPolicyMigrationWorstFitStaticThreshold(
            PowerVmSelectionPolicy vmSelectionPolicy,
            double utilizationThreshold) {
        super(vmSelectionPolicy, utilizationThreshold);
    }

    private void VerifyCpuMetricMinThreshold() throws FileNotFoundException {

        String METRICS_FILE = "/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-testbeds/src/main/java/org/cloudsimplus/sla/readJsonFile/SlaMetrics.json";
        SlaReader reader = new SlaReader(METRICS_FILE);
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isCpuUtilization())
                .findFirst()
                .ifPresent(this::catchCpuUnderUtilization);
    }

    private void catchCpuUnderUtilization(SlaMetric metric) {
        double minValue = metric.getDimensions().stream()
                .filter(d -> d.isValueMin())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MIN_VALUE);
       
        setUnderUtilizationThreshold(minValue / 100);
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
     * Compares two hosts. The host with the most available CPU MIPS is
     * considered to be greater than the other one. Thus, in a sort operation,
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
     * Gets the first PM that has enough resources to host a given VM, which has
     * the most available capacity and will not be overloaded after the
     * placement.
     *
     * @param vm The VM to find a host to
     * @param excludedHosts A list of hosts to be ignored
     * @return a PM to host the given VM or null if there isn't any suitable
     * one.
     */
    @Override
    public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
        for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
            if (!excludedHosts.contains(host) && host.isSuitableForVm(vm)
                    && !isHostOverUtilizedAfterAllocation(host, vm)) {
                return host;
            }
        }

        return PowerHost.NULL;
    }

    /**
     * Gets the first under utilized host based on the
     * {@link #getUnderUtilizationThreshold()}.
     *
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
                .filter(h -> h.getUtilizationOfCpu() < underUtilizationThreshold)
                .filter(h -> !allVmsAreMigratingOutOrThereAreVmsMigratingIn(h))
                .min((h1, h2) -> Double.compare(h1.getUtilizationOfCpu(), h2.getUtilizationOfCpu()))
                .orElse(PowerHost.NULL);
    }

    /**
     * Gets the percentage of total CPU utilization to indicate that a host is
     * under used and its VMs have to be migrated.
     *
     * @return the under utilization threshold (in scale is from 0 to 1, where 1
     * is 100%)
     */
    public double getUnderUtilizationThreshold() {
        return underUtilizationThreshold;
    }

    /**
     * Sets the percentage of total CPU utilization to indicate that a host is
     * under used and its VMs have to be migrated.
     *
     * @param underUtilizationThreshold the under utilization threshold (in
     * scale is from 0 to 1, where 1 is 100%)
     */
    public void setUnderUtilizationThreshold(double underUtilizationThreshold) {
        this.underUtilizationThreshold = underUtilizationThreshold;
    }
}
