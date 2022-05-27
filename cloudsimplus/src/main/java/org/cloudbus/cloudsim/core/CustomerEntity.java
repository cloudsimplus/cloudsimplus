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
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Represents an object that is owned by a {@link DatacenterBroker},
 * namely {@link Vm} and {@link Cloudlet}.
 * @author raysaoliveira
 */
public interface CustomerEntity extends UniquelyIdentifiable, ChangeableId, Delayable {
    /**
     * Gets the {@link DatacenterBroker} that represents the owner of this object.
     *
     * @return the broker or <b>{@link DatacenterBroker#NULL}</b> if a broker has not been set yet
     */
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this object.
     *
     * @param broker the {@link DatacenterBroker} to set
     */
    void setBroker(DatacenterBroker broker);

    /**
     * Gets the CloudSim instance that represents the simulation the Entity is related to.
     * @return
     */
    Simulation getSimulation();

    /**
     * Sets the last Datacenter where entity was tried to be created.
     * @param lastTriedDatacenter
     */
    void setLastTriedDatacenter(Datacenter lastTriedDatacenter);

    /** Gets the last Datacenter where entity was tried to be created. */
    Datacenter getLastTriedDatacenter();

    /**
     * Gets the absolute time the entity arrived at the broker, before being
     * submitted to a Datacenter.
     *
     * @return the arrived time (in seconds)
     * @see #getSubmissionDelay()
     */
    double getArrivedTime();

    /**
     * Sets the absolute time the entity arrived at the broker, before being
     * submitted to a Datacenter.
     *
     * @param time the time to set
     */
    CustomerEntity setArrivedTime(double time);

    /**
     * Gets the absolute time the entity was created into a Datacenter.
     *
     * @return the creation time (in seconds)
     * @see #getSubmissionDelay()
     */
    double getCreationTime();

    /**
     * Gets the total time the entity had to wait before being created,
     * either due to a given {@link #getSubmissionDelay() submission delay}
     * or because there was no suitable Host available after the VM submission.
     *
     * @return the total wait time (in seconds)
     * @see #getSubmissionDelay()
     */
    double getWaitTime();
}
