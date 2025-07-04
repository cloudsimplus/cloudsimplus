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

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;

/**
 * Represents an object that is owned by a {@link DatacenterBroker},
 * namely {@link Vm} and {@link Cloudlet}.
 * @author raysaoliveira
 */
public sealed interface CustomerEntity
    extends UniquelyIdentifiable, ChangeableId, SubmissionDelayable, Lifetimed, ExecDelayable
    permits Cloudlet, Vm, CustomerEntityAbstract
{

    /**
     * @return the last time the entity was finished (in seconds);
     * or {@link #NOT_ASSIGNED} means if it has not stopped or has not even started yet.
     */
    double getFinishTime();

    /**
     * @return the {@link DatacenterBroker} that represents the owner of this object;
     *         or <b>{@link DatacenterBroker#NULL}</b> if a broker has not been set yet
     */
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this object.
     *
     * @param broker the {@link DatacenterBroker} to set
     */
    CustomerEntity setBroker(DatacenterBroker broker);

    /**
     * Sets the last Datacenter where the entity was tried to be created.
     * @param lastTriedDatacenter the datacenter to set
     */
    CustomerEntity setLastTriedDatacenter(Datacenter lastTriedDatacenter);

    /** @return the last Datacenter where the entity was tried to be created. */
    Datacenter getLastTriedDatacenter();

    /**
     * @return the last time (in seconds) the entity arrived at the broker, before being
     * submitted to a Datacenter; or {@link #NOT_ASSIGNED} if it has not arrived yet.
     * @see #getSubmissionDelay()
     */
    double getBrokerArrivalTime();

    /**
     * Sets the last time the entity arrived at the broker, before being
     * submitted to a Datacenter.
     *
     * @param time the absolute time to set
     */
    CustomerEntity setBrokerArrivalTime(double time);

    /**
     * @return the last time (in seconds) the entity was created into a Datacenter,
     *         or {@link #NOT_ASSIGNED} if it has not been created yet.
     * @see #getSubmissionDelay()
     */
    double getCreationTime();

    /**
     * Gets the total time the entity had to wait before being created,
     * either due to a given {@link #getSubmissionDelay() submission delay}
     * or because there was no suitable Host available after the VM submission.
     *
     * @return the total wait time (in seconds) or {@link #NOT_ASSIGNED} if the entity was not created yet.
     * @see #getSubmissionDelay()
     */
    double getCreationWaitTime();
}
