/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.NetworkCloudletSpaceSharedScheduler;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;

/**
 * NetworkHost class extends {@link HostSimple} to support simulation of
 * networked datacenters. It executes actions related to management of packets
 * (sent and received) other than that of virtual machines (e.g., creation and
 * destruction). A host has a defined policy for provisioning memory and bw, as
 * well as an allocation policy for PE's to virtual machines.
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
 * @since CloudSim Toolkit 3.0
 */
public class NetworkHost extends HostSimple {

    private int totalDataTransferBytes = 0;

    private final List<HostPacket> hostPacketsToSendLocal;

    private final List<HostPacket> hostPacketsToSendGlobal;

    /**
     * List of received packets.
     */
    private final List<HostPacket> hostPacketsReceived;

    /**
     * Edge switch in which the Host is connected.
     */
    private EdgeSwitch edgeSwitch;

    /**
     * @TODO What exactly is this bandwidth? Because it is redundant with the bw
     * capacity defined in {@link Host#getBwProvisioner()}
     */
    public double bandwidth;

    /**
     * Creates a NetworkHost.
     *
     * @param id the id
     * @param storage the storage capacity
     * @param peList the host's PEs list
     *
     */
    public NetworkHost(int id, long storage, List<Pe> peList) {
        super(id, storage, peList);
        hostPacketsReceived = new ArrayList<>();
        hostPacketsToSendGlobal = new ArrayList<>();
        hostPacketsToSendLocal = new ArrayList<>();
    }

