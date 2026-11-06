/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// A Cloud Information Service (CIS) is an entity that keeps the list of [Datacenter]s
/// in the simulation, which other entities (such as brokers) use to discover them.
/// Each Datacenter is added to the list when it is created,
/// so the list is available even before the simulation starts.
///
/// It's also the source of events sent by the simulation itself.
/// An instance of this class is automatically created by [CloudSimPlus] upon initialization of the simulation.
///
/// @author Manzur Murshed
/// @author Rajkumar Buyya
/// @since CloudSim Toolkit 1.0
public class CloudInformationService extends CloudSimEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudInformationService.class.getSimpleName());

    /// The Datacenters created in the simulation.
    private final List<Datacenter> datacenterList;

    /// Instantiates a new CloudInformationService object.
    ///
    /// @param simulation [CloudSimPlus] instance that represents the simulation the Entity belongs to
    CloudInformationService(final CloudSim simulation) {
        super(simulation);
        datacenterList = new ArrayList<>();
    }

    /// {@return a read-only list of the Datacenters created in the simulation}
    public List<Datacenter> getDatacenterList() {
        return Collections.unmodifiableList(datacenterList);
    }

    /// Adds a Datacenter to the list. Called by the simulation when the Datacenter is created.
    /// @param datacenter the Datacenter to add
    void addDatacenter(final Datacenter datacenter) {
        datacenterList.add(datacenter);
    }

    /// The method has no effect on the current class.
    @Override
    protected void startInternal() {/**/}

    @Override
    public void processEvent(final SimEvent evt) {
        if (evt.getTag() == CloudSimTag.HOST_REMOVE) {
            forwardHostRemovalRequest((long) evt.getData());
        }
    }

    /// Forwards a Host removal request to the first Datacenter that has the Host.
    /// @param hostId the ID of the Host to remove
    private void forwardHostRemovalRequest(final long hostId) {
        datacenterList.stream()
            .filter(dc -> !Host.NULL.equals(dc.getHostById(hostId)))
            .findFirst()
            .ifPresentOrElse(
                //The Datacenter sends the event to itself, so that no network delay changes the removal time.
                dc -> dc.schedule(CloudSimTag.HOST_REMOVE, hostId),
                () -> LOGGER.warn("{}: {}: Host {} was not found in any Datacenter to be removed.", getSimulation().clockStr(), this, hostId));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        datacenterList.clear();
    }
}
