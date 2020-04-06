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
package org.cloudsimplus.examples;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Identifiable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.selectionpolicies.VmSelectionPolicyRandomSelection;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An example showing how CloudSim Plus
 * is flexible in managing logs of different entities.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 5.3.0
 * @see #configureLogs()
 */
public class LoggingExample {
    private static final int SCHEDULING_INTERVAL_SECS = 10;
    private static final double HOST_OVER_UTILIZATION_MIGRATION_THRESHOLD = 0.7;

    private static final int[] HOST_PES = {8, 8, 4, 4, 4};

    private static final int VMS = 5;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 2;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 50_000;

    private static final int HOST_MIPS =       10_000;
    private static final int HOST_RAM =         8_000;
    private static final int HOST_BW =         10_000;
    private static final int HOST_STORAGE = 1_000_000;

    private static final int VM_MIPS =          1_000;
    private static final int VM_RAM =           2_000;
    private static final int VM_BW =            2_000;
    private static final int VM_SIZE =         10_000;
    private static final boolean DISABLE_MIGRATIONS = false;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new LoggingExample();
    }

    private LoggingExample() {
        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        broker0.submitVmList(vmList);

        //Creates Cloudlets that uses 5% of all resources all the time
        cloudletList = createCloudlets(new UtilizationModelDynamic(0.05));

        //Creates Cloudlets that increase resource utilization along the time
        cloudletList.addAll(createCloudlets(createDynamicUtilizationModel()));

        //Creates Cloudlets that increase resource utilization along the time,
        //but are submitted just after some delay.
        cloudletList.addAll(createCloudlets(createDynamicUtilizationModel(), 10));

        Comparator<Cloudlet> hostComparator = Comparator.comparingLong(c -> c.getVm().getHost().getId());
        cloudletList.sort(hostComparator.thenComparingLong(c -> c.getVm().getId()).thenComparingLong(Identifiable::getId));
        broker0.submitCloudletList(cloudletList);

        configureLogs();
        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingLong(Cloudlet::getId));
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    private void configureLogs() {
        //Enables just some level of log messages for all entities.
        Log.setLevel(Level.INFO);

        //Enable different log levels for specific classes of objects
        Log.setLevel(DatacenterBroker.LOGGER, Level.ERROR);
        Log.setLevel(Datacenter.LOGGER, Level.WARN);
        Log.setLevel(VmAllocationPolicy.LOGGER, Level.OFF);
        Log.setLevel(CloudletScheduler.LOGGER, Level.WARN);
    }

    /**
     * Creates a UtilizationModel that starts the resource utilization at a given percentage
     * and keeps increasing along the time.
     */
    private UtilizationModelDynamic createDynamicUtilizationModel() {
        UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);
        utilizationModel.setUtilizationUpdateFunction(model -> model.getUtilization() + model.getTimeSpan()*0.1);
        return utilizationModel;
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOST_PES.length);
        for (int pes : HOST_PES) {
            Host host = createHost(pes);
            hostList.add(host);
        }

        /*Creates a VmAllocationPolicy that migrates VMs from under/overloaded hosts,
        selecting migrating VMs randomly.*/
        VmAllocationPolicy vmAllocationPolicy =
            new VmAllocationPolicyMigrationStaticThreshold(
                new VmSelectionPolicyRandomSelection(), HOST_OVER_UTILIZATION_MIGRATION_THRESHOLD);
        Datacenter dc = new DatacenterSimple(simulation, hostList, vmAllocationPolicy);
        dc.setSchedulingInterval(SCHEDULING_INTERVAL_SECS);
        if(DISABLE_MIGRATIONS) {
            dc.disableMigrations();
        }
        dc.setHostSearchRetryDelay(60);
        return dc;
    }

    private Host createHost(final int pes) {
        final List<Pe> peList = new ArrayList<>(pes);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < pes; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(HOST_MIPS));
        }

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(VM_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     * @param utilizationModel the UtilizationModel to be used for all Cloudlet resources (CPU, RAM and BW).
     */
    private List<Cloudlet> createCloudlets(final UtilizationModel utilizationModel) {
        return createCloudlets(utilizationModel, 0);
    }

    /**
     * Creates a list of Cloudlets.
     * @param utilizationModel the UtilizationModel to be used for all Cloudlet resources (CPU, RAM and BW).
     * @param submissionDelay the delay to submit Cloudlets to the broker
     */
    private List<Cloudlet> createCloudlets(final UtilizationModel utilizationModel, final double submissionDelay) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSubmissionDelay(submissionDelay);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }
}
