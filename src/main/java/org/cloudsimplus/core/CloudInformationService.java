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

    /// The method has no effect on the current class.
    @Override
    public void processEvent(final SimEvent evt) {/**/}

    @Override
    public void shutdown() {
        super.shutdown();
        datacenterList.clear();
    }
}
