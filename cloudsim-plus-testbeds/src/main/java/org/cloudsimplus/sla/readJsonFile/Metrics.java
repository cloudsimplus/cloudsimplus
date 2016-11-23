/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

/**
 * This class represents the metrics of sla contract and their values.
 *
 * @author raysaoliveira
 */
public final class Metrics {

    private String metricName;
    private int value;
    private String unit;

    public Metrics(String name, int value, String unit){
       
        this.metricName = name;
        this.value =  value;
        this.unit = unit;
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
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
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
        return "Metric{" + "Metric Name =" + this.metricName + ", value = " + this.value + ", unit = " + this.unit + '}';
    }

}
