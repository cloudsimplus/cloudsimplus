/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
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
 * <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>
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
