/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples.sla.readJsonFile;

/**
 * This class represents the violations of sla contract and their values 
 * designated as maximum and minimum acceptable to not violate the contract.
 * @author raysaoliveira
 */
public class Violations {
    
    private int id;
    private String metricNameViolation;
    private int max;
    private int min;
    private String unit;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the metricNameViolation
     */
    public String getMetricNameViolation() {
        return metricNameViolation;
    }

    /**
     * @param metricNameViolation the metricNameViolation to set
     */
    public void setMetricNameViolation(String metricNameViolation) {
        this.metricNameViolation = metricNameViolation;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    @Override
    public String toString() {
        return "Violations{" + "Metric Name =" + metricNameViolation + ", max = " + max + ", min = " + min +'}';
    }
}
