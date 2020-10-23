package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.models.PowerModel;

/**
 * Interface for power-aware entities such as {@link Host}s, {@link Datacenter}s
 * and other entities that can be introduced.
 *
 * @since CloudSim Plus 6.0.0
 */
public interface PowerAware<T extends PowerModel> {
    /**
     * Gets the model defining how the entity consumes power.
     * @return
     */
    T getPowerModel();

    /**
     * Sets the model defining how the entity consumes power.
     * @param powerModel the model to set
     */
    void setPowerModel(T powerModel);
}
