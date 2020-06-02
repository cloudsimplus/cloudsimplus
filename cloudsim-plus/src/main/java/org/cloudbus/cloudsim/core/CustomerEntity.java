package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Represents an object that is owned by a {@link DatacenterBroker},
 * namely {@link Vm} and {@link Cloudlet}.
 * @author raysaoliveira
 */
public interface CustomerEntity extends UniquelyIdentifiable, ChangeableId, Delayable {
    /**
     * Gets the {@link DatacenterBroker} that represents the owner of this object.
     *
     * @return the broker or <b>{@link DatacenterBroker#NULL}</b> if a broker has not been set yet
     */
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this object.
     *
     * @param broker the {@link DatacenterBroker} to set
     */
    void setBroker(DatacenterBroker broker);

    /**
     * Gets the CloudSim instance that represents the simulation the Entity is related to.
     *
     * @return
     */
    Simulation getSimulation();

    /**
     * Sets the last Datacenter where VM was tried to be created.
     * @param lastTriedDatacenter
     */
    void setLastTriedDatacenter(Datacenter lastTriedDatacenter);

    /** Gets the last Datacenter where VM was tried to be created. */
    Datacenter getLastTriedDatacenter();
}
