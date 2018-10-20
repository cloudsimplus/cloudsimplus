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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An example showing how to create a Datacenter with two hosts,
 * with one Vm in each one, and run 1 cloudlet in each Vm.
 * At the end, it shows the total CPU utilization of hosts
 * into a Datacenter.
 *
 * <p>Cloudlets run in VMs with different MIPS requirements and will
 * take different times to complete the execution, depending on the requested VM
 * performance.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HostsCpuUsageExample {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private List<Host> hostList;
    private DatacenterBroker broker;
    private CloudSim simulation;

    private static final int NUMBER_OF_VMS = 2;
    private static final int NUMBER_OF_HOSTS = 2;

    /**
     * Starts the example execution
     * @param args
     */
    public static void main(String[] args) {
        new HostsCpuUsageExample();
    }

    public HostsCpuUsageExample(){
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        broker = new DatacenterBrokerSimple(simulation);

        vmlist = new ArrayList<>(NUMBER_OF_VMS);
        cloudletList = new ArrayList<>(NUMBER_OF_VMS);

        final int pesNumber = 1; //number of cpus
        final int mips = 1000;
        for (int i = 1; i <= NUMBER_OF_VMS; i++) {
            Vm vm = createVm(pesNumber, mips);
            vmlist.add(vm);

            Cloudlet cloudlet = createCloudlet(pesNumber);
            cloudletList.add(cloudlet);
        }

        //Link each Cloudlet to a spacific VM
        for (int i = 0; i < NUMBER_OF_VMS; i++) {
            broker.bindCloudletToVm(cloudletList.get(i), vmlist.get(i));
        }

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList)
            .addColumn(5, new TextTableColumn("Host  ", "MIPS  "), cloudlet -> cloudlet.getVm().getHost().getMips())
            .addColumn(7, new TextTableColumn("VM MIPS"), cloudlet -> cloudlet.getVm().getMips())
            .build();

        showCpuUtilizationForAllHosts();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private Cloudlet createCloudlet(final int pesNumber) {
        final long length = 10000;
        final long fileSize = 300;
        final long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        return new CloudletSimple(length, pesNumber)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilizationModel);
    }

    /**
     * Creates a VM enabling the collection of CPU utilization history.
     * This way, hosts can get the CPU utilization based on VM utilization.
     * @param pesNumber
     * @param mips
     * @return
     */
    private Vm createVm(final int pesNumber, final long mips) {
        final long size = 10000; //image size (Megabyte)
        final int ram = 2048; //vm memory (Megabyte)
        final long bw = 1000;
        Vm vm = new VmSimple(mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        vm.getUtilizationHistory().enable();
        return vm;
    }

    /**
     * Shows CPU utilization of all hosts into a given Datacenter.
     */
    private void showCpuUtilizationForAllHosts() {
        System.out.println("\nHosts CPU utilization history for the entire simulation period");
        int numberOfUsageHistoryEntries = 0;
        for (Host host : hostList) {
            double mipsByPe = host.getTotalMipsCapacity() / (double)host.getNumberOfPes();
            System.out.printf("Host %d: Number of PEs %2d, MIPS by PE %.0f\n", host.getId(), host.getNumberOfPes(), mipsByPe);
            for (Map.Entry<Double, Double> entry : host.getUtilizationHistorySum().entrySet()) {
                final double time = entry.getKey();
                final double cpuUsage = entry.getValue()*100;
                numberOfUsageHistoryEntries++;
                System.out.printf("\tTime: %4.1f CPU Utilization: %6.2f%%\n", time, cpuUsage);
            }
            System.out.println("--------------------------------------------------");
        }

        if(numberOfUsageHistoryEntries == 0) {
            System.out.println("No CPU usage history was found");
        }
    }

    /**
     * Creates a datacenter setting the scheduling interval
     * that, between other things, defines the times to collect
     * VM CPU utilization.
     *
     * @return
     */
    private Datacenter createDatacenter() {
        hostList = new ArrayList<>(NUMBER_OF_HOSTS);
        final int pesNumber = 1;
        final int mips = 2000;
        for (int i = 1; i <= 2; i++) {
            Host host = createHost(pesNumber, mips*i);
            hostList.add(host);
        }

        DatacenterSimple dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(2);
        return dc;
    }

    private Host createHost(final int pesNumber, final long mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        final long ram = 2048; //host memory (Megabyte)
        final long storage = 1000000; //host storage (Megabyte)
        final long bw = 10000; //Megabits/s

        Host host = new HostSimple(ram, bw, storage, peList);
        host
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }
}
