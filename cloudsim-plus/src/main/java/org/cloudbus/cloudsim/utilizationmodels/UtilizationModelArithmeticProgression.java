/*
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
package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.util.Conversion;

/**
 * An Cloudlet {@link UtilizationModel} that uses Arithmetic Progression
 * to increases the utilization of the related resource along the simulation time.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class UtilizationModelArithmeticProgression extends UtilizationModelAbstract {
    /**
     * The value that represents 1%, taking a scale from 0 to 1, where 1 is 100%.
     */
    public static final double ONE_PERCENT = 0.1;

    /**@see #getUtilizationIncrementPerSecond() */
    private double utilizationIncrementPerSecond;

    /** @see #getInitialUtilization()  */
    private double initialUtilization = 0;

    /** @see #getMaxResourceUtilization() */
    private double maxResourceUtilization;

    /**
     * Creates a UtilizationModelArithmeticProgression that the resource utilization
     * will be defined in {@link Unit#PERCENTAGE} values.
     */
    public UtilizationModelArithmeticProgression() {
        super();
    }

    /**
     * Creates a UtilizationModelArithmeticProgression that the resource utilization
     * will be defined according to the given parameter.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     */
    public UtilizationModelArithmeticProgression(Unit unit) {
        super();
        setUnit(unit);
    }

    public UtilizationModelArithmeticProgression(final double utilizationIncrementPerSecond) {
        this();
        maxResourceUtilization = Conversion.HUNDRED_PERCENT;
        this.utilizationIncrementPerSecond = ONE_PERCENT;
        setUtilizationIncrementPerSecond(utilizationIncrementPerSecond);
    }

    /**
     * Instantiates a UtilizationModelProgressive,
     * setting the {@link #setUtilizationIncrementPerSecond(double) utilization increment}
     * and the {@link #setInitialUtilization(double) initial utilization}
     *
     * @param utilizationIncrementPerSecond
     * @param initialUtilization
     */
    public UtilizationModelArithmeticProgression(
        final double utilizationIncrementPerSecond,
        final double initialUtilization)
    {
        this(utilizationIncrementPerSecond);
        setInitialUtilization(initialUtilization);
    }

    @Override
    public double getUtilization(double time) {
        final double utilization = initialUtilization + (utilizationIncrementPerSecond * time);

        if(utilization <= 0) {
            return 0;
        }

        if(utilization > maxResourceUtilization) {
            return maxResourceUtilization;
        }

        return utilization;
    }
    /**
     * Gets the utilization to be incremented
     * at the total utilization returned by {@link #getUtilization(double)}
     * at every simulation second.
     *
     * <p>Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.</p>
     *
     * @return the utilization increment
     * @see #setUtilizationIncrementPerSecond(double)
     */
    public double getUtilizationIncrementPerSecond() {
        return utilizationIncrementPerSecond;
    }

    /**
     * Sets the utilization to be incremented
     * at the total utilization returned by {@link #getUtilization(double)}
     * at every simulation second.
     *
     * @param utilizationIncrementPerSecond the utilization increment to be set.
     * Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}. For instance, if the {@link #getUnit()} is set
     * as {@link Unit#PERCENTAGE} and it is given the value 0.1, it means that every
     * simulation second, the total utilization will be incremented by 1%.
     * If set a negative value, the utilization will be decreased every second
     * by the given value. If the value is set as 0, it means
     * the utilization will not change along the time.
     */
    public UtilizationModelArithmeticProgression setUtilizationIncrementPerSecond(double utilizationIncrementPerSecond) {
        validateUtilizationField("utilizationIncrementPerSecond", utilizationIncrementPerSecond, Double.MIN_VALUE);
        this.utilizationIncrementPerSecond = utilizationIncrementPerSecond;
        return this;
    }

    /**
     * Gets the initial utilization of resource
     * that cloudlets using this UtilizationModel will require
     * when they start to execute.
     *
     * <p>Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.</p>
     *
     * @return the initial utilization
     */
    public double getInitialUtilization() {
        return initialUtilization;
    }

    /**
     * Sets the initial utilization of resource
     * that cloudlets using this UtilizationModel will require
     * when they start to execute.
     *
     * <p>Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.</p>
     *
     * @param initialUtilization initial resource utilization
     */
    public UtilizationModelArithmeticProgression setInitialUtilization(double initialUtilization) {
        validateUtilizationField("initialUtilization", initialUtilization);
        this.initialUtilization = initialUtilization;
        return this;
    }

    /**
     * Gets the maximum amount of resource that will be used.
     *
     * <p>Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.</p>
     *
     * @return the maximum resource utilization
     */
    public double getMaxResourceUtilization() {
        return maxResourceUtilization;
    }

    /**
     * Sets the maximum amount of resource of resource that will be used.
     *
     * <p>Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.</p>
     *
     * @param maxResourceUsagePercentage the maximum resource usage
     */
    public UtilizationModelArithmeticProgression setMaxResourceUtilization(double maxResourceUsagePercentage) {
        validateUtilizationField("maxResourceUtilization", maxResourceUsagePercentage, ALMOST_ZERO);
        this.maxResourceUtilization = maxResourceUsagePercentage;
        return this;
    }
}
