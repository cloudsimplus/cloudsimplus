/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class takes the wait time metric threshold in the sla contract
 * 
 * @author raysaoliveira
 */
public class WaitTime {
    
    private double minValueWaitTime;
    private double maxValueWaitTime;
    private SlaReader reader;
    
    
    public WaitTime(SlaReader reader) {
        this.reader = reader;
    }

    public void checkSlaViolations() throws FileNotFoundException {
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isWaitTime())
                .findFirst()
                .ifPresent(this::waitTimeThreshold);
    }

    private void waitTimeThreshold(SlaMetric metric) {
        double minValue = metric.getDimensions().stream()
                .filter(d -> d.isValueMin())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MIN_VALUE);

        double maxValue =
                metric.getDimensions().stream()
                    .filter(d -> d.isValueMax())
                    .map(d -> d.getValue())
                    .findFirst().orElse(Double.MAX_VALUE);
        
        minValueWaitTime = minValue;
        maxValueWaitTime = maxValue;
    }

    
    /**
     * @return the minValueWaitTime
     */
    public double getMinValueWaitTime() {
        return minValueWaitTime;
    }

    /**
     * @param minValueWaitTime the minValueWaitTime to set
     */
    public void setMinValueWaitTime(double minValueWaitTime) {
        this.minValueWaitTime = minValueWaitTime;
    }

    /**
     * @return the maxValueWaitTime
     */
    public double getMaxValueWaitTime() {
        return maxValueWaitTime;
    }

    /**
     * @param maxValueWaitTime the maxValueWaitTime to set
     */
    public void setMaxValueWaitTime(double maxValueWaitTime) {
        this.maxValueWaitTime = maxValueWaitTime;
    }
    
    
}
