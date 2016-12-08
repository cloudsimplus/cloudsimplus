/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;

/**
 * CloudimShutdown waits for termination of all CloudSim user entities to determine the end of
 * simulation. This class will be created when a CloudSim instance is created.
 * Hence, do not need to worry about creating an object of
 * this class. This object signals the end of simulation to CloudInformationService (CIS) entity.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class CloudCloudSimShutdown extends CloudSimEntity {
    /**
     * Instantiates a new CloudCloudSimShutdown object.
     * <p/>
     * The total number of cloud user entities plays an important role to determine whether all
     * hostList' should be shut down or not. If one or more users are still not finished, then the
     * hostList's will not be shut down. Therefore, it is important to give a correct number of total
     * cloud user entities. Otherwise, CloudSim program will hang or encounter a weird behaviour.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @post $none
     */
    public CloudCloudSimShutdown(CloudSim simulation) {
        super(simulation);
    }

    /**
     * The main method that shuts down hostList's and Cloud Information Service (CIS). In addition,
     * this method writes down a report at the end of a simulation based on
     * <tt>reportWriterName</tt> defined in the Constructor. <br/>
     * <b>NOTE:</b> This method shuts down cloud hostList's and CIS entities either <tt>AFTER</tt> all
     * cloud users have been shut down or an entity requires an abrupt end of the whole simulation.
     * In the first case, the number of cloud users given in the Constructor <tt>must</tt> be
     * correct. Otherwise, CloudSim package hangs forever or it does not terminate properly.
     *
     * @param ev the ev
     * @pre $none
     * @post $none
     */
    @Override
    public void processEvent(SimEvent ev) {
        if (getSimulation().decrementNumberOfUsers() <= 0 || ev.getTag() == CloudSimTags.ABRUPT_END_OF_SIMULATION) {
            getSimulation().abruptallyTerminate();
        }
    }

    /**
     * The method has no effect at the current class.
     */
    @Override
    public void startEntity() {}

    /**
     * The method has no effect at the current class.
     */
    @Override
    public void shutdownEntity() {}
}
