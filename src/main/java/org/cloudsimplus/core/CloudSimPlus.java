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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;

import java.util.List;

/// Starts and manages cloud computing simulations running in the CloudSim Plus framework.
/// @author Manoel Campos da Silva Filho
public class CloudSimPlus extends CloudSim {

    /// Default value for [#getMinTimeBetweenEvents()] (in seconds)
    private static final double DEF_MIN_TIME_BETWEEN_EVENTS = 0.1;

    private final CircularTimeQueue clockQueue;

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private List<EventListener<SimEvent>> onEventProcessingListeners = List.of();

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private List<EventListener<EventInfo>> onSimulationPauseListeners = List.of();

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private List<EventListener<EventInfo>> onClockTickListeners = List.of();

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
    private List<EventListener<EventInfo>> onSimulationStartListeners = List.of();

    /// Creates a CloudSim Plus simulation.
    /// Internally, it creates a [CloudInformationService].
    ///
    /// @see CloudInformationService
    /// @see #CloudSimPlus(double)
    public CloudSimPlus() {
        this(DEF_MIN_TIME_BETWEEN_EVENTS);
    }

    /// Creates a CloudSim Plus simulation that tracks events happening in a time interval
    /// as little as the minTimeBetweenEvents parameter.
    /// Internally it creates a [CloudInformationService].
    ///
    /// @param minTimeBetweenEvents the minimal period between events.
    /// Events within shorter periods after the last event are discarded.
    /// @see CloudInformationService
    public CloudSimPlus(final double minTimeBetweenEvents) {
        super(minTimeBetweenEvents);
        this.clockQueue = new CircularTimeQueue(this);
    }

    @Override
    public double runFor(final double interval) {
        final double until = interval == Double.MAX_VALUE ? interval : clock() + interval;

        if(!processEvents(until)){
            /* If no event happening up to the given time, increases the clock
             * so that when the runFor method is called again with the new clock,
             * some events may be processed that time.
             * If some event is processed, the clock is automatically increased.*/
            setClock(until);

            if(noFutureEvents()){
                finish();
            }
        }

        return clock();
    }

    @Override
    protected void processEvent(SimEvent evt) {
        super.processEvent(evt);
        SimulationListeners.notifyOnEventProcessing(this, evt);
    }

    @Override
    protected boolean doPause() {
        final boolean wasPaused = super.doPause();
        if (wasPaused) {
            SimulationListeners.notifyOnSimulationPause(this);
        }

        return wasPaused;
    }

    /// {@inheritDoc}
    /// @see #onClockTickListeners
    @Override
    protected double setClock(double newTime) {
        final double oldTime = super.setClock(newTime);
        clockQueue.tryToUpdateListeners(clock -> SimulationListeners.notifyOnClockTick(this, clock));
        return oldTime;
    }

    @Override
    protected void notifyOnSimulationStartListeners() {
        SimulationListeners.notifyOnSimulationStart(this);
    }

    @Override
    public SimulationListeners listeners() {
        return new SimulationListeners(this);
    }
}
