package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;

/**
 * A Builder class to createBroker {@link DatacenterBrokerSimple} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class BrokerBuilder extends Builder implements BrokerBuilderInterface {
    private static final String BROKER_NAME_FORMAT = "Broker%d";
    private final List<DatacenterBrokerSimple> brokers;
    private int numberOfCreatedBrokers;

    public BrokerBuilder() {
        this.brokers = new ArrayList<>();
        this.numberOfCreatedBrokers = 0;
    }
    
    @Override
    public BrokerBuilderDecorator createBroker() {
        String name = String.format(BROKER_NAME_FORMAT, numberOfCreatedBrokers++);
        DatacenterBrokerSimple broker = new DatacenterBrokerSimple(name);
        brokers.add(broker);
        return new BrokerBuilderDecorator(this, broker);
    }

    @Override
    public List<DatacenterBrokerSimple> getBrokers() {
        return brokers;
    }

    @Override
    public DatacenterBrokerSimple get(final int index) {
        return brokers.get(index);
    }

    @Override
    public DatacenterBrokerSimple findBroker(final int id) throws RuntimeException {
        for (DatacenterBrokerSimple broker : brokers) {
            if (broker.getId() == id) {
                return broker;
            }
        }
        throw new RuntimeException(String.format("There isn't a broker with id %d", id));
    }
}

