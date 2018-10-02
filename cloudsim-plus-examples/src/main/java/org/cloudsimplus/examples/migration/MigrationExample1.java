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
package org.cloudsimplus.examples.migration;

import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationBestFitStaticThreshold;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationStaticThreshold;
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
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.HostHistoryTableBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An example showing how to create 1 Datacenter with: 5 hosts
 * with increasing number of PEs (starting at 4 PEs for the 1st host); 3 VMs with 2 PEs each one;
 * and 1 cloudlet by VM, each one having the same number of PEs from its VM.
 *
 *
 * <p>The example then performs VM migration using
 * a {@link VmAllocationPolicyMigrationBestFitStaticThreshold}.
 * Such a policy migrates VMs based on
 * a static host CPU utilization threshold.
 * The VmAllocationPolicy used in this example ignores power usage of Hosts.
 * This way, it isn't required to set a PowerModel for Hosts.</p>
 *
 * <p>According to the allocation policy, VM 0 will be allocated to Host 0.
 * Since Host 0 has just 4 PEs, allocating a second VM into it
 * would cause overload.
 * Each cloudlet will start using 80% of its VM CPU.
 * As the VM 0 will run one Cloudlet and requires just 2 PEs from Host 0 (which has 4 PEs),
 * the initial Host CPU usage will be just 40% (1 VM using 80% of 2 PEs from a total of 4 Host PEs = 0.8*2 / 4).
 *
 * Allocating a second VM into Host 0 would double the Host CPU utilization,
 * overreaching its upper utilization threshold (defined as 70%).
 * This way, VMs 1 and 2 are allocated to Host 1 which has 5 PEs.
 * </p>
 *
 * <p>The {@link VmAllocationPolicyMigrationBestFitStaticThreshold}
 * allows the definition of static under and over CPU utilization thresholds to
 * enable VM migration.
 * The example uses a {@link UtilizationModelDynamic} to define that the CPU usage of cloudlets
 * increases along the simulation time.
 * The first 2 Cloudlets all start with a usage of 80% of CPU,
 * that increases along the time (see {@link #CLOUDLET_CPU_INCREMENT_PER_SECOND}).
 * The third Cloudlet starts a a lower CPU usage and increases in the same way.
 * </p>
 *
 * <p>Some constants are used to create simulation objects such as
 * {@link  DatacenterSimple}, {@link  Host} and {@link  Vm}.
 * The values of these constants were careful and accordingly chosen to allow
 * migration of VMs due to either under and overloaded hosts and
 * to allow one developer to know exactly how the simulation will run
 * and what will be the final results.
 * Several values impact the simulation results, such as
 * hosts CPU capacity and number of PEs, VMs and cloudlets requirements
 * and even VM bandwidth (which defines the VM migration time).</p>
 *
 * <p>This way, if you want to change these values, you must
 * define new appropriated ones to allow the simulation
 * to run correctly.</p>
 *
 * <p>Realize that the Host State History is just collected
 * if {@link Host#isStateHistoryEnabled() history is enabled}
 * by calling {@link Host#enableStateHistory()}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @todo Verify if inter-datacenter VM migration is working by default using the DatacenterBroker class.
 */
public final class MigrationExample1 {
    /**
     * @see Datacenter#getSchedulingInterval()
     */
    private static final int  SCHEDULING_INTERVAL = 1;
    private static final int  HOSTS = 5;
    private static final int  VMS = 3;
    private static final int  HOST_MIPS = 1000; //for each PE
    private static final int  HOST_INITIAL_PES = 4;
    private static final long HOST_RAM = 500000; //host memory (MB)
    private static final long HOST_STORAGE = 1000000; //host storage

    /**
     * The time spent during VM migration depend on the
     * bandwidth of the target Host.
     * By default, a {@link Datacenter}
     * uses only 50% of the BW to migrate VMs, while the
     * remaining capacity is used for VM communication.
     * This can be changed by calling
     * {@link DatacenterSimple#setBandwidthPercentForMigration(double)}.
     *
     * <p>The 16000 Mb/s is the same as 2000 MB/s. Since just half of this capacity
     * is used for VM migration, only 1000 MB/s will be available for this process.
     * The time that takes to migrate a Vm depend on the VM RAM capacity.
     * Since VMs in this example are created with 2000 MB of RAM, any migration
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
    private static final double CLOUDLET_CPU_INCREMENT_PER_SECOND = 0.04;

    /**
     * List of all created VMs.
     */
    private final List<Vm> vmList = new ArrayList<>();

    private CloudSim simulation;
    private VmAllocationPolicyMigrationStaticThreshold allocationPolicy;
    private List<Host> hostList;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new MigrationExample1();
    }

    public MigrationExample1(){
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
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
            Comparator.comparingLong((Cloudlet c) -> c.getVm().getHost().getId())
                      .thenComparingLong(c -> c.getVm().getId()));
        new CloudletsTableBuilder(finishedList).build();
        System.out.println("\nHosts CPU usage History (when the allocated MIPS is lower than the requested, it is due to VM migration overhead)");

        hostList.stream().filter(h -> h.getId() <= 2).forEach(this::printHostHistory);
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void printHostHistory(Host host) {
        new HostHistoryTableBuilder(host).setTitle(host.toString()).build();
    }

    public void createAndSubmitCloudlets(DatacenterBroker broker) {
        final List<Cloudlet> list = new ArrayList<>(VMS -1);
        Cloudlet cloudlet = Cloudlet.NULL;
        UtilizationModelDynamic um = createCpuUtilizationModel(CLOUDLET_INITIAL_CPU_PERCENTAGE, 1);
        for(Vm vm: vmList){
            cloudlet = createCloudlet(vm, broker, um);
            list.add(cloudlet);
        }

        //Changes the CPU usage of the last cloudlet to start at a lower value and increase dynamically up to 100%
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
        final UtilizationModel utilizationModelFull = new UtilizationModelFull();

        final Cloudlet cloudlet =
            new CloudletSimple(CLOUDLET_LENGHT, (int)vm.getNumberOfPes())
                .setFileSize(CLOUDLET_FILESIZE)
                .setOutputSize(CLOUDLET_OUTPUTSIZE)
                .setUtilizationModelRam(utilizationModelFull)
                .setUtilizationModelBw(utilizationModelFull)
                .setUtilizationModelCpu(cpuUtilizationModel);
        broker.bindCloudletToVm(cloudlet, vm);

        return cloudlet;
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        List<Vm> list = new ArrayList<>(VMS);
        for(int i = 0; i < VMS; i++){
            Vm vm = createVm(broker, VM_PES);
            list.add(vm);
        }

        vmList.addAll(list);
        broker.submitVmList(list);
    }

    public Vm createVm(DatacenterBroker broker, int pes) {
        Vm vm = new VmSimple(VM_MIPS, pes);
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
    private double getCpuUsageIncrement(final UtilizationModelDynamic um){
        return um.getUtilization() + um.getTimeSpan()*CLOUDLET_CPU_INCREMENT_PER_SECOND;
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
            Host host = createHost(pes, HOST_MIPS);
            hostList.add(host);
        }
        System.out.println();

        /**
         * Sets an upper utilization threshold higher than the
         * {@link #HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION}
         * to enable placing VMs which will use more CPU than
         * defined by the value in the mentioned constant.
         * After VMs are all submitted to Hosts, the threshold is changed
         * to the value of the constant.
         * This is used to  place VMs into a Host which will
         * become overloaded in order to trigger the migration.
         */
        this.allocationPolicy =
            new VmAllocationPolicyMigrationBestFitStaticThreshold(
                new PowerVmSelectionPolicyMinimumUtilization(),
                HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION+0.2);

        DatacenterSimple dc = new DatacenterSimple(simulation, hostList, allocationPolicy);
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    public Host createHost(int numberOfPes, long mipsByPe) {
            List<Pe> peList = createPeList(numberOfPes, mipsByPe);
            Host host =
                new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
            host
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
            host.enableStateHistory();
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
