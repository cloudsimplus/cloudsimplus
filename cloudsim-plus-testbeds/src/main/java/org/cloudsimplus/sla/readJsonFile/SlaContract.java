/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raysaoliveira
 */
public class SlaContract {
    private List<SlaMetric> metrics;

    public SlaContract() {
        this.metrics = new ArrayList<>();
    }
    
    /**
     * @return the metrics
     */
    public List<SlaMetric> getMetrics() {
        return metrics;
    }

    /**
     * @param metrics the metrics to set
     */
    public void setMetrics(List<SlaMetric> metrics) {
        if(metrics == null){
            metrics = new ArrayList<>();
        }
        this.metrics = metrics;
    }
}
