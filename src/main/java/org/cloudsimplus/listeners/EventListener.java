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
package org.cloudsimplus.listeners;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

/**
 * An interface to define Observers (Listeners) that listen to specific changes in
 * the state of a given observable object (Subject).
 * This way, the EventListener gets notified when
 * the observed object has its state changed.
 *
 * <p>The interface was defined allowing the Subject object to have more than one state
 * to be observable. If the subject directly implements
 * this interface, it will allow only one kind of state change to be observable.
 * If the Subject has multiple state changes to be observed,
 * it can define multiple EventListener attributes
 * to allow multiple events to be observed.
 * </p>
 *
 * <p>Such Listeners are used for many simulation entities such as {@link Host}, {@link Vm} and {@link Cloudlet}.
 * Check the documentation of those interfaces above that provides some Listeners.
 * </p>
 *
 * @param <T> The class of the object containing information to be given to the
 * listener when the expected event happens.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
@FunctionalInterface
public interface EventListener<T extends EventInfo> {

    /**
     * An implementation of Null Object Pattern that makes nothing (it doesn't
     * perform any operation on each existing method). The pattern is used to
     * avoid {@link NullPointerException}'s and checking everywhere if a listener object
     * is not null to call its methods.
     */
    EventListener NULL = (EventListener<? extends EventInfo>) (EventInfo info) -> {};

    /**
     * Gets notified when the observed object (also called subject of
     * observation) has changed. This method has to be called by the observed
     * objects to notify its state change to the listener.
     *
     * @param info the data about the happened event.
     */
    void update(T info);
}
