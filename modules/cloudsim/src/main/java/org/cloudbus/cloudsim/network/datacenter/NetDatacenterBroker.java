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

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * NetDatacentreBroker represents a broker acting on behalf of Datacenter
 * provider. It hides VM management, as vm creation, submission of cloudlets to
 * these VMs and destruction of VMs. <br>
 * <tt>NOTE</tt>: This class is an example only. It works on behalf of a
 * provider not for users. One has to implement interaction with user broker to
 * this broker.
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 3.0
 * @todo The class is not a broker acting on behalf of users, but on behalf of a
 * provider. Maybe this distinction would be explicit by different class
 * hierarchy, such as UserDatacenterBroker and ProviderDatacenterBroker.
 */
public class NetDatacenterBroker extends DatacenterBrokerSimple {
    /**
     * The list of  {@link AppCloudlet AppCloudlets} submitted to the broker that are 
     * waiting to be created inside some Vm yet.
     */
    private List<? extends AppCloudlet> appCloudletWaitingList;

    private NetworkDatacenter networkDatacenter;

    /**
     * Creates a new NetDatacenterBroker object.
     *
     * @param name name to be associated with this entity
     *
     * @throws Exception the exception
     *
     * @pre name != null
     * @post $none
     */
    public NetDatacenterBroker(String name) throws Exception {
        super(name);
        setAppCloudletList(new ArrayList<>());
    }

    public void setNetworkDatacenter(NetworkDatacenter networkDatacenter) {
        this.networkDatacenter = networkDatacenter;
    }

    @Override
    public boolean hasMoreCloudletsToBeExecuted() {
        return super.hasMoreCloudletsToBeExecuted() || 
               (getAppCloudletList().size() > 0 && cloudletsCreated == 0);
    }
    
    @Override
    protected void createCloudletsInVms() {
        super.createCloudletsInVms(); 

        for (AppCloudlet app : appCloudletWaitingList) {
            for (int i = 0; i < app.getNumberOfVmsToUse(); i++) {
                getCloudletsWaitingList().add(app.getNetworkCloudletList().get(i));
                cloudletsCreated++;

                sendNow(
                        getVmsToDatacentersMap().get(this.getVmsCreatedList().get(0).getId()),
                        CloudSimTags.CLOUDLET_SUBMIT,
                        app.getNetworkCloudletList().get(i));
            }
            Log.printFormattedLine("Created AppCloudlet%d", app.getId());
        }
    }
    
    @Override
    protected boolean processVmCreate(SimEvent ev) {
        if(super.processVmCreate(ev)){
            int[] data = (int[]) ev.getData();
            int datacenterId = data[0];
            int vmId = data[1];
            getNetworkDatacenter().processVmCreateNetwork(VmList.getById(getVmsWaitingList(), vmId));
            return true;
        }
        
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends AppCloudlet> List<T> getAppCloudletList() {
        return (List<T>) appCloudletWaitingList;
    }

    public final <T extends AppCloudlet> void setAppCloudletList(List<T> appCloudletList) {
        this.appCloudletWaitingList = appCloudletList;
    }

    public NetworkDatacenter getNetworkDatacenter() {
        return networkDatacenter;
    }

}
