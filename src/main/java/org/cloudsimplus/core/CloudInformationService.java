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
import org.cloudsimplus.hosts.HostCapacityUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.cloudsimplus.resources.Processor.workingCores;

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
            forwardHostRequest(evt, (long) evt.getData());
        } else if (evt.getTag() == CloudSimTag.HOST_UPDATE) {
            forwardHostRequest(evt, ((HostCapacityUpdate) evt.getData()).hostId());
        }
    }

    /// Forwards a Host request to the first Datacenter that has a working Host with the given ID.
    /// A removed Host has all its cores failed and may be added again later, possibly to another Datacenter.
    /// The check uses working cores because a removal doesn't change [Host#isFailed()],
    /// and it also excludes [Host#NULL], which has no cores.
    /// @param evt the request to forward
    /// @param hostId the ID of the Host the request is about
    private void forwardHostRequest(final SimEvent evt, final long hostId) {
        datacenterList.stream()
            .filter(dc -> workingCores(dc.getHostById(hostId)) > 0)
            .findFirst()
            .ifPresentOrElse(
                //The Datacenter sends the event to itself, so that no network delay changes the request time.
                dc -> dc.schedule(evt.getTag(), evt.getData()),
                () -> LOGGER.warn("{}: {}: Working Host {} was not found in any Datacenter to process a request.", getSimulation().clockStr(), this, hostId));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        datacenterList.clear();
    }
}
