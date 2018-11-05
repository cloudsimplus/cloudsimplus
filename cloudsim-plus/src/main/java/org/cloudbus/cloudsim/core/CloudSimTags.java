/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;

/**
 * Contains various static command tags that indicate a type of action that
 * needs to be undertaken by CloudSim entities when they receive or send events.
 * <b>NOTE:</b> To avoid conflicts with other tags, CloudSim reserves numbers lower than 300 and the number 9600.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Anthony Sulistio
 * @since CloudSim Toolkit 1.0
 */
public final class CloudSimTags {

    /**
     * Denotes the end of simulation.
     * Despite it has a negative value, it isn't a priority event.
     */
    public static final int END_OF_SIMULATION = -1;

    /**
     * Starting constant value for cloud-related tags. *
     */
    private static final int BASE = 0;

    /**
     * Starting constant value for network-related tags. *
     */
    private static final int NETBASE = 100;

    /**
     * Denotes a request from a Datacenter to register itself. This tag is normally used
     * between {@link CloudInformationService} and Datacenter entities.
     * When such a {@link SimEvent} is sent, the {@link SimEvent#getData()}
     * must be a {@link Datacenter} object.
     */
    public static final int DATACENTER_REGISTRATION_REQUEST = BASE + 2;

    /**
     * Denotes a request from a broker to a {@link CloudInformationService} to get the list of all Datacenters,
     * including the ones that can support advanced reservation.
     */
    public static final int DATACENTER_LIST_REQUEST = BASE + 4;

    /**
     * Denotes a request to register a {@link CloudInformationService} entity as a regional CIS.
     * When such a {@link SimEvent} is sent, the {@link SimEvent#getData()}
     * must be a {@link CloudInformationService} object.
     */
    public static final int REGISTER_REGIONAL_CIS = BASE + 13;

    /**
     * Denotes a request to get a list of other regional CIS entities from the
     * system CIS entity.
     */
    public static final int REQUEST_REGIONAL_CIS = BASE + 14;

    /**
     * This tag is used by an entity to send ping requests.
     */
    public static final int ICMP_PKT_SUBMIT = NETBASE + 5;

    /**
     * This tag is used to return the ping request back to sender.
     */
    public static final int ICMP_PKT_RETURN = NETBASE + 6;

    /**
     * Denotes the return of a finished Cloudlet back to the sender.
     * This tag is normally used by Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_RETURN = BASE + 15;

    /**
     * Denotes the submission of a Cloudlet. This tag is normally used between
     * a DatacenterBroker and Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_SUBMIT = BASE + 16;

    /**
     * Denotes the submission of a Cloudlet with an acknowledgement. This tag is
     * normally used between DatacenterBroker and Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     *
     */
    public static final int CLOUDLET_SUBMIT_ACK = BASE + 17;

    /**
     * Cancels a Cloudlet submitted in the Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_CANCEL = BASE + 18;

    /**
     * Pauses a Cloudlet submitted in the Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_PAUSE = BASE + 19;

    /**
     * Pauses a Cloudlet submitted in the Datacenter entity with an
     * acknowledgement.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_PAUSE_ACK = BASE + 20;

    /**
     * Resumes a Cloudlet submitted in the Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_RESUME = BASE + 21;

    /**
     * Resumes a Cloudlet submitted in the Datacenter entity with an
     * acknowledgement.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_RESUME_ACK = BASE + 22;

    /**
     * Request a Cloudlet to be set as ready to start executing inside a VM.
     * This event is sent by a DatacenterBroker to itself to define the time when
     * a specific Cloudlet should start executing.
     * This tag is commonly used when Cloudlets are created
     * from a trace file such as a {@link GoogleTaskEventsTraceReader Google Cluster Trace}.
     *
     * <p>When the status of a Cloudlet is set to {@link Cloudlet.Status#READY},
     * the Cloudlet can be selected to start running as soon as possible
     * by a {@link CloudletScheduler}.</p>
     *
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_READY = BASE + 23;

    /**
     * Request a Cloudlet to be set as failed.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_FAIL = BASE + 24;

    /**
     * Requests an indefinite-length Cloudlet (negative value) to be finished by
     * setting its length as the current number of processed MI.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     *
     * <p>Events with a negative tag have higher priority.
     * In this case, if a message with this tag is sent,
     * it means that the Cloudlet has to be finished by replacing
     * its negative length with an actual positive value.
     * Only after that, the processing of Cloudlets can be updated.
     * That is way this event must be processed before other events.
     * </p>
     */
    public static final int CLOUDLET_FINISH = -(BASE + 25);