    /**
     * Creates a NetworkHost with the given parameters.
     *
     * @param id the id
     * @param ramProvisioner the ram provisioner
     * @param bwProvisioner the bw provisioner
     * @param storage the storage capacity
     * @param peList the host's PEs list
     * @param vmScheduler the VM scheduler
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public NetworkHost(
            int id,
            ResourceProvisioner ramProvisioner,
            ResourceProvisioner bwProvisioner,
            long storage,
            List<Pe> peList,
            VmScheduler vmScheduler)
    {
        this(id, storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setVmScheduler(vmScheduler);
    }

    @Override
    public double updateVmsProcessing(double currentTime) {
        double completionTimeOfNextFinishingCloudlet = super.updateVmsProcessing(currentTime);
        receivePackets();
        sendAllPacketListsOfAllVms();

        return  completionTimeOfNextFinishingCloudlet;
    }

    /**
     * Receives packets and forwards them to the targeting VMs and respective Cloudlets.
     */
    private void receivePackets() {
        try{
            for (HostPacket netPkt : hostPacketsReceived) {
                netPkt.getVmPacket().setReceiveTime(getSimulation().clock());

                Vm vm = VmList.getById(getVmList(), netPkt.getVmPacket().getDestination().getId());
                NetworkCloudletSpaceSharedScheduler sched =
                        ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler());

                sched.addPacketToListOfPacketsSentFromVm(netPkt.getVmPacket());
                Log.println(
                    Log.Level.DEBUG, getClass(), getSimulation().clock(),
                    "Host %d received pkt with %.0f bytes from Cloudlet %d in VM %d and fowarded it to Cloudlet %d in VM %d",
                    getId(), netPkt.getVmPacket().getSize(),
                    netPkt.getVmPacket().getSenderCloudlet().getId(),
                    netPkt.getVmPacket().getSource(),
                    netPkt.getVmPacket().getReceiverCloudlet().getId(),
                    netPkt.getVmPacket().getDestination());
            }

            hostPacketsReceived.clear();
        } catch(Exception e){
            throw new RuntimeException(
                    "Error when cloudlet was receiving packets at time " + getSimulation().clock(), e);
        }
    }

    /**
     * Gets all packet lists of all VMs placed into the host and send them all.
     * It checks whether a packet belongs to a local VM or to a VM hosted on other machine.
     */
    private void sendAllPacketListsOfAllVms() {
        getVmList().forEach(this::collectAllListsOfPacketsToSendFromVm);

        boolean flag = false;

        for (HostPacket netPkt : hostPacketsToSendLocal) {
            flag = true;
            netPkt.setSendTime(netPkt.getReceiveTime());
            netPkt.getVmPacket().setReceiveTime(getSimulation().clock());
            // insert the packet in recievedlist
            Vm vm = VmList.getById(getVmList(), netPkt.getVmPacket().getDestination().getId());

            ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler())
                    .addPacketToListOfPacketsSentFromVm(netPkt.getVmPacket());
        }

        if (flag) {
            for (Vm vm : getVmList()) {
                vm.updateVmProcessing(
                    getSimulation().clock(), getVmScheduler().getAllocatedMipsForVm(vm));
            }
        }

        //Sending packet to other VMs, therefore packet is forwarded to an Edge switch
        hostPacketsToSendLocal.clear();
        double avband = bandwidth / hostPacketsToSendGlobal.size();
        for (HostPacket netPkt : hostPacketsToSendGlobal) {
            double delay = (1000 * netPkt.getVmPacket().getSize()) / avband;
            totalDataTransferBytes += netPkt.getVmPacket().getSize();

            // send to switch with delay
            getSimulation().send(
                    getDatacenter().getId(), getEdgeSwitch().getId(),
                    delay, CloudSimTags.NETWORK_EVENT_UP, netPkt);
        }

        hostPacketsToSendGlobal.clear();
    }

    /**
     * Collects all lists of packets of a given Vm
     * in order to get them together to be sent.
     *
     * @param sourceVm the VM from where the packets will be sent
     */
    protected void collectAllListsOfPacketsToSendFromVm(Vm sourceVm) {
        NetworkCloudletSpaceSharedScheduler sched =
                (NetworkCloudletSpaceSharedScheduler) sourceVm.getCloudletScheduler();
        for (Entry<Vm, List<VmPacket>> es : sched.getHostPacketsToSendMap().entrySet()) {
            collectListOfPacketToSendFromVm(es);
        }
    }

    /**
     * Collects all packets of a specific packet list of a given Vm
     * in order to get them together to be sent.
     *
     * @param es The Map entry from a packet map where the key is the
     * sender VM id and the value is a list of packets to send
     */
    protected void collectListOfPacketToSendFromVm(Entry<Vm, List<VmPacket>> es) {
        List<VmPacket> hostPktList = es.getValue();
        for (VmPacket hostPkt : hostPktList) {
            HostPacket networkPkt = new HostPacket(this, hostPkt);
            Vm receiverVm = VmList.getById(this.getVmList(), hostPkt.getDestination().getId());
            if (receiverVm != Vm.NULL) {
                hostPacketsToSendLocal.add(networkPkt);
            } else {
                hostPacketsToSendGlobal.add(networkPkt);
            }
        }
    }

    /**
     * Gets the maximum utilization among the PEs of a given VM.
     *
     * @param vm The VM to get its PEs maximum utilization
     * @return The maximum utilization among the PEs of the VM.
     */
    public double getMaxUtilizationAmongVmsPes(Vm vm) {
        return PeList.getMaxUtilizationAmongVmsPes(getPeList(), vm);
    }

    public EdgeSwitch getEdgeSwitch() {
        return edgeSwitch;
    }

    public void setEdgeSwitch(EdgeSwitch sw) {
        this.edgeSwitch = sw;
        this.bandwidth = sw.getDownlinkBandwidth();
    }

    public int getTotalDataTransferBytes() {
        return totalDataTransferBytes;
    }

    /**
     * Adds a packet to the list of received packets in order
     * to further submit them to the respective target VMs and Cloudlets.
     *
     * @param hostPacket received network packet
     */
    public void addReceivedNetworkPacket(HostPacket hostPacket){
        hostPacketsReceived.add(hostPacket);
    }

}
