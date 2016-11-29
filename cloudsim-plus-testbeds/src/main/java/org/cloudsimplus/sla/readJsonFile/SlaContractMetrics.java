/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.readJsonFile;

/**
 * Represents the values for a specific metrics of a SLA contract.
 *
 * @author raysaoliveira
 */
public final class SlaContractMetrics {

    private String name;
    private int valueMin;
    private int valueMax;
    private String unit;

    public SlaContractMetrics() {
    }

    /**
     * @return the name of the metric
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
     * @return the valueMin of the metric
     */
    public int getValueMin() {
        return valueMin;
    }

    /**
     * @param valueMin the valueMin to set
     */
    public void setValueMin(int valueMin) {
        this.valueMin = valueMin;
    }

    /**
     * @return the valueMax
     */
    public int getValueMax() {
        return valueMax;
    }

    /**
     * @param valueMax the valueMax to set
     */
    public void setValueMax(int valueMax) {
        this.valueMax = valueMax;
    }

    /**
     * @return the unit of the metric valueMin
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

    /**
     * Checks if the metric has a given name.
     *
     * @param name the name to check
     * @return true if the metric has the given name, false otherwise
     */
    public boolean isNamed(String name) {
        return this.name.trim().equals(name);
    }

    @Override
    public String toString() {
        return "Metric{name =" + name + ", valueMin = " + valueMin + ", valueMax = " + valueMax + ", unit = " + unit + '}';
    }

}
