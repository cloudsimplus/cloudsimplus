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

import java.util.function.Predicate;

/// A class that implements the Null Object Design Pattern for [SimulationEvents].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see SimulationEvents#NULL
final class SimulationEventsNull implements SimulationEvents {
    @Override public SimEvent cancel(SimEntity src, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public boolean cancelAll(SimEntity src, Predicate<SimEvent> predicate) { return false; }
    @Override public SimEvent select(SimEntity dest, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public void wait(CloudSimEntity src, Predicate<SimEvent> predicate) {/**/}
    @Override public void pauseEntity(SimEntity src, double delay) {/**/}
    @Override public long getNumberOfFutureEvents(Predicate<SimEvent> predicate) { return 0; }
    @Override public boolean isThereAnyFutureEvt(Predicate<SimEvent> predicate) { return false; }
}
