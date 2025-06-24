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
package org.cloudsimplus.faultinjection;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A basic implementation of a {@link VmCloner}.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.2
 */
@Accessors
public class VmClonerSimple implements VmCloner {
    /** @see #setVmClonerFunction(UnaryOperator)  */
    @Setter @NonNull
    private UnaryOperator<Vm> vmClonerFunction;

    /** @see #setCloudletsClonerFunction(Function)  */
    @Setter @NonNull
    private Function<Vm, List<Cloudlet>> cloudletsClonerFunction;

    /** @see #getMaxClonesNumber() */
    @Getter @Setter
    private int maxClonesNumber;

    /** @see #getClonedVmsNumber() */
    @Getter
    private int clonedVmsNumber;

    /**
     * Creates a {@link Vm} cloner which clones Vm at maximum once after all VMs fail on a given broker.
     *
     * @param vmClonerFunction the {@link UnaryOperator} to be used to clone {@link Vm}s.
     * @param cloudletsClonerFunction the {@link Function} to be used to clone Vm's {@link Cloudlet}s.
     * @see #setMaxClonesNumber(int)
     */
    public VmClonerSimple(final UnaryOperator<Vm> vmClonerFunction, final Function<Vm, List<Cloudlet>> cloudletsClonerFunction){
        this.maxClonesNumber = 1;
        setVmClonerFunction(vmClonerFunction);
        setCloudletsClonerFunction(cloudletsClonerFunction);
    }

    @Override
    public Map.Entry<Vm, List<Cloudlet>> clone(@NonNull final Vm sourceVm) {
        final var clonedVm = vmClonerFunction.apply(sourceVm);
        final var clonedCloudletList = cloudletsClonerFunction.apply(sourceVm);
        if(clonedCloudletList.isEmpty()){
            LOGGER.warn(
                "{}: {}: There was no Cloudlet from {} in {} to clone.",
                sourceVm.getSimulation().clockStr(), getClass().getSimpleName(), sourceVm, sourceVm.getBroker());
        }

        clonedCloudletList.forEach(cloudlet -> cloudlet.setVm(clonedVm));
        clonedVmsNumber++;
        return new HashMap.SimpleEntry<>(clonedVm, clonedCloudletList);
    }

    @Override
    public boolean isMaxClonesNumberReached() {
        return clonedVmsNumber >= maxClonesNumber;
    }
}
