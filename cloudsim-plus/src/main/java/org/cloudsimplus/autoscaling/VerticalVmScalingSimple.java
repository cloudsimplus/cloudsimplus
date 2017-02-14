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
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;

/**
 * A {@link VerticalVmScaling} implementation that allows a {@link DatacenterBroker}
 * to perform on demand up or down scaling for some VM resource such as RAM, CPU or Bandwidth.
 *
 * <p>For each resource that is required to be scaled, a distinct VerticalVmScaling
 * instance must assigned to the VM to be scaled.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1
 */
public class VerticalVmScalingSimple extends VmScalingAbstract implements VerticalVmScaling {
    private double scalingFactor;
    private Class<? extends ResourceManageable> resourceClassToScale;

    /**
     * Creates a VerticalVmScaling.
     *
     * @param resourceClassToScale the class of Vm resource that this scaling object will request up or down scaling
     *  (such as {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class).
     * @param scalingFactor the factor that will be used to scale a Vm resource up or down,
     * whether if such a resource is over or underloaded, according to the
     * defined predicates (a percentage value in scale from 0 to 1).
     * In the case of up scaling, the value 1 will scale the resource in 100%, doubling its capacity.
     */
    public VerticalVmScalingSimple(Class<? extends ResourceManageable> resourceClassToScale, double scalingFactor){
        super();
        this.setResourceClassToScale(resourceClassToScale);
        this.setScalingFactor(scalingFactor);
    }

    @Override
    public Class<? extends ResourceManageable> getResourceClassToScale() {
        return this.resourceClassToScale;
    }

    @Override
    public final VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> resourceClassToScale) {
        Objects.requireNonNull(resourceClassToScale);
        this.resourceClassToScale = resourceClassToScale;
        return this;
    }

    @Override
    public double getScalingFactor() {
        return scalingFactor;
    }

    @Override
    public double getResourceAmountToScale() {
        final ResourceManageable vmResource = getVm().getResource(resourceClassToScale);
        return vmResource.getCapacity() * scalingFactor;
    }

    @Override
    public final VerticalVmScaling setScalingFactor(double scalingFactor) {
        this.scalingFactor = (scalingFactor >= 0 ? scalingFactor : 0);
        return this;
    }

    @Override
    protected boolean requestScaling(double time) {
        final Vm vm = this.getVm();
        vm.getSimulation().sendNow(vm.getId(), vm.getBroker().getId(), CloudSimTags.VM_VERTICAL_SCALING, this);
        return true;
    }

}
