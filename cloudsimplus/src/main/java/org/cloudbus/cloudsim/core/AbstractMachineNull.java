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
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link AbstractMachine}
 * objects.
 *
 * @author Manoel Campos da Silva Filho
 * @see AbstractMachine#NULL
 * @since CloudSim 1.2.0
 */
final class AbstractMachineNull implements AbstractMachine {
    @Override public Resource getBw() {
        return Resource.NULL;
    }
    @Override public Resource getRam() {
        return Resource.NULL;
    }
    @Override public Resource getStorage() {
        return Resource.NULL;
    }
    @Override public long getNumberOfPes() {
        return 0;
    }
    @Override public double getMips() {
        return 0;
    }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public double getStartTime() { return 0; }
    @Override public AbstractMachine setStartTime(double startTime) { return this; }
    @Override public double getLastBusyTime() { return 0; }
    @Override public boolean isIdle() { return true; }
    @Override public void setId(long id) {/**/}
    @Override public long getId() {
        return 0;
    }
    @Override public double getTotalMipsCapacity() { return 0.0; }
    @Override public List<ResourceManageable> getResources() { return Collections.emptyList(); }
}
