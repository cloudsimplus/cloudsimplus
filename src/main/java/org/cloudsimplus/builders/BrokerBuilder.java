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
package org.cloudsimplus.builders;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * A Builder class to create {@link DatacenterBrokerSimple} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class BrokerBuilder implements BrokerBuilderInterface {
    private final List<DatacenterBroker> brokers;
    private final SimulationScenarioBuilder scenario;

    public BrokerBuilder(SimulationScenarioBuilder scenario) {
        super();
        this.scenario = scenario;
        this.brokers = new ArrayList<>();
    }

    @Override
    public BrokerBuilderDecorator create() {
        return create(b -> {});
    }

    @Override
    public BrokerBuilderDecorator create(final Consumer<DatacenterBroker> brokerConsumer) {
        final DatacenterBrokerSimple broker = new DatacenterBrokerSimple(scenario.getSimulation());
        brokers.add(broker);
        brokerConsumer.accept(broker);
        return new BrokerBuilderDecorator(this, broker);
    }

    @Override
    public List<DatacenterBroker> getBrokers() {
        return brokers;
    }

    @Override
    public DatacenterBroker get(final int index) {
        return brokers.get(index);
    }

    @Override
    public DatacenterBroker findBroker(final int id) {
        return brokers.stream()
            .filter(broker -> broker.getId() == id)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("There isn't a broker with id %d".formatted(id)));
    }
}

