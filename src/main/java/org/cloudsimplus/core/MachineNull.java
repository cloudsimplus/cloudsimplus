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
package org.cloudsimplus.core;

import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Machine} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @see Machine#NULL
 * @since CloudSim 1.2.0
 */
final class MachineNull implements Machine {
    @Override public Resource getBw() { return Resource.NULL; }
    @Override public Resource getRam() { return Resource.NULL; }
    @Override public Resource getStorage() { return Resource.NULL; }
    @Override public long getPesNumber() { return 0; }
    @Override public double getMips() { return 0; }
    @Override public Simulation getSimulation() { return Simulation.NULL; }
    @Override public double getTotalExecutionTime() { return 0; }
    @Override public double getStartupDelay() { return 0; }
    @Override public boolean isShuttingDown() { return false; }
    @Override public double getShutdownBeginTime() { return -1; }
    @Override public void shutdown() {/**/}
    @Override public ExecDelayable setShutdownBeginTime(double shutdownBeginTime) { return this; }
    @Override public ExecDelayable setStartupDelay(double delay) { return this; }
    @Override public double getShutDownDelay() { return 0; }
    @Override public ExecDelayable setShutDownDelay(double delay) { return this; }
    @Override public double getStartTime() { return -1; }
    @Override public boolean isFinished() { return true; }
    @Override public Machine setStartTime(double startTime) { return this; }
    @Override public double getFinishTime() { return -1; }
    @Override public Startable setFinishTime(double stopTime) { return this; }
    @Override public double getLastBusyTime() { return 0; }
    @Override public Startable setLastBusyTime(double time) { return this; }
    @Override public boolean isIdle() { return true; }
    @Override public Machine setId(long id) { return this; }
    @Override public long getId() { return 0; }
    @Override public double getTotalMipsCapacity() { return 0.0; }
    @Override public List<ResourceManageable> getResources() { return Collections.emptyList(); }
}
