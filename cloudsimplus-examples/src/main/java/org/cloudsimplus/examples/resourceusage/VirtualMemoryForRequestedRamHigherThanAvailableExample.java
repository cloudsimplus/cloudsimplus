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
package org.cloudsimplus.examples.resourceusage;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.HarddriveStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An example showing how the processing of some Cloudlets is delayed
 * due to lack of available RAM and BW.
 *
 * <p>The example creates VMs with different number of Cloudlets.
 * Those Cloudlets have different {@link UtilizationModel}s for RAM.
 * Some Cloudlets inside a given VM will require more RAM than the VM has available.
 * For instance, two Cloudlets inside a VM always require 60% of the VM capacity at the same time.
 * Since that total will be 120% of the RAM capacity, there is no way to fulfill the request
 * for one of those Cloudlets.
 * This way, one Cloudlet will use 60% of the VM RAM, while the other one will use only 40%.
 * The 20% of RAM the second Cloudlets is requiring has to rely on Virtual Memory.
 * In this scenario, CloudSim Plus will simulate the swap of memory data belonging
 * to other processes (Cloudlets) between the RAM and the disk.
 * Since the latter is way slower than the former, that will impose a processing
 * delay for the requesting Cloudlets, which will take longer than expected to finish.
 * </p>
 *
 * <p>
 * Realize that VM storage depends on Host storage. This way, for this example,
 * we have to create Hosts with a {@link HarddriveStorage} that has
 * configured parameters such as {@link HarddriveStorage#getMaxTransferRate()}.
 * </p>
 *
 * <p>For a Cloudlet, it will require more BW than there will be available.
 * This way, the allocated BW will be reduced, causing more delay.
 * When some BW cannot be allocated to the Cloudlet (due to over-subscription),
 * the delay is computed based on the time needed to use
 * the required bandwidth after the reduced allocation.
 * For instance, if the required bandwidth is 10mbps, that means
 * the cloudlet is willing to transfer 10 mbits in one second.
 * If just 8 mbps is allocated to the cloudlet,
 * to transfer the same 10 mbits it will take 0,25 second more.
 * </p>
 *
 * Check the following docs for specific implementation details you may be aware of:
 * <ul>
 *     <li>{@link CloudletSchedulerAbstract#getVirtualMemoryDelay(CloudletExecution, double)} </li>
 *     <li>{@link CloudletSchedulerAbstract#getBandwidthOverSubscriptionDelay(CloudletExecution, double)}</li>
 * </ul>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.3.0
 */
public class VirtualMemoryForRequestedRamHigherThanAvailableExample {
    /**
     * @see Datacenter#getSchedulingInterval()
     */
    private static final int  SCHEDULING_INTERVAL = 2;

    /**
     * The reading speed (in mbps) for Hosts {@link HarddriveStorage},
     * that impact processing delay when cloudlets use Virtual Memory.
     * As slower is the reading speed, higher is the memory swapping overhead
     * and the increase in cloudlet processing (exec) time.
     */
    public static final int HOSTS_MAX_TRANSFER_RATE = 1600;

    private static final int HOSTS = 2;
    private static final int HOST_PES = 8;

    private static final int VMS = 3;
    private static final int VM_PES = 2;

    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 10000;

    /**
     * VM RAM capacity (in MB)
     */
    public static final int VM_RAM = 1000;


    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new VirtualMemoryForRequestedRamHigherThanAvailableExample();
    }

    private VirtualMemoryForRequestedRamHigherThanAvailableExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        createCloudlets();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        final Comparator<Cloudlet> comparator = Comparator.comparingLong(cl -> cl.getVm().getId());
        finishedCloudlets.sort(comparator.thenComparing(Cloudlet::getId));
        new CloudletsTableBuilder(finishedCloudlets)
            .addColumn(7, new TextTableColumn("VM RAM", "MB"), cl -> cl.getVm().getRam().getCapacity())
            .build();

        printOverSubscriptionDelay();
    }

    private void printOverSubscriptionDelay() {
        final String format = "%s exec time: %6.2f | RAM/BW over-subscription delay: %6.2f secs | Expected finish time (if no over-subscription): %6.2f secs%n";
        for (Vm vm : vmList) {
            vm.getCloudletScheduler()
              .getCloudletFinishedList()
              .stream()
              .filter(CloudletExecution::hasOverSubscription)
              .forEach(cle -> System.out.printf(format, cle, cle.getCloudlet().getActualCpuTime(), cle.getOverSubscriptionDelay(), cle.getExpectedFinishTime()));
        }
    }

    private void createCloudlets() {
        /*
         * Defines the UtilizationModels for BW that will be used for the next 2 Cloudlets.
         * If you change these 2 models to use 50% (0.5) of BW for each cloudlet,
         * there will be no additional delay due to a reduced allocation of BW for Cloudlets.
         */
        final List<UtilizationModel> utilizationModelBwList =
            Arrays.asList(
                new UtilizationModelDynamic(0.2),
                new UtilizationModelDynamic(1.0));

        //UtilizationModelDynamic defining that Cloudlets require 50% of the RAM capacity each one all the time
        cloudletList = createCloudlets(vmList.get(0), new UtilizationModelDynamic(0.5), utilizationModelBwList);

        /* UtilizationModelDynamic defining that Cloudlets require 60% of the RAM capacity each one all the time.
         * This this adds up to 120%, virtual memory will be used for the second cloudlet. */
        final UtilizationModel utilizationModelRam = new UtilizationModelDynamic(0.6);
        final List<Cloudlet> newList = createCloudlets(vmList.get(1), utilizationModelRam, utilizationModelBwList);
        cloudletList.addAll(newList);

        cloudletList.add(createCloudlet(vmList.get(2), new UtilizationModelFull()));
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
        final Datacenter dc = new DatacenterSimple(simulation, hostList);
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    /**
     * Creates a Host with a {@link HarddriveStorage} configured
     * to indicate delay in read operations,
     * that impact the delay imposed for Cloudlets using Virtual Memory.
     * @return
     */
    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storageSize = 1000000; //in Megabytes
        final HarddriveStorage hardDrive = new HarddriveStorage(storageSize);
        hardDrive.setAvgSeekTime(0).setLatency(0).setMaxTransferRate(HOSTS_MAX_TRANSFER_RATE);

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(ram, bw, hardDrive, peList);
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(VM_RAM).setBw(1000).setSize(10000);
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets using the same {@link UtilizationModel} for RAM and BW.
     * @param vm VM to run the Cloudlets
     * @param cloudlets number of cloudlets to create
     * @param utilizationModelRamBw  {@link UtilizationModel} to be used for cloudlet RAM and BW.
     */
    private List<Cloudlet> createCloudlets(final Vm vm, final int cloudlets, final UtilizationModel utilizationModelRamBw) {
        //Creates a List with the same UtilizationModel BW for all cloudlets
        final List<UtilizationModel> utilizationModelBwList = IntStream
                                                                  .range(0, cloudlets)
                                                                  .mapToObj(i -> utilizationModelRamBw)
                                                                  .collect(Collectors.toList());

        return createCloudlets(vm, utilizationModelRamBw, utilizationModelBwList);
    }

    /**
     * Creates one cloudlet for each BW {@link UtilizationModel} provided.
     * @param vm
     * @param utilizationModelRam
     * @param utilizationModelBwList BW {@link UtilizationModel} list to be used for each created cloudlet
     * @return
     */
    private List<Cloudlet> createCloudlets(
        final Vm vm,
        final UtilizationModel utilizationModelRam,
        final List<UtilizationModel> utilizationModelBwList)
    {
        final int cloudlets = utilizationModelBwList.size();
        final List<Cloudlet> list = new ArrayList<>(cloudlets);

        for (UtilizationModel utilizationModelBw : utilizationModelBwList) {
            list.add(createCloudlet(vm, utilizationModelRam, utilizationModelBw));
        }

        return list;
    }

    private Cloudlet createCloudlet(final Vm vm, final UtilizationModel utilizationModelRamBw) {
        return createCloudlet(vm, utilizationModelRamBw, utilizationModelRamBw);
    }

    private Cloudlet createCloudlet(
        final Vm vm, final UtilizationModel utilizationModelRam,
        final UtilizationModel utilizationModelBw)
    {
        final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
        cloudlet.setUtilizationModelCpu(new UtilizationModelFull())
                .setUtilizationModelRam(utilizationModelRam)
                .setUtilizationModelBw(utilizationModelBw);
        cloudlet.setSizes(1024).setVm(vm);
        return cloudlet;
    }
}
