/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Log;

/**
 * A Cloud Information Service (CIS) is an entity that provides cloud resource
 * registration, indexing and discovery services. The Cloud hostList tell their
 * readiness to process Cloudlets by registering themselves with this entity.
 * Other entities such as the resource broker can contact this class for
 * resource discovery service, which returns a list of registered resource IDs.
 * In summary, it acts like a yellow page service. This class will be created by
 * CloudSim upon initialisation of the simulation. Hence, do not need to worry
 * about creating an object of this class.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class CloudInformationService extends SimEntity {

    /**
     * A list containing the id of all Datacenters that are registered at the
     * Cloud Information Service (CIS).
     *
     */
    private final List<Integer> datacenterIdsList;

    /**
     * A list containing only the id of entities with Advanced Reservation
     * feature that are registered at the CIS.
     */
    private final List<Integer> datacenterIdsArList;

    /**
     * List of all regional CIS.
     */
    private final List<Integer> cisList;

    /**
     * Instantiates a new CloudInformationService object.
     *
     * @param name the name to be associated with this entity (as required by
     * {@link SimEntity} class)
     * @throws IllegalArgumentException when creating this entity before
     * initialising CloudSim package or this entity name is <tt>null</tt> or
     * empty
     * @pre name != null
     * @post $none
     *
     */
    public CloudInformationService(String name) throws IllegalArgumentException {
        super(name);
        datacenterIdsList = new LinkedList<>();
        datacenterIdsArList = new LinkedList<>();
        cisList = new LinkedList<>();
    }

    /**
     * The method has no effect at the current class.
     */
    @Override
    public void startEntity() {
    }

    @Override
    public void processEvent(SimEvent ev) {
        int id;  // requester id
        switch (ev.getTag()) {
            // storing regional CIS id
            case CloudSimTags.REGISTER_REGIONAL_GIS:
                cisList.add((Integer) ev.getData());
                break;

            // request for all regional CIS list
            case CloudSimTags.REQUEST_REGIONAL_GIS:

                // Get ID of an entity that send this event
                id = ((Integer) ev.getData());

                // Send the regional GIS list back to sender
                super.send(id, 0L, ev.getTag(), cisList);
                break;

            // A datacenter is requesting to register.
            case CloudSimTags.DATACENTER_REGISTRATION_REQUEST:
                datacenterIdsList.add((Integer) ev.getData());
                break;

            // A resource that can support Advanced Reservation
            case CloudSimTags.DATACENTER_REGISTRATION_REQUEST_AR:
                datacenterIdsList.add((Integer) ev.getData());
                datacenterIdsArList.add((Integer) ev.getData());
                break;

            // A Broker is requesting for a list of all datacenters.
            case CloudSimTags.DATACENTER_LIST:

                // Get ID of an entity that send this event
                id = ((Integer) ev.getData());

                // Send the resource list back to the sender
                super.send(id, 0L, ev.getTag(), datacenterIdsList);
                break;

            // A Broker is requesting for a list of all datacenters that support advanced reservation.
            case CloudSimTags.DATACENTER_AR_LIST:

                // Get ID of an entity that send this event
                id = ((Integer) ev.getData());

                // Send the resource AR list back to the sender
                super.send(id, 0L, ev.getTag(), datacenterIdsArList);
                break;

            default:
                processOtherEvent(ev);
                break;
        }
    }

    @Override
    public void shutdownEntity() {
        notifyAllEntity();
    }

    /**
     * Gets the list of all Datacenter IDs.
     *
     * @return list containing Datacenter IDs
     * @pre $none
     * @post $none
     */
    public List<Integer> getDatacenterIdsList() {
        return datacenterIdsList;
    }

    /**
     * Gets the list of Datacenter IDs that <b>only</b> support Advanced
     * Reservation.
     *
     * @return list containing Datacenter IDs. Each ID is represented by an
     * Integer object.
     * @pre $none
     * @post $none
     */
    public List<Integer> getDatacenterIdsArList() {
        return datacenterIdsArList;
    }

    /**
     * Checks whether a given datacenter supports Advanced Reservation or not.
     *
     * @param datacenterId a datacenter ID
     * @return <tt>true</tt> if the datacenter supports Advanced Reservation,
     * <tt>false</tt> otherwise
     * @pre id != null
     * @post $none
     */
    public boolean datacenterSupportAR(Integer datacenterId) {
        if (datacenterId == null || datacenterId < 0) {
            return false;
        }

        return datacenterExists(datacenterIdsArList, datacenterId);
    }

    /**
     * Checks whether the given Datacenter ID exists or not.
     *
     * @param id a Datacenter id
     * @return <tt>true</tt> if the given ID exists, <tt>false</tt> otherwise
     * @pre id != null
     * @post $none
     */
    public boolean datacenterExists(Integer id) {
        if (id == null || id < 0) {
            return false;
        }
        
        return datacenterExists(datacenterIdsList, id);
    }
    
    /**
     * Checks whether a datacenter list contains a particular datacenter id.
     *
     * @param list list of datacenter id
     * @param datacenterId a datacenter ID to find
     * @return true if a datacenter is in the list, otherwise false
     * @pre list != null
     * @pre id > 0
     * @post $none
     */
    private boolean datacenterExists(Collection<Integer> list, Integer datacenterId) {
        if (list == null || datacenterId == null || datacenterId < 0) {
            return false;
        }

        return list.contains(datacenterId);
    }    

    /**
     * Process non-default received events that aren't processed by the
     * {@link #processEvent(org.cloudbus.cloudsim.core.SimEvent)} method. This
     * method should be overridden by subclasses in other to process new defined
     * events.
     *
     * @param ev a SimEvent object
     * @pre ev != null
     * @post $none
     */
    protected void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            Log.printConcatLine("CloudInformationService.processOtherEvent(): ",
                    "Unable to handle a request since the event is null.");
            return;
        }

        Log.printLine("CloudInformationSevice.processOtherEvent(): " + "Unable to handle a request from "
                + CloudSim.getEntityName(ev.getSource()) + " with event tag = " + ev.getTag());
    }

    /**
     * Notifies the registered entities about the end of simulation. This method
     * should be overridden by child classes.
     */
    protected void processEndSimulation() {
        // this should be overridden by the child class
    }

    /**
     * Tells all registered entities about the end of simulation.
     *
     * @pre $none
     * @post $none
     */
    private void notifyAllEntity() {
        Log.printConcatLine(super.getName(), ": Notify all CloudSim entities for shutting down.");

        signalShutdown(datacenterIdsList);
        signalShutdown(cisList);

        // reset the values
        datacenterIdsList.clear();
        cisList.clear();
    }

    /**
     * Sends a {@link CloudSimTags#END_OF_SIMULATION} signal to all entity IDs
     * mentioned in the given list.
     *
     * @param list List storing entity IDs
     * @pre list != null
     * @post $none
     */
    protected void signalShutdown(Collection<Integer> list) {
        // checks whether a list is empty or not
        if (list == null) {
            return;
        }

        // Send END_OF_SIMULATION event to all entities in the list
        list.forEach(id -> super.send(id, 0L, CloudSimTags.END_OF_SIMULATION));
    }

}
