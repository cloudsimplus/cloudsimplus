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
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 *
 * An Integration Test (IT) running a simulation scenario with 1 PM, 2 VMs
 * and 2 cloudlets in each VM. It checks if the amount of available
 * CPU of the host is as expected along the simulation time.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CheckHostAvailableMipsDynamicUtilizationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckHostAvailableMipsDynamicUtilizationTest.class.getSimpleName());

    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 2;
    private static final int NUMBER_OF_VMS = HOST_PES;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES/NUMBER_OF_VMS;
    private static final int CLOUDLET_PES = VM_PES;
    private static final int CLOUDLET_LENGTH = HOST_MIPS*10;
    private static final int NUMBER_OF_CLOUDLETS = 2;

    private SimulationScenarioBuilder scenario;
    private UtilizationModelDynamic utilizationModel;
    private CloudSim simulation;

    /**
     * A lambda function used by the {@link Host#addOnUpdateProcessingListener(EventListener)}
     * that will be called every time a host updates the processing of its VMs.
     * It checks if the amount of available Host CPU is as expected,
     * every time a host updates the processing of all its VMs.
     *
     * @param evt
     */
    private void onUpdateVmsProcessing(HostUpdatesVmsProcessingEventInfo evt) {
        final double expectedAvailableHostMips =
               HOST_MIPS * HOST_PES * utilizationModel.getUtilization(evt.getTime());

        LOGGER.info(
            "- onUpdateVmProcessing at time {}: {} available mips: {} expected availability: {}",
            evt.getTime(), evt.getHost(), evt.getHost().getAvailableMips(), expectedAvailableHostMips);

        assertEquals(
                 expectedAvailableHostMips, evt.getHost().getAvailableMips(), 0,
                 "The amount of Host available HOST_MIPS was not as expected.");
    }

    @BeforeEach
    public void setUp() {
        this.simulation = new  CloudSim();
        scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder().setSchedulingInterval(2).createDatacenter(
                new HostBuilder()
                    .setVmSchedulerClass(VmSchedulerSpaceShared.class)
                    .setRam(4000).setBandwidth(400000)
                    .setPes(HOST_PES).setMips(HOST_MIPS)
                    .setOnUpdateVmsProcessingListener(this::onUpdateVmsProcessing)
                    .createOneHost()
                    .getHosts()
        );

        final BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();

        brokerBuilder.getVmBuilder()
                .setRam(1000).setBandwidth(100000)
                .setPes(VM_PES).setMips(VM_MIPS).setSize(50000)
                .setCloudletSchedulerSupplier(CloudletSchedulerTimeShared::new)
                .createAndSubmitVms(NUMBER_OF_VMS);

        utilizationModel = new UtilizationModelDynamic();
        utilizationModel.setUtilizationUpdateFunction(instance -> instance.getUtilization() + instance.getTimeSpan()*0.25);
        brokerBuilder.getCloudletBuilder()
                .setLength(CLOUDLET_LENGTH)
                .setUtilizationModelCpu(utilizationModel)
                .setPEs(CLOUDLET_PES)
                .createAndSubmitCloudlets(NUMBER_OF_CLOUDLETS);
    }

    @Test @Disabled("WARNING: It has to be checked if it is really required to use the "
                + " PowerDatacenter, PowerHostUtilizationHistory, Vm"
                + " and CloudletSchedulerDynamicWorkload to make the host CPU usage"
                + " to be correctly updated.")
    public void integrationTest() {
        simulation.start();
        final DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        printCloudletsExecutionResults(broker);
    }

    public void printCloudletsExecutionResults(DatacenterBroker broker) {
        new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
    }

}
