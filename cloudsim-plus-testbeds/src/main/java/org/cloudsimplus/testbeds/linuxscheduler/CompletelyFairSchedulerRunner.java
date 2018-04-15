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

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import static org.cloudsimplus.testbeds.linuxscheduler.CloudletSchedulerExperiment.MAX_CLOUDLET_PES;


/**
 * Runs the {@link CompletelyFairSchedulerExperiment} a defined number of times
 * and computes statistics.
 *
 * @author Manoel Campos da Silva Filho
 */
class CompletelyFairSchedulerRunner extends CloudletSchedulerRunner<CompletelyFairSchedulerExperiment> {
    /**
     * Starts the execution of the experiments
     * the number of times defines in {@link #numberOfSimulationRuns}.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new CompletelyFairSchedulerRunner().run();
    }

    @Override
    protected CompletelyFairSchedulerExperiment createExperiment(int i) {
        final ContinuousDistribution cloudletPesPrng = createRandomGen(i, 1, MAX_CLOUDLET_PES);
        final CompletelyFairSchedulerExperiment exp = new CompletelyFairSchedulerExperiment(i, this);

        exp
            .setCloudletPesPrng(cloudletPesPrng)
            .setNumCloudletsToCreate((int) getCloudletsNumberPrng().sample())
            .setAfterExperimentFinish(this::afterExperimentFinish)
            .setVerbose(false);

        return exp;
    }

}
