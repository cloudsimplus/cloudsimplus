package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;

/**
 * An interface for objects that have an Unique Identifier (UID)
 * that is compounded by a {@link DatacenterBroker} ID
 * and the object ID.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface UniquelyIdentifiable extends Identifiable {
    /**
     * Generates an Unique Identifier (UID).
     *
     * @param brokerId the id of the {@link DatacenterBroker} (user)
     * @param id the object id
     * @return the generated UID
     */
    static String getUid(long brokerId, long id) {
        return brokerId + "-" + id;
    }

    /**
     * Gets the Unique Identifier (UID) for the VM, that is compounded by the id
     * of a {@link DatacenterBroker} (representing the User)
     * and the object id.
     *
     * @return
     */
    String getUid();
}
