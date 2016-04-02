package org.cloudbus.cloudsim.builders;

import java.util.List;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;

/**
 * An interface to classes that build {@link DatacenterBrokerSimple} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface BrokerBuilderInterface {
    BrokerBuilderDecorator createBroker();
    DatacenterBrokerSimple findBroker(final int id) throws RuntimeException;
    List<DatacenterBrokerSimple> getBrokers();  
    DatacenterBrokerSimple get(final int index);
}
