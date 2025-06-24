/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.slametrics;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/// Represents a metric of an SLA contract.
/// Follows the standard defined by
/// [AWS Cloudwatch](http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html).
///
/// For more details, check
/// [Raysa Oliveira's Master Thesis (only in Portuguese)](https://ubibliorum.ubi.pt/handle/10400.6/7839).
///
/// @author raysaoliveira
@Getter @ToString
public class SlaMetric {
    public static final SlaMetric NULL = new SlaMetric();
    private static final SlaMetricDimension DEFAULT_MIN_DIMENSION = new SlaMetricDimension(-1);
    private static final SlaMetricDimension DEFAULT_MAX_DIMENSION = new SlaMetricDimension(Double.MAX_VALUE);

    private List<SlaMetricDimension> dimensions;
    private String name;

    /**
     * Creates an SLA metric with no name.
     */
    public SlaMetric(){
        this("");
    }

    /**
     * Creates an SLA metric
     * @param name the metric name
     */
    public SlaMetric(@NonNull final String name){
        this.name = name;
        this.dimensions = new ArrayList<>();
    }

    public SlaMetric setDimensions(final List<SlaMetricDimension> dimensions) {
        this.dimensions = dimensions == null ? new ArrayList<>() : dimensions;
        return this;
    }

    public SlaMetric setName(final String name) {
        this.name = name == null ? "" : name;
        return this;
    }

    /**
     * @return a {@link SlaMetricDimension} representing the minimum value expected for the metric.
     * If the {@link SlaMetricDimension#getValue()} is a negative number, it means
     * there is no minimum value.
     */
    public SlaMetricDimension getMinDimension() {
        return dimensions
                .stream()
                .filter(SlaMetricDimension::isMinValue)
                .findFirst()
                .orElse(DEFAULT_MIN_DIMENSION);
    }

    /**
     * @return a {@link SlaMetricDimension} representing the maximum value expected for the metric.
     * If the {@link SlaMetricDimension#getValue()} is equals to {@link Double#MAX_VALUE},
     * it means there is no maximum value.
     */
    public SlaMetricDimension getMaxDimension() {
        return dimensions
                .stream()
                .filter(SlaMetricDimension::isMaxValue)
                .findFirst()
                .orElse(DEFAULT_MAX_DIMENSION);
    }
}
