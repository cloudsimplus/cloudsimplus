/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

import java.util.List;

/**
 * This class represents the metrics of a SLA contract.
 *
 * Follows the standard used by Amazon Cloudwatch as at in:
<<<<<<< HEAD
 * <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>
=======
 * http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html
>>>>>>> upstream/master
 *
 * @author raysaoliveira
 */
public class SlaMetric {

    public static final String RESPONSE_TIME_NAME = "ResponseTime";
    public static final String CPU_UTILIZATION_NAME = "CpuUtilization";
    public static final String WAIT_TIME_NAME = "WaitTime";

    private List<SlaMetricDimension> dimensions;
    private String metricName;

    /**
     * @return the dimensions
     */
    public List<SlaMetricDimension> getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(List<SlaMetricDimension> dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * @return the metricName
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * @param metricName the metricName to set
     */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Checks if this is a response time metric.
     *
     * @return
     */
    public boolean isReponseTime() {
        return this.metricName.trim().equals(RESPONSE_TIME_NAME);
    }

    /**
     * Checks if this is a CPU utilization metric.
     *
     * @return
     */
    public boolean isCpuUtilization() {
        return this.metricName.trim().equals(CPU_UTILIZATION_NAME);
    }

    /**
     * Checks if this is a wait time metric.
     *
     * @return
     */
    public boolean isWaitTime() {
        return this.metricName.trim().equals(WAIT_TIME_NAME);
    }

    @Override
    public String toString() {
        return "Metric{name =" + metricName + ",  dimensions = " + dimensions + '}';
    }
}
