/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationWorstFitStaticThreshold;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.power.PowerVm;

/**
 * This simple example shows how to use a fault injector in the host.
 *
 * @author raysaoliveira
 */
public final class HostFaultInjectionExample {

    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;
    private static final double DATACENTER_COST_PER_CPU = 3.0;
    private static final double DATACENTER_COST_PER_RAM = 0.05;
    private static final double DATACENTER_COST_PER_STORAGE = 0.001;
    private static final double DATACENTER_COST_PER_BW = 0.0;

    private static final int  HOST_MIPS_BY_PE = 1000;
    private static final int  HOST_PES = 4;
    private static final long HOST_RAM = 500000; //host memory (MEGABYTE)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;

    /**
     * The percentage of host CPU usage that trigger VM migration due to over
     * utilization (in scale from 0 to 1, where 1 is 100%).
     */
    private static final double HOST_USAGE_THRESHOLD_VM_MIGRATION = 0.5;

    private static final int  VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MEGABYTE)
    private static final int  VM_RAM = 10000; //vm memory (MEGABYTE)
    private static final long VM_BW = 100000;
    private static final int  VM_PES = 2; //number of cpus
    
    private static final int  CLOUDLET_PES = 2; 
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
    private static final double CLOUDLET_INITIAL_CPU_USAGE_PERCENT = 0.8;

    /**
     * Defines the speed (in percentage) that CPU usage of a cloudlet will
     * increase during the simulation tie. (in scale from 0 to 1, where 1 is
     * 100%).
     *
     * @see #createAndSubmitCloudletsWithDynamicCpuUtilization(double, double,
     * Vm, DatacenterBroker)
     */
    public static final double CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND = 0.05;

    private static final int HOSTS = 3;
    private static final int VMS = HOSTS;
    private static final int CLOUDLETS_BY_VM = 1;

    private final List<Vm> vmlist = new ArrayList<>();
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new HostFaultInjectionExample();
    }

    public HostFaultInjectionExample() {
        Log.printConcatLine("Starting ", getClass().getSimpleName(), "...");

        simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
        createFaultInjectionForHosts(datacenter0);

        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        createAndSubmitVms(broker);

        createAndSubmitCloudlets(broker);

        simulation.start();

        new CloudletsTableBuilder(broker.getCloudletsFinishedList()).build();

        Log.printConcatLine(getClass().getSimpleName(), " finished!");
    }

    public void createAndSubmitCloudlets(DatacenterBroker broker) {
        double initialCloudletCpuUtilizationPercentage = CLOUDLET_INITIAL_CPU_USAGE_PERCENT;
        final int numberOfCloudlets = VMS - 1;
        for (int i = 0; i < numberOfCloudlets; i++) {
            createAndSubmitCloudletsWithStaticCpuUtilization(
                    initialCloudletCpuUtilizationPercentage, vmlist.get(i), broker);
            initialCloudletCpuUtilizationPercentage += 0.15;
        }
        //Create one last cloudlet which CPU usage increases dynamically
        Vm lastVm = vmlist.get(vmlist.size() - 1);
        createAndSubmitCloudletsWithDynamicCpuUtilization(0.2, 1, lastVm, broker);
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        for (int i = 0; i < VMS; i++) {
            PowerVm vm = createVm(broker);
            vmlist.add(vm);
        }
        broker.submitVmList(vmlist);
    }

    /**
     *
     * @param broker
     * @return
     *
     * @todo @author manoelcampos The use of other CloudletScheduler instead of
     * CloudletSchedulerDynamicWorkload makes the Host CPU usage not be updated
     * (and maybe VM CPU usage too).
     */
    public PowerVm createVm(DatacenterBroker broker) {
        PowerVm vm = new PowerVm(vmlist.size(), VM_MIPS, VM_PES);
        vm.setSchedulingInterval(1)
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setBroker(broker)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());

        Log.printConcatLine(
                "#Requested creation of VM ", vm.getId(), " with ", VM_MIPS, " MIPS x ", VM_PES);
        return vm;
    }

    /**
     * Creates the number of Cloudlets defined in
     * {@link #CLOUDLETS_BY_VM} and submits them to the
     * given broker.
     *
     * @param cloudletInitialCpuUsagePercent the percentage of CPU utilization
     * that created Cloudlets will use when they start to execute. If this value
     * is greater than 1 (100%), it will be changed to 1.
     * @param maxCloudletCpuUtilizationPercentage the maximum percentage of CPU
     * utilization that created Cloudlets are allowed to use. If this value is
     * greater than 1 (100%), it will be changed to 1.
     * @param hostingVm the VM that will run the Cloudlets
     * @param broker the broker that the created Cloudlets belong to
     * @param progressiveCpuUsage if the CPU usage of created Cloudlets have to
     * change along the execution time or not, according to the
     * {@link #getCpuUtilizationIncrement(org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic)}
     * @return the List of created Cloudlets
     */
    public List<Cloudlet> createAndSubmitCloudlets(
            double cloudletInitialCpuUsagePercent,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker,
            boolean progressiveCpuUsage) {
        cloudletInitialCpuUsagePercent = Math.min(cloudletInitialCpuUsagePercent, 1);
        maxCloudletCpuUtilizationPercentage = Math.min(maxCloudletCpuUtilizationPercentage, 1);
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS_BY_VM);
        UtilizationModel utilizationModelFull = new UtilizationModelFull();
        int cloudletId;
        for (int i = 0; i < CLOUDLETS_BY_VM; i++) {
            cloudletId = hostingVm.getId() + i;
            UtilizationModelDynamic cpuUtilizationModel;
            if (progressiveCpuUsage) {
                cpuUtilizationModel
                        = new UtilizationModelDynamic(cloudletInitialCpuUsagePercent)
                                .setUtilizationUpdateFunction(this::getCpuUtilizationIncrement);
            } else {
                cpuUtilizationModel = new UtilizationModelDynamic(cloudletInitialCpuUsagePercent);
            }
            cpuUtilizationModel.setMaxResourceUtilization(maxCloudletCpuUtilizationPercentage);

            Cloudlet c
                    = new CloudletSimple(
                            cloudletId, CLOUDLET_LENGHT, CLOUDLET_PES)
                            .setFileSize(CLOUDLET_FILESIZE)
                            .setOutputSize(CLOUDLET_OUTPUTSIZE)
                            .setUtilizationModelCpu(cpuUtilizationModel)
                            .setUtilizationModelRam(utilizationModelFull)
                            .setUtilizationModelBw(utilizationModelFull);
            c.setBroker(broker);
            list.add(c);
        }

        broker.submitCloudletList(list);
        for (Cloudlet c : list) {
            broker.bindCloudletToVm(c, hostingVm);
        }

        return list;
    }

    /**
     * Increments the CPU resource utilization, that is defined in percentage
     * values.
     *
     * @return the new resource utilization after the increment
     */
    private double getCpuUtilizationIncrement(UtilizationModelDynamic um) {
        return um.getUtilization() + um.getTimeSpan() * CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND;
    }

    public List<Cloudlet> createAndSubmitCloudletsWithDynamicCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                maxCloudletCpuUtilizationPercentage, hostingVm, broker, true);
    }

    public List<Cloudlet> createAndSubmitCloudletsWithStaticCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                initialCloudletCpuUtilizationPercentage,
                hostingVm, broker, false);
    }

    private Datacenter createDatacenter() {
        ArrayList<PowerHost> hostList = new ArrayList<>();
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost(i, HOST_PES, HOST_MIPS_BY_PE));
            Log.printConcatLine("#Created host ", i, " with ", HOST_MIPS_BY_PE, " mips x ", HOST_PES);
        }
        Log.printLine();

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                        .setCostPerSecond(DATACENTER_COST_PER_CPU)
                        .setCostPerMem(DATACENTER_COST_PER_RAM)
                        .setCostPerStorage(DATACENTER_COST_PER_STORAGE)
                        .setCostPerBw(DATACENTER_COST_PER_BW);

        //@todo nao está sendo usado por enquanto, apenas para evitar migração
        PowerVmAllocationPolicyMigrationWorstFitStaticThreshold allocationPolicy
                = new PowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
                        new PowerVmSelectionPolicyMinimumUtilization(),
                        HOST_USAGE_THRESHOLD_VM_MIGRATION);

        PowerDatacenter dc = new PowerDatacenter(simulation, characteristics, new VmAllocationPolicySimple());
        dc
          .setMigrationsEnabled(false)
          .setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS)
          .setLog(false);
        return dc;
    }

    /**
     *
     * @param id
     * @param numberOfPes
     * @param mipsByPe
     * @return
     *
     * @todo @author manoelcampos Using the {@link VmSchedulerSpaceShared} its
     * getting NullPointerException, probably due to lack of CPU for all VMs. It
     * has to be created an IT test to check this problem.
     *
     * @todo @author manoelcampos The method
     * {@link DatacenterBroker#getCloudletsFinishedList()} returns an empty list
     * when using null     {@link PowerDatacenter},
     * {@link PowerHost} and {@link PowerVm}.
     */
    public PowerHost createHost(int id, int numberOfPes, long mipsByPe) {
        List<Pe> peList = createPeList(numberOfPes, mipsByPe);
        PowerHost host
                = new PowerHostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
        host.setPowerModel(new PowerModelLinear(1000, 0.7))
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    public List<Pe> createPeList(int numberOfPEs, long mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter0
     */
    private void createFaultInjectionForHosts(Datacenter datacenter0) {
        //Inject Fault
        //final long seed = System.currentTimeMillis();
        final long seed = 11;
        PoissonProcess poisson = new PoissonProcess(0.2, seed);

        UniformDistr failurePesRand = new UniformDistr(seed);
        for (int i = 0; i < datacenter0.getHostList().size(); i++) {
            for (PowerHost host: datacenter0.<PowerHost>getHostList()) {
                if (poisson.haveKEventsHappened()) {
                    UniformDistr delayForFailureOfHostRandom = new UniformDistr(1, 10, seed + i);

                    //create a new intance of fault and start it.
                    HostFaultInjection fault = new HostFaultInjection(host);
                    fault.setNumberOfFailedPesRandom(failurePesRand);
                    fault.setDelayForFailureOfHostRandom(delayForFailureOfHostRandom);
                    Log.printFormattedLine("\tFault Injection created for Host %d.", host.getId());
                }
                i++;
            }
        }
    }

}
