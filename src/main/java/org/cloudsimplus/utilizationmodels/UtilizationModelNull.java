/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.Simulation;

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
    @Override public UtilizationModel setSimulation(final Simulation simulation) {return this;}
    @Override public double getUtilization(final double time) { return 0; }
    @Override public double getUtilization() {
        return 0;
    }
    @Override public boolean isOverCapacityRequestAllowed() { return false; }
    @Override public UtilizationModel setOverCapacityRequestAllowed(boolean allow) {return this;}
}
