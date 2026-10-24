/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.power.PowerMeter;
import org.cloudsimplus.power.models.PowerModel;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;
import org.cloudsimplus.vms.Vm;

/// Tags indicating a type of action that
/// needs to be undertaken by CloudSim entities when they receive or send events.
/// Such tags are used when sending events using [Simulation#send(SimEntity,SimEntity,double,int,Object)]
/// or [SimEntity#schedule(double,int)] or overloaded methods.
///
/// **NOTE:** To avoid conflicts with other tags,
/// CloudSim reserves numbers lower than 300 and the number 9600.
/// Each tag must have a unique value (ensured by tests).
/// An enum is not used here because that stops users to create custom tags without changing the framework code.
///
/// @author Manzur Murshed
/// @author Rajkumar Buyya
/// @author Anthony Sulistio
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class CloudSimTag{
    public static final int NONE = -1;

    /// Starting constant value for cloud-related tags.
    private static final int BASE = 0;

    /// Starting constant value for network-related tags.
    private static final int NET_BASE = 100;

    /// Denotes the end of the simulation.
    public static final int SIMULATION_END = -2;

    /// Tag used for requesting an entity to shut down.
    /// That ensures a graceful shutdown, after other entity events are processed.
    public static final int ENTITY_SHUTDOWN = -3;

    /// Denotes a request from a [Datacenter] to register itself. This tag is normally used
    /// between [CloudInformationService] and Datacenter entities.
    /// When such a [SimEvent] is sent, the [SimEvent#getData()]
    /// must be a [Datacenter] object.
    public static final int DC_REGISTRATION_REQUEST = BASE + 2;

    /// Denotes a request from a [DatacenterBroker] to a [CloudInformationService] to get
    /// the list of all [Datacenter]s, including the ones that can support advanced reservation.
    public static final int DC_LIST_REQUEST = BASE + 4;

    /// Tag used by an entity to send ping requests.
    public static final int ICMP_PKT_SUBMIT = NET_BASE + 5;

    /// Tag used to return the ping request back to the sender.
    public static final int ICMP_PKT_RETURN = NET_BASE + 6;

    /// Denotes a request to register a [CloudInformationService] entity as a regional CIS.
    /// When such a [SimEvent] is sent, the [SimEvent#getData()]
    /// must be a [CloudInformationService] object.
    public static final int REGISTER_REGIONAL_CIS = BASE + 12;

    /// Denotes a request to get a list of other regional [CloudInformationService] (CIS) entities from the
    /// system CIS entity.
    public static final int REQUEST_REGIONAL_CIS = BASE + 13;

    /// Denotes a [DatacenterBroker] request to schedule creation of waiting [Cloudlet]s.
    public static final int CLOUDLET_CREATION = BASE + 14;

    /// Denotes the return of a finished [Cloudlet] back to the sender.
    /// This tag is normally used by [Datacenter] entity.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_RETURN = BASE + 15;

    /// Denotes the submission of a [Cloudlet]. This tag is normally used between
    /// a [DatacenterBroker] and [Datacenter] entity.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_SUBMIT = BASE + 16;

    /// Denotes a message indicating the submission of a [Cloudlet], requiring an acknowledgement.
    /// This tag is normally used between [DatacenterBroker] and [Datacenter] entity.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_SUBMIT_ACK = BASE + 17;

    /// Cancels a [Cloudlet] submitted in the [Datacenter] entity.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_CANCEL = BASE + 18;

    /// Pauses a [Cloudlet] submitted in the [Datacenter] entity.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_PAUSE = BASE + 19;

    /// Denotes a message to request the pause a [Cloudlet] submitted in the [Datacenter] entity,
    /// requiring an acknowledgement.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_PAUSE_ACK = BASE + 20;

    /// Resumes a [Cloudlet] submitted in the [Datacenter] entity.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_RESUME = BASE + 21;

    /// Denotes a message to request resuming a [Cloudlet] submitted in the Datacenter entity,
    /// requiring an acknowledgement.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_RESUME_ACK = BASE + 22;

    /// Request a [Cloudlet] to be set as ready to start executing inside a VM.
    /// This event is sent by a DatacenterBroker to itself to define the time when
    /// a specific Cloudlet should start executing.
    /// This tag is commonly used when Cloudlets are created from a trace file
    /// such as a [Google Cluster Trace][GoogleTaskEventsTraceReader].
    ///
    /// When the status of a Cloudlet is set to [Cloudlet.Status#READY],
    /// the Cloudlet can be selected to start running as soon as possible
    /// by a [CloudletScheduler].
    ///
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_READY = BASE + 23;

    /// Request a [Cloudlet] to be set as failed.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_FAIL = BASE + 24;

    /// Requests an indefinite-length [Cloudlet] (negative value) to be finished by
    /// setting its length as the current number of processed MI.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    ///
    /// Events with a negative tag have higher priority.
    /// In this case, if a message with this tag is sent,
    /// it means that the Cloudlet has to be finished by replacing
    /// its negative length with an actual positive value.
    /// Only after that, the processing of Cloudlets can be updated.
    /// That way this event must be processed before other events.
    public static final int CLOUDLET_FINISH = -(BASE + 25);

    /// Requests a [Cloudlet] to be canceled.
    /// The Cloudlet can be canceled under user request or because
    /// another Cloudlet on which this one was dependent died.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Cloudlet] object.
    public static final int CLOUDLET_KILL = BASE + 26;

    /// Request a [Cloudlet] to have its attributes changed.
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Runnable] that represents a no-argument and no-return function
    /// that will perform the Cloudlet attribute update.
    /// The Runnable most encapsulates everything needed to update
    /// the Cloudlet's attributes, including the Cloudlet itself.
    ///
    /// Since the logic to update the attributes of a Cloudlet
    /// can be totally customized according to the researcher needs,
    /// there is no standard way to perform such an operation.
    /// As an example, you may want to reduce by half
    /// the number of CPU cores required by a Cloudlet from a list at a given time.
    /// This way, the Runnable function may be defined as a Lambda Expression as follows.
    /// Realize the `cloudletList` is considered to be accessible anywhere in the surrounding scope.
    ///
    /// ```
    /// Runnable runnable = () -> cloudletList.forEach(cloudlet -> cloudlet.setPesNumber(cloudlet.getPesNumber()/2));
    /// ```
    ///
    /// The `runnable` variable must be set as the data for the event to be sent with this tag.
    public static final int CLOUDLET_UPDATE_ATTRIBUTES = BASE + 27;

    /// Denotes a request to retry creating waiting [Vm]s from a [DatacenterBroker].
    public static final int VM_CREATE_RETRY = BASE + 31;

    /// Denotes a request to create a new [Vm] or List of Vms in a [Datacenter],
    /// where the [SimEvent#getData()] of the reply event is either a [Vm]
    /// or **Vm List** object (depending on how the broker submits VMs to the Datacenter).
    ///
    /// Using this tag, the Datacenter acknowledges the reception of the request.
    /// To check if the VM was in fact created inside the requested Datacenter,
    /// you just need to call [Vm#isCreated()].
    public static final int VM_CREATE_ACK = BASE + 32;

    /// Denotes a request to destroy a [Vm] in a [Datacenter].
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [Vm] object.
    public static final int VM_DESTROY = BASE + 33;

    /// Denotes a request to finish the migration of a new [Vm] in a [Datacenter].
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a `Map.Entry<VmAbstract, Host>` representing the [Host]
    /// a VM must be migrated to.
    ///
    /// If [Host#NULL] is given, the Datacenter will try to find
    /// a suitable Host when the migration request message is processed.
    public static final int VM_MIGRATE = BASE + 35;

    /// Denotes a request to finish the migration of a new [Vm] in a [Datacenter],
    /// requiring an acknowledgement.
    /// @see #VM_MIGRATE
    public static final int VM_MIGRATE_ACK = BASE + 36;

    /// Denotes an internal event generated in a [Datacenter]
    /// to notify itself to update the processing of [Cloudlet]s.
    ///
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// can be a [Host] object to indicate that just the Cloudlets
    /// running in VMs inside such a Host must be updated.
    /// The Host is an optional parameter which, if omitted,
    /// means that all Hosts from the Datacenter will have
    /// its cloudlets updated.
    public static final int VM_UPDATE_CLOUDLET_PROCESSING = BASE + 41;

    /// Denotes a request for vertical scaling VM resources
    /// (such as Ram, Bandwidth or Processor).
    /// When an event of this type is sent, the [SimEvent#getData()]
    /// must be a [VerticalVmScaling] object.
    public static final int VM_VERTICAL_SCALING = BASE + 42;

    /// Denotes the transmission of packets up through the network topology.
    public static final int NETWORK_EVENT_UP = BASE + 43;

    public static final int NETWORK_EVENT_SEND = BASE + 44;

    /// Denotes the transmission of packets down through the network topology.
    public static final int NETWORK_EVENT_DOWN = BASE + 46;

    /// Denotes the transmission of packets targeting a given [Host].
    /// The [SimEvent#getData()] must be a [HostPacket]
    /// to be processed.
    public static final int NETWORK_EVENT_HOST = BASE + 47;

    /// Denotes failure events such as [#HOST_FAILURE] or VM failures.
    public static final int FAILURE = BASE + 48;

    /// Denotes a request to generate a [Host] failure.
    public static final int HOST_FAILURE = FAILURE + 1;

    /// Denotes a request to a [Datacenter] to add a [Host] or list of Hosts.
    /// The [SimEvent#getData()] must be a Host to be added
    /// to the Datacenter where the message is being sent to.
    /// The source of such events is the [CloudInformationService].
    public static final int HOST_ADD = BASE + 60;

    /// Denotes a request to a [Datacenter] to remove a [Host] or list of Hosts.
    /// The [SimEvent#getData()] must be the ID of the Host that will be removed
    /// from the Datacenter they belong to.
    ///
    /// For this event, it uses the ID instead of the Host itself because the Host instance
    /// with the specified ID should be looked into the Datacenter Host list to remove it.
    /// A Host should be removed in case of maintenance or failure,
    /// but there isn't such a distinction yet, so a failure is simulated to remove the Host.
    /// The source of such events is the [CloudInformationService].
    public static final int HOST_REMOVE = BASE + 61;

    /// Denotes a power measurement performed periodically by a [PowerMeter] on
    /// entities having a [PowerModel], such as [Datacenter]s and [Host]s.
    public static final int POWER_MEASUREMENT = BASE + 70;

    /// Denotes a tag for starting up a [Host] inside a [Datacenter].
    /// When such a [SimEvent] is sent, the [SimEvent#getData()]
    /// must be a [Host] object.
    public static final int HOST_POWER_ON = BASE + 71;

    /// Denotes a tag for shutting down a [Host] inside a [Datacenter].
    /// When such a [SimEvent] is sent, the [SimEvent#getData()]
    /// must be a [Host] object.
    public static final int HOST_POWER_OFF = BASE + 72;

    /// {@return true if this tag is between a given range of tags, according to their values; false otherwise}
    /// @param startInclusive the tag starting the range to check
    /// @param endInclusive the tag finishing the range to check
    public static boolean between(final int tag, final int startInclusive, final int endInclusive){
        return tag >= startInclusive && tag <= endInclusive;
    }

    /// A private constructor to avoid class instantiation.
    private CloudSimTag() {/**/}
}
