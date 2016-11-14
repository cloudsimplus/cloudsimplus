package org.cloudbus.cloudsim.mocks;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.easymock.EasyMock;

/**
 * A utility class to create Mock objects.
 *
 * @author Manoel Campos da Silva Filho
 */
public class Mocks {
    /**
     * A private constructor to avoid the class to be instantiated.
     */
    private Mocks() {}

    public static DatacenterBroker createMockBroker(int brokerId) {
        final DatacenterBroker broker = EasyMock.createMock(DatacenterBroker.class);
        EasyMock.expect(broker.getId()).andReturn(brokerId).once();
        EasyMock.replay(broker);
        return broker;
    }
}
