/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * NetworkDatacenter class is a {@link Datacenter} whose hostList are
 * virtualized and networked. It contains all the information about internal
 * network. For example, which VM is connected to what switch etc. It deals with
 * processing of VM queries (i.e., handling of VMs) instead of processing
 * Cloudlet-related queries. So, even though an AllocationPolicy will be instantiated
 * (in the init() method of the superclass, it will not be used, as processing
 * of cloudlets are handled by the CloudletScheduler and processing of
 * Virtual Machines are handled by the VmAllocationPolicy.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * 
 * @todo @author manoelcampos If an AllocationPolicy is not being used, why it is being created. Perhaps a
 * better class hierarchy should be created, introducing some abstract class or
 * interface.
 *
 */
public class NetworkDatacenter extends DatacenterSimple {

    /**
     * @see #getVmToSwitchMap() 
     */
    private final Map<Integer, Integer> vmToSwitchMap;

    /**
     * @see #getHostToSwitchMap() 
     */
    private final Map<Integer, Integer> hostToSwitchMap;

    /**
     * @see #getSwitchMap() 
     */
    private final Map<Integer, Switch> switchMap;

    /**
     * @see #getVmToHostMap() 
     */
    private final Map<Integer, Integer> vmToHostMap;

    /**
     * Instantiates a new NetworkDatacenter object.
     *
     * @param name the name to be associated with this entity (as required by
     * {@link org.cloudbus.cloudsim.core.SimEntity})
     * @param characteristics the datacenter characteristics
     * @param vmAllocationPolicy the vmAllocationPolicy
     * @param storageList a List of storage elements, for data simulation
     * @param schedulingInterval the scheduling delay to process each datacenter
     * received event
     *
     * @throws IllegalArgumentException when one of the following scenarios occur:
     * <ul>
     * <li>creating this entity before initializing CloudSim package
     * <li>this entity name is <tt>null</tt> or empty
     * <li>this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * <br>
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     * </ul>
     *
     * @pre name != null
     * @pre resource != null
     * @post $none
     */
    public NetworkDatacenter(
            String name,
            DatacenterCharacteristics characteristics,
            VmAllocationPolicy vmAllocationPolicy,
            List<FileStorage> storageList,
            double schedulingInterval) {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
        vmToSwitchMap = new HashMap<>();
        hostToSwitchMap = new HashMap<>();
        vmToHostMap = new HashMap<>();
        switchMap = new HashMap<>();
    }

    /**
     * Gets a map of all Edge Switches in the Datacenter network. One can design
     * similar functions for other type of switches.
     *
     * @return a EdgeSwitch map, where each key is the switch id and each
     * value it the switch itself.
     */
    public Map<Integer, Switch> getEdgeSwitch() {
        return switchMap.entrySet().stream()
                .filter(entry -> entry.getValue().getLevel() == EdgeSwitch.LEVEL)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    @Override
    protected boolean processVmCreate(SimEvent ev, boolean ack) {
        if(!super.processVmCreate(ev, ack))
            return false;
        
        Vm vm = (Vm) ev.getData();
        vmToSwitchMap.put(vm.getId(), ((NetworkHost) vm.getHost()).getEdgeSwitch().getId());
        vmToHostMap.put(vm.getId(), vm.getHost().getId());
        Log.printLine(vm.getId() + " VM is created on " + vm.getHost().getId());
        return true;
    }
    
    /**
     * Adds a {@link Switch} to the Datacenter.
     * @param sw the Switch to be added
     */
    public void addSwitch(Switch sw){
        switchMap.put(sw.getId(), sw);
    }

    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        updateCloudletProcessing();

        try {
            // gets the Cloudlet object
            Cloudlet cl = (Cloudlet) ev.getData();

            // checks whether this Cloudlet has finished or not
            if (cl.isFinished()) {
                String name = CloudSim.getEntityName(cl.getUserId());
                Log.printConcatLine(
                        getName(), ": Warning - Cloudlet #", 
                        cl.getId(), " owned by ", name,
                        " is already completed/finished.");
                Log.printLine("Therefore, it is not being executed again\n");

                // NOTE: If a Cloudlet has finished, then it won't be processed.
                // So, if ack is required, this method sends back a result.
                // If ack is not required, this method don't send back a result.
                // Hence, this might cause CloudSim to be hanged since waiting
                // for this Cloudlet back.
                if (ack) {
                    int[] data = new int[3];
                    data[0] = getId();
                    data[1] = cl.getId();
                    data[2] = CloudSimTags.FALSE;

                    // unique tag = operation tag
                    int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
                    sendNow(cl.getUserId(), tag, data);
                }

                sendNow(cl.getUserId(), CloudSimTags.CLOUDLET_RETURN, cl);

                return;
            }

            // process this Cloudlet to this Datacenter
            cl.assignCloudletToDatacenter(
                    getId(), getCharacteristics().getCostPerSecond(), 
                    getCharacteristics().getCostPerBw());

            int userId = cl.getUserId();
            int vmId = cl.getVmId();

            // time to transfer the files
            double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

            Host host = getVmAllocationPolicy().getHost(vmId, userId);
            Vm vm = host.getVm(vmId, userId);
            CloudletScheduler scheduler = vm.getCloudletScheduler();
            double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);

            if (estimatedFinishTime > 0.0) { // if this cloudlet is in the exec
                // time to process the cloudlet
                estimatedFinishTime += fileTransferTime;
                send(getId(), estimatedFinishTime, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);

                // event to update the stages
                send(getId(), 0.0001, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
            }

            if (ack) {
                int[] data = new int[3];
                data[0] = getId();
                data[1] = cl.getId();
                data[2] = CloudSimTags.TRUE;

                // unique tag = operation tag
                int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
                sendNow(cl.getUserId(), tag, data);
            }
        } catch (ClassCastException c) {
            Log.printLine(getName() + ".processCloudletSubmit(): " + "ClassCastException error.");
            c.printStackTrace();
        } catch (Exception e) {
            Log.printLine(getName() + ".processCloudletSubmit(): " + "Exception error.");
            e.printStackTrace();
        }

        checkCloudletsCompletionForAllHosts();
    }

    /**
     * Gets a map of datacenter switches where each key is a switch id and the
     * corresponding value is the switch itself.
     * @return a read-only map of Switches from the Datacenter
     */
    public Map<Integer, Switch> getSwitchMap() {
        return Collections.unmodifiableMap(switchMap);
    }

    /**
     * Gets a map between VMs and Switches, where each key is a VM id and the
     * corresponding value is the id of the switch where the VM is connected to.
     * 
     * @return a read-only map of VMs connected to Switches
     */
    public Map<Integer, Integer> getVmToSwitchMap() {
        return Collections.unmodifiableMap(vmToSwitchMap);
    }
    
    /**
     * Connects a VM to a given Switch.
     * 
     * @param vm the VM to be connected
     * @param sw the Switch to connect the VM to
     */
    public void addVmToSwitch(Vm vm, Switch sw){
        vmToSwitchMap.put(vm.getId(), sw.getId());
    }

    /**
     * Gets a map between hosts and Switches, where each key is a host id and the
     * corresponding value is the id of the switch where the host is connected
     * to.
     * 
     * @return a read-only map of Host connected to Switches
     */
    public Map<Integer, Integer> getHostToSwitchMap() {
        return Collections.unmodifiableMap(hostToSwitchMap);
    }
    
    /**
     * Connects a host to a given Switch.
     * 
     * @param host the host to be connected
     * @param sw the Switch to connect the host to
     */
    public void addHostToSwitch(Host host, Switch sw){
        hostToSwitchMap.put(host.getId(), sw.getId());
    }

    /**
     * Gets a map between VMs and Hosts, where each key is a VM id and the
     * corresponding value is the id of the host where the VM is placed.
     * 
     * @return a read-only map of VMs placed into Hosts
     * 
     * @todo @author manoelcampos This mapping doesn't make sense, once the placement of 
     * VMs into Hosts is dynamic. A VM can be migrated to another host
     * due to several reasons.
     */
    public Map<Integer, Integer> getVmToHostMap() {
        return Collections.unmodifiableMap(vmToHostMap);
    }
    
    /**
     * Connects a VM to a given host.
     * 
     * @param vm the VM to be connected
     * @param host the host to connect the VM to
     */
    public void addVmToHost(Vm vm, Host host){
        vmToHostMap.put(vm.getId(), host.getId());
    }

}
