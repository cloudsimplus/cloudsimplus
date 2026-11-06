/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.network.topologies.NetworkTopology;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

/// An interface to be implemented by a class that manages simulation
/// execution, controlling all the simulation life cycle.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
///
/// @see CloudSimPlus
/// @since CloudSim Plus 1.0
public sealed interface Simulation permits CloudSim, SimulationNull {
    /// A standard predicate that matches any event.
    Predicate<SimEvent> ANY_EVT = evt -> true;

    /// An attribute that implements the Null Object Design Pattern for [Simulation] objects.
    Simulation NULL = new SimulationNull();

    /// Defines IDs for a list of [ChangeableId] entities that don't
    /// have one already assigned. Such entities can be a [Cloudlet],
    /// [Vm] or any object that implements [ChangeableId].
    ///
    /// @param <T> the type of entities to define an ID
    /// @param list list of entities to define an ID
    /// @return the last entity that had an id set or null if the list is empty
    static <T extends ChangeableId> @Nullable T setIdForEntitiesWithoutOne(final List<? extends T> list){
        return list.isEmpty() ? null : setIdForEntitiesWithoutOne(list, list.getLast());
    }

    /// Defines IDs for a list of [ChangeableId] entities that don't
    /// have one already assigned. Such entities can be a [Cloudlet],
    /// [Vm] or any object that implements [ChangeableId].
    ///
    /// @param <T> the type of entities to define an ID
    /// @param list list of entities to define an ID
    /// @param lastEntity the last created Entity which its ID will be used
    /// as the base for the next IDs
    /// @return the last entity that had an id set
    static <T extends ChangeableId> T setIdForEntitiesWithoutOne(final List<? extends T> list, final T lastEntity){
        Objects.requireNonNull(list);
        if(list.isEmpty()){
            return lastEntity;
        }

        long id = lastEntity.getId();
        //if the ID is a negative number lower than -1, it's set as -1 to start the first ID as 0
        id = Math.max(id, -1);
        T entity = lastEntity;
        for (int i = 0; i < list.size(); i++) {
            entity = list.get(i);
            if (entity.getId() < 0) {
                entity.setId(++id);
            }

            if (entity instanceof VmGroup vmGroup) {
                entity = (T) setIdForEntitiesWithoutOne(vmGroup.getVmList(), vmGroup);
                id = vmGroup.getId();
            }
        }

        return entity;
    }

    boolean isTerminationTimeSet();

    /// Aborts the simulation without finishing the processing
    /// of entities in the [entities list][#getEntityList()],
    /// **which may give unexpected results**.
    ///
    /// **Use this method just if you want to abandon the simulation and usually ignore the results.**
    void abort();

    /// @return true if the simulation has been aborted, false otherwise
    boolean isAborted();

    /// Adds a new entity to the simulation. Each [CloudSimEntity] object
    /// register itself when it is instantiated.
    /// A [org.cloudsimplus.datacenters.Datacenter] is also added to the [CloudInformationService].
    ///
    /// @param entity the new entity to add
    void addEntity(CloudSimEntity entity);

    /// @return the current simulation time in seconds.
    /// @see #clockInMinutes()
    /// @see #clockInHours()
    /// @see #isRunning()
    double clock();

    /// @return the current simulation time in seconds as a formatted String.
    /// @see #clock()
    String clockStr();

    /// @return the current simulation time in minutes.
    /// @see #clockInHours()
    /// @see #isRunning()
    double clockInMinutes();

    /// @return the current simulation time in hours.
    /// @see #clockInMinutes()
    /// @see #isRunning()
    double clockInHours();

    /// @return the simulation Calendar
    Calendar getCalendar();

    /// @return the [CloudInformationService]
    CloudInformationService getCis();

    /// @return a **read-only** list of entities created for the simulation.
    List<SimEntity> getEntityList();

    /// @return the minimum time between events (in seconds).
    /// Events within shorter periods after the last event are discarded.
    double getMinTimeBetweenEvents();

    /// @return the current number of entities in the simulation.
    int getNumEntities();

    /// {@return an object to manage and notify the [EventListener]s of this Simulation}
    ///
    /// @see SimulationListeners
    SimulationListeners listeners();

    /// {@return the object that manages the events of this simulation}
    /// Entities send events through [SimEntity#schedule(SimEvent)] and the other `schedule*()` methods.
    /// This object enables finding, selecting and canceling events already sent, and
    /// making entities wait or pause.
    SimulationEvents events();

