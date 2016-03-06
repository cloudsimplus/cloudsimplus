package org.cloudbus.cloudsim.builders;

import java.util.List;
import org.cloudbus.cloudsim.DatacenterBroker;

/**
 * An interface to classes that build {@link DatacenterBroker} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface BrokerBuilderInterface {
    BrokerBuilderDecorator createBroker();
    DatacenterBroker findBroker(final int id) throws RuntimeException;
    List<DatacenterBroker> getBrokers();    
}
