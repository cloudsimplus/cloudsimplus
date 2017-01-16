/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class takes the response time metric threshold in the sla contract
 * 
 * @author raysaoliveira
 */
public class ResponseTime {
    private SlaReader reader;
    private double minValueResponseTime;
    private double maxValueResponseTime;

    public ResponseTime(SlaReader reader) {
        this.reader = reader;
    }

    public void checkResponseTimeSlaContract() throws FileNotFoundException {
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isReponseTime())
                .findFirst()
                .ifPresent(this::responseTimeThreshold);

    }
    
    private void responseTimeThreshold(SlaMetric metric) {
        double minValue =
                metric.getDimensions().stream()
                    .filter(d -> d.isValueMin())
                    .map(d -> d.getValue())
                    .findFirst().orElse(Double.MIN_VALUE);
        double maxValue =
                metric.getDimensions().stream()
                    .filter(d -> d.isValueMax())
                    .map(d -> d.getValue())
                    .findFirst().orElse(Double.MAX_VALUE);
        
        minValueResponseTime = minValue;
        maxValueResponseTime = maxValue;
    }
    
    /**
     * @return the minValueResponseTime
     */
    public double getMinValueResponseTime() {
        return minValueResponseTime;
    }

    /**
     * @param minValueResponseTime the minValueResponseTime to set
     */
    public void setMinValueResponseTime(double minValueResponseTime) {
        this.minValueResponseTime = minValueResponseTime;
    }

    /**
     * @return the maxValueResponseTime
     */
    public double getMaxValueResponseTime() {
        return maxValueResponseTime;
    }

    /**
     * @param maxValueResponseTime the maxValueResponseTime to set
     */
    public void setMaxValueResponseTime(double maxValueResponseTime) {
        this.maxValueResponseTime = maxValueResponseTime;
    }
    
}
