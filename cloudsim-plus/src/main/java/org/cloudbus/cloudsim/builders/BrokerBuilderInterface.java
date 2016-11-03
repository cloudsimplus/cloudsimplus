package org.cloudbus.cloudsim.builders;

import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;

/**
 * An interface to classes that build {@link DatacenterBrokerSimple} objects.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface BrokerBuilderInterface {
    BrokerBuilderDecorator createBroker();
    DatacenterBroker findBroker(final int id) throws RuntimeException;
    List<DatacenterBroker> getBrokers();
    DatacenterBroker get(final int index);
}
