package org.cloudbus.cloudsim.builders;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

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
    
    public Host getHostOfDatacenter(final int hostIndex, final int datacenterIndex){
        return datacenterBuilder.getHostOfDatacenter(hostIndex, datacenterIndex);
    }
    
    public Host getFirstHostFromFirstDatacenter(){
        return datacenterBuilder.getHostOfDatacenter(0,0);
    }    
    
    public Vm getFirstVmFromFirstBroker() {
        return getVmFromBroker(0, 0);
    }
    
    public Vm getVmFromBroker(final int vmIndex, final int brokerIndex) {
        return brokerBuilder.get(brokerIndex).getWaitingVm(vmIndex);
    }    
}
