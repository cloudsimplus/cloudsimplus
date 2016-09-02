/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.Cloudlet;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;

/**
 * NetDatacentreBroker is a {@link DatacenterBroker} implementation
 * that works with network components such as {@link NetworkDatacenter},
 * {@link NetworkHost} and {@link NetworkVm}.
 * 
 * It accepts the submission of {@link NetworkCloudlet} lists,
 * each one that composes a {@link AppCloudlet}.
 * 
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * 
 * @since CloudSim Toolkit 3.0
 */
public class NetDatacenterBroker extends DatacenterBrokerSimple {
    /**
     * The list of  {@link AppCloudlet AppCloudlets} submitted to the broker that are 
     * waiting to be created inside some Vm yet.
     */
    private List<? extends AppCloudlet> appCloudletWaitingList;

    /**
     * Creates a new NetDatacenterBroker object.
     *
     * @param name name to be associated with this entity
     *
     * @throws IllegalArgumentException when the entity name is invalid
     *
     * @pre name != null
     * @post $none
     */
    public NetDatacenterBroker(String name) {
        super(name);
        setAppCloudletList(new ArrayList<>());
    }

    @Override
    public boolean hasMoreCloudletsToBeExecuted() {
        return super.hasMoreCloudletsToBeExecuted() || 
               (getAppCloudletList().size() > 0 && cloudletsCreated == 0);
    }
    
    public List<? extends AppCloudlet> getAppCloudletList() {
        return appCloudletWaitingList;
    }

    public final void setAppCloudletList(List<? extends AppCloudlet> appCloudletList) {
        this.appCloudletWaitingList = appCloudletList;
    }
}
