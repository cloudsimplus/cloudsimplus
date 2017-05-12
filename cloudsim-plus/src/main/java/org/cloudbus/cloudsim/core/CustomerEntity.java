package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * Represents an object that is owned by a {@link DatacenterBroker},
 * namely {@link Vm} and {@link Cloudlet}.
 * @author raysaoliveira
 */
public interface CustomerEntity extends ChangeableId, Delayable {
    /**
     * Gets the {@link DatacenterBroker} that represents the owner of this object.
     *
     * @return the broker or <tt>{@link DatacenterBroker#NULL}</tt> if a broker has not been set yet
     * @pre $none
     * @post $none
     */
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this object.
     *
     * @param broker the {@link DatacenterBroker} to set
     * @return
     */
    CustomerEntity setBroker(DatacenterBroker broker);
}
