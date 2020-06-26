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

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * An Integration Test (IT) running a simulation scenario with 1 PM, 2 VMs
 * and 1 Cloudlet in each VM. The Cloudlets use a {@link UtilizationModelFull} for
 * CPU usage. The IT checks if the amount of available
 * CPU of the host is as expected along the simulation time.
 *
 * <p>It is created one broker for each VM and one VM finishes executing
 * prior to the other. This way, the IT checks if the CPU used by the
 * finished VM is freed on the host.</p>
 *
 * <p>Creating the VMs for the same broker
 * doesn't make the finished VM to be automatically destroyed by default.
 * In this case, only after all user VMs are finished that they are
 * destroyed in order to free resources.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0.0
 */
public final class CheckHostAvailableMipsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckHostAvailableMipsTest.class.getSimpleName());

    private static final double HOST_MIPS = 1000;
    private static final int    HOST_PES = 5;
    private static final int    NUMBER_OF_VMS = 2;
    private static final double VM_MIPS = HOST_MIPS;
    private static final int    VM_PES = HOST_PES/NUMBER_OF_VMS;
    private static final int    CLOUDLET_PES = VM_PES;
    private static final long   CLOUDLET_LENGTH = (long)HOST_MIPS*10;
    private static final int    NUMBER_OF_CLOUDLETS = NUMBER_OF_VMS;
    private static final int    FIRST_VM_FINISH_TIME = 6;
    private static final int    LAST_VM_FINISH_TIME = 10;

    private SimulationScenarioBuilder scenario;
    private UtilizationModel utilizationModel;
    private CloudSim simulation;

    @BeforeEach
    public void setUp() {
        this.simulation = new  CloudSim();
        utilizationModel = new UtilizationModelFull();
        scenario = new SimulationScenarioBuilder(simulation);
        final List<Host> hosts = new HostBuilder()
            .setOnUpdateVmsProcessingListener(this::onUpdateVmsProcessing)
            .setPes(HOST_PES).setMips(HOST_MIPS)
            .create()
            .getHosts();
        scenario.getDatacenterBuilder().setSchedulingInterval(2).create(hosts);

        //Create VMs and cloudlets for different brokers
        for(int i = 0; i < NUMBER_OF_VMS; i++){
            final BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().create();
            brokerBuilder.getVmBuilder()
                .setPes(VM_PES).setMips(VM_MIPS)
                .createAndSubmit();

            final long cloudletLength = i == 0 ? CLOUDLET_LENGTH : CLOUDLET_LENGTH/2;
            brokerBuilder.getCloudletBuilder()
                .setLength(cloudletLength)
                .setUtilizationModelCpu(utilizationModel)
                .setPEs(CLOUDLET_PES)
                .createAndSubmit(1, i);
        }
    }

    /**
     * A lambda function used by the {@link Host#addOnUpdateProcessingListener(EventListener)}
     * that will be called every time a host updates the processing of its VMs.
     * It checks if the amount of available Host CPU is as expected,
     * every time a host updates the processing of all its VMs.
     *
     * @param evt
     */
    private void onUpdateVmsProcessing(final HostUpdatesVmsProcessingEventInfo evt) {
        final double time = (int)evt.getTime();
        final double expectedAvailableHostMips = getExpectedAvailableHostMips(time);

        LOGGER.info(
            "- VMs processing at time {}: {} available mips: {} expected availability: {}",
            time, evt.getHost(), evt.getHost().getTotalAvailableMips(), expectedAvailableHostMips);
    }

    private double getExpectedAvailableHostMips(final double time) {
        final double usedHostMips = NUMBER_OF_CLOUDLETS * CLOUDLET_PES * VM_MIPS * utilizationModel.getUtilization(time);
        final double expectedAvailableHostMips = HOST_MIPS * HOST_PES - usedHostMips;

        if(time > FIRST_VM_FINISH_TIME){
            /*After 6 seconds, one VM finishes and
            its used capacity will be free*/
            return expectedAvailableHostMips + VM_MIPS*VM_PES;
        }
        else if(time > LAST_VM_FINISH_TIME) {
            /*After 10 seconds all VMs finish and
            all host capacity will be free*/
            return 5000.0;
        }

        return expectedAvailableHostMips;
    }

    @Test
    public void integrationTest() {
        simulation.start();
        //scenario.getBrokerBuilder().getBrokers().forEach(b -> new CloudletsTableBuilder(b.getCloudletFinishedList()).setTitle(b.getName()).build());
    }

}
