/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Enables cloning a {@link Vm} which was destroyed
 * due to a {@link HostFaultInjection Host Failure}.
 * It provides all the features to clone a Vm, simulating
 * the creating of another Vm from an snapshot of the failed one.
 * It also enables re-creating Cloudlets which were running
 * inside the failed VM.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.3
 */
public interface VmCloner {
    VmCloner NULL = new VmCloner() {
        @Override public int getClonedVmsNumber() { return 0;}
        @Override public Map.Entry<Vm, List<Cloudlet>> clone(Vm sourceVm) { return new HashMap.SimpleEntry<>(Vm.NULL, Collections.EMPTY_LIST); }
        @Override public VmCloner setVmClonerFunction(UnaryOperator<Vm> vmClonerFunction) { return this; }
        @Override public VmCloner setCloudletsClonerFunction(Function<Vm, List<Cloudlet>> cloudletsClonerFunction) { return this; }
        @Override public int getMaxClonesNumber() { return 0; }
        @Override public boolean isMaxClonesNumberReached() { return false; }
        @Override public VmCloner setMaxClonesNumber(int maxClonesNumber) { return this; }
    };

    /**
     * Gets the number of VMs cloned so far.
     * @return
     */
    int getClonedVmsNumber();

    /**
     * Clones a given {@link Vm} using the Vm Cloner Function
     * and their Cloudlets using the Clodlets Cloner Function,
     * binding the cloned Cloudlets to the cloned Vm.
     *
     * @param sourceVm the Vm to be cloned
     * @return a {@link Map.Entry} where the key is the cloned Vm
     * and the value is the List of cloned Cloudltes.
     * @see #setVmClonerFunction(UnaryOperator)
     * @see #setCloudletsClonerFunction(Function)
     */
    Map.Entry<Vm, List<Cloudlet>> clone(Vm sourceVm);

    /**
     * Sets the {@link UnaryOperator} to be used to clone {@link Vm}s.
     * It is a Function which, when called, creates a clone of a specific Vm.
     * @param vmClonerFunction the {@link Vm} cloner Function to set
     * @return
     */
    VmCloner setVmClonerFunction(UnaryOperator<Vm> vmClonerFunction);

    /**
     * Gets the {@link Function} to be used to clone Vm's {@link Cloudlet}s.
     * When the given Function is called, creates a clone of cloudlets
     * which were running inside a specific Vm.
     *
     * <p>Such a Function is used to recreate those Cloudlets
     * inside a clone of the failed VM. In this case, all the Cloudlets are
     * recreated from scratch into the cloned VM.
     * This way, when they are submitted to a broker,
     * they re-start execution from the beginning.
     * </p>
     *
     * @param cloudletsClonerFunction the {@link Cloudlet}s cloner Function to set
     * @return
     */
    VmCloner setCloudletsClonerFunction(Function<Vm, List<Cloudlet>> cloudletsClonerFunction);

    /**
     * Gets the maximum number of Vm clones to create.
     * For instance, if this value is equal to 2,
     * it means if all VMs from a given broker are destroyed multiple times,
     * a clone will be created only 2 times. If all VMs are destroyed again
     * for the 3rd time, no clone will be created.
     * The default value is 1.
     *
     * @return
     */
    int getMaxClonesNumber();

    /**
     * Checks if the maximum number of Vm clones to be created was reached.
     * @return true if the maximum number of clones was reached, false otherwise
     */
    boolean isMaxClonesNumberReached();

    VmCloner setMaxClonesNumber(int maxClonesNumber);
}
