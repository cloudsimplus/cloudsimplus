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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelPlanetLab;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * An Integration Test (IT) running a simulation scenario with 1 PM, 1 VM
 * and 1 cloudlet with a list of required files.
 * The test checks if the end of cloudlet execution was
 * correctly delayed by the time to transfer the file list
 * to the VM.
 *
 * <p>It is created a Storage Area Network (SAN) for the Datacenter and
 * a list of {@link File Files} is stored on it.
 * The name of these files are then added to the list
 * of required files of the created Cloudlet.
 * Thus, the time to transfer these files from the SAN
 * to the Vm has to be added to cloudlet finish time.</p>
 *
 * @author Sun Lingyu
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.2.1
 */
public final class CheckCloudletLifetimeTest {
    private static final int HOST_MIPS = 100;
    private static final int HOST_PES = 1;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = 1;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100 * 900;
    private static final int SCHEDULING_INTERVAL = 300;

    private DatacenterBroker broker;
    private List<File> files;
    private SanStorage storage;
    private CloudSim simulation;

    @BeforeEach
    public void setUp() {

        this.simulation = new CloudSim();
        final SimulationScenarioBuilder scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder()
                .setSchedulingInterval(SCHEDULING_INTERVAL)
                .create(
                    new HostBuilder()
                        .setVmSchedulerSupplier(VmSchedulerSpaceShared::new)
                        .setPes(HOST_PES).setMips(HOST_MIPS)
                        .create()
                        .getHosts()
                );


        final BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().create();
        this.broker = brokerBuilder.getBroker();
        brokerBuilder.getVmBuilder()
                     .setPes(VM_PES).setMips(VM_MIPS)
                     .setCloudletSchedulerSupplier(CloudletSchedulerTimeShared::new)
                     .createAndSubmit();

        double[] utilization = {1, 0.5};
        brokerBuilder.getCloudletBuilder()
                     .setLength(CLOUDLET_LENGTH)
                     .setUtilizationModelCpu(new UtilizationModelPlanetLab(utilization, SCHEDULING_INTERVAL, UnaryOperator.identity()))
                     .setPEs(CLOUDLET_PES)
                     .setLifeTime(400)
                     .createAndSubmit(1);
    }

    @Test
    public void integrationTest() {
        simulation.start();
        final List<Cloudlet> cloudlets = broker.getCloudletFinishedList();
        /* The expected finish time considers the delay to transfer the Cloudlet
         * required files and the actual execution time.
         */
        final double expectedFinishTime = 400;
        final long expectedFinishedLength = 22500; // (300*0.5+100*0.75) * HOST_MIPS
        new CloudletsTableBuilder(broker.getCloudletFinishedList()).setTitle(broker.getName()).build();

        for (final Cloudlet c : cloudlets) {
            //Checks if each cloudlet finished at the expected time.
            assertEquals(expectedFinishTime, c.getFinishTime(), 0.13, c + " expected finish time");
            assertEquals(expectedFinishedLength, c.getFinishedLengthSoFar(), c + " expected finished length");
        }
    }

}
