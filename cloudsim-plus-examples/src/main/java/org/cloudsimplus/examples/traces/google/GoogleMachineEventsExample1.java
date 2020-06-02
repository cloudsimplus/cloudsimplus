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
package org.cloudsimplus.examples.traces.google;

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
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.TraceReaderAbstract;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.traces.google.GoogleMachineEventsTraceReader;
import org.cloudsimplus.traces.google.MachineEvent;
import org.cloudsimplus.traces.google.MachineEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * An example showing how to create Hosts from a Google Machine Events Trace
 * using a {@link GoogleMachineEventsTraceReader}. The trace is located in
 * resources/workload/google-traces/machine-events-sample-1.csv It has some
 * records that defines the addition and removal of Hosts to the Datacenters.
 *
 * <p>
 * The removal is used to simulate maintenance or failure but the trace doesn't
 * provide information about the cause of the removal. This way, CloudSim Plus
 * considers that any Host removal is due to a failure and injects that failure
 * for the Host at the specified timestamp. The same Host can fail and recover
 * as specified in the trace. CloudSim Plus accordingly process such events. You
 * can confirm in the logs that Host 3 is added and removed multiple times.
 * </p>
 *
 * <p>
 * Check important details at {@link TraceReaderAbstract}. To better understand
 * the structure of trace files, check the google-cluster-data-samples.xlsx
 * spreadsheet inside the docs dir.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 *
 * @TODO {@link MachineEventType#UPDATE} events are not being processed yet.
 */
public class GoogleMachineEventsExample1 {
    private static final String TRACE_FILENAME = "workload/google-traces/machine-events-sample-1.csv";
    private static final int HOST_BW = 10;
    private static final long HOST_STORAGE = 100000;
    private static final double HOST_MIPS = 1000;

    private static final int CLOUDLET_LENGTH = 100000;
    private static final int DATACENTERS_NUMBER = 2;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Datacenter> datacenters;

    public static void main(String[] args) {
        new GoogleMachineEventsExample1();
    }

    private GoogleMachineEventsExample1() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        createDatacenters();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        List<Vm> vmList = createAndSubmitVms(datacenters.get(0));

        createAndSubmitCloudlets(vmList);

        /* Sets a listener method that will be called every time a new Host becomes available during simulation runtime
        * for the second Datacenter*/
        datacenters.get(1).addOnHostAvailableListener(this::onHostAvailableListener);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets)
                .addColumn(5, new TextTableColumn("Host Startup", "Time"), this::getHostStartupTime)
                .build();
    }

    private double getHostStartupTime(final Cloudlet cloudlet) {
        return cloudlet.getVm().getHost().getStartTime();
    }

    /**
     * Method called every time a new Host becomes available during simulation runtime
     * for the second Datacenter. It creates a VM and a Cloudlet, submitting them when there is a new Host available.
     *
     * @param info event information (that includes the simulation time and the new available host)
     */
    private void onHostAvailableListener(HostEventInfo info) {
        final Vm vm = createVm(info.getHost());
        broker0.submitVm(vm);
        broker0.submitCloudlet(createCloudlet(vm));
    }

    /**
     * Creates the Datacenters and Hosts, where Hosts's specification is read from a
     * "machine events" Google Cluster Data trace file
     *
     * <p>
     * A {@link GoogleMachineEventsTraceReader} instance is used to read the file.
     * It requires a {@link Function}
     * that will be called internally to actually create the Hosts.
     * This function is the {@link #createHost(MachineEvent)}.
     * </p>
     */
    private void createDatacenters() {
        datacenters = new ArrayList<>(DATACENTERS_NUMBER);

        final GoogleMachineEventsTraceReader reader = GoogleMachineEventsTraceReader.getInstance(TRACE_FILENAME, this::createHost);
        reader.setMaxRamCapacity(32);
        reader.setMaxCpuCores(10);

        //Creates Datacenters with no hosts.
        for(int i = 0; i < DATACENTERS_NUMBER; i++){
            datacenters.add(new DatacenterSimple(simulation, new VmAllocationPolicySimple()));
        }

        /*Process the trace file and creates the Hosts that the timestamp is defined as zero inside the file.
        * Then, returns the list of immediately created Hosts (for timestamp 0).
        * The second Datacenter that is given as parameter will be used to add the Hosts with timestamp greater than 0.
        * */
        reader.setDatacenterForLaterHosts(datacenters.get(1));
        final List<Host> hostList = new ArrayList<>(reader.process());

        System.out.println();
        System.out.printf("# Created %d Hosts that were immediately available from the Google trace file%n", hostList.size());
        System.out.printf("# %d Hosts will be available later on (according to the trace timestamp)%n", reader.getNumberOfLaterAvailableHosts());
        System.out.printf("# %d Hosts will be removed later on (according to the trace timestamp)%n%n", reader.getNumberOfHostsForRemoval());

        //Finally, the immediately created Hosts are added to the first Datacenter
        datacenters.get(0).addHostList(hostList);
    }

    /**
     * A method that is used to actually create each Host defined in the trace file.
     * The researcher can write his/her own code inside this method to define
     * how he/she wants to create the Hosts based on the trace data.
     *
     * @param event an object containing the trace line read, used to create the Host.
     * @return
     */
    private Host createHost(final MachineEvent event) {
        final Host host = new HostSimple(event.getRam(), HOST_BW, HOST_STORAGE, createPesList(event.getCpuCores()));
        host.setId(event.getMachineId());
        return host;
    }

    private List<Pe> createPesList(final int count) {
        List<Pe> cpuCoresList = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            cpuCoresList.add(new PeSimple(HOST_MIPS, new PeProvisionerSimple()));
        }

        return cpuCoresList;
    }

    /**
     * Creates a list of VMs and Cloudlets in a given Datacenter.
     */
    private List<Vm> createAndSubmitVms(Datacenter datacenter) {
        final List<Vm> list = new ArrayList<>(datacenter.getHostList().size());
        /* Creates 1 VM for each available Host of the datacenter.
        *  Each VM will have the same RAM, BW and Storage of the its Host. */
        for (Host host : datacenter.getHostList()) {
            list.add(createVm(host));
        }

        broker0.submitVmList(list);
        return list;
    }

    /**
     * Creates a VM with the same capacity of a given Host.
     * This way, it will run one VM by Host.
     * @param host
     * @return
     */
    private Vm createVm(final Host host) {
        return new VmSimple(1000, host.getNumberOfPes())
            .setRam(host.getRam().getCapacity()).setBw(HOST_BW).setSize(HOST_STORAGE)
            .setCloudletScheduler(new CloudletSchedulerSpaceShared());
    }

    /**
     * Creates a list of Cloudlets for the given VMs.
     */
    private void createAndSubmitCloudlets(final List<Vm> vmList) {
        final List<Cloudlet> list = new ArrayList<>(vmList.size());
        for (Vm vm : vmList) {
            Cloudlet cloudlet = createCloudlet(vm);
            list.add(cloudlet);
        }

        broker0.submitCloudletList(list);
    }

    private Cloudlet createCloudlet(Vm vm) {
        UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(CLOUDLET_LENGTH, vm.getNumberOfPes())
            .setFileSize(1024)
            .setOutputSize(1024)
            .setUtilizationModel(utilization)
            .setVm(vm);
    }
}
