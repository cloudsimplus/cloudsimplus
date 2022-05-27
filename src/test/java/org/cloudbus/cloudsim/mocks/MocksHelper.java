package org.cloudbus.cloudsim.mocks;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
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

    public static DatacenterBroker createMockBroker(final CloudSim cloudsim) {
        return createMockBroker(cloudsim, broker -> {});
    }

    /**
     * Creates a DatacenterBroker mock object.
     * @param cloudsim the CloudSim instance or mock to use
     * @param consumer a {@link Runnable} that can be used for additional {@link Mockito#when(Object) calls}
     * @return
     */
    public static DatacenterBroker createMockBroker(final Simulation cloudsim, final Consumer<DatacenterBroker> consumer) {
        final DatacenterBroker broker = Mockito.mock(DatacenterBroker.class);
        Mockito.when(broker.getSimulation()).thenReturn(cloudsim);
        Mockito.when(broker.getId()).thenReturn(0L);
        consumer.accept(broker);
        return broker;
    }
}
