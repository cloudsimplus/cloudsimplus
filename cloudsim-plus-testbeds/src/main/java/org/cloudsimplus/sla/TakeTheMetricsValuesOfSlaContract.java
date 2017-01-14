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
package org.cloudsimplus.sla;

import java.io.FileNotFoundException;
import java.util.List;
import org.cloudsimplus.sla.readJsonFile.SlaMetric;
import org.cloudsimplus.sla.readJsonFile.SlaReader;

/**
 *
 * @author raysaoliveira
 */
public class TakeTheMetricsValuesOfSlaContract {
    private String metricsFile;

    public TakeTheMetricsValuesOfSlaContract(String file) {
        this.metricsFile = file;
    }
    
    private double minValueResponseTime;
    private double maxValueResponseTime;
    
    private double minValueCpuUtilization;
    private double maxValueCpuUtilization;
    
    private double minValueWaitTime;
    private double maxValueWaitTime;
    
    public void checkSlaViolations() throws FileNotFoundException {
        SlaReader reader = new SlaReader(metricsFile);
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isReponseTime())
                .findFirst()
                .ifPresent(this::checkResponseTimeViolation);

        metrics.stream()
                .filter(m -> m.isCpuUtilization())
                .findFirst()
                .ifPresent(this::checkCpuUtilizationViolation);

        metrics.stream()
                .filter(m -> m.isWaitTime())
                .findFirst()
                .ifPresent(this::checkWaitTimeViolation);
    }

    private void checkResponseTimeViolation(SlaMetric metric) {
        SlaMetricsMonitoring monitoring = new SlaMetricsMonitoring();
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

    private void checkCpuUtilizationViolation(SlaMetric metric) {
        SlaMetricsMonitoring monitoring = new SlaMetricsMonitoring();
        double minValue = metric.getDimensions().stream()
                .filter(d -> d.isValueMin())
                .map(d -> d.getValue())
                .findFirst().orElse(Double.MIN_VALUE);

        double maxValue =
                metric.getDimensions().stream()
                    .filter(d -> d.isValueMax())
                    .map(d -> d.getValue())
                    .findFirst().orElse(Double.MAX_VALUE);

        minValueCpuUtilization = minValue/100;
        maxValueCpuUtilization = maxValue/100;
    }

    private void checkWaitTimeViolation(SlaMetric metric) {
        SlaMetricsMonitoring monitoring = new SlaMetricsMonitoring();
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

    /**
     * @return the metricsFile
     */
    public String getMetricsFile() {
        return metricsFile;
    }

    /**
     * @param metricsFile the metricsFile to set
     */
    public void setMetricsFile(String metricsFile) {
        this.metricsFile = metricsFile;
    }

}
