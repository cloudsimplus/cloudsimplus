/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import org.cloudbus.cloudsim.lists.VmList;

/**
 * 
 * <p><b>A simple implementation of {@link DatacenterBroker} that try to host customer's VMs
 * at the first datacenter found. If there isn't capacity in that one,
 * it will try the other ones.</b></p>
 * 
 * The selection of VMs for each cloudlet is based on a Round-Robin policy,
 * cyclically selecting the next VM from the broker VM list for each requesting
 * cloudlet.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class DatacenterBrokerSimple extends DatacenterBrokerAbstract {
    /**
     * Creates a new DatacenterBroker object.
     *
     * @param name name to be associated with this entity
     * @throws IllegalArgumentException when the entity name is invalid
     * @pre name != null
     * @post $none
     */
    public DatacenterBrokerSimple(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     * <br>It always selects the first datacenter from the datacenter list.
     * 
     * @return {@inheritDoc}
     */
    @Override
    public int selectDatacenterForWaitingVms() {
        return getDatacenterIdsList().get(0);
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>It gets the first Datacenter that has not been tried yet.</p>
     * @return {@inheritDoc}
     */
    @Override
    public int selectFallbackDatacenterForWaitingVms() {
        for (int nextDatacenterId : getDatacenterIdsList()) {
            if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
                return nextDatacenterId;
            }
        }
        
        return -1;
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
        if (cloudlet.isBoundedToVm()) {
            // submit to the specific vm
            return VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
        } 

        //if user didn't bind this cloudlet and it has not been executed yet
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
