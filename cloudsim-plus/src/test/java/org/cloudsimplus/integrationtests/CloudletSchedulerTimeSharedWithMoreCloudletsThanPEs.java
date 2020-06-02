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
package org.cloudsimplus.integrationtests;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * An Integration Test (IT) running a simulation scenario with 1 PM of 2 PEs,
 * 1 VMs of 2 PEs and 4 cloudlet in that VM.
 * The VM uses a {@link CloudletSchedulerTimeShared}. As the number of Cloudlets
 * is the double of VM's PEs, all cloudlets will spend the double of the
 * time to finish, because they will concur for CPU.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CloudletSchedulerTimeSharedWithMoreCloudletsThanPEs {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 2;
    private static final int NUMBER_OF_VMS = 1;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = VM_MIPS*10;
    private static final int NUMBER_OF_CLOUDLETS = VM_PES*2;

    private DatacenterBroker broker;
    private CloudSim simulation;

    @BeforeEach
    public void setUp() {
        simulation = new CloudSim();
        final SimulationScenarioBuilder scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder().setSchedulingInterval(2).create(
            new HostBuilder()
                .setVmSchedulerSupplier(VmSchedulerSpaceShared::new)
                .setPes(HOST_PES).setMips(HOST_MIPS)
                .create()
                .getHosts()
        );

        final BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().create();
        broker = brokerBuilder.getBroker();
        brokerBuilder.getVmBuilder()
            .setPes(VM_PES).setMips(VM_MIPS)
            .setCloudletSchedulerSupplier(CloudletSchedulerTimeShared::new)
            .createAndSubmit(NUMBER_OF_VMS);

        final UtilizationModel utilizationModel = new UtilizationModelFull();
        brokerBuilder.getCloudletBuilder()
            .setLength(CLOUDLET_LENGTH)
            .setUtilizationModelCpu(utilizationModel)
            .setPEs(CLOUDLET_PES)
            .createAndSubmit(NUMBER_OF_CLOUDLETS);
    }

    @Test
    public void integrationTest() {
        simulation.start();
        printCloudletsExecutionResults(broker);

        final double time = 20;
        for(final Cloudlet c: broker.getCloudletFinishedList()){
            final String msg1 = String.format("Cloudlet %d doesn't have the expected finish time.", c.getId());
            assertEquals(time, c.getFinishTime(), 0.3, msg1);

            final String msg2 = String.format("Cloudlet %d doesn't have the expected exec time.", c.getId());
            assertEquals(time, c.getActualCpuTime(), 0.3, msg2);
        }
    }

    private void printCloudletsExecutionResults(DatacenterBroker broker) {
        new CloudletsTableBuilder(broker.getCloudletFinishedList())
                .setTitle(broker.getName())
                .build();
    }

}
