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
import java.util.Map.Entry;

import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmScheduler;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
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

    private final List<NetworkPacket> networkPacketsToSendLocal;

    private final List<NetworkPacket> networkPacketsToSendGlobal;

    /**
     * List of received packets.
     */
    private final List<NetworkPacket> networkPacketsReceived;

    /**
     * Edge switch in which the Host is connected.
     */
    private EdgeSwitch edgeSwitch;

    /**
     * @todo What exactly is this bandwidth? Because it is redundant with the bw
     * capacity defined in {@link Host#bwProvisioner}
     */
    public double bandwidth;

    public NetworkHost(
            int id,
            ResourceProvisioner<Integer> ramProvisioner,
            ResourceProvisioner<Long> bwProvisioner,
            long storage,
            List<Pe> peList,
            VmScheduler vmScheduler) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);

        networkPacketsReceived = new ArrayList<>();
        networkPacketsToSendGlobal = new ArrayList<>();
        networkPacketsToSendLocal = new ArrayList<>();

    }

    @Override
    public double updateVmsProcessing(double currentTime) {
        receivePackets();        
        sendPackets();

        return super.updateVmsProcessing(currentTime);
    }

    /**
     * Receives packets and forwards them to the corresponding VM.
     */
    private void receivePackets() {
        for (NetworkPacket netPkt : networkPacketsReceived) {
            netPkt.getHostPacket().setReceiveTime(CloudSim.clock());

            // insert the packet in recievedlist of VM
            Vm vm = VmList.getById(getVmList(), netPkt.getHostPacket().getReceiverVmId());
            NetworkCloudletSpaceSharedScheduler sched = 
                    ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler());
            List<HostPacket> hostPktList = 
                    sched.getHostPacketsReceivedMap().get(netPkt.getHostPacket().getSenderVmId());

            if (hostPktList == null) {
                hostPktList = new ArrayList<>();
                sched.getHostPacketsReceivedMap().put(
                        netPkt.getHostPacket().getSenderVmId(), hostPktList);
            }
            hostPktList.add(netPkt.getHostPacket());

        }
        
        networkPacketsReceived.clear();
    }

    /**
     * Sends packets to another Hosts/VMs.
     * It checks whether a packet belongs to a local VM or to a VM
     * hosted on other machine.
     */
    private void sendPackets() {
        getVmList().forEach(vm -> sendAllPacketListsOfVm(vm));

        boolean flag = false;

        for (NetworkPacket netPkt : networkPacketsToSendLocal) {
            flag = true;
            netPkt.setSendTime(netPkt.getReceiveTime());
            netPkt.getHostPacket().setReceiveTime(CloudSim.clock());
            // insert the packet in recievedlist
            Vm vm = VmList.getById(getVmList(), netPkt.getHostPacket().getReceiverVmId());

            List<HostPacket> hostPktList = 
                    ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler())
                            .getHostPacketsReceivedMap()
                            .get(netPkt.getHostPacket().getSenderVmId());
            if (hostPktList == null) {
                hostPktList = new ArrayList<>();
                    ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler())
                            .getHostPacketsReceivedMap()
                            .put(netPkt.getHostPacket().getSenderVmId(), hostPktList);
            }
            hostPktList.add(netPkt.getHostPacket());
        }
        
        if (flag) {
            for (Vm vm : getVmList()) {
                vm.updateVmProcessing(
                        CloudSim.clock(), getVmScheduler().getAllocatedMipsForVm(vm));
            }
        }

        // Sending packet to other VMs therefore packet is forwarded to an Edge switch
        networkPacketsToSendLocal.clear();
        double avband = bandwidth / networkPacketsToSendGlobal.size();
        for (NetworkPacket netPkt : networkPacketsToSendGlobal) {
            double delay = (1000 * netPkt.getHostPacket().getDataLength()) / avband;
            totalDataTransferBytes += netPkt.getHostPacket().getDataLength();

            // send to switch with delay
            CloudSim.send(
                    getDatacenter().getId(), getEdgeSwitch().getId(), 
                    delay, CloudSimTags.NETWORK_EVENT_UP, netPkt);
        }
        
        networkPacketsToSendGlobal.clear();
    }

    /**
     * Sends all lists of packets of a given Vm.
     * @param sourceVm the VM from where the packets will be sent
     */
    protected void sendAllPacketListsOfVm(Vm sourceVm) {
        NetworkCloudletSpaceSharedScheduler sched = 
                (NetworkCloudletSpaceSharedScheduler) sourceVm.getCloudletScheduler();
        for (Entry<Integer, List<HostPacket>> es : sched.getHostPacketsToSendMap().entrySet()) {
            sendPacketListOfVm(es, sourceVm);
        }
    }

    /**
     * Sends all packets of a specific packet list of a given Vm.
     * 
     * @param es The entry set from a packet map where the key is the 
     * sender VM id and the value is the list of packets to send
     * @param senderVm the sender VM
     */
    protected void sendPacketListOfVm(Entry<Integer, List<HostPacket>> es, Vm senderVm) {
        List<HostPacket> hostPktList = es.getValue();
        for (HostPacket hostPkt : hostPktList) {
            NetworkPacket networkPkt = new NetworkPacket(getId(), hostPkt);
            Vm receiverVm = VmList.getById(this.getVmList(), hostPkt.getReceiverVmId());
            if (receiverVm != null) {
                networkPacketsToSendLocal.add(networkPkt);
            } else {
                networkPacketsToSendGlobal.add(networkPkt);
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

    public List<NetworkPacket> getNetworkPacketsReceived() {
        return networkPacketsReceived;
    }

}
