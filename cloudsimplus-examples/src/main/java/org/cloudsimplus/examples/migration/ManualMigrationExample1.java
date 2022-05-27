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
package org.cloudsimplus.examples.migration;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigration;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationBestFitStaticThreshold;
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
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.HostHistoryTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An example showing how perform a manual VM migration
 * according to any desired condition, such as
 * when the simulation reaches specific times.
 *
 * <p>This is a manual migration, different when using some
 * {@link VmAllocationPolicyMigration} implementation.
 * Such a policy automatically migrates VMs based on
 * a static host CPU utilization threshold.
 * In this example, such an implementation is not used
 * and therefore, automatic VM migration based on CPU utilization threshold is not triggered.
 * If you want to implement a VM migration policy, you should
 * create a subclass from some of the existing ones.
 * Usually the starting point is the {@link VmAllocationPolicyMigrationAbstract}
 * but concrete implementations such as the {@link VmAllocationPolicyMigrationBestFitStaticThreshold}
 * can be used as an example.
 * </p>
 *
 * <p>VMs are initially placed into Hosts following the
 * {@link VmAllocationPolicyFirstFit} policy, which doesn't implement automatic VM
 * migration based on CPU utilization threshold. We call this VM as "manual migration"
 * exactly because we are not using a {@link VmAllocationPolicyMigration} implementation
 * and the migration is defined by the simulation class itself.</p>
 *
 * <p>When the simulation clock reaches a specific time,
 * an arbitrary VM is migrated to an arbitrary Host.
 * The clock advance is tracked by the {@link #clockTickListener(EventInfo)},
 * that actually fires the manual migration request.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @see MigrationExample1
 * @since CloudSim Plus 5.0.4
 */
public final class ManualMigrationExample1 {
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

    private static final int    VM_MIPS = 1000; //for each PE
    private static final long   VM_SIZE = 1000; //image size (MB)
    private static final int    VM_RAM = 10000; //VM memory (MB)
    private static final double VM_BW = HOST_BW/(double)VMS;
    private static final int    VM_PES = 2;

    private static final long   CLOUDLET_LENGHT = 20000;
    private static final long   CLOUDLET_FILESIZE = 300;
    private static final long   CLOUDLET_OUTPUTSIZE = 300;

    /**
     * List of all created VMs.
     */
    private final List<Vm> vmList = new ArrayList<>();
    private final DatacenterBrokerSimple broker;
    private final Datacenter datacenter0;

    private CloudSim simulation;
    private List<Host> hostList;
    private boolean migrationRequested;
    private int migrationsNumber;

    public static void main(String[] args) {
        new ManualMigrationExample1();
    }

    private ManualMigrationExample1(){
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.datacenter0 = createDatacenter();
        broker = new DatacenterBrokerSimple(simulation);
        createAndSubmitVms(broker);
        createAndSubmitCloudlets(broker);
        simulation.addOnClockTickListener(this::clockTickListener);

        simulation.start();

        final List<Cloudlet> finishedList = broker.getCloudletFinishedList();
        finishedList.sort(
            Comparator.comparingLong((Cloudlet c) -> c.getVm().getHost().getId())
                      .thenComparingLong(c -> c.getVm().getId()));
        new CloudletsTableBuilder(finishedList).build();
        System.out.printf("%nHosts CPU usage History (when the allocated MIPS is lower than the requested, it is due to VM migration overhead)%n");

        hostList.forEach(this::printHostHistory);
        System.out.printf("Number of VM migrations: %d%n", migrationsNumber);
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    /**
     * Event listener which is called every time the simulation clock advances.
     * When the simulation clock reaches 10 seconds, it migrates an arbitrary VM to
     * an arbitrary Host.
     *
     * @param info information about the event happened.
     * @see CloudSim#addOnClockTickListener(EventListener)
     */
    private void clockTickListener(EventInfo info) {
        if(!migrationRequested && info.getTime() >= 10){
            Vm sourceVm = vmList.get(0);
            Host targetHost = hostList.get(hostList.size() - 1);
            System.out.printf("%n# Requesting the migration of %s to %s%n%n", sourceVm, targetHost);
            datacenter0.requestVmMigration(sourceVm, targetHost);
            this.migrationRequested = true;
        }
    }

    /**
     * Prints the state of a Host along the simulation time.
     * <p>Realize that the Host State History is just collected
     * if {@link Host#isStateHistoryEnabled() history is enabled}
     * by calling {@link Host#enableStateHistory()}.</p>
     *
     * @param host
     */
    private void printHostHistory(Host host) {
        final boolean cpuUtilizationNotZero =
            host.getStateHistory()
                .stream()
                .map(HostStateHistoryEntry::percentUsage)
                .anyMatch(cpuUtilization -> cpuUtilization > 0);

        if(cpuUtilizationNotZero) {
            new HostHistoryTableBuilder(host).setTitle(host.toString()).build();
        } else System.out.printf("\t%s CPU was zero all the time%n", host);
    }

    public void createAndSubmitCloudlets(DatacenterBroker broker) {
        final List<Cloudlet> list = new ArrayList<>(VMS);
        for(Vm vm: vmList){
            list.add(createCloudlet(vm, broker));
        }

        broker.submitCloudletList(list);
    }

    /**
     * Creates a Cloudlet.
     *
     * @param vm the VM that will run the Cloudlets
     * @param broker the broker that the created Cloudlets belong to
     * @return the created Cloudlets
     */
    public Cloudlet createCloudlet(Vm vm, DatacenterBroker broker) {
        final Cloudlet cloudlet =
            new CloudletSimple(CLOUDLET_LENGHT, (int)vm.getNumberOfPes())
                .setFileSize(CLOUDLET_FILESIZE)
                .setOutputSize(CLOUDLET_OUTPUTSIZE)
                .setUtilizationModel(new UtilizationModelFull());
        broker.bindCloudletToVm(cloudlet, vm);

        return cloudlet;
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        final List<Vm> list = new ArrayList<>(VMS);
        for(int i = 0; i < VMS; i++){
            list.add(createVm(VM_PES));
        }

        vmList.addAll(list);
        broker.submitVmList(list);

        vmList.forEach(vm -> vm.addOnMigrationStartListener(this::startMigration));
    }

    /**
     * A listener method that is called when a VM migration starts.
     * @param info information about the happened event
     *
     * @see #createAndSubmitVms(DatacenterBroker)
     * @see Vm#addOnMigrationFinishListener(EventListener)
     */
    private void startMigration(final VmHostEventInfo info) {
        final Vm vm = info.getVm();
        final Host targetHost = info.getHost();
        System.out.printf(
            "# %.2f: %s started migrating to %s (you can perform any operation you want here)%n",
            info.getTime(), vm, targetHost);

        migrationsNumber++;
    }

    public Vm createVm(int pes) {
        final Vm vm = new VmSimple(VM_MIPS, pes);
        vm
          .setRam(VM_RAM).setBw((long)VM_BW).setSize(VM_SIZE)
          .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
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
            hostList.add(createHost(pes, HOST_MIPS));
        }
        System.out.println();

        Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicyFirstFit());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    public Host createHost(int numberOfPes, long mipsByPe) {
            List<Pe> peList = createPeList(numberOfPes, mipsByPe);
            Host host = new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
            host.setVmScheduler(new VmSchedulerTimeShared());
            host.enableStateHistory();
            return host;
    }

    public List<Pe> createPeList(int numberOfPEs, long mips) {
        final List<Pe> list = new ArrayList<>(numberOfPEs);
        for(int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips));
        }

        return list;
    }

}
