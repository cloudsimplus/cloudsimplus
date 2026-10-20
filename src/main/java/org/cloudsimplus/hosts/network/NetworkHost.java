/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.hosts.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.network.switches.EdgeSwitch;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskSchedulerSimple;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// A Host that support simulation of networked [Datacenter]s. It executes actions related to the management of packets
/// (sent and received) besides [Vm] management (e.g., creation and destruction).
/// A Host has a defined policy for provisioning memory and bw, as
/// well as an allocation policy for [Pe]'s to virtual machines.
///
/// Please refer to the following publication for more details:
///
/// -
///   [Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011.](https://doi.org/10.1109/UCC.2011.24)
///
/// @author Saurabh Kumar Garg
/// @since CloudSim Toolkit 3.0
@Accessors
public class NetworkHost extends HostSimple {
    public static final NetworkHost NULL = new NetworkHost();
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHost.class.getSimpleName());

    @Getter
    private long totalDataTransferBytes;

    /// A buffer of packets to send for VMs inside this Host.
    private final List<HostPacket> pktsToSendForLocalVms;

    /// A buffer of packets to send for VMs outside this Host.
    private final List<HostPacket> pktsToSendForExternalVms;

    /// List of received packets.
    private final List<HostPacket> hostPktsReceived;

    /// An Edge Switch the Host is directly connected to.
    /// TODO Setter is to be called only by the [EdgeSwitch#connectHost(NetworkHost)] method.
    /// See https://github.com/cloudsimplus/cloudsimplus/issues/589
    @Setter @Nullable
    private EdgeSwitch edgeSwitch;

    /// Creates and starts up a NetworkHost using a [VmSchedulerSpaceShared] as default.
    ///
    /// @param ram the RAM capacity in Megabytes
    /// @param bw the Bandwidth (BW) capacity in Megabits/s
    /// @param storage the storage capacity in Megabytes
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    public NetworkHost(final long ram, final long bw, final long storage, final long pesNumber, final double mips) {
        super(ram, bw, storage, pesNumber, mips);
        hostPktsReceived = new ArrayList<>();
        pktsToSendForExternalVms = new ArrayList<>();
        pktsToSendForLocalVms = new ArrayList<>();
    }

    /// @deprecated Use [#NetworkHost(long, long, long, long, double)] instead.
    @Deprecated(forRemoval = true)
    public NetworkHost(final long ram, final long bw, final long storage, final List<Pe> peList) {
        super(ram, bw, storage, peList);
        hostPktsReceived = new ArrayList<>();
        pktsToSendForExternalVms = new ArrayList<>();
        pktsToSendForLocalVms = new ArrayList<>();
    }

    /// Creates a NetworkHost with no resources.
    private NetworkHost() {
        this(0, 0, 0, 1, 0);
        setId(-1);
    }

    @Override
    public double updateProcessing(final double currentTime) {
        final double nextFinishingCloudletTime = super.updateProcessing(currentTime);
        receivePackets();
        sendAllPacketListsOfAllVms();

        return  nextFinishingCloudletTime;
    }

    /// Receives packets and forwards them to target VMs and respective Cloudlets.
    private void receivePackets() {
        for (final HostPacket hostPkt : hostPktsReceived) {
            receivePacket(hostPkt.getVmPacket());
        }

        hostPktsReceived.clear();
    }

    private void receivePacket(final VmPacket vmPacket) {
        final Vm destinationVm = receiveVmPacket(vmPacket);
        if(getVmList().contains(destinationVm)){
            final CloudletTaskScheduler taskScheduler = getVmPacketScheduler(destinationVm);
            taskScheduler.addPacketToListOfPacketsSentFromVm(vmPacket);
            LOGGER.trace(
                "{}: {}: {} received pkt with {} bytes from {} in {} and forwarded it to {} in {}",
                getSimulation().clockStr(), getClass().getSimpleName(), this,
                vmPacket.getSize(), vmPacket.getSenderCloudlet(), vmPacket.getSource(),
                vmPacket.getReceiverCloudlet(), vmPacket.getDestination());
            return;
        }

        LOGGER.warn(
                "{}: {}: Destination {} was not found inside {}",
                getSimulation().clockStr(), getClass(), vmPacket.getDestination(), this);
    }

    /// Receives a packet targeting some VM and sets the packet receive time.
    ///
    /// @param vmPacket the [VmPacket] to receive
    /// @return the targeting VM
    private Vm receiveVmPacket(final VmPacket vmPacket) {
        vmPacket.setReceiveTime(getSimulation().clock());
        return vmPacket.getDestination();
    }

    /// Gets all packet lists from all VMs placed into the Host and sends them all.
    /// It checks whether a packet belongs to a local VM or to a VM hosted on another machine.
    private void sendAllPacketListsOfAllVms() {
        getVmList().forEach(this::collectAllPacketsToSendFromVm);
        sendPacketsToLocalVms();
        sendPacketsToExternalVms();
    }

    /// Gets the packets from the local packets buffer and sends them to VMs inside this host.
    private void sendPacketsToLocalVms() {
        for (final HostPacket hostPkt : pktsToSendForLocalVms) {
            hostPkt.setSendTime(hostPkt.getReceiveTime());
            final Vm destinationVm = receiveVmPacket(hostPkt.getVmPacket());
            // insert the packet in the received list
            getVmPacketScheduler(destinationVm).addPacketToListOfPacketsSentFromVm(hostPkt.getVmPacket());
        }

        if (!pktsToSendForLocalVms.isEmpty()) {
            for (final Vm vm : getVmList()) {
                vm.updateProcessing(getVmScheduler().getAllocatedMips(vm));
            }
        }

        pktsToSendForLocalVms.clear();
    }

    /// {@return the Edge Switch the Host is directly connected to}
    /// @throws IllegalStateException when the Host is not connected to any switch
    public EdgeSwitch getEdgeSwitch() {
        if (edgeSwitch == null) {
            throw new IllegalStateException("%s is not connected to any %s".formatted(this, EdgeSwitch.class.getSimpleName()));
        }

        return edgeSwitch;
    }

    /// Sends packets from the local packets buffer to VMs outside this host.
    private void sendPacketsToExternalVms() {
        for (final HostPacket pkt : pktsToSendForExternalVms) {
            final double delay = getEdgeSwitch().downlinkTransferDelay(pkt, pktsToSendForExternalVms.size());
            totalDataTransferBytes += pkt.getSize();

            getSimulation().send(
                    getDatacenter(), getEdgeSwitch(),
                    delay, CloudSimTag.NETWORK_EVENT_UP, pkt);
        }

        pktsToSendForExternalVms.clear();
    }

    private CloudletTaskScheduler getVmPacketScheduler(final Vm vm) {
        return vm.getCloudletScheduler().getTaskScheduler();
    }

    /// {@inheritDoc}
    ///
    /// **It also creates and sets a [CloudletTaskScheduler] for each
    /// Vm that doesn't have one already.**
    ///
    /// @param vm {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public HostSuitability createVm(final Vm vm) {
        final HostSuitability suitability = super.createVm(vm);
        setPacketScheduler(vm);
        return suitability;
    }

    private void setPacketScheduler(final Vm vm) {
        final var scheduler = vm.getCloudletScheduler();
        if(!scheduler.isThereTaskScheduler()){
            scheduler.setTaskScheduler(new CloudletTaskSchedulerSimple());
        }
    }

    /// Collects all packets inside a specific packet list from a Vm, so that those packets can be sent further on.
    ///
    /// @param sourceVm the VM from where the packets will be sent
    private void collectAllPacketsToSendFromVm(final Vm sourceVm) {
        final CloudletTaskScheduler taskScheduler = getVmPacketScheduler(sourceVm);
        for (final VmPacket vmPkt : taskScheduler.getVmPacketsToSend()) {
            collectPacketToSendFromVm(vmPkt);
        }

        taskScheduler.clearVmPacketsToSend();
    }

    /// Collects a specific packet from a given Vm to join with other packets to be sent.
    ///
    /// @param vmPkt a packet to be sent from a Vm to another one
    /// @see #collectAllPacketsToSendFromVm(Vm)
    private void collectPacketToSendFromVm(final VmPacket vmPkt) {
        final var hostPkt = new HostPacket(this, vmPkt);
        final var receiverVm = vmPkt.getDestination();

        // If the VM is inside this Host, the packet doesn't travel through the network
        final var pktsToSendList = getVmList().contains(receiverVm) ? pktsToSendForLocalVms : pktsToSendForExternalVms;
        pktsToSendList.add(hostPkt);
    }

    /// Adds a packet to the list of received packets
    /// to further submit them to the respective target VMs and Cloudlets.
    ///
    /// @param hostPacket received network packet
    public void addReceivedNetworkPacket(final HostPacket hostPacket){
        hostPktsReceived.add(hostPacket);
    }
}
