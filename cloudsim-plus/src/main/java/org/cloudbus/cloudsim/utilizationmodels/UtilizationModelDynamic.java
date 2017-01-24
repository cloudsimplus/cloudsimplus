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

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A Cloudlet {@link UtilizationModel} that allows to increases the utilization of the related resource along
 * the simulation time. It accepts a Lambda Expression that defines how the utilization increment must behave.
 * By this way, the class enables the developer to define such a behaviour when instantiating objects
 * of this class.
 *
 * <p>For instance, it is possible to use the class to arithmetically or geometrically increment resource usage,
 * but any kind of increment as logarithmic or exponential is possible.
 * For more details, see the {@link #setUtilizationIncrementFunction(BiFunction)}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class UtilizationModelDynamic extends UtilizationModelAbstract {
    /** @see #getInitialUtilization()  */
    private double initialUtilization = 0;

    /** @see #getMaxResourceUtilization() */
    private double maxResourceUtilization;

    /**
     * The time the utilization model was used for the first time.
     */
    private double startTime;

    private BiFunction<Double, Double, Double> utilizationIncrementFunction;

    /**
     * Creates a UtilizationModelDynamic with no initial utilization and resource utilization
     * unit defined in {@link Unit#PERCENTAGE}.
     */
    public UtilizationModelDynamic() {
        this(Unit.PERCENTAGE, 0);
    }

    /**
     * Creates a UtilizationModelDynamic with no initial utilization and resource utilization
     * {@link Unit} be defined according to the given parameter.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     */
    public UtilizationModelDynamic(Unit unit) {
        this(unit, 0);
    }

    /**
     * Creates a UtilizationModelDynamic that the initial resource utilization
     * will be defined according to the given parameter and the {@link Unit}
     * will be set as {@link Unit#PERCENTAGE}.
     *
     * @param initialUtilization the initial percentage of resource utilization
     */
    public UtilizationModelDynamic(final double initialUtilization) {
        this(Unit.PERCENTAGE, initialUtilization);
    }

    /**
     * Creates a UtilizationModelDynamic that the initial resource utilization
     * and the {@link Unit} will be defined according to the given parameters.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param initialUtilization the initial of resource utilization, that the unit depends
     *                           on the {@code unit} parameter
     */
    public UtilizationModelDynamic(Unit unit, final double initialUtilization) {
        super(unit);
        this.maxResourceUtilization = (unit == Unit.PERCENTAGE ? Conversion.HUNDRED_PERCENT : 0);
        this.startTime = -1;
        this.setInitialUtilization(initialUtilization);
        /**
         * Creates a default lambda function that doesn't increment the utilization along the time.
         * The {@link #setUtilizationIncrementFunction(BiFunction)} should be used to defined
         * a different increment function.
         * */
        utilizationIncrementFunction = (timeSpan, initialUsage) -> initialUsage;
    }


    @Override
    public double getUtilization(double time) {
        final double utilization = utilizationIncrementFunction.apply(timeSpan(time), initialUtilization);
        if (utilization <= 0) {
            return 0;
        }

        if (utilization > maxResourceUtilization && maxResourceUtilization > 0) {
            return maxResourceUtilization;
        }

        return utilization;
    }

    private double timeSpan(double time) {
        if(startTime <= -1){
            startTime = time;
        }
        return time - startTime;
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
    public final UtilizationModelDynamic setInitialUtilization(double initialUtilization) {
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
    public final UtilizationModelDynamic setMaxResourceUtilization(double maxResourceUsagePercentage) {
        validateUtilizationField("maxResourceUtilization", maxResourceUsagePercentage, ALMOST_ZERO);
        this.maxResourceUtilization = maxResourceUsagePercentage;
        return this;
    }

    /**
     * Gets the function that defines how the resource utilization will be incremented along the time.
     *
     * @return the utilization increment function
     * @see #setUtilizationIncrementFunction(BiFunction)
     */
    public BiFunction<Double, Double, Double> getUtilizationIncrementFunction() {
        return utilizationIncrementFunction;
    }

    /**
     * Sets the function that defines how the resource utilization will be incremented along the time.
     *
     * <p>Such a function must be one with two {@code Double} parameters, that when called internally by this UtilizationModel
     * will receive the {@code timeSpan} and the {@code initialUtilization}, that respectively represents the time interval
     * that has passed since the last time the {@link #getUtilization(double)} method was called and
     * the {@link #getInitialUtilization() initial resource utilization}
     * (that may be a percentage or absolute value, depending on the {@link #getUnit()}).
     * </p>
     *
     * <p>Such parameters that will be passed to the Lambda function given to this setter
     * must be used by the developer to define how the utilization will be incremented.
     * For instance, to define an arithmetic increment, a Lambda function
     * to be given to this setter could be as below:
     * </p>
     *
     * <p>{@code (timeSpan, initialUtilization) -> initialUtilization + (0.1 * timeSpan)}</p>
     *
     * <p>Considering that the UtilizationModel {@link Unit} was defined in {@link Unit#PERCENTAGE},
     * such an Lambda Expression will increment the usage in 10% for each second that has passed
     * since the last time the {@link #getUtilization(double)} was called.</p>
     *
     * <p>The value returned by the given Lambda Expression will be automatically validated
     * to avoid negative utilization or utilization over 100% (when the UtilizationModel {@link #getUnit() unit}
     * is defined in percentage).</p>
     *
     * <p>Defining a geometric progression for the resource utilization is as simple as changing the plus signal
     * to a multiplication signal.</p>
     *
     * @param utilizationIncrementFunction the utilization increment function to set
     * @return
     */
    public final UtilizationModelDynamic setUtilizationIncrementFunction(BiFunction<Double, Double, Double> utilizationIncrementFunction) {
        Objects.requireNonNull(utilizationIncrementFunction);
        this.utilizationIncrementFunction = utilizationIncrementFunction;
        return this;
    }
}
