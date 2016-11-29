package org.cloudsimplus.builders;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * An builder to help getting instance of other CloudSim object builders.
 *
 * @author Manoel Campos da Silva Filho
 */
public class SimulationScenarioBuilder {
    private final DatacenterBuilder datacenterBuilder;
    private final BrokerBuilder brokerBuilder;
    private final CloudSim simulation;

    public SimulationScenarioBuilder(CloudSim simulation) {
        this.simulation = simulation;
        this.datacenterBuilder = new DatacenterBuilder(this);
        this.brokerBuilder = new BrokerBuilder(this);
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

    public CloudSim getSimulation() {
        return simulation;
    }
}
