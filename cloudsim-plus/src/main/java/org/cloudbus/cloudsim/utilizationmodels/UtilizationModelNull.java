package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.core.Simulation;

/**
 * A class that implements the Null Object Design Pattern for {@link UtilizationModel}
 * class.
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
    @Override public UtilizationModel setSimulation(Simulation simulation) {
        return this;
    }
    @Override public double getUtilization(double time) {
        return 0;
    }
    @Override public double getUtilization() {
        return 0;
    }
}
