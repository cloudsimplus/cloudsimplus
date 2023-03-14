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
package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

/**
 * A class that implements the Null Object Design Pattern for {@link SimEvent}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see SimEvent#NULL
 */
final class SimEventNull implements SimEvent {
    @Override public void setSimulation(Simulation simulation) { /**/ }
    @Override public Type getType() { return Type.NULL; }
    @Override public SimEntity getDestination() { return SimEntity.NULL; }
    @Override public SimEntity getSource() {
        return SimEntity.NULL;
    }
    @Override public double getEndWaitingTime() { return 0; }
    @Override public CloudSimTag getTag() {
        return CloudSimTag.NONE;
    }
    @Override public int getPriority() { return 0; }
    @Override public Object getData() {
        return 0;
    }
    @Override public void setSource(SimEntity source) {/**/}
    @Override public void setDestination(SimEntity destination) {/**/}
    @Override public double getTime() {
        return 0;
    }
    @Override public EventListener<EventInfo> getListener() { return EventListener.NULL; }
    @Override public int compareTo(SimEvent evt) {
        return 0;
    }
    @Override public long getSerial() {
        return 0;
    }
    @Override public void setSerial(long serial) {/**/}
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
}
