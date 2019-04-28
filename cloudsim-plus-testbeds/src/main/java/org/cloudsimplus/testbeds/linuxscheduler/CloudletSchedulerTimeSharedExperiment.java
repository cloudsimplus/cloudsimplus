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
package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.testbeds.ExperimentRunner;

/**
 * An experiment runs Cloudlets using a {@link CloudletSchedulerTimeShared} to get results
 * to compare to the {@link CloudletSchedulerCompletelyFair} scheduler experiment
 * implemented in {@link CloudletSchedulerExperiment}.
 *
 * <p>Check the super class {@link CloudletSchedulerExperiment}</p> to see the general
 * experiment configuration and goals.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletSchedulerExperiment
 */
final class CloudletSchedulerTimeSharedExperiment extends CloudletSchedulerExperiment {
    /**
     * Creates a simulation experiment.
     *
     * @param index  the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge
     *               of executing this experiment a defined number of times and to collect
     */
    CloudletSchedulerTimeSharedExperiment(int index, CloudletSchedulerTimeSharedRunner runner) {
        super(index, runner);
    }

    @Override
    protected Vm createVm(final DatacenterBroker broker, final int id) {
        final Vm vm = super.createVm(broker, id);
        return vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
    }
}