    /**
     * Requests a Cloudlet to be cancelled.
     * The Cloudlet can be cancelled under user request or because
     * another Cloudlet on which this one was dependent died.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_KILL = BASE + 26;

    /**
     * Request a Cloudlet to have its attributes changed.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Runnable} that represents a no-argument and no-return function
     * that will perform the Cloudlet attribute update.
     * The Runnable most encapsulate everything needed to update
     * the Cloudlet's attributes, including the Cloudlet
     * which will be updated.
     *
     * <p>Since the logic to update the attributes of a Cloudlet
     * can be totally customized according to the researcher needs,
     * there is no standard way to perform such an operation.
     * As an example, you may want to reduce by half
     * the number of PEs required by a Cloudlet from a list at a given time.
     * This way, the Runnable function may be defined as a Lambda Expression as follows.
     * Realize the {@code cloudletList} is considered to be accessible anywhere in the surrounding scope.
     * </p>
     *
     * <pre>
     * {@code Runnable runnable = () -> cloudletList.forEach(cloudlet -> cloudlet.setNumberOfPes(cloudlet.getNumberOfPes()/2));}
     * </pre>
     *
     * <p>The {@code runnable} variable must be set as the data for the event to be sent with this tag.</p>
     */
    public static final int CLOUDLET_UPDATE_ATTRIBUTES = BASE + 27;

    /**
     * Denotes a request to create a new VM in a {@link Datacenter}
     * without requiring and acknowledgement to be sent back to the sender.
     */
    public static final int VM_CREATE = BASE + 31;

    /**
     * Denotes a request to create a new VM in a {@link Datacenter} with
     * acknowledgement information sent by the Datacenter,
     * where the {@link SimEvent#getData()} of the reply event
     * is a {@link Vm} object.
     * To check if the VM was in fact created inside the requested Datacenter
     * one has only to call {@link Vm#isCreated()}.
     */
    public static final int VM_CREATE_ACK = BASE + 32;

    /**
     * Denotes a request to destroy a VM in a {@link Datacenter}.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Vm} object.
     */
    public static final int VM_DESTROY = BASE + 33;

    /**
     * Denotes a request to destroy a new VM in a {@link Datacenter} with
     * acknowledgement information sent by the Datacener.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Vm} object.
     */
    public static final int VM_DESTROY_ACK = BASE + 34;

    /**
     * Denotes a request to migrate a new VM in a {@link Datacenter}.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@code Map.Entry<Vm, Host>} representing to which Host
     * a VM must be migrated.
     */
    public static final int VM_MIGRATE = BASE + 35;

    /**
     * Denotes a request to migrate a new VM in a {@link Datacenter} with
     * acknowledgement information sent by the Datacenter.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@code Map.Entry<Vm, Host>} representing to which Host
     * a VM must be migrated.
     */
    public static final int VM_MIGRATE_ACK = BASE + 36;

    /**
     * Denotes an internal event generated in a {@link Datacenter}
     * to notify itself to update the processing of cloudlets.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * can be a {@link Host} object to indicate that just the Cloudlets
     * running in VMs inside such a Host must be updated.
     * The Host is an optional parameter which if omitted,
     * means that all Hosts from the Datacenter will have
     * its cloudlets updated.
     */
    public static final int VM_UPDATE_CLOUDLET_PROCESSING = BASE + 41;

    /**
     * Defines the tag to be used to request vertical scaling of VM resources
     * such as Ram, Bandwidth or Pe.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link VerticalVmScaling} object.
     */
    public static final int VM_VERTICAL_SCALING = BASE + 42;

    /**
     * Defines the tag to be used to send packets
     * up through the network topology.
     */
    public static final int NETWORK_EVENT_UP = BASE + 43;

    public static final int NETWORK_EVENT_SEND = BASE + 44;

    public static final int NETWORK_HOST_REGISTER = BASE + 45;

    /**
     * Defines the tag to be used to send packets
     * down through the network topology.
     */
    public static final int NETWORK_EVENT_DOWN = BASE + 46;

    public static final int NETWORK_EVENT_HOST = BASE + 47;

    /**
     * Defines the base tag to be used for failure events such as
     * failure of hosts or VMs.
    */
    public static final int FAILURE = BASE + 48;

    /**
     * Defines the tag that represents a request to generate a host failure.
     */
    public static final int HOST_FAILURE = FAILURE + 1;

    /**
     * Defines the tag that represents a request to a Datacenter to add a Host or list of Hosts to a Datacenter.
     * The {@link SimEvent#getData()} must be a Host to be added to
     * to the Datacenter where the message is being sent to.
     * The source of such events is the {@link CloudInformationService}.
     */
    public static final int HOST_ADD = BASE + 60;

    /**
     * Defines the tag that represents a request to a Datacenter to remove a Host or list of Hosts from a Datacenter.
     * The {@link SimEvent#getData()} must be the ID of the Host that will be removed
     * from the Datacenter they belong to.
     * For this event, it's used the ID instead of the Host itself because the Host instance
     * with the specified ID should be looked into the Datacenter Host list in order to remove it.
     * A Host should be removed in case of maintenance or failure but there isn't such a distinction yet,
     * so a failure is simulated to remove the Host.
     * The source of such events is the {@link CloudInformationService}.
     */
    public static final int HOST_REMOVE = BASE + 61;

    /**
     * Private constructor to avoid instantiating such a class.
     */
    private CloudSimTags() {
        throw new UnsupportedOperationException("CloudSimTags cannot be instantiated");
    }
}
