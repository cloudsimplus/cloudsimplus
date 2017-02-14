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
