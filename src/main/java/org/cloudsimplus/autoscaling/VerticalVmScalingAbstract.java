/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.autoscaling.resources.ResourceScalingGradual;
import org.cloudsimplus.autoscaling.resources.ResourceScalingInstantaneous;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.provisioners.ResourceProvisioner;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/**
 * An abstract class for implementing {@link VerticalVmScaling}.
 * <p>The constructors define a {@link ResourceScalingGradual}
 * as the default {@link ResourceScaling}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.0.4
 */
@Accessors
public non-sealed abstract class VerticalVmScalingAbstract extends VmScalingAbstract implements VerticalVmScaling {
    @Getter @NonNull
    private Function<Vm, Double> upperThresholdFunction;

    @Getter @NonNull
    private Function<Vm, Double> lowerThresholdFunction;

    @Setter @NonNull
    private ResourceScaling resourceScaling;
    private final Class<? extends ResourceManageable> resourceClassToScale;

    @Getter @Setter
    private double scalingFactor;
    private Resource vmResource;

    /**
     * Creates a VerticalVmScaling.
     *
     * @param resourceClassToScale the class of Vm resource that this scaling object will request
     *                             up or down scaling (such as {@link Ram}.class,
     *                             {@link Bandwidth}.class or {@link Processor}.class).
     * @param resourceScaling {@link ResourceScaling} that defines how the resource has to be resized.
     * @param scalingFactor the factor (a percentage value between 0 and 1)
     *                      that will be used to scale a Vm resource up or down,
     *                      whether such a resource is over or underloaded, according to the
     *                      defined predicates.
     *                      In the case of upscaling, value 1 will scale the resource in 100%,
     *                      doubling its capacity.
     * @see VerticalVmScaling#setResourceScaling(ResourceScaling)
     */
    public VerticalVmScalingAbstract(
        final Class<? extends ResourceManageable> resourceClassToScale,
        final ResourceScaling resourceScaling, final double scalingFactor)
    {
        super();
        this.setResourceScaling(resourceScaling);
        this.lowerThresholdFunction = VerticalVmScaling.NULL.getLowerThresholdFunction();
        this.upperThresholdFunction = VerticalVmScaling.NULL.getUpperThresholdFunction();
        this.resourceClassToScale = validateResourceClass(resourceClassToScale);
        this.setScalingFactor(scalingFactor);
    }

    /**
     * Validates the class of Vm resource that this scaling object will request up or down scaling.
     * Such a class can be {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class.
     * @param resourceClass the resource class to set
     * @return the validated resource class
     */
    private Class<? extends ResourceManageable> validateResourceClass(@NonNull final Class<? extends ResourceManageable> resourceClass) {
        if(Pe.class.equals(resourceClass)){
            return Processor.class;
        }

        return resourceClass;
    }

    @Override
    public final VerticalVmScaling setUpperThresholdFunction(final Function<Vm, Double> upperThresholdFunction) {
        validateFunctions(lowerThresholdFunction, upperThresholdFunction);
        this.upperThresholdFunction = upperThresholdFunction;
        return this;
    }

    @Override
    public final VerticalVmScaling setLowerThresholdFunction(final Function<Vm, Double> lowerThresholdFunction) {
        validateFunctions(lowerThresholdFunction, upperThresholdFunction);
        this.lowerThresholdFunction = lowerThresholdFunction;
        return this;
    }

    /// Validates lower and upper threshold functions,
    /// throwing an exception if the under and overload predicates:
    ///
    /// - are equal (to make clear that under and overload situations must be defined by different conditions);
    /// - or any of them are null.
    ///
    /// @param lowerThresholdFunction the lower threshold function
    /// @param upperThresholdFunction the upper threshold function
    /// @throws IllegalArgumentException if the two functions are equal
    /// @throws NullPointerException if any of the functions is null
    private void validateFunctions(
        @NonNull final Function<Vm, Double> lowerThresholdFunction,
        @NonNull final Function<Vm, Double> upperThresholdFunction)
    {
        if(upperThresholdFunction.equals(lowerThresholdFunction)){
            throw new IllegalArgumentException("Lower and Upper utilization threshold functions cannot be equal.");
        }
    }

    @Override
    public Resource getResource() {
        return vmResource;
    }

    @Override
    public final boolean requestUpScalingIfPredicateMatches(@NonNull final VmHostEventInfo evt) {
        if(!isTimeToCheckPredicate(evt.getTime())) {
            return false;
        }

        final boolean requestedScaling = (isVmUnderloaded() || isVmOverloaded()) && requestScaling(evt.getTime());
        setLastProcessingTime(evt.getTime());
        return requestedScaling;
    }

    @Override
    public Class<? extends ResourceManageable> getResourceClass() {
        return this.resourceClassToScale;
    }

    @Override
    public long getAllocatedResource() {
        return getResource().getAllocatedResource();
    }

    @Override
    public Function<Vm, Double> getResourceUsageThresholdFunction(){
        if(isVmUnderloaded()) {
            return lowerThresholdFunction;
        }

        if(isVmOverloaded()) {
            return upperThresholdFunction;
        }

        return vm -> 0.0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>If a {@link ResourceScaling} implementation such as
     * {@link ResourceScalingGradual} or {@link ResourceScalingInstantaneous} is used,
     * it will rely on the {@link #getScalingFactor()} to compute the amount of resource to scale.
     * </p>
     *
     * <h3>NOTE:</h3>
     * <b>The return of this method is rounded up to avoid
     * values smaller than 1. For instance, up scaling the number of CPUs in 0.5
     * means that half of a CPU should be added to the VM. Since number of CPUs is
     * an integer value, this 0.5 will be converted to zero, causing no effect.
     * For other resources such as RAM, adding 0.5 MB has not practical advantages either.
     * This way, the value is always rounded up.
     *
     * @return {@inheritDoc}
     */
    @Override
    public double getResourceAmountToScale() {
        return Math.ceil(resourceScaling.getResourceAmountToScale(this));
    }

    @Override
    protected boolean requestScaling(final double time) {
        final var broker = this.getVm().getBroker();
        broker.getSimulation().sendNow(broker, broker, CloudSimTag.VM_VERTICAL_SCALING, this);
        return true;
    }

    @Override
    public VmScalingAbstract setVm(final Vm vm) {
        super.setVm(vm);
        this.vmResource = vm.getResource(this.resourceClassToScale);
        return this;
    }

    private ResourceProvisioner getResourceProvisioner(){
        return getVm().getHost().getProvisioner(resourceClassToScale);
    }

    private Resource getHostResource(){
        return getVm().getHost().getResource(resourceClassToScale);
    }

    @Override
    public boolean allocateResourceForVm(){
        if (isNotHostResourceAvailable()) {
            return false;
        }

        final double newTotalVmResource = vmResource.getCapacity() + getResourceAmountToScale();
        final var isAllocated = getResourceProvisioner().allocateResourceForVm(getVm(), newTotalVmResource);
        if(isAllocated)
            logResourceAllocated();
        else logResourceUnavailable();

        return isAllocated;
    }

    public boolean isNotHostResourceAvailable(){
        return !getHostResource().isAmountAvailable(getResourceAmountToScale());
    }

    @Override
    public void logResourceUnavailable() {
        final Vm vm = getVm();
        LOGGER.warn(
            "{}: {}: {} requested more {} of {} capacity but the {} has just {} of available {}",
            vm.getSimulation().clockStr(),
            getClass().getSimpleName(),
            vm, (long) getResourceAmountToScale(),
            resourceClassToScale.getSimpleName(), vm.getHost(),
            getHostResource().getAvailableResource(), resourceClassToScale.getSimpleName());
    }

    @Override
    public void logDownscaleToZeroNotAllowed() {
        final Vm vm = getVm();
        LOGGER.warn(
            "{}: {}: {} {} is underloaded but cannot be downscaled to zero.",
            vm.getSimulation().clockStr(),
            getClass().getSimpleName(), vm,
            resourceClassToScale.getSimpleName());
    }

    private void logResourceAllocated(){
        final Vm vm = getVm();
        LOGGER.info(
            "{}: {}: {} {} more {} allocated to {}: new capacity is {}. Current resource usage is {}%",
            vm.getSimulation().clockStr(),
            getClass().getSimpleName(),
            (long) getResourceAmountToScale(), vmResource.getUnit(), resourceClassToScale.getSimpleName(),
            vm, vmResource.getCapacity(),
            vmResource.getPercentUtilization() * 100);
    }
}
