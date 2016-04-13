/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Cloudlet;

import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * NetDatacentreBroker represents a broker acting on behalf of Datacenter
 * provider. It hides VM management, as vm creation, submission of cloudlets to
 * these VMs and destruction of VMs. <br/>
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
     * The list of submitted {@link AppCloudlet AppCloudlets}.
     */
    private List<? extends AppCloudlet> appCloudletList;

    /**
     * The list of submitted {@link AppCloudlet AppCloudlets}.
     *
     * @todo attribute appears to be redundant with {@link #appCloudletList}
     */
    private final Map<Integer, Integer> appCloudletReceived;

    private NetworkDatacenter networkDatacenter;

    public static int cachedCloudlet = 0;

    /**
     * Creates a new DatacenterBroker object.
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
        appCloudletReceived = new HashMap<>();
    }

    public void setNetworkDatacenter(NetworkDatacenter aLinkDC) {
        this.networkDatacenter = aLinkDC;
    }

    /**
     * Processes the ack received due to a request for VM creation.
     *
     * @param ev a SimEvent object
     *
     * @pre ev != null
     * @post $none
     */
    /**
     * Processes a cloudlet return event.
     *
     * @param ev a SimEvent object
     *
     * @pre ev != $null
     * @post $none
     */
    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (CloudletSimple) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        cloudletsSubmitted--;
        
        // all cloudlets executed
        if (getCloudletList().isEmpty() && cloudletsSubmitted==0) {
            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": All Cloudlets executed. Finishing...");
            clearDatacenters();
            finishExecution();
        } 
        // some cloudlets haven't finished yet
        else if (getAppCloudletList().size() > 0 && cloudletsSubmitted == 0) {
            // all the cloudlets sent finished. It means that some bount
            // cloudlet is waiting its VM be created
            clearDatacenters();
            createVmsInDatacenter(0);
        }
    }

    @Override
    protected void createVmsInDatacenter(int datacenterId) {
        super.createVmsInDatacenter(datacenterId); 
        
        for (AppCloudlet app : appCloudletList) {
            for (int i = 0; i < app.getNumberOfVmsToUse(); i++) {
                getAppCloudletReceived().put(app.getId(), app.getNumberOfVmsToUse());
                getCloudletSubmittedList().add(app.getNetworkCloudletList().get(i));
                cloudletsSubmitted++;

                // Sending cloudlet
                sendNow(
                        getVmsToDatacentersMap().get(this.getVmList().get(0).getId()),
                        CloudSimTags.CLOUDLET_SUBMIT,
                        app.getNetworkCloudletList().get(i));
            }
            Log.printFormattedLine("Created AppCloudlet%d", app.getId());
        }
        
    }
    
    
    
    @Override
    protected void submitCloudlets() {
        
    }

    @Override
    protected boolean processVmCreate(SimEvent ev) {
        if(super.processVmCreate(ev)){
            int[] data = (int[]) ev.getData();
            int vmId = data[1];
            getNetworkDatacenter().processVmCreateNetwork(VmList.getById(getVmList(), vmId));
            return true;
        }
        
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends AppCloudlet> List<T> getAppCloudletList() {
        return (List<T>) appCloudletList;
    }

    public final <T extends AppCloudlet> void setAppCloudletList(List<T> appCloudletList) {
        this.appCloudletList = appCloudletList;
    }

    public NetworkDatacenter getNetworkDatacenter() {
        return networkDatacenter;
    }

    public Map<Integer, Integer> getAppCloudletReceived() {
        return appCloudletReceived;
    }

}
