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
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
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
     * @see #getVmToSwitchMap()
     */
    private final Map<Vm, Switch> vmToSwitchMap;

    /**
     * @see #getHostToSwitchMap()
     */
    private final Map<Host, Switch> hostToSwitchMap;

    /**
     * @see #getSwitchMap()
     */
    private final List<Switch> switchMap;

    /**
     * @see #getVmToHostMap()
     */
    private final Map<Vm, NetworkHost> vmToHostMap;

    /**
     * Creates a NetworkDatacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the sws to be created
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

        vmToSwitchMap = new HashMap<>();
        hostToSwitchMap = new HashMap<>();
        vmToHostMap = new HashMap<>();
        switchMap = new ArrayList<>();
    }

    /**
     * Creates a NetworkDatacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the sws to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storageList a List of storage elements, for data simulation
     * @param schedulingInterval the scheduling delay to process each sws received event
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
     * One can design similar functions for other type of sws.
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

        Vm vm = (Vm) ev.getData();
        vmToSwitchMap.put(vm, ((NetworkHost) vm.getHost()).getEdgeSwitch());
        vmToHostMap.put(vm, (NetworkHost)vm.getHost());
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

        try {
            // gets the Cloudlet object
            Cloudlet cl = (Cloudlet) ev.getData();

            // checks whether this Cloudlet has finished or not
            if (cl.isFinished()) {
                String name = getSimulation().getEntityName(cl.getBroker().getId());
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
                    int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
                    sendNow(cl.getBroker().getId(), tag, cl);
                }

                sendNow(cl.getBroker().getId(), CloudSimTags.CLOUDLET_RETURN, cl);

                return;
            }

            // process this Cloudlet to this Datacenter
            cl.assignToDatacenter(this);

            // time to transfer the files
            double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());

            CloudletScheduler scheduler = cl.getVm().getCloudletScheduler();
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
     * Gets a <b>read-only</b> list of network Datacenter's sws.
     * @return
     */
    public List<Switch> getSwitchMap() {
        return Collections.unmodifiableList(switchMap);
    }

    /**
     * Gets a map between VMs and Switches, where each key is a VM and the
     * corresponding value is the switch where the VM is connected to.
     *
     * @return a read-only map of VMs connected to Switches
     */
    public Map<Vm, Switch> getVmToSwitchMap() {
        return Collections.unmodifiableMap(vmToSwitchMap);
    }

    /**
     * Connects a VM to a given AbstractSwitch.
     *
     * @param vm the VM to be connected
     * @param sw the AbstractSwitch to connect the VM to
     */
    public void addVmToSwitch(Vm vm, Switch sw){
        vmToSwitchMap.put(vm, sw);
    }

    /**
     * Gets a map between hosts and Switches, where each key is a host and the
     * corresponding value is the switch where the host is connected
     * to.
     *
     * @return a read-only map of Host connected to Switches
     */
    public Map<Host, Switch> getHostToSwitchMap() {
        return Collections.unmodifiableMap(hostToSwitchMap);
    }

    /**
     * Connects a host to a given AbstractSwitch.
     *
     * @param host the host to be connected
     * @param sw the AbstractSwitch to connect the host to
     */
    public void addHostToSwitch(Host host, Switch sw){
        hostToSwitchMap.put(host, sw);
    }

    /**
     * Gets a map between VMs and Hosts, where each key is a VM and the
     * corresponding value is the host where the VM is placed.
     *
     * @return a read-only map of VMs placed into Hosts
     *
     * @TODO @author manoelcampos This mapping doesn't make sense, once the placement of
     * VMs into Hosts is dynamic. A VM can be migrated to another host
     * due to several reasons.
     */
    public Map<Vm, NetworkHost> getVmToHostMap() {
        return Collections.unmodifiableMap(vmToHostMap);
    }

    /**
     * Connects a VM to a given host.
     *  @param vm the VM to be connected
     * @param host the host to connect the VM to
     */
    public void addVmToHost(Vm vm, NetworkHost host){
        vmToHostMap.put(vm, host);
    }

}
