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
package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.resources.ResourceScalingGradual;
import org.cloudsimplus.autoscaling.resources.ResourceScaling;

import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link VerticalVmScaling} implementation that allows a {@link DatacenterBroker}
 * to perform on demand up or down scaling for some VM resource such as RAM, CPU or Bandwidth.
 *
 * <p>For each resource that is required to be scaled, a distinct VerticalVmScaling
 * instance must assigned to the VM to be scaled.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1.0
 */
public class VerticalVmScalingSimple extends VmScalingAbstract implements VerticalVmScaling {
    private ResourceScaling resourceScaling;
    private double scalingFactor;
    private Class<? extends ResourceManageable> resourceClassToScale;
    private Function<Vm, Double> upperUtilizationThresholdFunction;
    private Function<Vm, Double> lowerUtilizationThresholdFunction;

    /**
     * Creates a VerticalVmScalingSimple with a {@link ResourceScaling} scaling type.
     *
     * @param resourceClassToScale the class of Vm resource that this scaling object will request up or down scaling
     *  (such as {@link Ram}.class, {@link Bandwidth}.class or {@link Processor}.class).
     * @param scalingFactor the factor that will be used to scale a Vm resource up or down,
     * whether if such a resource is over or underloaded, according to the
     * defined predicates (a percentage value in scale from 0 to 1).
     * In the case of up scaling, the value 1 will scale the resource in 100%, doubling its capacity.
     * @see VerticalVmScaling#setResourceScaling(ResourceScaling)
     */
    public VerticalVmScalingSimple(Class<? extends ResourceManageable> resourceClassToScale, double scalingFactor){
        super();
        this.setResourceScaling(new ResourceScalingGradual());
        this.lowerUtilizationThresholdFunction = VerticalVmScaling.NULL.getLowerThresholdFunction();
        this.upperUtilizationThresholdFunction = VerticalVmScaling.NULL.getUpperThresholdFunction();
        this.setResourceClassToScale(resourceClassToScale);
        this.setScalingFactor(scalingFactor);
    }

    @Override
    public Function<Vm, Double> getUpperThresholdFunction() {
        return upperUtilizationThresholdFunction;
    }

    @Override
    public final VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction) {
        validateFunctions(lowerUtilizationThresholdFunction, upperThresholdFunction);
        this.upperUtilizationThresholdFunction = upperThresholdFunction;
        return this;
    }

    @Override
    public Function<Vm, Double> getLowerThresholdFunction() {
        return lowerUtilizationThresholdFunction;
    }

    @Override
    public final VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction) {
        validateFunctions(lowerThresholdFunction, upperUtilizationThresholdFunction);
        this.lowerUtilizationThresholdFunction = lowerThresholdFunction;
        return this;
    }

    @Override
    public final VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling) {
        Objects.requireNonNull(resourceScaling);
        this.resourceScaling = resourceScaling;
        return this;
    }

    /**
     * Throws an exception if the under and overload predicates are equal (to make clear
     * that over and underload situations must be defined by different conditions)
     * or if any of them are null.
     *
     * @param lowerThresholdFunction the lower threshold function
     * @param upperThresholdFunction the upper threshold function
     * @throws IllegalArgumentException if the two functions are equal
     * @throws NullPointerException if any of the functions is null
     */
    private void validateFunctions(Function<Vm, Double> lowerThresholdFunction, Function<Vm, Double> upperThresholdFunction) {
        Objects.requireNonNull(lowerThresholdFunction);
        Objects.requireNonNull(upperThresholdFunction);
        if(upperThresholdFunction.equals(lowerThresholdFunction)){
            throw new IllegalArgumentException("Lower and Upper utilization threshold functions cannot be equal.");
        }
    }

    @Override
    public Class<? extends ResourceManageable> getResourceClassToScale() {
        return this.resourceClassToScale;
    }

    @Override
    public final VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> resourceClassToScale) {
        Objects.requireNonNull(resourceClassToScale);
        this.resourceClassToScale = resourceClassToScale;
        if(Pe.class.equals(this.resourceClassToScale)){
            this.resourceClassToScale = Processor.class;
        }
        return this;
    }

    @Override
    public double getScalingFactor() {
        return scalingFactor;
    }

    @Override
    public final boolean requestScalingIfPredicateMatch(double time) {
        if(!isTimeToCheckPredicate(time)) {
            return false;
        }

        final boolean requestedScaling = (isVmUnderloaded() || isVmOverloaded()) && requestScaling(time);
        setLastProcessingTime(time);
        return requestedScaling;
    }

    @Override
    public boolean isVmUnderloaded() {
        return getVmResourceToScale().getPercentUtilization() < lowerUtilizationThresholdFunction.apply(getVm());
    }

    @Override
    public boolean isVmOverloaded() {
        return getVmResourceToScale().getPercentUtilization() > upperUtilizationThresholdFunction.apply(getVm());
    }

    @Override
    public Resource getVmResourceToScale() {
        return getVm().getResource(resourceClassToScale);
    }

    @Override
    public long getResourceAmountToScale() {
        return resourceScaling.getResourceAmountToScale(this);
    }

    @Override
    public Function<Vm, Double> getResourceUsageThresholdFunction(){
        if(isVmUnderloaded()) {
            return lowerUtilizationThresholdFunction;
        } else if(isVmOverloaded()) {
            return upperUtilizationThresholdFunction;
        }

        return vm -> 0.0;
    }

    @Override
    public final VerticalVmScaling setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
        return this;
    }

    @Override
    protected boolean requestScaling(double time) {
        final Vm vm = this.getVm();
        vm.getSimulation().sendNow(vm.getId(), vm.getBroker().getId(), CloudSimTags.VM_VERTICAL_SCALING, this);
        return true;
    }
}
