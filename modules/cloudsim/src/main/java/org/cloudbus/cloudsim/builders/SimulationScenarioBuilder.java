package org.cloudbus.cloudsim.builders;

/**
 * An builder to help getting instance of other CloudSim object builders.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class SimulationScenarioBuilder {
    private final DatacenterBuilder datacenterBuilder;
    private final BrokerBuilder brokerBuilder;

    public SimulationScenarioBuilder() {
        this.datacenterBuilder = new DatacenterBuilder();
        this.brokerBuilder = new BrokerBuilder();
    }

    public DatacenterBuilder getDatacenterBuilder() {
        return datacenterBuilder;
    }

    public BrokerBuilder getBrokerBuilder() {
        return brokerBuilder;
    }
    
    
}
