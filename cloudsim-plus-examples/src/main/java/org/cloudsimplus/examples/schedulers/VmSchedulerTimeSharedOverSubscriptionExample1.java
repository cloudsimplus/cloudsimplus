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
package org.cloudsimplus.examples.schedulers;

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
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing the usage of the {@link VmSchedulerTimeSharedOverSubscription}
 * which allows over-subscription of Host's PEs.
 * When running VMs request more MIPS than available into a Host,
 * they will be allowed to run but with a reduced amount of MIPS.
 *
 * <p>Using other {@code VmSchedulers} such as the {@link VmSchedulerTimeShared},
 * if the Host doesn't have the MIPS capacity required by a VM, the VM is not allowed
 * to execute into this Host.</p>
 *
 * <p>This example creates 2 Hosts with 1 PE of 500 MIPS.
 * However, there are 2 VMs with 1 PE of 1000 MIPS.
 * There are 2 Cloudlets, each one executed into one VM,
 * requiring 1 PE and having a length of 10000 MI.
 *
 * If each Cloudlet had one exclusive PE to execute,
 * they would finish in 10 seconds.
 * But since their VMs are sharing the same physical PE,
 * the time doubles to 20.
 * But since the Host PE has just half of the MIPS required
 * by the VMs' PEs, the time doubles again, to 40 seconds,
 * as can be seen in the results.
 * </p>
 *
 * <p>At the end, the Hosts' state history is shown.
 * The state history stores data about CPU utilization
 * and requested and allocated MIPS.
 * If you just want CPU utilization.
 * If you just need CPU utilization history, check the other
 * examples mentioned below.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see org.cloudsimplus.examples.resourceusage.HostsCpuUsageExample
 * @see org.cloudsimplus.examples.power.PowerExample
 */
public class VmSchedulerTimeSharedOverSubscriptionExample1 {
    private static final int HOSTS     = 2;
    private static final int VMS       = 2;
    private static final int CLOUDLETS = VMS;

    private static final int HOST_PES     = 1;
    private static final int VM_PES       = HOST_PES;
    private static final int CLOUDLET_PES = VM_PES;

    private static final int HOST_MIPS = 500;
    private static final int VM_MIPS   = HOST_MIPS*2;

    private static final int CLOUDLET_LENGTH = VM_MIPS*10;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private static final int SCHEDULING_INTERVAL = 10;

    public static void main(String[] args) {
        new VmSchedulerTimeSharedOverSubscriptionExample1();
    }

    private VmSchedulerTimeSharedOverSubscriptionExample1() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLETS);

        createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        createVms();
        createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        new CloudletsTableBuilder(broker0.getCloudletFinishedList())
            .addColumn(5,  new TextTableColumn("Host MIPS", "total"), c -> c.getVm().getHost().getTotalMipsCapacity())
            .addColumn(8,  new TextTableColumn("VM MIPS", "total"), c -> c.getVm().getTotalMipsCapacity())
            .addColumn(9,  new TextTableColumn("  VM MIPS", "requested"), this::getVmRequestedMips)
            .addColumn(10, new TextTableColumn("  VM MIPS", "allocated"), this::getVmAllocatedMips)
            .build();

        System.out.printf("%n-------------------------------------- Hosts CPU usage History --------------------------------------%n");
        hostList.forEach(this::printHostStateHistory);
    }

    /**
     * Prints the {@link Host#getStateHistory() state history} for a given Host.
     * Realize that the state history is just collected if that is enabled before
     * starting the simulation by calling {@link Host#enableStateHistory()}.
     *
     * @param host
     */
    private void printHostStateHistory(Host host) {
        System.out.printf("Host: %d%n", host.getId());
        System.out.println("-----------------------------------------------------------------------------------------------------");
        host.getStateHistory().stream().forEach(System.out::print);
        System.out.println();
    }

    private double getVmRequestedMips(Cloudlet c) {
        if(c.getVm().getStateHistory().isEmpty()){
            return 0;
        }

        return c.getVm().getStateHistory().get(c.getVm().getStateHistory().size()-1).getRequestedMips();
    }

    private double getVmAllocatedMips(Cloudlet c) {
        if(c.getVm().getStateHistory().isEmpty()){
            return 0;
        }

        return c.getVm().getStateHistory().get(c.getVm().getStateHistory().size()-1).getAllocatedMips();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for(int h = 0; h < HOSTS; h++) {
            Host host = createHost();
            hostList.add(host);
        }

        Datacenter dc0 = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(HOST_MIPS, new PeProvisionerSimple()));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        Host host = new HostSimple(ram, bw, storage, peList);
        host
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(new VmSchedulerTimeSharedOverSubscription());
        host.enableStateHistory();
        return host;
    }

    /**
     * Creates a list of VMs for the {@link #broker0}.
     */
    private void createVms() {
        for (int i = 0; i < VMS; i++) {
            Vm vm =
                new VmSimple(VM_MIPS, VM_PES)
                    .setRam(512).setBw(1000).setSize(10000);
            vmList.add(vm);
        }
    }

    /**
     * Creates a list of Cloudlets for the {@link #broker0}.
     */
    private void createCloudlets() {
        for (int i = 0; i < CLOUDLETS; i++) {
            Cloudlet cloudlet =
                new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES)
                    .setFileSize(1024)
                    .setOutputSize(1024)
                    .setUtilizationModel(new UtilizationModelFull());
            cloudletList.add(cloudlet);
        }
    }
}
