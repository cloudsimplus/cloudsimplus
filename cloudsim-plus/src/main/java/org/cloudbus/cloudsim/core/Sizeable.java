package org.cloudbus.cloudsim.core;

/**
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.2
 */
public interface Sizeable {
    /**
     * Gets the size of the entity, represented
     * by the number of internal other entities/elements it holds.
     * @return
     */
    long size();
}
