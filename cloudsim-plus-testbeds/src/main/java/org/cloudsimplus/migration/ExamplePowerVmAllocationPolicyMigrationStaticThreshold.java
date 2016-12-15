/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.migration;

import java.io.FileNotFoundException;
import java.util.List;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudsimplus.sla.readJsonFile.SlaMetric;
import org.cloudsimplus.sla.readJsonFile.SlaReader;

/**
 *
 * @author raysaoliveira
 */
public class ExamplePowerVmAllocationPolicyMigrationStaticThreshold extends PowerVmAllocationPolicyMigrationAbstract {

    /**
     * @see #getOverUtilizationThreshold(PowerHost)
     */
    private double overUtilizationThreshold;

    /**
     * Creates a PowerVmAllocationPolicyMigrationStaticThreshold.
     *
     * @param vmSelectionPolicy    the policy that defines how VMs are selected for migration
     * @param overUtilizationThreshold the over utilization threshold
     */
    public ExamplePowerVmAllocationPolicyMigrationStaticThreshold(
        PowerVmSelectionPolicy vmSelectionPolicy,
        double overUtilizationThreshold) {
        super(vmSelectionPolicy);
        setOverUtilizationThreshold(overUtilizationThreshold);
    }
    
    private void VerifyCpuMetricMaxThreshold() throws FileNotFoundException {

        String METRICS_FILE = "/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-testbeds/src/main/java/org/cloudsimplus/sla/readJsonFile/SlaMetrics.json";
        SlaReader reader = new SlaReader(METRICS_FILE);
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isCpuUtilization())
                .findFirst()
                .ifPresent(this::catchCpuOverUtilization);
    }

    private void catchCpuOverUtilization(SlaMetric metric) {
        double maxValue = metric.getDimensions().stream()
                .filter(d -> d.isValueMax())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MAX_VALUE);
       
        setOverUtilizationThreshold(maxValue / 100);
    }

    /**
     * Sets the static host CPU utilization threshold to detect over utilization.
     * It is a percentage value from 0 to 1
     * that can be changed when creating an instance of the class.
     *
     * @param overUtilizationThreshold the new over utilization threshold
     */
    protected final void setOverUtilizationThreshold(double overUtilizationThreshold) {
        this.overUtilizationThreshold = overUtilizationThreshold;
    }

    /**
     * Gets the static host CPU utilization threshold to detect over utilization.
     * It is a percentage value from 0 to 1 that can be changed when creating an instance of the class.
     *
     * <p><b>This method always return the same over utilization threshold for any given host</b></p>
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc} (that is the same for any given host)
     */
    @Override
    public double getOverUtilizationThreshold(PowerHost host) {
        return overUtilizationThreshold;
    }

    /**
     * @return the overUtilizationThreshold
     */
    public double getOverUtilizationThreshold() {
        return overUtilizationThreshold;
    }
 
}
