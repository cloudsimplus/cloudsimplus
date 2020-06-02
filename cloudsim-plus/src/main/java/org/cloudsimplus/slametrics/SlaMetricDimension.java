/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

/**
 * Represents a value for a specific metric of a SLA contract,
 * following the format defined by the
 * <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html">AWS CloudWatch</a>.
 *
 * <p>Each dimension contains the name of the metric, the minimum and maximum
 * acceptable values, and the metric unit. Each metric may have multiple dimensions.</p>
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 */
public final class SlaMetricDimension {
    private static final String MAX_VALUE_NAME ="maxValue";
    private static final String MIN_VALUE_NAME ="minValue";

    private String name;
    private String unit;
    private double value;

    public SlaMetricDimension(){
        this(0);
    }

    public SlaMetricDimension(final double value){
        this.name = "";
        this.unit = "";
        setValue(value);
    }

    public String getName() {
        return name;
    }

    public SlaMetricDimension setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the value of the dimension,
     * in absolute or percentage, according to the
     * {@link #getUnit()}.
     *
     * <p>When the unit is "Percent", the values are defined
     * in scale from 0 to 100%, but they are stored in this class
     * in scale from 0 to 1, because everywhere percentage values
     * are defined in this scale.</p>
     * @return
     */
    public double getValue() {
        return isPercent() ? value/100.0 : value;
    }

    public SlaMetricDimension setValue(final double value) {
        this.value = value;
        return this;
    }

    public boolean isMaxValue(){
        return this.name.trim().equals(MAX_VALUE_NAME);
    }

    public boolean isMinValue(){
        return this.name.trim().equals(MIN_VALUE_NAME);
    }

    /**
     * Checks if the unit is defined in percentage values.
     * @return
     */
    public boolean isPercent() {
        return "Percent".equalsIgnoreCase(unit);
    }

    @Override
    public String toString() {
        return String.format(
                    "Dimension{name = %s, value = %s}", name,
                    value == Double.MAX_VALUE ? "Double.MAX_VALUE" : String.format("%.4f", value));
    }

    /**
     * Gets the unit of the dimension, if "Percent" or "Absolute".
     * When the unit is "Percent", the values are defined
     * in scale from 0 to 100%, but they are stored in this class
     * in scale from 0 to 1, because everywhere percentage values
     * are defined in this scale.
     * @return
     */
    public String getUnit() {
        return unit;
    }

    public SlaMetricDimension setUnit(String unit) {
        this.unit = unit;
        return this;
    }
}
