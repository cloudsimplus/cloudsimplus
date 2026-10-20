/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// A task executed by a [NetworkCloudlet] that
/// receives data from a [CloudletSendTask].
/// Each receiver task expects to receive packets
/// from just one VM.
///
/// The task never takes more packets than the [expected number][#getExpectedPacketsToReceive()].
/// The remaining ones are kept for the next receive tasks of the same Cloudlet.
///
/// If multiple Cloudlets in the same [source VM][#getSourceVm()] send packets
/// to the Cloudlet of this task (such as in a DAG where a Cloudlet has multiple parents in the same VM),
/// you should give the [sender Cloudlet][#getSenderCloudlet()], so that the task
/// only accepts packets from that Cloudlet.
/// If you don't, the simulation still finishes, as long as the total number of packets
/// sent from that VM to the Cloudlet is equal to the total expected by its receive tasks.
/// However, a task may receive packets sent by another Cloudlet in that VM
/// (for instance, the task waiting for a packet from Cloudlet A may receive the packet from Cloudlet B),
/// so it may finish before the Cloudlet it depends on.
///
/// If fewer packets are sent than expected, the task waits forever
/// and the Cloudlet never finishes, unless a [timeout][#setTimeout(double)] is set.
/// When the timeout is reached, the task stops waiting and the Cloudlet moves to its next task.
/// If there is no timeout, a warning is logged for the first task in a VM
/// that waits longer than [#WAIT_WARNING_TIME].
///
/// A Cloudlet waiting for packets doesn't use the CPU and doesn't hold its PEs.
/// This way, other Cloudlets (such as the sender in the same VM) can run meanwhile,
/// even with a [org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared].
/// When the Cloudlet receives its packets and there are no free PEs,
/// the running Cloudlets share the VM MIPS until some of them finish.
///
/// Please refer to the following publication for more details:
///
/// -
///   [Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS Press, USA), Melbourne, Australia, December 5-7, 2011.](https://doi.org/10.1109/UCC.2011.24)
///
/// @author Saurabh Kumar Garg
/// @author Manoel Campos da Silva Filho
///
/// @since CloudSim Toolkit 1.0
///
/// TODO How is the network delay being computed?
/// See https://github.com/cloudsimplus/cloudsimplus/issues/589
@Getter @Setter
public non-sealed class CloudletReceiveTask extends CloudletTask {
    /// The time (in seconds) a task without a [timeout][#getTimeout()] can wait for packets
    /// before a warning is logged, telling the user the Cloudlet may be waiting
    /// for packets that are never sent.
    /// The warning is logged just for the first task in each VM that waits that long.
    public static final double WAIT_WARNING_TIME = 60;

    private final List<VmPacket> packetsReceived;

    /// The number of packets that are expected to be received.
    /// After receiving them, the task is marked as finished.
    private long expectedPacketsToReceive;

    /// Vm from where packets are expected to be received.
    @NonNull
    private final Vm sourceVm;

    /// The Cloudlet from where packets are expected to be received,
    /// or null if packets from any Cloudlet inside the [source VM][#getSourceVm()] are accepted.
    @Nullable
    private final NetworkCloudlet senderCloudlet;

    /// The maximum time (in seconds) the task waits for the expected packets,
    /// counted from the time the task starts.
    /// When it's reached, the task finishes without all the packets and is [timed out][#isTimedOut()].
    /// Zero (the default) means the task waits forever.
    /// In this case, a warning may be logged if the task waits too long (see [#WAIT_WARNING_TIME]).
    @Setter(AccessLevel.NONE)
    private double timeout;

    /// Indicates if the task finished because the [#getTimeout()] was reached
    /// before receiving all the expected packets.
    @Setter(AccessLevel.NONE)
    private boolean timedOut;

    /// Creates a new task that accepts packets from any Cloudlet inside the given VM.
    /// If multiple Cloudlets in that VM send packets to the Cloudlet of this task,
    /// the task may receive packets from a Cloudlet other than the one it depends on.
    /// In such a case, use [#CloudletReceiveTask(int,Vm,NetworkCloudlet)].
    ///
    /// @param id id to assign to the task
    /// @param sourceVm Vm from where packets are expected to be received
    /// @see #CloudletReceiveTask(int, Vm, NetworkCloudlet)
    public CloudletReceiveTask(final int id, final Vm sourceVm) {
        this(id, sourceVm, null);
    }

    /// Creates a new task that accepts packets just from a given Cloudlet.
    ///
    /// @param id id to assign to the task
    /// @param sourceVm Vm from where packets are expected to be received
    /// @param senderCloudlet Cloudlet from where packets are expected to be received
    /// (or null to accept packets from any Cloudlet inside the sourceVm)
    public CloudletReceiveTask(final int id, @NonNull final Vm sourceVm, @Nullable final NetworkCloudlet senderCloudlet) {
        super(id);
        this.packetsReceived = new ArrayList<>();
        this.sourceVm = sourceVm;
        this.senderCloudlet = senderCloudlet;
    }

    /// Checks if a packet is expected by this task, considering its
    /// [source VM][#getSourceVm()], [sender Cloudlet][#getSenderCloudlet()] (if given)
    /// and the Cloudlet the task belongs to.
    ///
    /// @param packet the packet to check
    /// @return true if the packet is expected by this task, false otherwise
    public boolean isPacketExpected(@NonNull final VmPacket packet) {
        return packet.getSource().equals(sourceVm) &&
               packet.getReceiverCloudlet().equals(getCloudlet()) &&
               isPacketFromExpectedSender(packet);
    }

    private boolean isPacketFromExpectedSender(final VmPacket packet) {
        return senderCloudlet == null || senderCloudlet.equals(packet.getSenderCloudlet());
    }

    /// @return the number of packets still to be received for the task to finish.
    public long getRemainingPacketsToReceive() {
        return Math.max(0, expectedPacketsToReceive - packetsReceived.size());
    }

    /// Receives a packet sent from a [CloudletSendTask]
    /// and add it to the received packet list.
    ///
    /// @param packet the packet received
    public void receivePacket(final VmPacket packet) {
        packet.setReceiveTime(getCloudlet().getSimulation().clock());
        this.packetsReceived.add(packet);
        final boolean finished = this.packetsReceived.size() >= expectedPacketsToReceive;
        setFinished(finished);
    }

    /// @return a read-only list of received packets
    public List<VmPacket> getPacketsReceived() {
        return Collections.unmodifiableList(packetsReceived);
    }

    /// Sets the maximum time (in seconds) the task waits for the expected packets.
    ///
    /// @param timeout the timeout to set, or zero to wait forever
    /// @return this task
    /// @see #getTimeout()
    public CloudletReceiveTask setTimeout(final double timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout cannot be negative.");
        }

        this.timeout = timeout;
        return this;
    }

    /// @return true if a [#getTimeout()] is set, false if the task waits forever
    public boolean hasTimeout() {
        return timeout > 0;
    }

    /// Gets the time remaining for the task to time out.
    ///
    /// @param currentTime the current simulation time (in seconds)
    /// @return the time remaining (in seconds) for the task to time out;
    /// or [Double#MAX_VALUE] if the task has no timeout, hasn't started or is finished.
    public double getRemainingTimeout(final double currentTime) {
        if (!hasTimeout() || !isWaiting()) {
            return Double.MAX_VALUE;
        }

        return Math.max(0, timeout - getWaitingTime(currentTime));
    }

    /// Gets the time the task has been waiting for packets.
    ///
    /// @param currentTime the current simulation time (in seconds)
    /// @return the time (in seconds) since the task started;
    /// or 0 if the task hasn't started or is finished.
    public double getWaitingTime(final double currentTime) {
        return isWaiting() ? Math.max(0, currentTime - getStartTime()) : 0;
    }

    /// Checks if the task has no [#getTimeout()] and has been waiting
    /// for packets for at least [#WAIT_WARNING_TIME].
    ///
    /// @param currentTime the current simulation time (in seconds)
    /// @return true if the task is waiting too long, false otherwise
    public boolean isWaitingTooLong(final double currentTime) {
        return getRemainingWaitWarningTime(currentTime) == 0;
    }

    /// Gets the time remaining for the task to reach the [#WAIT_WARNING_TIME].
    ///
    /// @param currentTime the current simulation time (in seconds)
    /// @return the time remaining (in seconds) for the task to be waiting too long;
    /// or [Double#MAX_VALUE] if the task has a timeout, hasn't started or is finished.
    public double getRemainingWaitWarningTime(final double currentTime) {
        if (hasTimeout() || !isWaiting()) {
            return Double.MAX_VALUE;
        }

        return Math.max(0, WAIT_WARNING_TIME - getWaitingTime(currentTime));
    }

    /// @return true if the task has started and is still waiting for packets, false otherwise
    private boolean isWaiting() {
        return getStartTime() >= 0 && isActive();
    }

    /// Finishes the task if its [#getTimeout()] is reached,
    /// so that the Cloudlet stops waiting for packets.
    ///
    /// @param currentTime the current simulation time (in seconds)
    /// @return true if the task has just timed out, false otherwise
    public boolean finishIfTimedOut(final double currentTime) {
        if (getRemainingTimeout(currentTime) > 0) {
            return false;
        }

        timedOut = true;
        setFinished(true);
        return true;
    }
}