    /// @return true if the simulation is paused, false otherwise.
    boolean isPaused();

    /// Requests the simulation to be paused as soon as possible.
    ///
    /// @return true if the simulation was paused, false if it was already paused or has finished
    boolean pause();

    /// Requests the simulation to be paused at a given time.
    /// The method schedules the pause request and then returns immediately.
    ///
    /// @param time the time at which the simulation has to be paused
    /// @return true if a pause request was successfully received (the given time
    /// is greater than or equal to the current simulation time), false otherwise.
    boolean pause(double time);

    /// Resumes the simulation if it has previously been paused.
    ///
    /// @return true if the simulation has been restarted or false if it wasn't paused before.
    boolean resume();

    /// Check if the simulation is still running.
    /// Even if the simulation [is paused][#isPaused()],
    /// the method returns true to indicate that the simulation is in fact active already.
    ///
    /// This method should be used by entities to check if they should continue executing.
    /// It returns false both before the simulation starts and after it stops.
    ///
    /// @return true if the simulation is running, false otherwise
    boolean isRunning();

    /// Runs the simulation for a specific period of time and then immediately returns.
    /// You need to invoke this method multiple times to complete the whole simulation.
    ///
    /// **NOTE:** Should be used only in the **synchronous** mode
    /// (after starting the simulation with [#startSync()]).
    ///
    /// @param interval the interval for which the simulation should be run (in seconds)
    /// @return clock at the end of the simulation interval (in seconds)
    double runFor(double interval);

    /// Starts simulation execution and **waits for all entities to finish**,
    /// i.e., until all entities reach the non-RUNNABLE state or there are no more events in the future event queue.
    ///
    /// **NOTE**: This method should be called only after all entities
    /// have been set up and added. The method blocks until the simulation is ended.
    ///
    /// @return the last clock time (in seconds)
    /// @throws UnsupportedOperationException When the simulation has already run once.
    /// If you paused the simulation and wants to resume it,
    /// you must use [#resume()] instead of calling the current method.
    ///
    /// @see #startSync()
    double start();

    /// Starts simulation execution in synchronous mode, retuning immediately.
    /// You need to call [#runFor(double)] method subsequently to actually process simulation steps.
    ///
    /// **NOTE**: This method should be called only after all entities have been set up and added.
    /// The method returns immediately after preparing the internal state of the simulation.
    ///
    /// @throws UnsupportedOperationException When the simulation has already run once.
    /// If you paused the simulation and wants to resume it,
    /// you must use [#resume()] instead of calling the current method.
    ///
    /// @see #runFor(double)
    void startSync();

    boolean isTimeToTerminateSimulationUnderRequest();

    /// Forces the termination of the simulation before it ends.
    ///
    /// @return true if the simulation was running and the termination request was accepted,
    /// false if the simulation was not started yet
    boolean terminate();

    /// Schedules the termination of the simulation for a given time (in seconds).
    ///
    /// If a termination time is set, the simulation stays running even
    /// if there is no event to process.
    /// It keeps waiting for new dynamic events, such as the creation
    /// of Cloudlets and VMs at runtime.
    /// If no event happens, the clock is increased to simulate time passing.
    /// The clock increment is defined according to:
    ///
    /// - the lowest [Datacenter#getSchedulingInterval()] between existing Datacenters;
    /// - or [#getMinTimeBetweenEvents()] in case
    ///   no [Datacenter] has its schedulingInterval set.
    ///
    /// @param time the time at which the simulation has to be terminated (in seconds)
    /// @return true if the time given is greater than the current simulation time, false otherwise
    boolean terminateAt(double time);

    /// @return the [NetworkTopology] used for Network simulations.
    NetworkTopology getNetworkTopology();

    /// Sets the [NetworkTopology] used for Network simulations.
    ///
    /// @param networkTopology the network topology to set
    /// @return this simulation
    Simulation setNetworkTopology(NetworkTopology networkTopology);

    /// @return the last time (in seconds) some Cloudlet was processed in the simulation.
    double getLastCloudletProcessingUpdate();

    /// Sets the last time some Cloudlet was processed in the simulation.
    /// @param lastCloudletProcessingUpdate the time to set (in seconds)
    Simulation setLastCloudletProcessingUpdate(double lastCloudletProcessingUpdate);

    /// @return true if a request to abort the simulation was already sent, false otherwise.
    boolean isAbortRequested();
}
