/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

import org.cloudbus.cloudsim.core.CloudSim;

/**
 *
 * <p><b>A simple implementation of {@link DatacenterBroker} that try to host customer's VMs
 * at the first sws found. If there isn't capacity in that one,
 * it will try the other ones.</b></p>
 *
 * The selection of VMs for each cloudlet is based on a Round-Robin policy,
 * cyclically selecting the next VM from the broker VM list for each requesting
 * cloudlet.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBrokerSimple extends DatacenterBrokerAbstract {
    /**
     * Creates a new DatacenterBroker object.
     *
     * @param simulation name to be associated with this entity
     * @post $none
     */
    public DatacenterBrokerSimple(CloudSim simulation) {
        super(simulation);
    }

    /**
     * {@inheritDoc}
     * <br>It always selects the first sws from the sws list.
     *
     * @return {@inheritDoc}
     */
    @Override
    public Datacenter selectDatacenterForWaitingVms() {
        return (getDatacenterList().isEmpty() ? Datacenter.NULL : getDatacenterList().get(0));
    }

    /**
     * {@inheritDoc}
     *
     * <p>It gets the first Datacenter that has not been tried yet.</p>
     * @return {@inheritDoc}
     */
    @Override
    public Datacenter selectFallbackDatacenterForWaitingVms() {
        return getDatacenterList().stream()
            .filter(dc -> !getDatacenterRequestedList().contains(dc))
            .findFirst()
            .orElse(Datacenter.NULL);
    }

    /**
     * {@inheritDoc}
     *
     * <br>It applies a Round-Robin policy to cyclically select
     * the next Vm from the list of waiting VMs.
     *
     * @param cloudlet {@inheritDoc}
     * @return  {@inheritDoc}
     */
    @Override
    public Vm selectVmForWaitingCloudlet(Cloudlet cloudlet) {
        if (cloudlet.isBindToVm() && getVmsCreatedList().contains(cloudlet.getVm())) {
            return cloudlet.getVm();
        }

        /*If user didn't bind this cloudlet to a specific Vm
        or if the bind VM was not created, try the next Vm on the list of created*/
        return getVmFromCreatedList(getNextVmIndex());
    }

    /**
     * Gets the index of next VM in the broker's created VM list.
     * If not VM was selected yet, selects the first one,
     * otherwise, cyclically selects the next VM.
     *
     * @return the index of the next VM to bind a cloudlet to
     */
    protected int getNextVmIndex() {
        int vmIndex = getVmsCreatedList().indexOf(getLastSelectedVm());
        return (vmIndex == -1 ? 0 : (vmIndex + 1) % getVmsCreatedList().size());
    }

}
