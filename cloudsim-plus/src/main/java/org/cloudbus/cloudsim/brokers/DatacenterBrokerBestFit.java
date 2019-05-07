package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.vms.Vm;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * <p>A simple implementation of {@link DatacenterBroker} that uses a best fit
 * mapping among submitted cloudlets and Vm's.
 * The Broker then places the submitted Vm's at the first Datacenter found.
 * If there isn't capacity in that one, it will try the other ones.</p>
 *
 * @author Humaira Abdul Salam
 * @since CloudSim Plus 4.3.8
 */
public class DatacenterBrokerBestFit extends DatacenterBrokerSimple {

    Map<Long, Long> vmNumberOfPesUpdated;
    boolean firstVmMapperCall;

    /**
     * Creates a new DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerBestFit(final CloudSim simulation) {
        super(simulation);
        this.vmNumberOfPesUpdated = new HashMap<>();
        firstVmMapperCall = true;
    }

    /**
     * Processes the end of execution of a given cloudlet inside a Vm.
     *
     * @param evt the cloudlet that has just finished to execute and was returned to the broker
     */
    @Override
    protected void processCloudletReturn(final SimEvent evt) {
        for (Vm vm : getVmCreatedList())
        {
            updateNumberOfPes(vm.getId(), vm.getNumberOfPes());
        }

        super.processCloudletReturn(evt);
    }
    /**
     * Selects the VM with the lowest number of PEs that is able to run a given Cloudlet.
     * In case the algorithm can't find such a VM, it uses the
     * default DatacenterBroker VM mapper as a fallback.
     *
     * @param cloudlet the Cloudlet to find a VM to run it
     * @return the VM selected for the Cloudlet or {@link Vm#NULL} if no suitable VM was found
     */
    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToCreatedVm()) {
            return cloudlet.getVm();
        }

        if (firstVmMapperCall)
        {
            for (Vm vm : getVmCreatedList())
            {
                updateNumberOfPes(vm.getId(), vm.getNumberOfPes());
            }
            firstVmMapperCall = false;
        }

        Optional<Map.Entry<Long, Long>> minVmPes = vmNumberOfPesUpdated
                                                    .entrySet()
                                                    .stream()
                                                    .filter(x -> x.getValue() >= cloudlet.getNumberOfPes())
                                                    .min(Comparator.comparingLong(x -> x.getValue()));

        if (minVmPes.isPresent()) {
            Vm mappedVm = getVmCreatedList()
                .stream()
                .filter(vm -> vm.getId() == minVmPes.get().getKey())
                .findFirst()
                .orElse(Vm.NULL);

            if (mappedVm != Vm.NULL) {
                LOGGER.debug("{}: {}: {} (PEs: {}) mapped to {} (available PEs: {})", getSimulation().clock(), getName(),
                    cloudlet, cloudlet.getNumberOfPes(), mappedVm, vmNumberOfPesUpdated.get(mappedVm.getId()));
                updateNumberOfPes(mappedVm.getId(), mappedVm.getNumberOfPes() - cloudlet.getNumberOfPes());
            }
            return mappedVm;
        }
        else
        {
            LOGGER.warn(": {}: {}: {} (PEs: {}) couldn't be mapped to any VM",
                getSimulation().clock(), getName(), cloudlet, cloudlet.getNumberOfPes());
        }
        return Vm.NULL;
    }

    private void updateNumberOfPes(final long vmId, final long availablePes)
    {
        vmNumberOfPesUpdated.put(vmId, availablePes);
    }
}
