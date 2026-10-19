/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A task executed by a {@link NetworkCloudlet} that
 * receives data from a {@link CloudletSendTask}.
 * Each receiver task expects to receive packets
 * from just one VM.
 *
 * <p>The task never takes more packets than the {@link #getExpectedPacketsToReceive() expected number}.
 * The remaining ones are kept for the next receive tasks of the same Cloudlet.</p>
 *
 * <p>If multiple Cloudlets in the same {@link #getSourceVm() source VM} send packets
 * to the Cloudlet of this task (such as in a DAG where a Cloudlet has multiple parents in the same VM),
 * you should give the {@link #getSenderCloudlet() sender Cloudlet}, so that the task
 * only accepts packets from that Cloudlet.
 * If you don't, the simulation still finishes, as long as the total number of packets
 * sent from that VM to the Cloudlet is equal to the total expected by its receive tasks.
 * However, a task may receive packets sent by another Cloudlet in that VM
 * (for instance, the task waiting for a packet from Cloudlet A may receive the packet from Cloudlet B),
 * so it may finish before the Cloudlet it depends on.</p>
 *
 * <p>If fewer packets are sent than expected, the task waits forever
 * and the Cloudlet never finishes, since there is no timeout for receiving packets yet.</p>
 *
 * <p><b>WARNING:</b> A Cloudlet waiting for packets keeps using its PEs.
 * If you use a {@link org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared}
 * and the sender Cloudlet is waiting in the same VM for PEs to be released,
 * the simulation will get stuck (deadlock).</p>
 *
 * <p>
 * Please refer to the following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 *
 * TODO For how long will the task be waiting for packets?
 * The sender task has a defined amount of packets to send, but
 * the receiver doesn't have to know how many packets to wait for.
 * Considering a real distributed app such as a web app, the sender can be
 * a browser and the receiver a web server. In this case,
 * the web server runs indefinitely. However, the simulation
 * has to have a time interval. For instance, it may be simulated
 * the operation of this distributed app for 24 hours.
 * This way, the receiver task could have a specific amount
 * of time to run. In the web app scenario, the web server just
 * keep running, waiting for any client to send packets to it,
 * but it is not required that a client do that. The web app
 * may not be accessed during this time.
 * On other hand, the client may send packets to the server
 * and require a response. It then waits for this response
 * and must have a timeout period, so that the client can:
 * (i) stop waiting a reply, (ii) then move to the next task or finish.
 *
 * Each task has to have a status (such as the Cloudlet itself)
 * to define if it was executed successfully or not.
 * For instance, a CloudletReceiveTask that is waiting to receive
 * a list of packets can be configured to finish
 * after a given timeout without receiving the expected packets.
 *
 * How is the network delay being computed?
 */
@Getter @Setter
public non-sealed class CloudletReceiveTask extends CloudletTask {
    private final List<VmPacket> packetsReceived;

    /**
     * The number of packets that are expected to be received.
     * After receiving them, the task is marked as finished.
     */
    private long expectedPacketsToReceive;

    /**
     * Vm from where packets are expected to be received.
     */
    @NonNull
    private final Vm sourceVm;

    /**
     * The Cloudlet from where packets are expected to be received,
     * or null if packets from any Cloudlet inside the {@link #getSourceVm() source VM} are accepted.
     */
    private final NetworkCloudlet senderCloudlet;

    /**
     * Creates a new task that accepts packets from any Cloudlet inside the given VM.
     * If multiple Cloudlets in that VM send packets to the Cloudlet of this task,
     * the task may receive packets from a Cloudlet other than the one it depends on.
     * In such a case, use {@link #CloudletReceiveTask(int, Vm, NetworkCloudlet)}.
     *
     * @param id id to assign to the task
     * @param sourceVm Vm from where packets are expected to be received
     * @see #CloudletReceiveTask(int, Vm, NetworkCloudlet)
     */
    public CloudletReceiveTask(final int id, final Vm sourceVm) {
        this(id, sourceVm, null);
    }

    /**
     * Creates a new task that accepts packets just from a given Cloudlet.
     *
     * @param id id to assign to the task
     * @param sourceVm Vm from where packets are expected to be received
     * @param senderCloudlet Cloudlet from where packets are expected to be received
     *                       (or null to accept packets from any Cloudlet inside the sourceVm)
     */
    public CloudletReceiveTask(final int id, @NonNull final Vm sourceVm, final NetworkCloudlet senderCloudlet) {
        super(id);
        this.packetsReceived = new ArrayList<>();
        this.sourceVm = sourceVm;
        this.senderCloudlet = senderCloudlet;
    }

    /**
     * Checks if a packet is expected by this task, considering its
     * {@link #getSourceVm() source VM}, {@link #getSenderCloudlet() sender Cloudlet} (if given)
     * and the Cloudlet the task belongs to.
     *
     * @param packet the packet to check
     * @return true if the packet is expected by this task, false otherwise
     */
    public boolean isPacketExpected(@NonNull final VmPacket packet) {
        return packet.getSource().equals(sourceVm) &&
               packet.getReceiverCloudlet().equals(getCloudlet()) &&
               isPacketFromExpectedSender(packet);
    }

    private boolean isPacketFromExpectedSender(final VmPacket packet) {
        return senderCloudlet == null || senderCloudlet.equals(packet.getSenderCloudlet());
    }

    /**
     * @return the number of packets still to be received for the task to finish.
     */
    public long getRemainingPacketsToReceive() {
        return Math.max(0, expectedPacketsToReceive - packetsReceived.size());
    }

    /**
     * Receives a packet sent from a {@link CloudletSendTask}
     * and add it to the received packet list.
     *
     * @param packet the packet received
     */
    public void receivePacket(final VmPacket packet) {
        packet.setReceiveTime(getCloudlet().getSimulation().clock());
        this.packetsReceived.add(packet);
        final boolean finished = this.packetsReceived.size() >= expectedPacketsToReceive;
        setFinished(finished);
    }

    /**
     * @return a read-only list of received packets
     */
    public List<VmPacket> getPacketsReceived() {
        return Collections.unmodifiableList(packetsReceived);
    }
}
