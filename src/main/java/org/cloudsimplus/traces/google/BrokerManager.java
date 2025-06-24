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
package org.cloudsimplus.traces.google;

import lombok.NonNull;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

/**
 * Manages creation and access to {@link DatacenterBroker}s used by
 * {@link GoogleTaskEventsTraceReader}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.0.1
 */
public final class BrokerManager {
    /**
     * A default broker to be used by all created Cloudlets.
     * @see #setDefaultBroker(DatacenterBroker)
     * @see #brokersMap
     */
    private DatacenterBroker defaultBroker;

    /**
     * A map of brokers created according to the username from the trace file,
     * representing a customer. Each key is the username field and the value is the created broker.
     * If a default {@link #defaultBroker} is set, the map is empty.
     * @see #getBrokers()
     */
    private final Map<String, DatacenterBroker> brokersMap;

    private final GoogleTaskEventsTraceReader reader;

    BrokerManager(@NonNull final GoogleTaskEventsTraceReader reader){
        this.reader = reader;
        this.brokersMap = new HashMap<>();
    }

    /**
     * @return the List of brokers created according to the username from the trace file,
     * representing a customer.
     *
     * @see #setDefaultBroker(DatacenterBroker)
     */
    public List<DatacenterBroker> getBrokers() {
        return defaultBroker == null ? new ArrayList<>(brokersMap.values()) : singletonList(defaultBroker);
    }

    /// Defines a default broker to will be used for all created Cloudlets.
    /// This way, the username field inside the trace file won't be used
    /// to dynamically create brokers.
    /// The [#getBrokers()] will only return a unitary list containing this broker.
    ///
    /// @param broker the broker for all created cloudlets, representing a single username (customer)
    public void setDefaultBroker(final DatacenterBroker broker) {
        this.defaultBroker = broker;
        this.brokersMap.clear();
    }


    /// Gets a [default broker (if ones was set)][#setDefaultBroker(DatacenterBroker)]
    /// or the one with the specified username (creating it if not yet).
    /// @param username the username of the broker
    /// @return (i) an already existing broker with the given username or a new one if not created yet;
    ///         (ii) the default broker if one was set.
    DatacenterBroker getOrCreateBroker(final String username){
        return getBroker(() -> brokersMap.computeIfAbsent(username, this::createBroker));
    }

    private DatacenterBroker createBroker(final String username) {
        return new DatacenterBrokerSimple(reader.getSimulation(), "Broker_"+username);
    }

    /**
     * @return a {@link DatacenterBroker} instance representing the username from the last trace line read.
     */
    DatacenterBroker getBroker(){
        final String value = TaskEventField.USERNAME.getValue(reader);
        return getBroker(value);
    }

    /**
     * {@return a DatacenterBroker instance represented by a given username}
     * If a {@link #setDefaultBroker(DatacenterBroker) default broker was set to be used for all created Cloudlets},
     * that one is returned, ignoring the username given.
     *
     * @param username the name of the user read from a trace line
     */
    DatacenterBroker getBroker(final String username){
        return getBroker(() -> brokersMap.get(username));
    }

    /**
     * {@return the default broker or the one returned by the provided supplier}
     * @param supplier a broker {@link Supplier} Function.
     */
    DatacenterBroker getBroker(final Supplier<DatacenterBroker> supplier){
        return defaultBroker == null ? supplier.get() : defaultBroker;
    }
}
