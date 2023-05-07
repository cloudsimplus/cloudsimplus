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
     * Gets the last time the machine was started up (in seconds).
     * The value -1 means it was not started yet.
     *
     * @return
     */
    double getStartTime();

    /**
     * {@return true of false} to indicate whether the entity has started and it's not finished yet.
     */
    default boolean hasStarted(){
        return !isFinished() && getStartTime() > NOT_ASSIGNED;
    }

    /**
     * Checks whether this entity has finished executing or not.
     *
     * @return true if this entity has finished execution; false otherwise
     */
    boolean isFinished();

    /**
     * Sets the current machine startup time.
     * The value -1 means it was not started yet.
     *
     * @param startTime the start time to set (in seconds)
     * @return
     */
    Startable setStartTime(double startTime);

    /**
     * Gets the time the entity was stopped (in seconds).
     * The value -1 means it has not stopped or has not even started yet.
     *
     * @return
     */
    double getFinishTime();

    /**
     * Sets the time the entity was stopped (in seconds).
     * The value -1 means the VM has not stopped or has not even started yet.
     *
     * @param stopTime the stop time to set (in seconds)
     * @return
     */
    Startable setFinishTime(double stopTime);

    /**
     * Gets the last time the entity was running some process or -1 if it has not been busy yet.
     * @return the last busy time (in seconds)
     */
    double getLastBusyTime();

    /**
     * Set the last time the entity was running some process.
     * @param time the time to set (-1 to indicate the entity has not been busy yet)
     * @return
     */
    Startable setLastBusyTime(double time);

    /**
     * Gets the CloudSimPlus instance that represents the simulation the Entity is related to.
     * @return
     */
    Simulation getSimulation();

    /**
     * Gets the total execution time of the entity so far (in seconds),
     * if the entity has finished already or not.
     *
     * @return
     */
    double getTotalExecutionTime();

}
