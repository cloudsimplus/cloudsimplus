/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters.network;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.network.switches.AbstractSwitch;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * NetworkDatacenter class is a {@link Datacenter} whose hostList are
 * virtualized and networked. It contains all the information about internal
 * network. For example, which VM is connected to what switch, etc.
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
 */
public class NetworkDatacenter extends DatacenterSimple {

    /**
     * @see #getSwitchMap()
     */
    private final List<Switch> switchMap;

    /**
     * Creates a NetworkDatacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the Datacenter to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     *
     * @throws IllegalArgumentException when this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * <br>
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     *
     * @post $none
     */
    public NetworkDatacenter(
        Simulation simulation,
        DatacenterCharacteristics characteristics,
        VmAllocationPolicy vmAllocationPolicy)
    {
        super(simulation, characteristics, vmAllocationPolicy);

        switchMap = new ArrayList<>();
    }

    /**
     * Creates a NetworkDatacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the Datacenter to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storageList a List of storage elements, for data simulation
     * @param schedulingInterval the scheduling delay to process each Datacenter received event
     *
     * @throws IllegalArgumentException when this entity has <tt>zero</tt> number of PEs (Processing Elements).
     * <br>
     * No PEs mean the Cloudlets can't be processed. A CloudResource must
     * contain one or more Machines. A Machine must contain one or more PEs.
     *
     * @post $none
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public NetworkDatacenter(
        CloudSim simulation,
        DatacenterCharacteristics characteristics,
        VmAllocationPolicy vmAllocationPolicy,
        List<FileStorage> storageList,
        double schedulingInterval)
    {
        this(simulation, characteristics, vmAllocationPolicy);
        setStorageList(storageList);
        setSchedulingInterval(schedulingInterval);
    }

    /**
     * Gets a map of all Edge Switches in the Datacenter network, where each key is the switch id
     * and each value is the switch itself.
     * One can design similar functions for other type of Datacenter.
     *
     * @return
     */
    public List<Switch> getEdgeSwitch() {
        return switchMap.stream()
                .filter(sw -> sw.getLevel() == EdgeSwitch.LEVEL)
                .collect(toList());
    }

    @Override
    protected boolean processVmCreate(SimEvent ev, boolean ackRequested) {
        if(!super.processVmCreate(ev, ackRequested))
            return false;

        final Vm vm = (Vm) ev.getData();
        Log.printLine(vm.getId() + " VM is created on " + vm.getHost().getId());
        return true;
    }

    /**
     * Adds a {@link AbstractSwitch} to the Datacenter.
     * @param sw the AbstractSwitch to be added
     */
    public void addSwitch(Switch sw){
        switchMap.add(sw);
    }

    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        updateCloudletProcessing();

        // gets the Cloudlet object
        final Cloudlet cl = (Cloudlet) ev.getData();

        // checks whether this Cloudlet has finished or not
        if (cl.isFinished()) {
            final String name = getSimulation().getEntityName(cl.getBroker().getId());
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
                // unique tag = operation tag
                final int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
                sendNow(cl.getBroker().getId(), tag, cl);
            }

            sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_RETURN, cl);

            return;
        }

        // process this Cloudlet to this Datacenter
        cl.assignToDatacenter(this);

        // time to transfer the files
        final double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

        final CloudletScheduler scheduler = cl.getVm().getCloudletScheduler();
        double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);

        if (estimatedFinishTime > 0.0) { // if this cloudlet is in the exec
            // time to process the cloudlet
            estimatedFinishTime += fileTransferTime;
            send(getId(),
                getCloudletProcessingUpdateInterval(estimatedFinishTime),
                CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);

            // event to update the stages
            send(getId(), 0.0001, CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }

        if (ack) {
            // unique tag = operation tag
            sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cl);
        }

        checkCloudletsCompletionForAllHosts();
    }

    /**
     * Gets a <b>read-only</b> list of network Datacenter's Switches.
     * @return
     */
    public List<Switch> getSwitchMap() {
        return Collections.unmodifiableList(switchMap);
    }

}
