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

import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationMedianAbsoluteDeviation;
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
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.*;

/**
 * An example showing how to create 1 Datacenter with 5 hosts,
 * 3 VMs and 1 cloudlet by VM and perform VM migration using
 * a custom VmAllocationPolicy. Such a policy migrates VMs relying on a
 * {@link VmAllocationPolicyMigrationMedianAbsoluteDeviation
 * dynamic host CPU utilization threshold} based on CPU utilization.
 *
 * <p>This VmAllocationPolicy considers the power usage of Hosts to place VMs.
 * So that, a {@link org.cloudbus.cloudsim.power.models.PowerModel} is being
 * set to every created Host by means of {@code host.setPowerModel(powerModel)}.</p>
 *
 * <p>The example uses a {@link UtilizationModelDynamic} that defines the CPU usage of cloudlets
 *  increases along the simulation time.</p>
 *
 * <p>It is used some constants to create simulation objects such as
 * {@link  DatacenterSimple}, {@link  Host} and {@link  Vm}.
 * The values of these constants were careful and accordingly chosen to allow
 * migration of VMs due to either under and overloaded hosts and
 * to allow one developer to know exactly how the simulation will run
 * and what will be the final results.
 * Several values impact the simulation results, such as
 * hosts CPU capacity and number of PEs, VMs and cloudlets requirements
 * and even VM bandwidth (that defines the VM migration time).</p>
 *
 * <p>This way, if you want to change these values, you must
 * define new appropriated ones to allow the simulation
 * to run correctly.</p>
 *
 * <p>Realize that the Host State History is just collected
 * if {@link Host#isStateHistoryEnabled() history is enabled}
 * by calling {@link Host#enableStateHistory()}.
 * The Host CPU Utilization History also is only computed
 * if VMs utilization history is enabled by calling
 * {@code vm.getUtilizationHistory().enable()}</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public final class MigrationExample2_PowerUsage {
    private static final int SCHEDULING_INTERVAL = 5;

    /** @see Datacenter#setHostSearchRetryDelay(double) */
    private static final int HOST_SEARCH_RETRY_DELAY = 60;


    private static final int HOSTS = 5;
    private static final int VMS = 3;

    private static final int    HOST_MIPS = 1000; //for each PE

    private static final int    HOST_INITIAL_PES = 4;
    private static final long   HOST_RAM = 500000; //host memory (MB)
    private static final long   HOST_STORAGE = 1000000; //host storage

    /**
     * The time spent during VM migration depend on the
     * bandwidth of the target Host.
     * By default, a {@link Datacenter}
     * uses only 50% of the BW to migrate VMs, while the
     * remaining capacity is used for VM communication.
     * This can be changed by calling
     * {@link Datacenter#setBandwidthPercentForMigration(double)}.
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
    private VmAllocationPolicyMigrationMedianAbsoluteDeviation allocationPolicy;
    private List<Host> hostList;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new MigrationExample2_PowerUsage();
    }

    private MigrationExample2_PowerUsage(){
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

        simulation.start();

        final List<Cloudlet> finishedList = broker.getCloudletFinishedList();
        finishedList.sort(
            Comparator.comparingLong((Cloudlet c) -> c.getVm().getHost().getId())
                      .thenComparingLong(c -> c.getVm().getId()));
        new CloudletsTableBuilder(finishedList).build();
        System.out.printf("%n    WHEN A HOST CPU ALLOCATED MIPS IS LOWER THAN THE REQUESTED, IT'S DUE TO VM MIGRATION OVERHEAD)%n%n");

        hostList.stream().forEach(this::printHistory);
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void printHistory(Host host){
        if(printHostStateHistory(host)) {
            printHostCpuUsageAndPowerConsumption(host);
        }
    }

    /**
     * Prints Host state history
     * @param host the host to print information
     * @return true if the Host was powered on during simulation, false otherwise
     */
    private boolean printHostStateHistory(Host host) {
        if(host.getStateHistory().stream().anyMatch(HostStateHistoryEntry::isActive)) {
            System.out.printf("%nHost: %6d State History%n", host.getId());
            System.out.println("-------------------------------------------------------------------------------------------");
            host.getStateHistory().forEach(System.out::print);
            System.out.println();
            return true;
        }
        else System.out.printf("Host: %6d was powered off during all the simulation%n", host.getId());
        return false;
    }

    /**
     * Shows Host CPU utilization history and power consumption.
     * The history is shown in the interval defined by {@link #SCHEDULING_INTERVAL},
     * which is the interval in which simulation is updated and usage data is collected.
     *
     * <p>The Host CPU Utilization History also is only computed
     * if VMs utilization history is enabled by calling
     * {@code vm.getUtilizationHistory().enable()}
     * </p>
     *
     * @param host the Host to print information
     */
    private void printHostCpuUsageAndPowerConsumption(final Host host) {
        System.out.printf("Host: %6d | CPU Usage | Power Consumption in Watt-Second (Ws)%n", host.getId());
        System.out.println("-------------------------------------------------------------------------------------------");
        SortedMap<Double, DoubleSummaryStatistics> utilizationHistory = host.getUtilizationHistory();
        //The total power the Host consumed in the period (in Watt-Sec)
        double totalHostPowerConsumptionWattSec = 0;
        for (Map.Entry<Double, DoubleSummaryStatistics> entry : utilizationHistory.entrySet()) {
            final double time = entry.getKey();
            //The sum of CPU usage of every VM which has run in the Host
            final double hostCpuUsage = entry.getValue().getSum();
            System.out.printf("Time: %6.1f | %9.2f | %.2f%n", time, hostCpuUsage, host.getPowerModel().getPower(hostCpuUsage));
            totalHostPowerConsumptionWattSec += host.getPowerModel().getPower(hostCpuUsage);
        }
        System.out.printf("Total Host power consumption in the period: %.2f Watt-Sec%n", totalHostPowerConsumptionWattSec);
        System.out.println();
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
        vm.getUtilizationHistory().enable();
        return vm;
    }

    /**
     * Creates a CPU UtilizationModel for a Cloudlet.
     * If the initial usage is lower than the max usage, the usage will
     * be dynamically incremented along the time, according to the
     * {@link #getCpuUsageIncrement(UtilizationModelDynamic)}
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
        return  um.getUtilization() + um.getTimeSpan()*CLOUDLET_CPU_INCREMENT_PER_SECOND;
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

        /* Defines the fallback VmAllocationPolicy to be used
        * in case there is not enough host CPU utilization history
        * to compute the Median Absolute Deviation (MAD).
        * The MAD is used to define the dynamic CPU upper utilization threshold
        * of Hosts to migrate VMs. */
        final VmAllocationPolicyMigrationStaticThreshold fallback =
            new VmAllocationPolicyMigrationStaticThreshold(
                new VmSelectionPolicyMinimumUtilization(), HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION);

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
            new VmAllocationPolicyMigrationMedianAbsoluteDeviation(
                new VmSelectionPolicyMinimumUtilization(),
                HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION+0.2, fallback);

        Datacenter dc = new DatacenterSimple(simulation, hostList, allocationPolicy);
        dc.setSchedulingInterval(SCHEDULING_INTERVAL)
          .setHostSearchRetryDelay(HOST_SEARCH_RETRY_DELAY);
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
            host.setPowerModel(new PowerModelLinear(50, 0.3));
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
