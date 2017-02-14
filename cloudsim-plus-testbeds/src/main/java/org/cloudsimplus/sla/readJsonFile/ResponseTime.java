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
