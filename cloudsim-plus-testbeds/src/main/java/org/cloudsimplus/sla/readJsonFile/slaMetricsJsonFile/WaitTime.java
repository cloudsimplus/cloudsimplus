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
package org.cloudsimplus.sla.readJsonFile.slaMetricsJsonFile;

import org.cloudsimplus.sla.readJsonFile.slaMetricsJsonFile.SlaMetric;
import org.cloudsimplus.sla.readJsonFile.slaMetricsJsonFile.SlaReader;

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
