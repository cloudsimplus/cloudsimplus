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
package org.cloudsimplus.mocks;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerAbstract;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.Simulation;
import org.mockito.Mockito;

import java.util.function.Consumer;

/**
 * A utility class to create Mock objects.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class MocksHelper {
    /**
     * A private constructor to avoid the class to be instantiated.
     */
    private MocksHelper() {}

    /**
     * Creates a mocked DatacenterBroker where the {@link DatacenterBroker#getId()}
     * is expected to be called just once. For such a call, it will be returned
     * the given id.
     *
     * @param brokerId the id to return for the expected call of {@link DatacenterBroker#getId()}
     * @return a mocked DatacenterBroker
     */
    public static DatacenterBroker createMockBroker(int brokerId) {
        return createMockBroker(brokerId, 1);
    }

    /**
     * Creates a mocked DatacenterBroker where the {@link DatacenterBroker#getId()}
     * is expected to be called a given number of times. For each call, it will be returned
     * the given id.
     *
     * @param brokerId the id to return for each call of {@link DatacenterBroker#getId()}
     * @param expectedCallsToGetId the number of times the {@link DatacenterBroker#getId()} is expected to be called
     * @return a mocked DatacenterBroker
     */
    public static DatacenterBroker createMockBroker(long brokerId, int expectedCallsToGetId) {
        final DatacenterBroker broker = Mockito.mock(DatacenterBroker.class);
        Mockito.when(broker.getId()).thenReturn(brokerId);
        return broker;
    }

    public static DatacenterBroker createMockBroker(final CloudSimPlus cloudsim) {
        return createMockBroker(cloudsim, broker -> {});
    }

    /**
     * Creates a DatacenterBroker mock object.
     * @param cloudsim the CloudSimPlus instance or mock to use
     * @param consumer a {@link Runnable} that can be used for additional {@link Mockito#when(Object) calls}
     * @return
     */
    public static DatacenterBroker createMockBroker(final Simulation cloudsim, final Consumer<DatacenterBroker> consumer) {
        final DatacenterBroker broker = Mockito.mock(DatacenterBrokerAbstract.class);
        Mockito.when(broker.getSimulation()).thenReturn(cloudsim);
        Mockito.when(broker.getId()).thenReturn(0L);
        consumer.accept(broker);
        return broker;
    }
}
