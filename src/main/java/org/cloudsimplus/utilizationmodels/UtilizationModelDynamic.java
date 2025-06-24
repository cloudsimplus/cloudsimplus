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
package org.cloudsimplus.utilizationmodels;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.util.Conversion;

import java.util.function.Function;

/**
 * A {@link Cloudlet} {@link UtilizationModel} that allows to increase the utilization of
 * the related resource along the simulation time.
 * It accepts a Lambda Expression that defines how the utilization increment must behave.
 * This way, the class enables the developer to define such a behavior when instantiating
 * objects of this class.
 *
 * <p>For instance, it is possible to use the class to arithmetically or
 * geometrically increment resource usage, but any kind of increment as
 * logarithmic or exponential is possible.
 * For more details, see the {@link #setUtilizationUpdateFunction(Function)}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class UtilizationModelDynamic extends UtilizationModelAbstract {
    /// Indicates whether the utilization model is readonly or not.
    /// It's set to true when using the
    /// [copy constructor][#UtilizationModelDynamic(UtilizationModelDynamic)]
    /// to clone the current UtilizationModel instance.
    ///
    /// Such a clone is given as parameter when the
    /// [#utilizationUpdateFunction] is called to inform
    /// the researcher's simulation that the utilization has changed.
    /// Check the copy constructor documentation for more details on how this attribute is used.
    private boolean readOnly;
    private double currentUtilization;

    /// The maximum amount of resource that will be used.
    ///
    /// **WARNING:** Such a value can be a percentage in scale from [0 to 1] or an absolute value,
    /// depending on the [#getUnit()].
    @Getter
    private double maxResourceUtilization;

    /// A function defining how the resource utilization will be
    /// incremented or decremented along the time.
    ///
    /// Such a function must require one [UtilizationModelDynamic] parameter
    /// and return the new resource utilization.
    /// When this function is called internally by this `UtilizationModel`,
    /// it receives a read-only [UtilizationModelDynamic] instance and allows
    /// the developer using this `UtilizationModel` to
    /// define how the utilization must be updated.
    ///
    /// For instance, to define an arithmetic increment, a Lambda function
    /// to be given to this setter could be defined as below:
    ///
    /// ```java
    /// um -> um.getUtilization() + um.getTimeSpan()*0.1
    /// ```
    ///
    /// Considering the `UtilizationModel` [Unit] was defined in [Unit#PERCENTAGE],
    /// such a Lambda Expression will increment the usage in 10% for each second that has passed
    /// since the last time the utilization was computed.
    ///
    /// The value returned by the given Lambda Expression will be automatically validated
    /// to avoid negative utilization or utilization over 100%
    /// (when the `UtilizationModel` [unit][#getUnit()] is defined in percentage).
    /// The function would be defined to decrement the utilization along the time,
    /// by just changing the plus to a minus signal.
    ///
    /// Implementing a geometric progression for defining resource utilization is as simple as
    /// changing the plus signal to a multiplication signal.
    @Setter @NonNull
    private Function<UtilizationModelDynamic, Double> utilizationUpdateFunction;

    /**
     * The last time the utilization was updated.
     */
    private double previousUtilizationTime;

    /**
     * The time that the utilization is being currently requested.
     */
    private double currentUtilizationTime;

    /// Creates a UtilizationModelDynamic with no initial utilization.
    /// The resource utilization unit is defined in [Unit#PERCENTAGE].
    ///
    /// **The utilization won't be dynamically incremented
    /// until an increment function is defined by the [#setUtilizationUpdateFunction(Function)].**
    /// @see #setUtilizationUpdateFunction(Function)
    public UtilizationModelDynamic() {
        this(Unit.PERCENTAGE, 0);
    }

    /// Creates a UtilizationModelDynamic with no initial utilization.
    /// The resource utilization [Unit] is defined according to the given parameter.
    ///
    /// **The utilization won't be dynamically incremented
    /// until an increment function is defined by the [#setUtilizationUpdateFunction(Function)].**
    ///
    /// @param unit the [Unit] that determines how the resource is used (for instance, if
    ///             resource usage is defined in percentage of the Vm resource or in absolute values)
    public UtilizationModelDynamic(final Unit unit) {
        this(unit, 0);
    }

    /// Creates a UtilizationModelDynamic that the initial resource utilization
    /// will be defined according to the given parameter and the [Unit]
    /// will be set as [Unit#PERCENTAGE].
    ///
    /// **The utilization will not be dynamically incremented
    /// until an increment function is defined by the [#setUtilizationUpdateFunction(Function)].**
    /// @param initialUtilizationPercent the initial percentage of resource utilization
    public UtilizationModelDynamic(final double initialUtilizationPercent) {
        this(Unit.PERCENTAGE, initialUtilizationPercent);
    }

    /// Creates a UtilizationModelDynamic with an initial resource utilization value
    /// and max resource utilization, where the [Unit] is set as [Unit#PERCENTAGE].
    ///
    /// **The utilization will not be dynamically incremented
    /// until an increment function is defined by the [#setUtilizationUpdateFunction(Function)].**
    /// @param initialUtilization the initial resource utilization, that the unit depends on the `unit` parameter
    /// @param maxResourceUtilization the maximum resource utilization
    public UtilizationModelDynamic(final double initialUtilization, final double maxResourceUtilization) {
        this(Unit.PERCENTAGE, initialUtilization, maxResourceUtilization);
    }

    /// Creates a UtilizationModelDynamic that the initial resource utilization
    /// and the [Unit] will be defined according to the given parameters.
    ///
    /// **The utilization will not be dynamically incremented
    /// until an increment function is defined by the [#setUtilizationUpdateFunction(Function)].**
    /// @param unit the [Unit] that determines how the resource is used (for instance, if
    ///             resource usage is defined in percentage of the Vm resource or in absolute values)
    /// @param initialUtilization the initial resource utilization, that the unit depends
    ///                           on the `unit` parameter
    public UtilizationModelDynamic(final Unit unit, final double initialUtilization) {
        this(unit, initialUtilization, unit == Unit.PERCENTAGE ? Conversion.HUNDRED_PERCENT : 0);
    }

    /// Creates a UtilizationModelDynamic that the initial resource utilization,
    /// max resource utilization and the [Unit]
    /// will be defined according to the given parameters.
    ///
    /// **The utilization will not be dynamically incremented
    /// until an increment function is defined by the [#setUtilizationUpdateFunction(Function)].**
    /// @param unit the [Unit] that determines how the resource is used (for instance, if
    ///             resource usage is defined in percentage of the Vm resource or in absolute values)
    /// @param initialUtilization the initial resource utilization, that the unit depends on the `unit` parameter
    /// @param maxResourceUtilization the maximum resource utilization
    public UtilizationModelDynamic(final Unit unit, final double initialUtilization, final double maxResourceUtilization) {
        super(unit);
        this.readOnly = false;
        this.setMaxResourceUtilization(maxResourceUtilization);
        this.previousUtilizationTime = 0;
        this.currentUtilizationTime = 0;
        this.setCurrentUtilization(initialUtilization);

        utilizationUpdateFunction = modelInstance -> modelInstance.currentUtilization;
    }

    /**
     * A copy constructor that creates a read-only UtilizationModelDynamic based on another one.
     *
     * @param source the source UtilizationModelDynamic to create an instance from
     */
    protected UtilizationModelDynamic(final UtilizationModelDynamic source){
        this(source, source.currentUtilization);

        /// The copy constructor doesn't copy the utilizationUpdateFunction because
        /// when this constructor is used, it sets the copy
        /// to readonly. This way, the [#getUtilization()] doesn't use such a function
        /// to return the current utilization, but the last utilization value stored
        /// in the [#currentUtilization] attribute.
        /// Without such a trick, if the researcher calls the [#getUtilization(double)]
        /// method inside the update function he/she assigned to the UtilizationModel,
        /// that will cause an infinite loop. This happens since the [#getUtilization(double)] will call
        /// the given function to increase the current utilization and return the current value.
        this.utilizationUpdateFunction = modelInstance -> modelInstance.currentUtilization;
        this.readOnly = true;
    }

    /**
     * A copy constructor that creates a UtilizationModelDynamic based on another one.
     *
     * @param source the source UtilizationModelDynamic to create an instance from
     * @param initialUtilization the initial resource utilization (in the same unit of the given UtilizationModelDynamic instance)
     */
    public UtilizationModelDynamic(@NonNull final UtilizationModelDynamic source, final double initialUtilization){
        this(source.getUnit(), initialUtilization);
        this.currentUtilizationTime = source.currentUtilizationTime;
        this.previousUtilizationTime = source.previousUtilizationTime;
        this.maxResourceUtilization = source.maxResourceUtilization;
        this.setSimulation(source.getSimulation());
        this.setUtilizationUpdateFunction(source.utilizationUpdateFunction);
    }

    /**
     * It will automatically increment the {@link #getUtilization()}
     * by applying the {@link #setUtilizationUpdateFunction(Function) increment function}.
     */
    @Override
    protected double getUtilizationInternal(final double time) {
        if(readOnly) {
            return currentUtilization;
        }

        currentUtilizationTime = time;
        if (previousUtilizationTime == time) {
            return currentUtilization;
        }

        /*
        Pass a copy of this current UtilizationModel to avoid it to be changed
        and also to enable the developer to call the getUtilization() method from
        his/her given utilizationUpdateFunction on such an instance,
        without causing infinity loop.

        Without passing a UtilizationModel clone,
        since the utilizationUpdateFunction function usually will call this current one,
        that in turn calls the utilizationUpdateFunction to update the utilization progress,
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

        return currentUtilization;
    }

    /**
     * @return the time difference from the current simulation time to the
     * last time the resource utilization was updated.
     */
    public double getTimeSpan(){
        return currentUtilizationTime - previousUtilizationTime;
    }

    /// Sets the current resource utilization.
    ///
    /// Such a value can be a percentage in scale from [0 to 1] or an absolute value,
    /// depending on the [#getUnit()].
    ///
    /// @param currentUtilization current resource utilization
    private void setCurrentUtilization(final double currentUtilization) {
        validateUtilizationField("currentUtilization", currentUtilization);
        this.currentUtilization = currentUtilization;
    }

    /// Sets the maximum amount of resource that will be used.
    ///
    /// **WARNING:** Such a value can be a percentage in scale from [0 to 1] or an absolute value,
    /// depending on the [#getUnit()].
    ///
    /// @param maxResourceUsage the maximum resource usage (in percentage or absolut value)
    public final UtilizationModelDynamic setMaxResourceUtilization(final double maxResourceUsage) {
        validateUtilizationField("maxResourceUtilization", maxResourceUsage, ALMOST_ZERO);
        this.maxResourceUtilization = maxResourceUsage;
        return this;
    }
}
