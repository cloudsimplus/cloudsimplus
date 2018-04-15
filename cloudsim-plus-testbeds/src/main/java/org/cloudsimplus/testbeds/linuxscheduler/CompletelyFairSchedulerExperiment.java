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
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.testbeds.ExperimentRunner;

/**
 * An experiment that shows how the oversimplied time-shared CloudletScheduler implementation provided by
 * the {@link CloudletSchedulerTimeShared} CloudSim class increases task completion time
 * of all Cloudlets and ignores Cloudlets priorities.
 *
 * <p>It also shows how a more realistic scheduler such as the {@link CloudletSchedulerCompletelyFair}
 * provided by CloudSim Plus is concerned in Cloudlets priorities and gets overall reduction of
 * task completion time. This scheduler is an simplified implementation of the
 * <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a> used by Linux Kernel.</p>
 *
 * <p>Check the super class {@link CloudletSchedulerExperiment}</p> to see the general
 * experiment configuration and goals.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CompletelyFairSchedulerExperiment extends CloudletSchedulerExperiment {

    /**
     * Creates a simulation experiment.
     *
     * @param index  the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge
     *               of executing this experiment a defined number of times and to collect
     */
    CompletelyFairSchedulerExperiment(int index, CompletelyFairSchedulerRunner runner) {
        super(index, runner);
    }

    @Override
    protected Vm createVm(DatacenterBroker broker) {
        return new VmSimple(VM_MIPS, VM_PES)
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_STORAGE)
                .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
    }

}
