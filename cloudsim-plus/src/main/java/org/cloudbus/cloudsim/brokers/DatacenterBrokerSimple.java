/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A simple implementation of {@link DatacenterBroker} that try to host customer's VMs
 * at the first Datacenter found. If there isn't capacity in that one,
 * it will try the other ones.
 * <p>
 * <p>The selection of VMs for each cloudlet is based on a Round-Robin policy,
 * cyclically selecting the next VM from the broker VM list for each requesting
 * cloudlet.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBrokerSimple extends DatacenterBrokerAbstract {
    /**
     * Creates a new DatacenterBroker.
     *
     * @param simulation name to be associated with this entity
     */
    public DatacenterBrokerSimple(final CloudSim simulation) {
        this(simulation, "");
    }

    /**
     * Creates a DatacenterBroker giving a specific name.
     *
     * @param simulation the CloudSim instance that represents the simulation the Entity is related to
     * @param name the DatacenterBroker name
     */
    public DatacenterBrokerSimple(final CloudSim simulation, final String name) {
        super(simulation, name);
        setDatacenterSupplier(this::selectDatacenterForWaitingVms);
        setFallbackDatacenterSupplier(this::selectFallbackDatacenterForWaitingVms);
        setVmMapper(this::defaultVmMapper);
    }

    /**
     * Defines the policy to select a Datacenter to Host a VM.
     * It always selects the first Datacenter from the Datacenter list.
     *
     * @return the Datacenter selected to request the creating
     * of waiting VMs or {@link Datacenter#NULL} if no suitable Datacenter was found
     */
    protected Datacenter selectDatacenterForWaitingVms() {
        return getDatacenterList().isEmpty() ? Datacenter.NULL : getDatacenterList().get(0);
    }

    /**
     * Defines the policy to select a fallback Datacenter to Host a VM
     * when a previous selected Datacenter failed to create the requested VMs.
     *
     * <p>It gets the first Datacenter that has not been tried yet.</p>
     *
     * @return the Datacenter selected to try creating
     * the remaining VMs or {@link Datacenter#NULL} if no suitable Datacenter was found
     */
    protected Datacenter selectFallbackDatacenterForWaitingVms() {
        return getDatacenterList()
                .stream()
                .filter(dc -> !getDatacenterRequestedList().contains(dc))
                .findFirst()
                .orElse(Datacenter.NULL);
    }

    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBindToVm() && getVmExecList().contains(cloudlet.getVm())) {
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
    private int getNextVmIndex() {
        if (getVmExecList().isEmpty()) {
            return -1;
        }

        final int vmIndex = getVmExecList().indexOf(getLastSelectedVm());
        return (vmIndex + 1) % getVmExecList().size();
    }

}
