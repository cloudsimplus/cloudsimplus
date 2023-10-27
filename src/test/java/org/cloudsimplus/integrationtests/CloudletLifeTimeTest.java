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

import ch.qos.logback.classic.Level;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.Lifetimed;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * An integration test to check if cloudlet lifetime is as expected using a datacenter scheduling interval or not.
 * @author Manoel Campos da Silva Filho
 */
public class CloudletLifeTimeTest {

    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 1;
    private static final int HOST_RAM = 4000;
    private static final int CLOUDLET_LENGTH = 10_000;

    /**
     * Maximum time (in seconds) Cloudlets are allowed to execute.
     * @see Lifetimed#setLifeTime(double)
     */
    private static final double CLOUDLET_LIFE_TIME = 5;

    private CloudSimPlus simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    @BeforeAll
    static void beforeAll(){
        Log.setLevel(Level.OFF);
    }

    @Test
    void checkFinishTimeEqualsToLifeTimeWithNoSchedulingInterval(){
        final double expectedFinishTime = 5.0;
        final double expectedFinishedLen = 5000;
        final var cloudlet = createScenario(-1);
        assertEquals(expectedFinishTime, cloudlet.getFinishTime(), 0.2);
        assertEquals(expectedFinishedLen, cloudlet.getFinishedLengthSoFar());
    }

    @Test
    void checkFinishTimeEqualsToLifeTimeWithSchedulingInterval(){
        final double expectedFinishTime = 5.0;
        final double expectedFinishedLen = 5000;
        final var cloudlet = createScenario(2);
        assertEquals(expectedFinishTime, cloudlet.getFinishTime(), 0.2);
        assertEquals(expectedFinishedLen, cloudlet.getFinishedLengthSoFar(), 10);
    }

    /**
     * Creates a scenario with our without scheduling interval
     * @param schedulingInterval the desired scheduling or -1 for none
     * @return finished cloudlet
     */
    private Cloudlet createScenario(final int schedulingInterval) {
        simulation = new CloudSimPlus();
        Datacenter datacenter0 = createDatacenter(schedulingInterval);

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVmList();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        return broker0.getCloudletFinishedList().get(0);
    }

    private Datacenter createDatacenter(final int schedulingInterval) {
        final var hostList = List.of(createHost());
        final var datacenter = new DatacenterSimple(simulation, hostList);
        datacenter.setSchedulingInterval(schedulingInterval);
        return datacenter;
    }

    private Host createHost() {
        final List<Pe> peList = range(0, HOST_PES).mapToObj(__ -> (Pe)new PeSimple(HOST_MIPS)).toList();
        final long bw = 10_000; //in Megabits/s
        final long storage = 1_000_000; //in Megabytes
        return new HostSimple(HOST_RAM, bw, storage, peList);
    }

    private static List<Vm> createVmList() {
        return List.of(new VmSimple(HOST_MIPS, HOST_PES));
    }

    private List<Cloudlet> createCloudlets() {
        final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, HOST_PES);
        cloudlet
                .setUtilizationModelCpu(new UtilizationModelFull())
                .setLifeTime(CLOUDLET_LIFE_TIME);
        return List.of(cloudlet);
    }
}
