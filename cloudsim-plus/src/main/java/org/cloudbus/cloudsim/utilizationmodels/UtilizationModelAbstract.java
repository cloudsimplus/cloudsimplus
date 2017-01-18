/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.core.Simulation;

import java.util.Objects;

/**
 * An abstract implementation of {@link UtilizationModel}.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2
 */
public abstract class UtilizationModelAbstract implements UtilizationModel {
    private Simulation simulation;

    public UtilizationModelAbstract(){
        this.setSimulation(Simulation.NULL);
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public final UtilizationModel setSimulation(Simulation simulation) {
        Objects.requireNonNull(simulation);
        this.simulation = simulation;
        return this;
    }

    @Override
    public double getUtilization() {
        return getUtilization(simulation.clock());
    }
}
