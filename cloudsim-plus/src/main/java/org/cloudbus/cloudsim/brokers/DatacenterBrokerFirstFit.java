package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A implementation of {@link DatacenterBroker} that uses a First Fit
 * mapping between submitted cloudlets and Vm's, trying to place a Cloudlet
 * at the first suitable Vm which can be found (according to the required Cloudlet's PEs).
 * The Broker then places the submitted Vm's at the first Datacenter found.
 * If there isn't capacity in that one, it will try the other ones.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.6.0
 */
public class DatacenterBrokerFirstFit extends DatacenterBrokerSimple {
    /**
     * The index of the last Vm used to place a Cloudlet.
     */
    private int lastVmIndex;

    /**
     * Creates a DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerFirstFit(final CloudSim simulation) {
        super(simulation);
    }

    /**
     * Selects the first VM with the lowest number of PEs that is able to run a given Cloudlet.
     * In case the algorithm can't find such a VM, it uses the
     * default DatacenterBroker VM mapper as a fallback.
     *
     * @param cloudlet the Cloudlet to find a VM to run it
     * @return the VM selected for the Cloudlet or {@link Vm#NULL} if no suitable VM was found
     */
    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToVm()) {
            return cloudlet.getVm();
        }

        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = getVmCreatedList().size();
        for (int i = 0; i < maxTries; i++) {
            final Vm vm = getVmCreatedList().get(lastVmIndex);
            if (vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes()) {
                LOGGER.trace("{}: {}: {} (PEs: {}) mapped to {} (available PEs: {}, tot PEs: {})",
                    getSimulation().clockStr(), getName(), cloudlet, cloudlet.getNumberOfPes(), vm,
                    vm.getExpectedFreePesNumber(), vm.getFreePesNumber());
                return vm;
            }

            /* If it gets here, the previous Vm doesn't have capacity to place the Cloudlet.
             * Then, moves to the next Vm.
             * If the end of the Vm list is reached, starts from the beginning,
             * until the max number of tries.*/
            lastVmIndex = ++lastVmIndex % getVmCreatedList().size();
        }

        LOGGER.warn("{}: {}: {} (PEs: {}) couldn't be mapped to any suitable VM.",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getNumberOfPes());

        return Vm.NULL;
    }


}
