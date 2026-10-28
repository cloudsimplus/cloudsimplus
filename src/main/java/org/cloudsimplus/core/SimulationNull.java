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
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.network.topologies.NetworkTopology;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

/// A class that implements the Null Object Design Pattern for [Simulation] class.
///
/// @author Manoel Campos da Silva Filho
/// @see Simulation#NULL
final class SimulationNull implements Simulation {
    @Override public boolean isThereAnyFutureEvt(Predicate<SimEvent> predicate) { return false; }
    @Override public boolean isTerminationTimeSet() { return false; }
    @Override public void abort() {/**/}
    @Override public void addEntity(CloudSimEntity entity) {/**/}
    @Override public SimEvent cancel(SimEntity src, Predicate<SimEvent> predicate) {
        return SimEvent.NULL;
    }
    @Override public boolean cancelAll(SimEntity src, Predicate<SimEvent> predicate) {
        return false;
    }
    @Override public double clock() { return 0.0; }
    @Override public String clockStr() { return ""; }
    @Override public double clockInMinutes() { return 0.0; }
    @Override public double clockInHours() { return 0.0; }
    @Override public SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public Calendar getCalendar() {
        return Calendar.getInstance();
    }
    @SuppressWarnings("NullAway") // there is no Null Object for CloudInformationService
    @Override public CloudInformationService getCis() {
        return null;
    }
    @Override public List<SimEntity> getEntityList() { return Collections.emptyList(); }
    @Override public double getMinTimeBetweenEvents() {
        return 0;
    }
    @Override public int getNumEntities() {
        return 0;
    }
    @Override public SimulationListeners listeners() { return SimulationListeners.NULL; }
    @Override public boolean isPaused() {
        return false;
    }
    @Override public void pauseEntity(SimEntity src, double delay) {/**/}
    @Override public boolean pause() {
        return false;
    }
    @Override public boolean pause(double time) {
        return false;
    }
    @Override public boolean resume() {
        return false;
    }
    @Override public boolean isRunning() {
        return false;
    }
    @Override public SimEvent select(SimEntity dest, Predicate<SimEvent> predicate) { return SimEvent.NULL; }
    @Override public void send(SimEvent evt) {/**/}
    @Override public void send(SimEntity src, SimEntity dest, double delay, int tag, @Nullable Object data) {/**/}
    @Override public void sendFirst(SimEvent evt) {/**/}
    @Override public void sendFirst(SimEntity src, SimEntity dest, double delay, int tag, @Nullable Object data) {/**/}
    @Override public void sendNow(SimEntity src, SimEntity dest, int tag, @Nullable Object data) {/**/}
    @Override public double runFor(double interval) { return 0; }
    @Override public double start() throws RuntimeException { return 0; }
    @Override public void startSync() { /**/ }
    @Override public boolean isTimeToTerminateSimulationUnderRequest() { return false; }
    @Override public boolean terminate() {
        return false;
    }
    @Override public boolean terminateAt(double time) {
        return false;
    }
    @Override public void wait(CloudSimEntity src, Predicate<SimEvent> predicate) {/**/}
    @Override public NetworkTopology getNetworkTopology() { return NetworkTopology.NULL; }
    @Override public Simulation setNetworkTopology(NetworkTopology networkTopology) { return this; }
    @Override public long getNumberOfFutureEvents(Predicate<SimEvent> predicate) { return 0; }
    @Override public double getLastCloudletProcessingUpdate() { return 0; }
    @Override public Simulation setLastCloudletProcessingUpdate(double lastCloudletProcessingUpdate) { return this; }
    @Override public boolean isAbortRequested() {return true;}
    @Override public boolean isAborted() {return true; }
}
