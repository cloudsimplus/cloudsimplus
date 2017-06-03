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
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

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
     * Denotes cloud resource allocation policy. This tag is normally used
     * between CloudSim and Datacenter entity.
     */
    public static final int RESOURCE_DYNAMICS = BASE + 7;

    /**
     * Denotes a request to get the total number of Processing Elements (PEs) of
     * a resource. This tag is normally used between CloudSim and Datacenter
     * entity.
     */
    public static final int RESOURCE_NUM_PE = BASE + 8;

    /**
     * Denotes a request to get the total number of free Processing Elements
     * (PEs) of a resource. This tag is normally used between CloudSim and
     * Datacenter entity.
     */
    public static final int RESOURCE_NUM_FREE_PE = BASE + 9;

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
    public static final int CLOUDLET_RETURN = BASE + 20;

    /**
     * Denotes the submission of a Cloudlet. This tag is normally used between
     * CloudSim User and Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_SUBMIT = BASE + 21;

    /**
     * Denotes the submission of a Cloudlet with an acknowledgement. This tag is
     * normally used between CloudSim User and Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     *
     */
    public static final int CLOUDLET_SUBMIT_ACK = BASE + 22;

    /**
     * Cancels a Cloudlet submitted in the Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_CANCEL = BASE + 23;

    /**
     * Pauses a Cloudlet submitted in the Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_PAUSE = BASE + 25;

    /**
     * Pauses a Cloudlet submitted in the Datacenter entity with an
     * acknowledgement.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_PAUSE_ACK = BASE + 26;

    /**
     * Resumes a Cloudlet submitted in the Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_RESUME = BASE + 27;

    /**
     * Resumes a Cloudlet submitted in the Datacenter entity with an
     * acknowledgement.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link Cloudlet} object.
     */
    public static final int CLOUDLET_RESUME_ACK = BASE + 28;

    /**
     * Moves a Cloudlet to another Datacenter entity.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be an Object array containing data about the migration,
     * where the index 0 will be a Cloudlet and
     * the index 1 will be the id of the destination VM.
     */
    public static final int CLOUDLET_MOVE = BASE + 29;

    /**
     * Moves a Cloudlet to another Datacenter entity with an acknowledgement.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be an Object array containing data about the migration,
     * where the index 0 will be a Cloudlet and
     * the index 1 will be the id of the destination VM.
     */
    public static final int CLOUDLET_MOVE_ACK = BASE + 30;

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
     * Denotes an event to send a file from a user to a
     * {@link Datacenter}.
     */
    public static final int VM_DATA_ADD = BASE + 37;

    /**
     * Denotes an event to send a file from a user to a {@link Datacenter}
     * with acknowledgement information sent by the Datacener.
     */
    public static final int VM_DATA_ADD_ACK = BASE + 38;

    /**
     * Denotes an event to remove a file from a {@link Datacenter} .
     */
    public static final int VM_DATA_DEL = BASE + 39;

    /**
     * Denotes an event to remove a file from a {@link Datacenter} with
     * acknowledgement information sent by the Datacener.
     */
    public static final int VM_DATA_DEL_ACK = BASE + 40;

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
    public static final int VM_UPDATE_CLOUDLET_PROCESSING_EVENT = BASE + 41;

    /**
     * Defines the tag to be used to request vertical scaling of VM resources
     * such as Ram, Bandwidth or Pe.
     * When an event of this type is sent, the {@link SimEvent#getData()}
     * must be a {@link VerticalVmScaling} object.
     */
    public static final int VM_VERTICAL_SCALING = BASE + 42;

    public static final int NETWORK_EVENT_UP = BASE + 43;

    public static final int NETWORK_EVENT_SEND = BASE + 44;

    public static final int NETWORK_HOST_REGISTER = BASE + 45;

    public static final int NETWORK_EVENT_DOWN = BASE + 46;

    public static final int NETWORK_EVENT_HOST = BASE + 47;

    /**
     * Defines the base tag to be used for failure events such as
     * failure of hosts or VMs.
    */
    public static final int FAILURE = BASE + 47;

    /**
     * Defines the tag that represents a request to generate a host failure.
     */
    public static final int HOST_FAILURE = FAILURE + 1;

    /**
     * Private constructor to avoid instantiating such a class.
     */
    private CloudSimTags() {
        throw new UnsupportedOperationException("CloudSimTags cannot be instantiated");
    }

}
