/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A Cloud Information Service (CIS) is an entity that provides cloud resource
 * registration, indexing and discovery services. The Cloud datacenters tell their
 * readiness to process Cloudlets by registering themselves with this entity.
 * Other entities such as the broker can contact this class for
 * resource discovery service, which returns a list of registered resource.
 *
 * <p>
 * In summary, it acts like a yellow page service.
 * An instance of this class is automatically created by CloudSim upon initialisation of the simulation.
 * </p>
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class CloudInformationService extends CloudSimEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudInformationService.class.getSimpleName());

    /**
     * A list containing all Datacenters that are registered at the
     * Cloud Information Service (CIS).
     */
    private final List<Datacenter> datacenterList;

    /**
     * List of all regional CIS.
     */
    private final Set<CloudInformationService> cisList;

    /**
     * Instantiates a new CloudInformationService object.
     *
     * @param simulation CloudSim instance that represents the simulation the Entity belongs to
     */
    CloudInformationService(final CloudSim simulation) {
        super(simulation);
        datacenterList = new ArrayList<>();
        cisList = new TreeSet<>();
    }

    /**
     * The method has no effect at the current class.
     */
    @Override
    protected void startInternal() {/**/}

    @Override
    public void processEvent(final SimEvent evt) {
        switch (evt.getTag()) {
            case REGISTER_REGIONAL_CIS -> cisList.add((CloudInformationService) evt.getData());
            case REQUEST_REGIONAL_CIS -> super.send(evt.getSource(), 0, evt.getTag(), cisList);
            case DC_REGISTRATION_REQUEST -> datacenterList.add((Datacenter) evt.getData());
            // A Broker is requesting a list of all datacenters.
            case DC_LIST_REQUEST -> super.send(evt.getSource(), 0, evt.getTag(), datacenterList);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        LOGGER.info("{}: Notify all CloudSim Plus entities to shutdown.{}", super.getName(), System.lineSeparator());

        signalShutdown(datacenterList);
        signalShutdown(cisList);

        // reset the values
        datacenterList.clear();
        cisList.clear();
    }

    /**
     * Gets the list of all registered Datacenters.
     *
     * @return
     */
    public List<Datacenter> getDatacenterList() {
        return datacenterList;
    }

    /**
     * Sends a {@link CloudSimTag#SIMULATION_END} signal to all entity IDs
     * mentioned in the given list.
     *
     * @param list List of entities to notify about simulation end
     */
    private void signalShutdown(final Collection<? extends SimEntity> list) {
        if (list == null) {
            return;
        }

        list.forEach(entity -> super.send(entity, 0L, CloudSimTag.SIMULATION_END));
    }

}
