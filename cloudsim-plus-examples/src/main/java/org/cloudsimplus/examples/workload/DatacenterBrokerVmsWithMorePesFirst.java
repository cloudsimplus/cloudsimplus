package org.cloudsimplus.examples.workload;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * A Broker which requests for creation of VMs inside a switches
 * following the order of VM's required PEs number. VMs that require
 * more PEs are submitted first.
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterBrokerVmsWithMorePesFirst extends DatacenterBrokerSimple {

    public DatacenterBrokerVmsWithMorePesFirst(CloudSim simulation) {
        super(simulation);
    }

    /**
     * Gets the list of submitted VMs in descending order of PEs number.
     * @param <T>
     * @return the list of submitted VMs
     */
    @Override
    public <T extends Vm> List<T> getVmsWaitingList() {
        Collections.sort(super.getVmsWaitingList(), new Comparator<Vm>() {
            @Override
            public int compare(Vm vm1, Vm vm2) {
                return Integer.compare(vm2.getNumberOfPes(), vm1.getNumberOfPes());
            }
        });

        return super.getVmsWaitingList();
    }
}
