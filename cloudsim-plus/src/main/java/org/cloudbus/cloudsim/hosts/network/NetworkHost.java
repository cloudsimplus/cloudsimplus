/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts.network;

import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.network.HostPacket;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.CloudletTaskSchedulerSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
 * <a href="https://doi.org/10.1109/UCC.2011.24">
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
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHost.class.getSimpleName());

    private int totalDataTransferBytes;

    /**
     * A buffer of packets to send for VMs inside this Host.
     */
    private final List<HostPacket> pktsToSendForLocalVms;

    /**
     * A buffer of packets to send for VMs outside this Host.
     */
    private final List<HostPacket> pktsToSendForExternalVms;

    /**
     * List of received packets.
     */
    private final List<HostPacket> hostPktsReceived;

    /**
     * Edge switch in which the Host is connected.
     */
    private EdgeSwitch edgeSwitch;

    /**
     * @TODO What exactly is this bandwidth? Because it is redundant with the bw
     * capacity defined in {@link Host#getBwProvisioner()}
     *
     * @see #getBandwidth()
     */
    private double bandwidth;

    /**
     * Creates a NetworkHost.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     *
     */
    public NetworkHost(final long ram, final long bw, final long storage, final List<Pe> peList) {
        super(ram, bw, storage, peList);
        hostPktsReceived = new ArrayList<>();
        pktsToSendForExternalVms = new ArrayList<>();
        pktsToSendForLocalVms = new ArrayList<>();
    }

    /**
     * Creates a NetworkHost.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     * @param vmScheduler the VM scheduler
     *
     */
    public NetworkHost(final long ram, final long bw, final long storage, final List<Pe> peList, final VmScheduler vmScheduler)
    {
        this(ram, bw, storage, peList);
        setVmScheduler(vmScheduler);
    }

    @Override
    public double updateProcessing(final double currentTime) {
        final double timeOfNextFinishingCloudlet = super.updateProcessing(currentTime);
        receivePackets();
        sendAllPacketListsOfAllVms();

        return  timeOfNextFinishingCloudlet;
    }

    /**
     * Receives packets and forwards them to targeting VMs and respective Cloudlets.
     */
    private void receivePackets() {
        for (final HostPacket hostPkt : hostPktsReceived) {
            final Vm destinationVm = receiveVmPacket(hostPkt);
            //Checks if the destinationVm is inside this host
            if(!getVmList().contains(destinationVm)){
                LOGGER.error(
                    "{}: {}: Destination {} was not found inside {}",
                    getSimulation().clock(), getClass(),
                    hostPkt.getVmPacket().getDestination(), this);
                return;
            }

            final CloudletTaskScheduler taskScheduler = getVmPacketScheduler(destinationVm);
            taskScheduler.addPacketToListOfPacketsSentFromVm(hostPkt.getVmPacket());
            LOGGER.trace(
                "{}: {}: {} received pkt with {} bytes from {} in {} and forwarded it to {} in {}",
                getSimulation().clock(), getClass().getSimpleName(),
                this, hostPkt.getVmPacket().getSize(),
                hostPkt.getVmPacket().getSenderCloudlet(),
                hostPkt.getVmPacket().getSource(),
                hostPkt.getVmPacket().getReceiverCloudlet(),
                hostPkt.getVmPacket().getDestination());
        }

        hostPktsReceived.clear();
    }

    /**
     * Receives a packet from a Host that is targeting some VM
     * and sets the packet receive time.
     *
     * @param hostPkt the {@link HostPacket} to receive
     * @return the targeting VM
     */
    private Vm receiveVmPacket(final HostPacket hostPkt) {
        hostPkt.getVmPacket().setReceiveTime(getSimulation().clock());
        return hostPkt.getVmPacket().getDestination();
    }

    /**
     * Gets all packet lists of all VMs placed into the host and send them all.
     * It checks whether a packet belongs to a local VM or to a VM hosted on other machine.
     */
    private void sendAllPacketListsOfAllVms() {
        getVmList().forEach(this::collectListOfPacketsToSendFromVm);
        sendPacketsToLocalVms();
        sendPacketsToExternalVms();
    }

    /**
     * Gets the packets from the local packets buffer and sends them
     * to VMs inside this host.
     */
    private void sendPacketsToLocalVms() {
        for (final HostPacket hostPkt : pktsToSendForLocalVms) {
            hostPkt.setSendTime(hostPkt.getReceiveTime());
            final Vm destinationVm = receiveVmPacket(hostPkt);
            // insert the packet in receivedlist
            getVmPacketScheduler(destinationVm).addPacketToListOfPacketsSentFromVm(hostPkt.getVmPacket());
        }

        if (!pktsToSendForLocalVms.isEmpty()) {
            for (final Vm vm : getVmList()) {
                vm.updateProcessing(
                    getSimulation().clock(), getVmScheduler().getAllocatedMips(vm));
            }
        }

        pktsToSendForLocalVms.clear();
    }

    /**
     * Gets the packets from the local packets buffer and sends them
     * to VMs outside this host.
     */
    private void sendPacketsToExternalVms() {
        final double availableBwByPacket = getBandwidthByPacket(pktsToSendForExternalVms.size());
        for (final HostPacket hostPkt : pktsToSendForExternalVms) {
            final double delay = Conversion.bytesToMegaBits(hostPkt.getVmPacket().getSize()) / availableBwByPacket;
            totalDataTransferBytes += hostPkt.getVmPacket().getSize();

            // send to Datacenter with delay
            getSimulation().send(
                    getDatacenter(), getEdgeSwitch(),
                    delay, CloudSimTags.NETWORK_EVENT_UP, hostPkt);
        }

        pktsToSendForExternalVms.clear();
    }

    /**
     * Gets the bandwidth (in  Megabits/s) that will be available for each packet considering a given number of packets
     * that are expected to be sent.
     *
     * @param numberOfPackets the expected number of packets to sent
     * @return the available bandwidth (in  Megabits/s) for each packet or the total bandwidth if the number of packets is 0 or 1
     */
    private double getBandwidthByPacket(final double numberOfPackets) {
        return numberOfPackets == 0 ? bandwidth : bandwidth / numberOfPackets;
    }

    private CloudletTaskScheduler getVmPacketScheduler(Vm vm) {
        return vm.getCloudletScheduler().getTaskScheduler();
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>It also creates and sets a {@link CloudletTaskScheduler} for each
     * Vm that doesn't have one already.</b></p>
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean createVm(final Vm vm) {
        final boolean isVmCreated = super.createVm(vm);
        setPacketScheduler(vm);
        return isVmCreated;
    }

    private void setPacketScheduler(final Vm vm) {
        final CloudletScheduler scheduler = vm.getCloudletScheduler();
        if(!scheduler.isThereTaskScheduler()){
            scheduler.setTaskScheduler(new CloudletTaskSchedulerSimple());
        }
    }

    /**
     * Collects all packets of a specific packet list from a Vm
     * in order to get them together to be sent.
     *
     * @param sourceVm the VM from where the packets will be sent
     */
    private void collectListOfPacketsToSendFromVm(final Vm sourceVm) {
        final CloudletTaskScheduler taskScheduler = getVmPacketScheduler(sourceVm);
        for (final VmPacket vmPkt : taskScheduler.getVmPacketsToSend()) {
            collectPacketToSendFromVm(vmPkt);
        }

        taskScheduler.clearVmPacketsToSend();
    }

    /**
     * Collects a specific packet from a given Vm
     * in order to get it together with other packets to be sent.
     *
     * @param vmPkt a packet to be sent from a Vm to another one
     * @see #collectListOfPacketsToSendFromVm(Vm)
     */
    private void collectPacketToSendFromVm(final VmPacket vmPkt) {
        final HostPacket hostPkt = new HostPacket(this, vmPkt);
        final Vm receiverVm = vmPkt.getDestination();
        //Checks if the VM is inside this Host
        if (getVmList().contains(receiverVm)) {
            pktsToSendForLocalVms.add(hostPkt);
        } else {
            pktsToSendForExternalVms.add(hostPkt);
        }
    }

    public EdgeSwitch getEdgeSwitch() {
        return edgeSwitch;
    }

    public void setEdgeSwitch(final EdgeSwitch edgeSwitch) {
        this.edgeSwitch = edgeSwitch;
        this.bandwidth = edgeSwitch.getDownlinkBandwidth();
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
    public void addReceivedNetworkPacket(final HostPacket hostPacket){
        hostPktsReceived.add(hostPacket);
    }

    /**
     * Gets the Host bandwidth capacity in  Megabits/s.
     * @return
     */
    public double getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets the Host bandwidth capacity in  Megabits/s.
     * @param bandwidth the bandwidth to set
     */
    public void setBandwidth(final double bandwidth) {
        this.bandwidth = bandwidth;
    }
}
