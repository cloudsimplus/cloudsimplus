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

import org.cloudsimplus.brokers.DatacenterBroker;

/**
 * An interface that represent data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when some events happen for a given {@link DatacenterBroker}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface DatacenterBrokerEventInfo extends EventInfo {

    /**
     * Gets the {@link DatacenterBroker} for which the event happened.
     * @return
     */
    DatacenterBroker getDatacenterBroker();

    /**
     * Gets a {@code DatacenterBrokerEventInfo} instance from the given parameters.
     *
     * @param listener the listener to be notified about the event
     * @param broker the {@link DatacenterBroker} where the event happened
     */
    static DatacenterBrokerEventInfo of(final EventListener<? extends EventInfo> listener, final DatacenterBroker broker) {
        final double time = broker.getSimulation().clock();
        return new DatacenterBrokerEventInfo() {
            @Override public double getTime() { return time; }
            @Override public DatacenterBroker getDatacenterBroker() { return broker; }
            @Override public EventListener<? extends EventInfo> getListener() { return listener; }
        };
    }
}
