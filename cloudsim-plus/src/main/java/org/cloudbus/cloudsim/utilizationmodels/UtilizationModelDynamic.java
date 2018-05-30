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
import java.util.function.Function;

/**
 * A Cloudlet {@link UtilizationModel} that allows to increase the utilization of the related resource along
 * the simulation time. It accepts a Lambda Expression that defines how the utilization increment must behave.
 * By this way, the class enables the developer to define such a behaviour when instantiating objects
 * of this class.
 *
 * <p>For instance, it is possible to use the class to arithmetically or geometrically increment resource usage,
 * but any kind of increment as logarithmic or exponential is possible.
 * For more details, see the {@link #setUtilizationUpdateFunction(Function)}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class UtilizationModelDynamic extends UtilizationModelAbstract {
    private boolean readOnly;
    private double currentUtilization;

    /** @see #getMaxResourceUtilization() */
    private double maxResourceUtilization;

    /**
     * @see #setUtilizationUpdateFunction(Function)
     */
    private Function<UtilizationModelDynamic, Double> utilizationUpdateFunction;

    /**
     * The last time the utilization was updated.
     */
    private double previousUtilizationTime;

    /**
     * The time that the utilization is being currently requested.
     */
    private double currentUtilizationTime;

    /**
     * Creates a UtilizationModelDynamic with no initial utilization and resource utilization
     * unit defined in {@link Unit#PERCENTAGE}.
     *
     * <p><b>The utilization will not be dynamically incremented
     * until an increment function is defined by the {@link #setUtilizationUpdateFunction(Function)}.</b></p>
     * @see #setUtilizationUpdateFunction(Function)
     */
    public UtilizationModelDynamic() {
        this(Unit.PERCENTAGE, 0);
    }

    /**
     * Creates a UtilizationModelDynamic with no initial utilization and resource utilization
     * {@link Unit} be defined according to the given parameter.
     *
     * <p><b>The utilization will not be dynamically incremented
     * until that an increment function is defined by the {@link #setUtilizationUpdateFunction(Function)}.</b></p>
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     */
    public UtilizationModelDynamic(final Unit unit) {
        this(unit, 0);
    }

    /**
     * Creates a UtilizationModelDynamic that the initial resource utilization
     * will be defined according to the given parameter and the {@link Unit}
     * will be set as {@link Unit#PERCENTAGE}.
     *
     * <p><b>The utilization will not be dynamically incremented
     * until that an increment function is defined by the {@link #setUtilizationUpdateFunction(Function)}.</b></p>
     * @param initialUtilizationPercent the initial percentage of resource utilization
     */
    public UtilizationModelDynamic(final double initialUtilizationPercent) {
        this(Unit.PERCENTAGE, initialUtilizationPercent);
    }

    /**
     * Creates a UtilizationModelDynamic that the initial resource utilization
     * and the {@link Unit} will be defined according to the given parameters.
     *
     * <p><b>The utilization will not be dynamically incremented
     * until that an increment function is defined by the {@link #setUtilizationUpdateFunction(Function)}.</b></p>
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param initialUtilization the initial of resource utilization, that the unit depends
     *                           on the {@code unit} parameter
     */
    public UtilizationModelDynamic(final Unit unit, final double initialUtilization) {
        super(unit);
        this.readOnly = false;
        this.maxResourceUtilization = (unit == Unit.PERCENTAGE ? Conversion.HUNDRED_PERCENT : 0);
        this.previousUtilizationTime = 0;
        this.currentUtilizationTime = 0;
        this.setCurrentUtilization(initialUtilization);

        utilizationUpdateFunction = um -> um.currentUtilization;
    }

    /**
     * A copy constructor that creates a read-only UtilizationModelDynamic based on a source object.
     *
     * @param source the source UtilizationModelDynamic to create an instance from
     */
    @SuppressWarnings("CopyConstructorMissesField")
    protected UtilizationModelDynamic(final UtilizationModelDynamic source){
        this(source.getUnit(), source.currentUtilization);
        this.currentUtilizationTime = source.currentUtilizationTime;
        this.previousUtilizationTime = source.previousUtilizationTime;
        this.maxResourceUtilization = source.maxResourceUtilization;
        this.setSimulation(source.getSimulation());

        /** The copy constructor doesn't copy the utilizationUpdateFunction because
         * when this constructor is used, it sets the copy
         * to readonly. This way, the  {@link #getUtilization()} doesn't use such a function
         * to return the current utilization, but the last utilization value stored
         * in the {@link #currentUtilization} attribute.
         */
        this.readOnly = true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>It will automatically increment the {@link #getUtilization()}
     * by applying the {@link #setUtilizationUpdateFunction(Function) increment function}.</p>
     * @param time {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double getUtilization(final double time) {
        currentUtilizationTime = time;
        if(previousUtilizationTime != time) {
            /*
            Pass a copy of this current UtilizationModel to avoid it to be changed
            and also to enable the developer to call the getUtilization() method from
            his/her given utilizationUpdateFunction on such an instance,
            without causing infinity loop. Without passing a UtilizationModel clone,
            since the utilizationUpdateFunction function usually will call this current one, that in turns
            calls the utilizationUpdateFunction to update the utilization progress,
            it would lead to an infinity loop.
            */
            currentUtilization = utilizationUpdateFunction.apply(new UtilizationModelDynamic(this));
            previousUtilizationTime = time;
            if (currentUtilization <= 0) {
                currentUtilization = 0;
            }

            if (currentUtilization > maxResourceUtilization && maxResourceUtilization > 0) {
                currentUtilization = maxResourceUtilization;
            }
        }

        return currentUtilization;
    }

    @Override
    public double getUtilization() {
        return readOnly ? currentUtilization : super.getUtilization();
    }

    /**
     * Gets the time difference from the current simulation time to the
     * last time the resource utilization was updated.
     * @return
     */
    public double getTimeSpan(){
        return currentUtilizationTime - previousUtilizationTime;
    }

    /**
     * Sets the current resource utilization.
     *
     * <p>Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.</p>
     *
     * @param currentUtilization current resource utilization
     */
    private void setCurrentUtilization(final double currentUtilization) {
        validateUtilizationField("currentUtilization", currentUtilization);
        this.currentUtilization = currentUtilization;
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
     * @return
     */
    public final UtilizationModelDynamic setMaxResourceUtilization(final double maxResourceUsagePercentage) {
        validateUtilizationField("maxResourceUtilization", maxResourceUsagePercentage, ALMOST_ZERO);
        this.maxResourceUtilization = maxResourceUsagePercentage;
        return this;
    }

    /**
     * Sets the function defining how the resource utilization will be incremented or decremented along the time.
     *
     * <p>Such a function must require one {@link UtilizationModelDynamic} parameter and return the new resource utilization.
     * When this function is called internally by this {@code UtilizationModel},
     * it receives a read-only {@link UtilizationModelDynamic} instance and allow the developer using this {@code UtilizationModel} to
     * define how the utilization must be updated.
     *
     * <p>For instance, to define an arithmetic increment, a Lambda function
     * to be given to this setter could be defined as below:</p>
     * </p>
     *
     * <p>{@code um -> um.getUtilization() + um.getTimeSpan()*0.1}</p>
     *
     * <p>Considering the {@code UtilizationModel} {@link Unit} was defined in {@link Unit#PERCENTAGE},
     * such a Lambda Expression will increment the usage in 10% for each second that has passed
     * since the last time the utilization was computed.</p>
     *
     * <p>The value returned by the given Lambda Expression will be automatically validated
     * to avoid negative utilization or utilization over 100% (when the {@code UtilizationModel} {@link #getUnit() unit}
     * is defined in percentage). The function would be defined to decrement the utilization along the time,
     * by just changing the plus to a minus signal.</p>
     *
     * <p>Defining a geometric progression for the resource utilization is as simple as changing the plus signal
     * to a multiplication signal.</p>
     *
     * @param utilizationUpdateFunction the utilization increment function to set, that will receive the
     *                                  UtilizationModel instance and must return the new utilization value
     *                                  based on the previous utilization.
     * @return
     */
    public final UtilizationModelDynamic setUtilizationUpdateFunction(final Function<UtilizationModelDynamic, Double> utilizationUpdateFunction) {
        Objects.requireNonNull(utilizationUpdateFunction);
        this.utilizationUpdateFunction = utilizationUpdateFunction;
        return this;
    }
}
