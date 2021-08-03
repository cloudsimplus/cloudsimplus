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
package org.cloudsimplus.examples.resourceusage;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * An example that uses a {@link UtilizationModelDynamic} to define that each Cloudlet
 * will request more than 100% of the CPU capacity to simulate CPU over-subscription.
 * This example creates one cloudlet for each VM.
 * Since there is no way to allocated more than 100% of the CPU capacity,
 * warnings are issued to indicate that and the total CPU capacity is allocated to each Cloudlet.
 * This way, Cloudlets will always finish in the expected time considering the total CPU capacity is allocated to
 * each of them.
 * If you, for instance, reduce the percentage of allocated CPU for each Cloudlet in
 * {@link #CLOUDLET_CPU_UTILIZATION} to 0.5 (50%), Cloudlets will take the double of previous time to finish.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 6.3.7
 */
public class UtilizationModelDynamicOversubscriptionExample {
    private static final double SCHEDULING_INTERVAL = 1; //in Seconds

    private static final int  HOSTS = 1;
    private static final int  HOST_PES = 4;
    private static final long HOST_MIPS = 1000; // in Million Instructions per Second

    private static final int  VMS = 2;
    private static final int  VM_PES = 2; // number of VM's CPU cores

    private static final int  CLOUDLETS_BY_VM = 1;
    private static final int  CLOUDLET_LENGTH = 10_000; //in Million Instructions (MI)

    /**
     * The percentage of CPU each Cloudlet will request along the entire simulation run.
     * Since there is no way to allocate 200% of the CPU for each Cloudlet, only 100% will
     * be allocated and warnings issued.
     */
    private static final double CLOUDLET_CPU_UTILIZATION = 2.0;

    private final CloudSim simulation;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    /**
     * Starts the example.
     * @param args
     */
    public static void main(String[] args) {
        new UtilizationModelDynamicOversubscriptionExample();
    }

    /**
     * Default constructor that builds the simulation.
     */
    private UtilizationModelDynamicOversubscriptionExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();
        Log.setLevel(DatacenterBroker.LOGGER, Level.WARN);

        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        this.vmList = new ArrayList<>(VMS);
        this.cloudletList = new ArrayList<>(VMS);

        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm();
            this.vmList.add(vm);
            this.cloudletList.addAll(createCloudlets(vm, CLOUDLETS_BY_VM));
        }
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.addOnClockTickListener(this::clockTickListener);
        simulation.start();

        new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void clockTickListener(final EventInfo evt) {
        vmList.forEach(vm -> System.out.printf("%s: %s CPU MIPS -> Requested %.0f%% Allocated: %.0f%%%n", evt.getTime(), vm, vm.getCpuPercentRequested(evt.getTime())*100, vm.getCpuPercentUtilization()*100));
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple())
                    .setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        final long  ram = 2048; // host memory (Megabyte)
        final long storage = 1000000; // host storage (Megabyte)
        final long bw = 10000; //in Megabits/s

        final List<Pe> peList = new ArrayList<>(HOST_PES); //List of CPU cores

        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(HOST_MIPS, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm() {
        final long   storage = 10000; // vm image size (Megabyte)
        final int    ram = 512; // vm memory (Megabyte)
        final long   bw = 1000; // vm bandwidth (Megabits/s)

        return new VmSimple(vmList.size(), HOST_MIPS, VM_PES)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private List<Cloudlet> createCloudlets(final Vm vm, final int count) {
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution
        final List<Cloudlet> cloudlets = new ArrayList<>(count);

        //Defines that the Cloudlet will use all the VM's RAM and Bandwidth.
        final UtilizationModel utilizationFull = new UtilizationModelFull();

        final UtilizationModel utilizationModelDynamic =
            new UtilizationModelDynamic(CLOUDLET_CPU_UTILIZATION, CLOUDLET_CPU_UTILIZATION)
                        .setOverCapacityRequestAllowed(true);
        for (int i = 0; i < count; i++) {
            Cloudlet cloudlet = new CloudletSimple(
                cloudletList.size(), CLOUDLET_LENGTH, vm.getNumberOfPes())
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModelBw(utilizationFull)
                .setUtilizationModelRam(utilizationFull)
                .setUtilizationModelCpu(utilizationModelDynamic)
                .setVm(vm);
            cloudlets.add(cloudlet);
        }

        return cloudlets;
    }

}
