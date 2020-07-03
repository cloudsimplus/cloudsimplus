package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Simulation;

/**
 * A class that implements the Null Object Design Pattern for {@link UtilizationModel}
 * class. A {@link Cloudlet} using such a utilization model for one of its resources
 * will not consume any amount of that resource ever.
 *
 * @author Manoel Campos da Silva Filho
 * @see UtilizationModel#NULL
 */
final class UtilizationModelNull implements UtilizationModel {
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public Unit getUnit() {
        return Unit.PERCENTAGE;
    }
    @Override public UtilizationModel setSimulation(final Simulation simulation) {
        return this;
    }
    @Override public double getUtilization(final double time) { return 0; }
    @Override public double getUtilization() {
        return 0;
    }
    @Override public boolean isOverCapacityRequestAllowed() { return false; }
    @Override public UtilizationModel setOverCapacityRequestAllowed(boolean allow) { return this; }
}
