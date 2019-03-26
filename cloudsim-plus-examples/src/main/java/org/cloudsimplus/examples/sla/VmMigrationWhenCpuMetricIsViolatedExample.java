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
package org.cloudsimplus.examples.sla;

import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigration;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationWorstFitStaticThreshold;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.selectionpolicies.VmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.slametrics.SlaContract;

import java.util.ArrayList;
import java.util.List;

/**
 * This example shows how to do a migration using CpuUtilization threshold
 * defined in an SLA Contract. VM migration is triggered when the CPU metric is violated.
 *
 * @author raysaoliveira
 */
public final class VmMigrationWhenCpuMetricIsViolatedExample {
    /**
     * @see Datacenter#setSchedulingInterval(double)
     */
    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;

    private static final int  HOST_MIPS_BY_PE = 1000;
    private static final int  HOST_NUMBER_OF_PES = 12;
    private static final long HOST_RAM = 500000; //host memory (MB)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;

    private static final int  VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MB)
    private static final int  VM_RAM = 10000; //vm memory (MB)
    private static final long VM_BW = 100000;
    private static final int  VM_PES_NUM = 3; //number of cpus

    private static final long CLOUDLET_LENGHT = 20000;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * The percentage of CPU that a cloudlet will use when it starts executing
     * (in scale from 0 to 1, where 1 is 100%). For each cloudlet create, this
     * value is used as a base to define CPU usage.
     *
     * @see #createAndSubmitCloudlets(DatacenterBroker)
     */
    private static final double CLOUDLET_INITIAL_CPU_USAGE_PERCENT = 0.6;

    /**
     * Defines the speed (in percentage) that CPU usage of a cloudlet will
     * increase during the simulation time. (in scale from 0 to 1, where 1 is
     * 100%).
     *
     * @see #createAndSubmitCloudletsWithDynamicCpuUsage(double, double,
     * Vm, DatacenterBroker)
     */
    private static final double CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND = 0.05;

    private static final int HOSTS = 20;
    private static final int VMS = HOSTS;
    private static final int CLOUDLETS_BY_VM = 4;

    private final List<Vm> vmList = new ArrayList<>();
    private CloudSim simulation;

    /**
     * The file containing the Customer's SLA Contract in JSON format.
     */
    private static final String CUSTOMER_SLA_CONTRACT = "CustomerSLA.json";

    private SlaContract contract;
    private List<Cloudlet> cloudletList;

    public static void main(String[] args) {
        new VmMigrationWhenCpuMetricIsViolatedExample();
    }

    private VmMigrationWhenCpuMetricIsViolatedExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.contract = SlaContract.getInstance(CUSTOMER_SLA_CONTRACT);
        cloudletList = new ArrayList<>(CLOUDLETS_BY_VM);

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);

        createAndSubmitVms(broker);

        createAndSubmitCloudlets(broker);

        simulation.start();

        new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();

        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void createAndSubmitCloudlets(final DatacenterBroker broker) {
        double initialCloudletCpuUsagePercent = CLOUDLET_INITIAL_CPU_USAGE_PERCENT;
        final int numberOfCloudlets = VMS - 1;
        for (int i = 0; i < numberOfCloudlets; i++) {
            createAndSubmitCloudletsWithStaticCpuUsage(initialCloudletCpuUsagePercent, vmList.get(i), broker);
            initialCloudletCpuUsagePercent += 0.15;
        }

        //Create one last cloudlet which CPU usage increases dynamically
        Vm lastVm = vmList.get(vmList.size() - 1);
        createAndSubmitCloudletsWithDynamicCpuUsage(0.2, 1, lastVm, broker);
    }

    private void createAndSubmitVms(final DatacenterBroker broker) {
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm();
            vmList.add(vm);
        }
        broker.submitVmList(vmList);
    }

    private Vm createVm() {
        final Vm vm = new VmSimple(vmList.size(), VM_MIPS, VM_PES_NUM);
        vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
          .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    private void createAndSubmitCloudlets(
        final double cloudletInitialCpuUsagePercent,
        final double maxCloudletCpuUsagePercent,
        final Vm hostingVm,
        final DatacenterBroker broker,
        final boolean progressiveCpuUsage)
    {
        final UtilizationModel um = new UtilizationModelDynamic(UtilizationModel.Unit.ABSOLUTE, 50);
        long cloudletId;
        for (int i = 0; i < CLOUDLETS_BY_VM; i++) {
            cloudletId = hostingVm.getId() + i;
            UtilizationModelDynamic cpuUtilizationModel =
                createUtilizationModel(
                    cloudletInitialCpuUsagePercent,
                    maxCloudletCpuUsagePercent,
                    progressiveCpuUsage);


            final Cloudlet c
                    = new CloudletSimple(
                            cloudletId, CLOUDLET_LENGHT, 2)
                            .setFileSize(CLOUDLET_FILESIZE)
                            .setOutputSize(CLOUDLET_OUTPUTSIZE)
                            .setUtilizationModelCpu(cpuUtilizationModel)
                            .setUtilizationModelRam(um)
                            .setUtilizationModelBw(um);
            cloudletList.add(c);
        }

        broker.submitCloudletList(cloudletList);
        for (Cloudlet c : cloudletList) {
            broker.bindCloudletToVm(c, hostingVm);
        }
    }

    /**
     * Creates a {@link UtilizationModel} for a Cloudlet
     * defines if CPU usage will be static or dynamic.
     *
     * @param initialCpuUsagePercent the percentage of CPU the Cloudlet will use initially
     * @param maxCloudletCpuUsagePercent the maximum percentage of CPU the Cloudlet will use
     * @param progressiveCpuUsage true if the CPU usage must increment along the time, false if it's static.
     * @return the  {@link UtilizationModel} for a Cloudlet's CPU usage
     */
    private UtilizationModelDynamic createUtilizationModel(
        double initialCpuUsagePercent,
        double maxCloudletCpuUsagePercent,
        final boolean progressiveCpuUsage)
    {
        initialCpuUsagePercent = Math.min(initialCpuUsagePercent, 1);
        maxCloudletCpuUsagePercent = Math.min(maxCloudletCpuUsagePercent, 1);
        final UtilizationModelDynamic um = new UtilizationModelDynamic(initialCpuUsagePercent);

        if (progressiveCpuUsage) {
            um.setUtilizationUpdateFunction(this::getCpuUtilizationIncrement);
        }

        um.setMaxResourceUtilization(maxCloudletCpuUsagePercent);
        return um;
    }

    /**
     * Increments the CPU resource utilization, that is defined in percentage
     * values.
     *
     * @return the new resource utilization after the increment
     */
    private double getCpuUtilizationIncrement(final UtilizationModelDynamic um) {
        return um.getUtilization() + um.getTimeSpan() * CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND;
    }

    private void createAndSubmitCloudletsWithDynamicCpuUsage(
        final double initialCloudletCpuUsagePercent,
        final double maxCloudletCpuUsagePercent,
        final Vm hostingVm,
        final DatacenterBroker broker)
    {
        createAndSubmitCloudlets(
                initialCloudletCpuUsagePercent,
                maxCloudletCpuUsagePercent, hostingVm, broker, true);
    }

    private void createAndSubmitCloudletsWithStaticCpuUsage(
        final double initialCloudletCpuUsagePercent,
        final Vm hostingVm,
        final DatacenterBroker broker)
    {
        createAndSubmitCloudlets(
                initialCloudletCpuUsagePercent,
                initialCloudletCpuUsagePercent,
                hostingVm, broker, false);
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost(HOST_NUMBER_OF_PES, HOST_MIPS_BY_PE));
        }
        System.out.println();

        final VmAllocationPolicyMigration allocationPolicy
                = new VmAllocationPolicyMigrationWorstFitStaticThreshold(
                        new VmSelectionPolicyMinimumUtilization(),
                        contract.getCpuUtilizationMetric().getMaxDimension().getValue());
        allocationPolicy.setUnderUtilizationThreshold(contract.getCpuUtilizationMetric().getMinDimension().getValue());

        final DatacenterSimple dc = new DatacenterSimple(simulation, hostList, allocationPolicy);
        dc.enableMigrations().setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    private Host createHost(final int numberOfPes, final long mipsByPe) {
        final List<Pe> peList = createPeList(numberOfPes, mipsByPe);
        final Host host = new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
        host.setPowerModel(new PowerModelLinear(1000, 0.7))
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    private List<Pe> createPeList(final int numberOfPEs, final long mips) {
        final List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }
}
