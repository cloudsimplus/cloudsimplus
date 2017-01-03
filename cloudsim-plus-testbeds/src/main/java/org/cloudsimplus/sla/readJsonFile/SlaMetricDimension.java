/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
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

/**
 * Represents the values for a specific metrics of a SLA contract.
 * 
 * Each dimension contains the name of the metric, the minimum and maximum 
 * acceptable value, and the metric unit. 
 * See: <http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html>
 * 
 * @author raysaoliveira
 */
public final class SlaMetricDimension {
    public static final String VALUE_MAX_NAME="valueMax";
    public static final String VALUE_MIN_NAME="valueMin";
    
    private String name;
    private double value;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        if(value < 0){
            throw new IllegalArgumentException("Metric value cannot be negative");
        }
        this.value = value;
    }
    
    /**
     * Checks if this is a max value dimension.
     *
     * @return 
     */
    public boolean isValueMax(){
        return this.name.trim().equals(VALUE_MAX_NAME);
    }

    /**
     * Checks if this is a min value dimension.
     *
     * @return 
     */
    public boolean isValueMin(){
        return this.name.trim().equals(VALUE_MIN_NAME);
    }

    @Override
    public String toString() {
        return String.format("Dimension{name = %s, value = %.2f}", name, value);
    }
    
    
    
    
}   