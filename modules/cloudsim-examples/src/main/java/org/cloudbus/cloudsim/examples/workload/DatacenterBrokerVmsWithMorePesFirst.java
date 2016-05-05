package org.cloudbus.cloudsim.examples.workload;

import java.util.Comparator;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;

/**
 * A Broker which requests for creation of VMs inside a datacenter
 * follow the order of VM's required PEs number. VMs that require
 * more PEs are submitted first.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBrokerVmsWithMorePesFirst extends DatacenterBrokerSimple {
    
    public DatacenterBrokerVmsWithMorePesFirst(String name) throws Exception {
        super(name);
    }

    /**
     * Gets the list of submitted VMs in descending order of PEs number.
     * @param <T>
     * @return the list of submitted VMs
     */
    @Override
    public <T extends Vm> List<T> getVmsWaitingList() {
        super.getVmsWaitingList().sort(new Comparator<Vm>() {
            @Override
            public int compare(Vm vm1, Vm vm2) {
                return Integer.compare(vm2.getNumberOfPes(), vm1.getNumberOfPes());
            }
        }); 
        
        return (List<T>)super.getVmsWaitingList();
    }    
}
