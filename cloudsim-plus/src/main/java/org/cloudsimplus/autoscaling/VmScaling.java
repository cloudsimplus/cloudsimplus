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

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;

import java.util.function.Predicate;

/**
 * An interface to allow implementing <a href="https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling">horizontal and vertical scaling</a> of VMs.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface VmScaling {
    /**
     * A {@link Predicate} that always returns false independently of any condition.
     */
    Predicate<Vm> FALSE_PREDICATE = vm -> false;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VmScaling}
     * objects.
     */
    VmScaling NULL = new VmScaling() {
        @Override public Vm getVm() { return Vm.NULL; }
        @Override public VmScaling setVm(Vm vm) { return this; }
        @Override public Predicate<Vm> getOverloadPredicate() { return FALSE_PREDICATE; }
        @Override public VmScaling setOverloadPredicate(Predicate<Vm> predicate) { return this; }
        @Override public Predicate<Vm> getUnderloadPredicate() { return FALSE_PREDICATE; }
        @Override public VmScaling setUnderloadPredicate(Predicate<Vm> predicate) { return this; }
        @Override public boolean requestScalingIfPredicateMatch(double time) { return false; }
    };

    /**
     * Gets the {@link Vm} that this Load Balancer is linked to.
     * @return
     */
    Vm getVm();

    /**
     * Sets a {@link Vm} to this Load Balancer. The broker will call this Load Balancer
     * in order to balance load when its Vm is over utilized.
     *
     * <p>When the VmScaling is assigned to a Vm, the Vm sets itself to the VmScaling object,
     * creating an association between the two objects.</b></p>
     * @param vm the Vm to set
     * @return
     */
    VmScaling setVm(Vm vm);

    /**
     * Gets a {@link Predicate} that defines when {@link #getVm() Vm} is overloaded or not,
     * that will make the Vm's broker to dynamically scale the VM up.
     *
     * @return
     * @see #setOverloadPredicate(Predicate)
     */
    Predicate<Vm> getOverloadPredicate();

    /**
     * Sets a {@link Predicate} that defines when {@link #getVm() Vm} is overloaded or not,
     * that will make the Vm's broker to dynamically scale the VM up.
     *
     * @param predicate a predicate that checks certain conditions
     *                  to define that the {@link #getVm() Vm} is over utilized.
     *                  The predicate receives the Vm to allow the it
     *                  to define the over utilization condition.
     *                  Such a condition can be defined, for instance,
     *                  based on Vm's {@link Vm#getCpuPercentUse(double)} CPU usage}
     *                  and/or any other VM resource usage.
     * @return
     */
    VmScaling setOverloadPredicate(Predicate<Vm> predicate);

    /**
     * Gets a {@link Predicate} that defines when {@link #getVm() Vm} is underloaded or not,
     * that will make the Vm's broker to dynamically scale Vm down.
     *
     * @return
     * @see #setUnderloadPredicate(Predicate)
     */
    Predicate<Vm> getUnderloadPredicate();

    /**
     * Sets a {@link Predicate} that defines when {@link #getVm() Vm} is underloaded or not,
     * that will make the Vm's broker to dynamically scale Vm down.
     *
     * @param predicate a predicate that checks certain conditions
     *                  to define that the {@link #getVm() Vm} is under utilized.
     *                  The predicate receives the Vm to allow the it
     *                  to define the over utilization condition.
     *                  Such a condition can be defined, for instance,
     *                  based on Vm's {@link Vm#getCpuPercentUse(double)} CPU usage}
     *                  and/or any other VM resource usage.
     * @return
     */
    VmScaling setUnderloadPredicate(Predicate<Vm> predicate);

    /**
     * Requests the Vm to be scaled up or down if it is over or underloaded, respectively.
     * The scaling request will be sent to the broker only
     * if the {@link #getOverloadPredicate() over} or {@link #getUnderloadPredicate() underload} condition meets.
     *
     * <p>The Vm to which this scaling object is related to, creates an {@link Vm#addOnUpdateProcessingListener(EventListener) UpdateProcessingListener}
     * that will call this method to check if it time to perform an down or up scaling, every time
     * the Vm processing is updated.</p>
     *
     * @param time current simulation time
     * @return true if the Vm is over or underloaded and up or down scaling request was sent to the broker, false otherwise
     */
    boolean requestScalingIfPredicateMatch(double time);
}
