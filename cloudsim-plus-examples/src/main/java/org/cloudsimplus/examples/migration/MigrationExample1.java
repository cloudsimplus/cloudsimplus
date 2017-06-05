
/*
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
package org.cloudsimplus.examples.migration;

import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigration;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationBestFitStaticThreshold;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>An example showing how to create 1 Datacenter with 3 hosts,
 * 1 VM by host and 1 cloudlet by VM and perform VM migration using
 * a custom VmAllocationPolicy. Such a policy migrates VMs based on
 * {@link PowerVmAllocationPolicyMigrationBestFitStaticThreshold
 * static host CPU utilization threshold}. </p>
 *
 * <p>The created {@link PowerVmAllocationPolicyMigrationBestFitStaticThreshold policy}
 * allows the definition of static under and over CPU utilization thresholds to
 * enable VM migration.
 * The example uses a custom UtilizationModel to define CPU usage of cloudlets which
 * {@link UtilizationModelDynamic increases along the simulation time}.</p>
 *
 * It is used some constants to create simulation objects such as
 * {@link  PowerDatacenter}, {@link  PowerHost} and {@link  PowerVm}.
 * The values of these constants were careful and accordingly chosen to allow
 * migration of VMs due to either under and overloaded hosts and
 * to allow one developer to know exactly how the simulation will run
 * and what will be the final results.
 * Several values impact the simulation results, such as
 * hosts CPU capacity and number of PEs, VMs and cloudlets requirements
 * and even Vm bandwidth (that defines the VM migration time).
 *
 * <p>This way, if you want to change these values, you must
 * define new appropriated ones to allow the simulation
 * to run correctly.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public final class MigrationExample1 {
    private static final int    SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;

    private static final int HOSTS = 5;
    private static final int VMS = 3;

    private static final int    HOST_MIPS = 1000; //for each PE

    private static final int    HOST_INITIAL_PES = 4;
    private static final long   HOST_RAM = 500000; //host memory (MB)
    private static final long   HOST_STORAGE = 1000000; //host storage

    /**
     * The time spent during VM migration depend on the
     * bandwidth of the target Host.
     * By default, a {@link PowerVmAllocationPolicyMigration}
     * uses only 50% of the BW to migrate VMs, while the
     * remaining capacity is used for VM communication.
     * This can be changed by calling
     * {@link PowerDatacenter#setBandwidthForMigrationPercent(double)}.
     *
     * <p>The 16000 Mb/s is equal to 2000 MB/s. Since just half of this capacity
     * is used for VM migration, only 1000 MB/s will be available for this process.
     * The time that take to migrate a Vm depend on the VM RAM capacity.
     * Since VMs in this example are creates with 2000 MB, any migration
     * will take 2 seconds to finish, as can be seen in the logs.
     */
    private static final long   HOST_BW = 16000L; //Mb/s

    /**
     * The percentage of host CPU usage that trigger VM migration
     * due to over utilization (in scale from 0 to 1, where 1 is 100%).
     */
    private static final double HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION = 0.7;

    private static final int    VM_MIPS = 1000; //for each PE
    private static final long   VM_SIZE = 1000; //image size (MB)
    private static final int    VM_RAM = 10000; //VM memory (MB)
    private static final double VM_BW = HOST_BW/(double)VMS;
    private static final int    VM_PES = 2;

    private static final long   CLOUDLET_LENGHT = 20000;
    private static final long   CLOUDLET_FILESIZE = 300;
    private static final long   CLOUDLET_OUTPUTSIZE = 300;

    /**
     * The percentage of CPU that a cloudlet will use when
     * it starts executing (in scale from 0 to 1, where 1 is 100%).
     * For each cloudlet create, this value is used
     * as a base to define CPU usage.
     * @see #createAndSubmitCloudlets(DatacenterBroker)
     */
    private static final double CLOUDLET_INITIAL_CPU_PERCENTAGE = 0.8;

    /**
     * Defines the speed (in percentage) that CPU usage of a cloudlet
     * will increase during the simulation execution.
     * (in scale from 0 to 1, where 1 is 100%).
     * @see #createCpuUtilizationModel(double, double)
     */
    private static final double CLOUDLET_CPU_INCREMENT_PER_SECOND = 0.1;

    /**
     * List of all created VMs.
     */
    private final List<Vm> vmList = new ArrayList<>();

    private CloudSim simulation;
    private PowerVmAllocationPolicyMigrationBestFitStaticThreshold allocationPolicy;
    private List<PowerHostUtilizationHistory> hostList;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new MigrationExample1();
    }

    public MigrationExample1(){
        Log.printConcatLine("Starting ", getClass().getSimpleName(), "...");
        simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
        datacenter0.setLog(false);
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        createAndSubmitVms(broker);
        createAndSubmitCloudlets(broker);

        /*
        After all VMs are created, sets the allocation policy to the default value
        so that some Hosts will be overloaded with the placed VMs and migration will be fired.
        */
        broker.addOneTimeOnVmsCreatedListener(evt -> allocationPolicy.setOverUtilizationThreshold(HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION));

        simulation.start();

        final List<Cloudlet> finishedList = broker.getCloudletFinishedList();
        finishedList.sort(
            Comparator.comparingInt((Cloudlet c) -> c.getVm().getHost().getId())
                      .thenComparingInt(c -> c.getVm().getId()));
        new CloudletsTableBuilder(finishedList).build();
        System.out.println("\nHosts CPU usage History (when the allocated MIPS is lower than the requested, it is due to VM migration overhead)");

        hostList.stream().filter(h -> h.getId() <= 2).forEach(this::printHostHistory);
        Log.printConcatLine(getClass().getSimpleName(), " finished!");
    }

    private void printHostHistory(PowerHost h) {
        System.out.printf("Host: %d\n", h.getId());
        System.out.println("------------------------------------------------------------------------------------------");
        h.getStateHistory().stream().forEach(System.out::print);
        System.out.println();
    }

    public void createAndSubmitCloudlets(DatacenterBroker broker) {
        double initialCloudletCpuUtilizationPercentage = CLOUDLET_INITIAL_CPU_PERCENTAGE;
        final List<Cloudlet> list = new ArrayList<>(VMS -1);
        Cloudlet cloudlet = Cloudlet.NULL;
        int id = 0;
        UtilizationModelDynamic um = createCpuUtilizationModel(initialCloudletCpuUtilizationPercentage, 1);
        for(Vm vm: vmList){
            cloudlet = createCloudlet(vm, broker, um);
            list.add(cloudlet);
        }

        //Changes the CPU usage of the last cloudlet to increase dynamically
        cloudlet.setUtilizationModelCpu(createCpuUtilizationModel(0.2, 1));

        broker.submitCloudletList(list);
    }

    /**
     * Creates a Cloudlet.
     *
     * @param vm the VM that will run the Cloudlets
     * @param broker the broker that the created Cloudlets belong to
     * @param cpuUtilizationModel the CPU UtilizationModel for the Cloudlet
     * @return the created Cloudlets
     */
    public Cloudlet createCloudlet(Vm vm, DatacenterBroker broker, UtilizationModel cpuUtilizationModel) {
        UtilizationModel utilizationModelFull = new UtilizationModelFull();
        final Cloudlet cloudlet =
            new CloudletSimple(CLOUDLET_LENGHT, (int)vm.getNumberOfPes())
                .setFileSize(CLOUDLET_FILESIZE)
                .setOutputSize(CLOUDLET_OUTPUTSIZE)
                .setUtilizationModelCpu(cpuUtilizationModel)
                .setUtilizationModelRam(utilizationModelFull)
                .setUtilizationModelBw(utilizationModelFull);
        broker.bindCloudletToVm(cloudlet, vm);
        return cloudlet;
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        List<Vm> list = new ArrayList<>(VMS);
        for(int i = 0; i < VMS; i++){
            PowerVm vm = createVm(broker, VM_PES);
            list.add(vm);
        }

        vmList.addAll(list);
        broker.submitVmList(list);
    }

    public PowerVm createVm(DatacenterBroker broker, int pes) {
        PowerVm vm = new PowerVm(VM_MIPS, pes);
        vm
          .setRam(VM_RAM).setBw((long)VM_BW).setSize(VM_SIZE)
          .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates a CPU UtilizationModel for a Cloudlet
     * which will always use the given initial CPU usage percentage.
     * This way, the usage will not change over the time,
     * since the max usage will be the initial usage.
     *
     * @param initialCpuUsagePercent the percentage of CPU utilization
     * that created Cloudlets will use when they start to execute.
     * If this value is greater than 1 (100%), it will be changed to 1.
     * @return
     */
    private UtilizationModelDynamic createCpuUtilizationModel(double initialCpuUsagePercent) {
        return createCpuUtilizationModel(initialCpuUsagePercent, initialCpuUsagePercent);
    }

    /**
     * Creates a CPU UtilizationModel for a Cloudlet.
     * If the initial usage is lower than the max usage, the usage will
     * be dynamically incremented along the time, according to the
     * {@link #getCpuUsageIncrement(org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic)}
     * function. Otherwise, the CPU usage will be static, according to the
     * defined initial usage.
     *
     * @param initialCpuUsagePercent the percentage of CPU utilization
     * that created Cloudlets will use when they start to execute.
     * If this value is greater than 1 (100%), it will be changed to 1.
     * @param maxCpuUsagePercentage the maximum percentage of
     * CPU utilization that created Cloudlets are allowed to use.
     * If this value is greater than 1 (100%), it will be changed to 1.
     * It must be equal or greater than the initial CPU usage.
     * @return
     */
    private UtilizationModelDynamic createCpuUtilizationModel(double initialCpuUsagePercent, double maxCpuUsagePercentage) {
        if(maxCpuUsagePercentage < initialCpuUsagePercent){
            throw new IllegalArgumentException("Max CPU usage must be equal or greater than the initial CPU usage.");
        }

        initialCpuUsagePercent = Math.min(initialCpuUsagePercent, 1);
        maxCpuUsagePercentage = Math.min(maxCpuUsagePercentage, 1);
        UtilizationModelDynamic um;
        if (initialCpuUsagePercent < maxCpuUsagePercentage) {
            um = new UtilizationModelDynamic(initialCpuUsagePercent)
                        .setUtilizationUpdateFunction(this::getCpuUsageIncrement);
        } else {
            um = new UtilizationModelDynamic(initialCpuUsagePercent);
        }

        um.setMaxResourceUtilization(maxCpuUsagePercentage);
        return um;
    }

    /**
     * Increments the CPU resource utilization, that is defined in percentage values.
     * @return the new resource utilization after the increment
     */
    private double getCpuUsageIncrement(UtilizationModelDynamic um){
        return  um.getUtilization() + um.getTimeSpan()* CLOUDLET_CPU_INCREMENT_PER_SECOND;
    }

    /**
     * Creates a Datacenter with number of Hosts defined by {@link #HOSTS},
     * but only some of these Hosts will be active (powered on) initially.
     *
     * @return
     */
    private Datacenter createDatacenter() {
        this.hostList = new ArrayList<>();
        for(int i = 0; i < HOSTS; i++){
            final int pes = HOST_INITIAL_PES + i;
            PowerHostUtilizationHistory host = createHost(pes, HOST_MIPS);
            hostList.add(host);
            Log.printFormattedLine("#Created host %d with %d MIPS x %2d PEs. Powered on: %s", i, HOST_MIPS, pes, host.isActive());
        }
        Log.printLine();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);

        /*Sets an upper utilization threshold that is higher than the defined
        * value to enable placing VMs that will use more PEs than
        * the defined by the default migration threshold.
        * After VMs are all submitted to Hosts, the threshold is changed
        * to the default value.
        * This is used to  place VMs into a Host that will
        * become overloaded, what will trigger the migration.
        * */
        this.allocationPolicy =
            new PowerVmAllocationPolicyMigrationBestFitStaticThreshold(
                new PowerVmSelectionPolicyMinimumUtilization(),
                HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION+0.2);

        PowerDatacenter dc = new PowerDatacenter(simulation, characteristics, allocationPolicy);
        dc.setMigrationsEnabled(true)
          .setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS)
          .setLog(true);
        return dc;
    }

    public PowerHostUtilizationHistory createHost(int numberOfPes, long mipsByPe) {
            List<Pe> peList = createPeList(numberOfPes, mipsByPe);
            PowerHostUtilizationHistory host =
                new PowerHostUtilizationHistory(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
            host
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
            return host;
    }

    public List<Pe> createPeList(int numberOfPEs, long mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for(int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }
}
