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

import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/**
 * A class that implements the Null Object Design Pattern for {@link VerticalVmScaling}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VerticalVmScaling#NULL
 * @since CloudSim Plus 1.2.0
 */
final class VerticalVmScalingNull implements VerticalVmScaling {
    @Override public Class<? extends ResourceManageable> getResourceClass() { return ResourceManageable.class; }
    @Override public double getScalingFactor() {
        return 0;
    }
    @Override public Function<Vm, Double> getResourceUsageThresholdFunction() { return vm -> 0.0; }
    @Override public double getResourceAmountToScale() {
        return 0.0;
    }
    @Override public VerticalVmScaling setScalingFactor(double scalingFactor) {return this;}
    @Override public boolean isVmUnderloaded() { return false; }
    @Override public boolean isVmOverloaded() { return false; }
    @Override public Resource getResource() { return Resource.NULL; }
    @Override public boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt) {
        return false;
    }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public VmScaling setVm(Vm vm) {return this;}
    @Override public Function<Vm, Double> getUpperThresholdFunction() {
        return vm -> Double.MAX_VALUE;
    }
    @Override public VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction) { return this; }
    @Override public Function<Vm, Double> getLowerThresholdFunction() { return vm -> Double.MIN_VALUE; }
    @Override public VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction) { return this; }
    @Override public VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling) {return this;}
    @Override public long getAllocatedResource() { return 0; }
    @Override public boolean allocateResourceForVm() { return false; }
    @Override public void logResourceUnavailable() {/**/}
    @Override public void logDownscaleToZeroNotAllowed() {/**/}
}
