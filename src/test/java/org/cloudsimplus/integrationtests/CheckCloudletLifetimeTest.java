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

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Sun Lingyu
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.2.1
 */
public final class CheckCloudletLifetimeTest {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 1;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = 1;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100_000;
    private static final int SCHEDULING_INTERVAL = 10; //seconds
    private static final int CLOUDLET_LIFE_TIME = 50; //seconds

    private DatacenterBroker broker;
    private CloudSimPlus simulation;

    @BeforeEach
    public void setUp() {
        this.simulation = new CloudSimPlus();
        final var scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder()
                .setSchedulingInterval(SCHEDULING_INTERVAL)
                .create(
                    new HostBuilder()
                        .setVmSchedulerSupplier(VmSchedulerSpaceShared::new)
                        .setPes(HOST_PES).setMips(HOST_MIPS)
                        .create()
                        .getHosts()
                );


        final var brokerBuilder = scenario.getBrokerBuilder().create();
        this.broker = brokerBuilder.getBroker();
        brokerBuilder.getVmBuilder()
                     .setPes(VM_PES).setMips(VM_MIPS)
                     .setCloudletSchedulerSupplier(CloudletSchedulerTimeShared::new)
                     .createAndSubmit();

        brokerBuilder.getCloudletBuilder()
                     .setLength(CLOUDLET_LENGTH)
                     .setPes(CLOUDLET_PES)
                     .setLifeTime(CLOUDLET_LIFE_TIME)
                     .createAndSubmit(1);
    }

    @Test
    public void integrationTest() {
        simulation.start();
        final var cloudlets = broker.getCloudletFinishedList();
        /* The expected finish time considers the delay to transfer the Cloudlet
         * required files and the actual execution time.
         */
        final double expectedFinishTime = CLOUDLET_LIFE_TIME;
        final long expectedFinishedLength = 50_000; //HOST MIPS * LIFETIME
        //new CloudletsTableBuilder(broker.getCloudletFinishedList()).setTitle(broker.getName()).build();

        final double errorMargin = 11;
        for (final var c : cloudlets) {
            //Checks if each cloudlet finished at the expected time.
            assertEquals(expectedFinishTime, c.getFinishTime(), errorMargin/100, c + " expected finish time");
            assertEquals(expectedFinishedLength, c.getFinishedLengthSoFar(), errorMargin*10, c + " expected finished length");
        }
    }

}
