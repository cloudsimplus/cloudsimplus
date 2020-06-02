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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An example showing how to create a Datacenter with two hosts and run
 * two cloudlets on it. At the end, it shows the total CPU utilization of VMs
 * into a Datacenter.
 *
 * <p>
 * Cloudlets run in VMs with different MIPS requirements and will
 * take different times to complete the execution, depending on the requested VM
 * performance.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see VmsRamAndBwUsageExample
 * @see org.cloudsimplus.examples.power.PowerExample
 */
public class VmsCpuUsageExample {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private DatacenterBroker broker;
    private static final int VMS = 2;
    private static final int HOSTS = 2;

    /**
     * Starts the example execution.
     * @param args
     */
    public static void main(String[] args) {
        new VmsCpuUsageExample();
    }

    private VmsCpuUsageExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        CloudSim simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter(simulation);

        broker = new DatacenterBrokerSimple(simulation);

        vmlist = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(VMS);

        final int mips = 1000;
        final int pesNumber = 2;
        for (int i = 1; i <= VMS; i++) {
            Vm vm = createVm(pesNumber*2, mips * i, i - 1);
            vmlist.add(vm);
            Cloudlet cloudlet = createCloudlet(pesNumber);
            cloudletList.add(cloudlet);
            cloudlet.setVm(vm);
        }
        cloudletList.add(createCloudlet(pesNumber));

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        final double finishTime = simulation.start();

        new CloudletsTableBuilder(broker.getCloudletFinishedList())
            .addColumn(7, new TextTableColumn("VM MIPS"), cloudlet -> cloudlet.getVm().getMips())
            .build();

        showCpuUtilizationForAllVms(finishTime);

        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private Cloudlet createCloudlet(final int pesNumber) {
        final long length = 10000;
        final long fileSize = 300;
        final long outputSize = 300;
        UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(0.25);
        UtilizationModel utilizationModelCpu = new UtilizationModelDynamic(0.5);

        Cloudlet cloudlet = new CloudletSimple(length, pesNumber);
        cloudlet.setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(utilizationModelCpu)
            .setUtilizationModelBw(utilizationModelDynamic)
            .setUtilizationModelRam(utilizationModelDynamic);
        return cloudlet;
    }

    /**
     * Creates a VM enabling the collection of CPU utilization history.
     * @param pesNumber
     * @param mips
     * @param id
     * @return
     */
    private Vm createVm(final int pesNumber, final int mips, final int id) {
        final long size = 10000; //image size (Megabyte)
        final int ram = 2048; //vm memory (Megabyte)
        final long bw = 1000;

        //create two VMs
        Vm vm = new VmSimple(id, mips, pesNumber);
        vm.setRam(ram).setBw(bw)
            .setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        vm.getUtilizationHistory().enable();;
        return vm;
    }

    private void showCpuUtilizationForAllVms(final double simulationFinishTime) {
        System.out.printf("%nHosts CPU utilization history for the entire simulation period%n%n");
        int numberOfUsageHistoryEntries = 0;
        for (Vm vm : vmlist) {
            System.out.printf("VM %d%n", vm.getId());
            if (vm.getUtilizationHistory().getHistory().isEmpty()) {
                System.out.println("\tThere isn't any usage history");
                continue;
            }

            for (Map.Entry<Double, Double> entry : vm.getUtilizationHistory().getHistory().entrySet()) {
                final double time = entry.getKey();
                final double vmCpuUsage = entry.getValue()*100;
                if (vmCpuUsage > 0) {
                    numberOfUsageHistoryEntries++;
                    System.out.printf("\tTime: %2.0f CPU Utilization: %6.2f%%%n", time, vmCpuUsage);
                }
            }
        }

        if (numberOfUsageHistoryEntries == 0) {
            System.out.println("No CPU usage history was found");
        }
    }

    /**
     * Creates a datacenter setting the scheduling interval
     * that, between other things, defines the times to collect
     * VM CPU utilization.
     *
     * @param simulation
     * @return
     */
    private static Datacenter createDatacenter(CloudSim simulation) {
        List<Host> hostList = new ArrayList<>(HOSTS);
        final int pesNumber = 4;
        final int mips = 1000;
        for (int i = 1; i <= HOSTS; i++) {
            Host host = createHost(pesNumber, mips*i);
            hostList.add(host);
        }

        DatacenterSimple dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(2);
        return dc;
    }

    private static Host createHost(final int pesNumber, final long mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        final int ram = 2048; //host memory (Megabyte)
        final long storage = 1000000; //host storage
        final int bw = 10000;

        final Host host = new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        host.enableStateHistory();
        return host;
    }
}
