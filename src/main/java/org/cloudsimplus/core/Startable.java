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

/**
 * An entity that can be started and stopped.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 8.2.0
 */
public interface Startable {
    /**
     * Indicates that some attribute was not set and therefore its value should be ignored.
     */
    int NOT_ASSIGNED = -1;

    /**
     * @return the last time the machine was started up (in seconds);
     *         or {@link #NOT_ASSIGNED} if it was not started yet.
     */
    double getStartTime();

    /**
     * @return true if the entity has started and it's not finished yet; false otherwise.
     */
    default boolean hasStarted(){
        return !isFinished() && getStartTime() > NOT_ASSIGNED;
    }

    /**
     * @return true if this entity has finished execution; false otherwise
     */
    boolean isFinished();

    /**
     * Sets the current entity startup time.
     * The value {@link #NOT_ASSIGNED} means it was not started yet.
     *
     * @param startTime the start time to set (in seconds)
     * @return this entity
     */
    Startable setStartTime(double startTime);

    /**
     * @return the time the entity was stopped (in seconds);
     *         or {@link #NOT_ASSIGNED} if it has not stopped or has not even started yet.
     */
    double getFinishTime();

    /**
     * Sets the time the entity was stopped (in seconds).
     * The value {@link #NOT_ASSIGNED} means the entity has not stopped or has not even started yet.
     *
     * @param stopTime the stop time to set (in seconds)
     * @return this entity
     */
    Startable setFinishTime(double stopTime);

    /**
     * Gets the last time the entity was running some process or {@link #NOT_ASSIGNED} if it has not been busy yet.
     * @return the last busy time (in seconds)
     */
    double getLastBusyTime();

    /**
     * Set the last time the entity was running some process.
     * @param time the time to set ({@link #NOT_ASSIGNED} to indicate the entity has not been busy yet)
     * @return this entity
     */
    Startable setLastBusyTime(double time);

    /**
     * @return the {@link CloudSimPlus} instance that represents the simulation this Entity belongs to.
     */
    Simulation getSimulation();

    /**
     * @return the total execution time of the entity so far (in seconds),
     *         if the entity has finished already or not.
     */
    double getTotalExecutionTime();
}
