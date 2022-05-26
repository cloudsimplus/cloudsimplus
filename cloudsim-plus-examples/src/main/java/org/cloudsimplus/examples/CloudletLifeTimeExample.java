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
package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * An example showing how to use the new {@link Cloudlet#setLifeTime(double)} to
 * define the maximum time the Cloudlet is allowed to execute.
 * After this time is reached, the Cloudlet is finished as soon as possible.</p>
 *
 * @author Sun Lingyu
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.2.1
 */
public class CloudletLifeTimeExample {
    private static final int HOSTS = 3;
    private static final int HOST_PES = 10;

    private static final int VMS = 4;
    private static final int VM_PES = 4;
    private static final int VM_MIPS = 1000;

    private static final int CLOUDLETS = 4;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10_000;

    /** @see Datacenter#getSchedulingInterval() */
    private static final int SCHEDULING_INTERVAL = 2;

    /**
     * Maximum time (in seconds) Cloudlets are allowed to execute.
     * Set -1 to disable lifeTime and execute the Cloudlet entirely.
     */
    private static final double CLOUDLET_LIFE_TIME = 5;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new CloudletLifeTimeExample();
    }

    private CloudletLifeTimeExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        final double startSecs = TimeUtil.currentTimeSecs();
        System.out.printf("Simulation started at %s%n%n", LocalTime.now());
        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
        System.out.printf("Simulation finished at %s. Execution time: %.2f seconds%n", LocalTime.now(), TimeUtil.elapsedSeconds(startSecs));
    }

    private Datacenter createDatacenter() {
        final var hostList = new ArrayList<Host>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        final var datacenter = new DatacenterSimple(simulation, hostList);
        datacenter.setSchedulingInterval(SCHEDULING_INTERVAL);
        return datacenter;
    }

    private Host createHost() {
        final var peList = new ArrayList<Pe>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        IntStream.range(0, HOST_PES).forEach(i -> peList.add(new PeSimple(1000)));

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        final var host = new HostSimple(ram, bw, storage, peList);
        host.setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    private List<Vm> createVms() {
        final var vmList = new ArrayList<Vm>(VMS);
        for (int i = 0; i < VMS; i++) {
            final var vm = new VmSimple(i, VM_MIPS, VM_PES);
            vmList.add(vm);
        }

        return vmList;
    }

    private List<Cloudlet> createCloudlets() {
        final var cloudletList = new ArrayList<Cloudlet>(CLOUDLETS);
        for (int i = 0; i < CLOUDLETS; i++) {
            final var cloudlet =
                new CloudletSimple(i, CLOUDLET_LENGTH, CLOUDLET_PES)
                    .setFileSize(1024)
                    .setOutputSize(1024)
                    .setUtilizationModelCpu(new UtilizationModelFull())
                    .setUtilizationModelBw(new UtilizationModelStochastic())
                    .setUtilizationModelRam(new UtilizationModelStochastic())
            		.setLifeTime(CLOUDLET_LIFE_TIME);
            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}
