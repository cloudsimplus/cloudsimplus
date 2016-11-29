/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers.network;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.network.AppCloudlet;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

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
public class NetworkDatacenterBroker extends DatacenterBrokerSimple {
    /**
     * The list of  {@link AppCloudlet AppCloudlets} submitted to the broker that are
     * waiting to be created inside some Vm yet.
     */
    private List<? extends AppCloudlet> appCloudletWaitingList;

    /**
     * Creates a new NetworkDatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     *
     * @post $none
     */
    public NetworkDatacenterBroker(CloudSim simulation) {
        super(simulation);
        setAppCloudletList(new ArrayList<>());
    }

    @Override
    public boolean hasMoreCloudletsToBeExecuted() {
        return super.hasMoreCloudletsToBeExecuted() ||
               (getAppCloudletList().size() > 0 && getCloudletsCreated() == 0);
    }

    public List<? extends AppCloudlet> getAppCloudletList() {
        return appCloudletWaitingList;
    }

    public final void setAppCloudletList(List<? extends AppCloudlet> appCloudletList) {
        this.appCloudletWaitingList = appCloudletList;
    }
}
