/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class takes the cpu utilization metric threshold in the sla contract.
 * 
 * @author raysaoliveira
 */
public class CpuUtilization {

    private double minValueCpuUtilization;
    private double maxValueCpuUtilization;
    private SlaReader reader;

    public CpuUtilization(SlaReader reader) {
        this.reader = reader;
    }

    public void checkCpuUtilizationSlaContract() throws FileNotFoundException {
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isCpuUtilization())
                .findFirst()
                .ifPresent(this::cpuUtilizationThreshold);
    }

    private void cpuUtilizationThreshold(SlaMetric metric) {
        double minValue = metric.getDimensions().stream()
                .filter(d -> d.isValueMin())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MIN_VALUE);

        double maxValue
                = metric.getDimensions().stream()
                .filter(d -> d.isValueMax())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MAX_VALUE);

        minValueCpuUtilization = minValue / 100;
        maxValueCpuUtilization = maxValue / 100;
    }

    /**
     * @return the minValueCpuUtilization
     */
    public double getMinValueCpuUtilization() {
        return minValueCpuUtilization;
    }

    /**
     * @param minValueCpuUtilization the minValueCpuUtilization to set
     */
    public void setMinValueCpuUtilization(double minValueCpuUtilization) {
        this.minValueCpuUtilization = minValueCpuUtilization;
    }

    /**
     * @return the maxValueCpuUtilization
     */
    public double getMaxValueCpuUtilization() {
        return maxValueCpuUtilization;
    }

    /**
     * @param maxValueCpuUtilization the maxValueCpuUtilization to set
     */
    public void setMaxValueCpuUtilization(double maxValueCpuUtilization) {
        this.maxValueCpuUtilization = maxValueCpuUtilization;
    }

}
