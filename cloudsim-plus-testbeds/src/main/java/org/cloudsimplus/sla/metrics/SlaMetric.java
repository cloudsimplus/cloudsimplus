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
package org.cloudsimplus.sla.metrics;

import java.util.ArrayList;
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
    public static final SlaMetric NULL = new SlaMetric();
    private static final SlaMetricDimension DEFAULT_MIN_DIMENSION = new SlaMetricDimension(-1);
    private static final SlaMetricDimension DEFAULT_MAX_DIMENSION = new SlaMetricDimension(Double.MAX_VALUE);

    private List<SlaMetricDimension> dimensions;
    private String metricName;

    public SlaMetric(){
        this("");
    }

    public SlaMetric(final String name){
        this.metricName = name;
        this.dimensions = new ArrayList<>();
    }

    public List<SlaMetricDimension> getDimensions() {
        return dimensions;
    }

    public SlaMetric setDimensions(List<SlaMetricDimension> dimensions) {
        this.dimensions = dimensions == null ? new ArrayList<>() : dimensions;
        return this;
    }

    public String getMetricName() {
        return metricName;
    }

    public SlaMetric setMetricName(String metricName) {
        this.metricName = metricName == null ? "" : metricName;
        return this;
    }

    @Override
    public String toString() {
        return "Metric{name = " + metricName + ",  dimensions = " + dimensions + '}';
    }

    /**
     * Gets a {@link SlaMetricDimension} representing the minimum value expected for the metric.
     * If the {@link SlaMetricDimension#getValue()} is a negative number, it means
     * there is no minimum value.
     * @return
     */
    public SlaMetricDimension getMinDimension() {
        System.out.println(DEFAULT_MIN_DIMENSION);
        return dimensions.stream()
            .filter(d -> d.isValueMin())
            .findFirst().orElse(DEFAULT_MIN_DIMENSION);
    }

    /**
     * Gets a {@link SlaMetricDimension} representing the maximum value expected for the metric.
     * If the {@link SlaMetricDimension#getValue()} is equals to {@link Double#MAX_VALUE}, it means
     * there is no maximum value.
     * @return
     */
    public SlaMetricDimension getMaxDimension() {
        return dimensions.stream()
            .filter(d -> d.isValueMax())
            .findFirst().orElse(DEFAULT_MAX_DIMENSION);
    }
}
