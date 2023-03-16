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

import org.cloudsimplus.core.events.SimEvent;

/**
 * A base interface used internally to implement the Null Object Design Pattern
 * for interfaces extending {@link SimEntity}.
 * It's just used to avoid the boilerplate code in such Null Object implementations.
 *
 * @author Manoel Campos da Silva Filho
 * @see SimEntity#NULL
 */
public interface SimEntityNullBase extends SimEntity {
    @Override default State getState() { return State.FINISHED; }
    @Override default SimEntity setState(State state) { return this; }
    @Override default boolean isStarted() { return false; }
    @Override default boolean isAlive() { return false; }
    @Override default boolean isFinished() { return false; }
    @Override default Simulation getSimulation() { return Simulation.NULL; }
    @Override default void processEvent(SimEvent evt) {/**/}
    @Override default boolean schedule(SimEvent evt) { return false; }
    @Override default boolean schedule(SimEntity dest, double delay, int tag, Object data) { return false; }
    @Override default boolean schedule(double delay, int tag, Object data) { return false; }
    @Override default boolean schedule(SimEntity dest, double delay, int tag) { return false; }
    @Override default boolean schedule(int tag, Object data) { return false; }
    @Override default boolean schedule(double delay, int tag) { return false; }
    @Override default void run() {/**/}
    @Override default boolean start() { return false; }
    @Override default void shutdown() {/**/}
    @Override default SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    @Override default String getName() {  return ""; }
    @Override default long getId() { return -1; }
    @Override default double getShutdownTime(){ return -1; }
    @Override default double getStartTime(){ return -1; }
}
