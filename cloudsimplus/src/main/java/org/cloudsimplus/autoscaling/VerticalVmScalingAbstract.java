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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.autoscaling.resources.ResourceScalingGradual;
import org.cloudsimplus.autoscaling.resources.ResourceScalingInstantaneous;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract class for implementing {@link VerticalVmScaling}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.0.4
 */
public abstract class VerticalVmScalingAbstract extends VmScalingAbstract implements VerticalVmScaling {
    private Function<Vm, Double> upperThresholdFunction;
    private Function<Vm, Double> lowerThresholdFunction;
    private ResourceScaling resourceScaling;
    private final Class<? extends ResourceManageable> resourceClassToScale;
    private double scalingFactor;
    private Resource vmResource;

    /**
     * Creates a VerticalVmScalingAbstract.
     *
     * @param resourceClassToScale the class of Vm resource that this scaling object will request
     *                             up or down scaling (such as {@link Ram}.class,
     *                             {@link Bandwidth}.class or {@link Processor}.class).
     * @param resourceScaling {@link ResourceScaling} that defines how the resource has to be resized.
     * @param scalingFactor the factor (a percentage value in scale from 0 to 1)
     *                      that will be used to scale a Vm resource up or down,
     *                      whether such a resource is over or underloaded, according to the
     *                      defined predicates.
     *                      In the case of up scaling, the value 1 will scale the resource in 100%,
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
     * @return
     */
    private Class<? extends ResourceManageable> validateResourceClass(final Class<? extends ResourceManageable> resourceClass) {
        Objects.requireNonNull(resourceClass);
        if(Pe.class.equals(resourceClass)){
            return Processor.class;
        }

        return resourceClass;
    }

    @Override
    public Function<Vm, Double> getUpperThresholdFunction() {
        return upperThresholdFunction;
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

    /**
     * Throws an exception if the under and overload predicates are equal (to make clear
     * that over and under-load situations must be defined by different conditions)
     * or if any of them are null.
     *
     * @param lowerThresholdFunction the lower threshold function
     * @param upperThresholdFunction the upper threshold function
     * @throws IllegalArgumentException if the two functions are equal
     * @throws NullPointerException if any of the functions is null
     */
    private void validateFunctions(
        final Function<Vm, Double> lowerThresholdFunction,
        final Function<Vm, Double> upperThresholdFunction)
    {
        Objects.requireNonNull(lowerThresholdFunction);
        Objects.requireNonNull(upperThresholdFunction);
        if(upperThresholdFunction.equals(lowerThresholdFunction)){
            throw new IllegalArgumentException("Lower and Upper utilization threshold functions cannot be equal.");
        }
    }

    @Override
    public Function<Vm, Double> getLowerThresholdFunction() {
        return lowerThresholdFunction;
    }

    /**
     * {@inheritDoc}
     * <p>This class' constructors define a {@link ResourceScalingGradual}
     * as the default {@link ResourceScaling}.</p>
     * @param resourceScaling {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public final VerticalVmScaling setResourceScaling(final ResourceScaling resourceScaling) {
        this.resourceScaling = Objects.requireNonNull(resourceScaling);
        return this;
    }

    @Override
    public double getScalingFactor() {
        return scalingFactor;
    }

    @Override
    public final VerticalVmScaling setScalingFactor(final double scalingFactor) {
        this.scalingFactor = scalingFactor;
        return this;
    }

    @Override
    public Resource getResource() {
        return vmResource;
    }

    @Override
    public final boolean requestUpScalingIfPredicateMatches(final VmHostEventInfo evt) {
        if(!isTimeToCheckPredicate(evt.getTime())) {
            return false;
        }

        final boolean requestedScaling = (isVmUnderloaded() || isVmOverloaded()) && requestUpScaling(evt.getTime());
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
        } else if(isVmOverloaded()) {
            return upperThresholdFunction;
        }

        return vm -> 0.0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>If a {@link ResourceScaling} implementation such as
     * {@link ResourceScalingGradual} or {@link ResourceScalingInstantaneous} are used,
     * it will rely on the {@link #getScalingFactor()} to compute the amount of resource to scale.
     * Other implementations may use the scaling factor by it is up to them.
     * </p>
     *
     * <h3>NOTE:</h3>
     * <b>The return of this method is rounded up to avoid
     * values between ]0 and 1[</b>. For instance, up scaling the number of CPUs in 0.5
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
    protected boolean requestUpScaling(final double time) {
        final DatacenterBroker broker = this.getVm().getBroker();
        broker.getSimulation().sendNow(broker, broker, CloudSimTag.VM_VERTICAL_SCALING, this);
        return true;
    }

    @Override
    public void setVm(final Vm vm) {
        super.setVm(vm);
        this.vmResource = vm.getResource(this.resourceClassToScale);
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
            "{}: {}: {} more {} allocated to {}: new capacity is {}. Current resource usage is {}%",
            vm.getSimulation().clockStr(),
            getClass().getSimpleName(),
            (long) getResourceAmountToScale(), resourceClassToScale.getSimpleName(),
            vm, vmResource.getCapacity(),
            vmResource.getPercentUtilization() * 100);
    }
}
