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
 * Defines methods for an object that its execution can be delayed by some time
 * when it is submitted to a {@link Datacenter} by a {@link DatacenterBroker}.
 *
 * @see Vm
 * @see Cloudlet
 *
 * @author Manoel Campos da Silva Filho
 */
public interface SubmissionDelayable extends ExecDelayable {
    /**
     * Gets the time (in seconds) that a {@link DatacenterBroker} will wait
     * to submit the entity to a Datacenter, in order to request the creation of the object.
     * This is a relative time from the current simulation time.
     *
     * @return the submission delay (in seconds)
     */
    double getSubmissionDelay();

    /**
     * Sets a relative time (in seconds), from current simulation time, that a {@link DatacenterBroker} will wait
     * to submit the entity to a Datacenter, in order to request the creation of the object.
     * This is a relative time from the current simulation time.
     *
     * @param submissionDelay the number of seconds from the current simulation
     * time that the object will wait to be submitted
     */
    void setSubmissionDelay(double submissionDelay);

    /**
     * @return true if this object has a submission delay, false otherwise
     */
    boolean isSubmissionDelayed();
}
