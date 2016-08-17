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

    private final List<NetworkPacket> packetToSendLocal;

    private final List<NetworkPacket> packetToSendGlobal;

    /**
     * List of received packets.
     */
    private final List<NetworkPacket> packetsReceived;

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

        packetsReceived = new ArrayList<>();
        packetToSendGlobal = new ArrayList<>();
        packetToSendLocal = new ArrayList<>();

    }

    @Override
    public double updateVmsProcessing(double currentTime) {
        double smallerTime = Double.MAX_VALUE;
        // insert in each vm packet recieved
        receivePackets();
        for (Vm vm : getVmList()) {
            double time = 
                    ((NetworkVm) vm).updateVmProcessing(
                            currentTime, 
                            getVmScheduler().getAllocatedMipsForVm(vm));
            if (time > 0.0 && time < smallerTime) {
                smallerTime = time;
            }
        }
        
        // send the packets to other hosts/VMs
        sendPackets();

        return smallerTime;
    }

    /**
     * Receives packets and forward them to the corresponding VM.
     */
    private void receivePackets() {
        for (NetworkPacket hs : packetsReceived) {
            hs.getPkt().setReceiveTime(CloudSim.clock());

            // insert the packet in recievedlist of VM
            Vm vm = VmList.getById(getVmList(), hs.getPkt().getReceiverVmId());
            NetworkCloudletSpaceSharedScheduler sched = ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler());
            List<HostPacket> pktlist = sched.getHostPacketsReceivedMap().get(hs.getPkt().getSenderVmId());

            if (pktlist == null) {
                pktlist = new ArrayList<>();
                sched.getHostPacketsReceivedMap().put(hs.getPkt().getSenderVmId(), pktlist);
            }
            pktlist.add(hs.getPkt());

        }
        packetsReceived.clear();
    }

    /**
     * Sends packets checks whether a packet belongs to a local VM or to a VM
     * hosted on other machine.
     */
    private void sendPackets() {
        getVmList().forEach(vm -> sendAllPacketListsOfVm(vm));

        boolean flag = false;

        for (NetworkPacket hs : packetToSendLocal) {
            flag = true;
            hs.setSendTime(hs.getReceiveTime());
            hs.getPkt().setReceiveTime(CloudSim.clock());
            // insert the packet in recievedlist
            Vm vm = VmList.getById(getVmList(), hs.getPkt().getReceiverVmId());

            List<HostPacket> pktlist = 
                    ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler())
                            .getHostPacketsReceivedMap().get(hs.getPkt().getSenderVmId());
            if (pktlist == null) {
                pktlist = new ArrayList<>();
                    ((NetworkCloudletSpaceSharedScheduler) vm.getCloudletScheduler())
                            .getHostPacketsReceivedMap().put(hs.getPkt().getSenderVmId(), pktlist);
            }
            pktlist.add(hs.getPkt());
        }
        
        if (flag) {
            for (Vm vm : getVmList()) {
                vm.updateVmProcessing(
                        CloudSim.clock(), getVmScheduler().getAllocatedMipsForVm(vm));
            }
        }

        // Sending packet to other VMs therefore packet is forwarded to an Edge switch
        packetToSendLocal.clear();
        double avband = bandwidth / packetToSendGlobal.size();
        for (NetworkPacket hs : packetToSendGlobal) {
            double delay = (1000 * hs.getPkt().getDataLength()) / avband;
            totalDataTransferBytes += hs.getPkt().getDataLength();

            // send to switch with delay
            CloudSim.send(
                    getDatacenter().getId(), getEdgeSwitch().getId(), 
                    delay, CloudSimTags.NETWORK_EVENT_UP, hs);
        }
        packetToSendGlobal.clear();
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
        List<HostPacket> pktList = es.getValue();
        for (HostPacket hostPkt : pktList) {
            NetworkPacket networkPkt = new NetworkPacket(getId(), hostPkt);
            Vm receiverVm = VmList.getById(this.getVmList(), hostPkt.getReceiverVmId());
            if (receiverVm != null) {
                packetToSendLocal.add(networkPkt);
            } else {
                packetToSendGlobal.add(networkPkt);
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

    public List<NetworkPacket> getPacketsReceived() {
        return packetsReceived;
    }

}
