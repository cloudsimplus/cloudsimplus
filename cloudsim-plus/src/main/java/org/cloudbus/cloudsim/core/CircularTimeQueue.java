/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

/**
 * A static-sized queue that removes the oldest time value when it's full.
 * It keeps track of the last two simulation events time to check whether they happened at different times,
 * meaning the simulation clock has changed
 * and some events should be fired.
 *
 * @since CloudSim Plus 6.0.0
 * @author Manoel Campos da Silva Filho
 */
class CircularTimeQueue {
    /**
     * The simulation clock time queue.
     * It's an array that works as a circular queue with capacity for just 2 elements
     * (defined in the constructor). When a new element is added to the queue,
     * the first element is removed to open space for that new one.
     * This queue stores the last 2 simulation clock values.
     * It is used to know when it's time to notify listeners that
     * the simulation clock has increased.
     *
     * <p>The head (value at index 0) of the queue is the oldest simulation time stored,
     * the tail (value at index 1) is the newest one.</p>
     *
     * <p>Such a structure is required because multiple events
     * can be received consecutively for the same simulation time.
     * When the head of the queue is lower than the tail,
     * it means the last event for that head time
     * was already processed and a more recent event
     * has just arrived.
     * </p>
     *
     */
    private final double[] queue;

    /**
     * Creates the time queue.
     * @param currentTime the current simulation time to store in the queue.
     */
    public CircularTimeQueue(final double currentTime){
        this.queue = new double[]{currentTime, currentTime};
    }

    /**
     * Adds a new time value to the queue, making it rotate, i.e.:
     * removing the first time value, then adding a new one.
     */
    public void addTime(final double time) {
        queue[0] = queue[1];
        queue[1] = time;
    }

    public double previous(){
        return queue[0];
    }

    /**
     * Checks if the previous time value is older then the newest one.
     * @return
     */
    public boolean isPreviousTimeOlder(){
        return queue[0] < queue[1];
    }

}
