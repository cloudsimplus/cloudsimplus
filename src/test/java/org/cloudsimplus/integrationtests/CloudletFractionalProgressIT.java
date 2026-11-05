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
package org.cloudsimplus.integrationtests;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks that a Cloudlet executing less than 1 MI per processing update
/// (due to a small scheduling interval or a small allocated MIPS)
/// still makes progress and finishes.
///
/// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/598">Issue #598</a>
final class CloudletFractionalProgressIT {
    private static final double MIPS = 5;
    private static final int CLOUDLET_LENGTH = 10;
    private static final double TERMINATION_TIME = 25;
    private static final double TIME_DELTA = 0.001;

    @ParameterizedTest(name = "interval={0}s, cpu={1}")
    @CsvSource({
        "0.125, 1.0, 2.1",
        "1.0,   1.0, 2.2",
        "1.0,   0.1, 20.1"
    })
    void cloudletFinishesWhenEachUpdateExecutesLessThanOneMI(
        final double schedulingInterval, final double cpuUtilization, final double expectedFinishTime)
    {
        final Cloudlet cloudlet = runSimulation(schedulingInterval, cpuUtilization);
        assertTrue(cloudlet.isFinished());
        assertEquals(CLOUDLET_LENGTH, cloudlet.getFinishedLengthSoFar());
        assertEquals(expectedFinishTime, cloudlet.getFinishTime(), TIME_DELTA);
    }

    private static Cloudlet runSimulation(final double schedulingInterval, final double cpuUtilization) {
        final var simulation = new CloudSimPlus();
        simulation.terminateAt(TERMINATION_TIME);

        final var host = HostSimple.of(2048, 10000, 100000, new CoreCapacity(MIPS));
        host.setVmScheduler(new VmSchedulerTimeShared());
        DatacenterSimple.of(simulation, List.of(host)).setSchedulingInterval(schedulingInterval);

        final var vm = VmSimple.of(new CoreCapacity(MIPS), new CloudletSchedulerTimeShared());
        vm.setRam(512).setBw(1000).setSize(1000);

        final var cloudlet = CloudletSimple.of(CLOUDLET_LENGTH, 1);
        cloudlet.setUtilizationModelCpu(new UtilizationModelDynamic(cpuUtilization));
        cloudlet.setFileSize(1).setOutputSize(1);

        final var broker = new DatacenterBrokerSimple(simulation);
        broker.submitVm(vm);
        broker.submitCloudlet(cloudlet);
        simulation.start();
        return cloudlet;
    }
}
