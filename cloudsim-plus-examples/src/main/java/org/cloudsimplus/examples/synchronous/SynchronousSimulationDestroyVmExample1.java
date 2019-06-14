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
package org.cloudsimplus.examples.synchronous;

import ch.qos.logback.classic.Level;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A example showing how to destroy a VM running within a synchronous simulation,
 * by calling {@link org.cloudbus.cloudsim.brokers.DatacenterBrokerAbstract#destroyVm(Vm)}).
 *
 * Being able to kill a virtual machine, while it is still executing cloudlets, enables to simulate interesting
 * scenarios (e.g. including VM failures or manually shutting down the system).
 *
 * @author Pawel Koperek
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.7.0
 * @see SynchronousSimulationExample1
 */
public class SynchronousSimulationDestroyVmExample1 {
    /**
     * Defines the time (in seconds) to run the simulation for.
     * The clock is increased in the interval defined here.
     */
    private static final double INTERVAL = 1;
    private static final int HOSTS = 2;
    private static final int HOST_PES = 4;

    private static final int VMS = 4;
    private static final int VM_PES = 2;

    private static final int CLOUDLETS = 8;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    private final CloudSim simulation;
    private DatacenterBrokerSimple broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private double previousClock;

    public static void main(String[] args) {
        new SynchronousSimulationDestroyVmExample1();
    }

    public SynchronousSimulationDestroyVmExample1() {
        Log.setLevel(Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        int iteration = 0;
        simulation.startSync();
        while(simulation.isRunning()){

            if(iteration == 15) {
                Vm vm = vmList.get(0);
                List<Cloudlet> affected = broker0.destroyVm(vm);
                affected.stream().forEach(cl -> cl.setSubmissionDelay(cl.getSubmissionDelay() + simulation.clock()));

                broker0.submitCloudletList(affected);
            }

            simulation.runFor(INTERVAL);
            printVmCpuUtilization();
            iteration++;
        }

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingLong(Cloudlet::getId));
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    private void printVmCpuUtilization() {
        if(simulation.clock() == previousClock){
            return;
        }

        previousClock = simulation.clock();
        System.out.printf("\t\tVM CPU utilization for Time %.0f\n", simulation.clock());
        for (final Vm vm : broker0.getVmExecList()) {
            System.out.printf(" Vm %5d |", vm.getId());
        }
        System.out.println();

        for (final Vm vm : broker0.getVmExecList()) {
            System.out.printf(" %7.0f%% |", vm.getCpuPercentUtilization()*100);
        }
        System.out.println("\n");
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(ram, bw, storage, peList);
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets with different submission delays.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
            cloudlet.setUtilizationModelCpu(new UtilizationModelFull())
                    .setSizes(1024)
                    .setSubmissionDelay(i);
            list.add(cloudlet);
        }

        return list;
    }
}
