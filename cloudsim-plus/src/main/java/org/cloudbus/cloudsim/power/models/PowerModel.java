package org.cloudbus.cloudsim.power.models;

/**
 * Common interface for PowerModels.
 *
 * A PowerModel computes the current power usage of an entity during the simulation.
 */
public interface PowerModel {

    /**
     * Computes abd returns the current power usage of the entity.
     */
    PowerMeasurement measure();

}

