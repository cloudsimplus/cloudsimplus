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
package org.cloudsimplus.examples.brokers;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.datacenters.TimeZoned;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.util.Log;

import java.util.*;

/**
 * An example showing how to use the {@link DatacenterBrokerSimple}
 * to select the {@link Datacenter} closest to every submitted {@link Vm},
 * according to their {@link TimeZoned#getTimeZone() TimeZone offset}.
 *
 * <p>Realize that for this feature to work, the {@link DatacenterBroker#setSelectClosestDatacenter(boolean)}
 * must be called to enable it.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.8.0
 */
public class DatacenterSelectionByTimeZoneExample {
    /**
     * Amazon Web Services Regions (just some of them).
     * @see <a href="https://aws.amazon.com/about-aws/global-infrastructure/">AWS Global Infrastructure</a>
     */
    private static final Map<String, Double> DATACENTERS_TIMEZONES = new TreeMap<String, Double>(){{
        put("Oregon",     -7.0);
        put("California", -7.0);
        put("Canada",     -4.0);
        put("São Paulo",  -3.0);
        put("Ireland",     1.0);
        put("London",      1.0);
        put("Paris",       2.0);
        put("Frankfurt",   2.0);
        put("Cape Town",   2.0);
        put("Bahrain",     3.0);
        put("Mumbai",      5.5);
        put("Singapore",   8.0);
        put("Tokyo",       9.0);
        put("Seoul",       9.0);
        put("Sydney",     10.0);
    }};

    private static final int HOSTS = 5;
    private static final int HOST_PES = 8;

    /**
     * The list of timezones where each VM is expected to be placed.
     * This way, the broker will try to place each VM at the closest
     * Datacenter as possible.
     */
    private static final double[] VMS_TIMEZONES = {1, 5, -3, -5, 0, 5.5, 2, 4, 9, 11, -9};

    private static final int VM_PES = 4;

    private static final int CLOUDLETS = VMS_TIMEZONES.length;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private List<Datacenter> datacenterList;
    private long lastHostId;

    public static void main(String[] args) {
        new DatacenterSelectionByTimeZoneExample();
    }

    private DatacenterSelectionByTimeZoneExample() {
        Log.setLevel(Level.WARN);

        simulation = new CloudSim();
        datacenterList = createDatacenters();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();

        /*Enables the selection of the closest datacenter for every VM,
         then submits Vms and Cloudlets.*/
        broker0.setSelectClosestDatacenter(true)
               .submitVmList(vmList)
               .submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingDouble(cl -> cl.getVm().getTimeZone()));

        new CloudletsTableBuilder(finishedCloudlets)
                .addColumn(3, new TextTableColumn("   DC   ", "TimeZone"), this::getDatacenterTimeZone)
                .addColumn(8, new TextTableColumn("VM Expected", " TimeZone "), this::getVmTimeZone)
                .build();
    }

    public String getDatacenterTimeZone(final Cloudlet cloudlet) {
        return TimeZoned.format(cloudlet.getVm().getHost().getDatacenter().getTimeZone());
    }

    public String getVmTimeZone(final Cloudlet cloudlet) {
        return TimeZoned.format(cloudlet.getVm().getTimeZone());
    }

    /**
     * Creates a List of Datacenters, each Datacenter having
     * Hosts with a number of PEs higher than the previous Datacenter.
     * @return
     */
    private List<Datacenter> createDatacenters(){
        final List<Datacenter> list = new ArrayList<>(DATACENTERS_TIMEZONES.size());
        for (Map.Entry<String, Double> entry : DATACENTERS_TIMEZONES.entrySet()) {
            final Datacenter dc = createDatacenter(entry.getValue());
            list.add(dc);
            System.out.printf("Created Datacenter %2d in %15s | %s%n", dc.getId(), entry.getKey(), TimeZoned.format(entry.getValue()));
        }
        System.out.println();

        return list;
    }

    /**
     * Creates a Datacenter in a given timezone.
     * @param timeZone the time zone offset
     */
    private Datacenter createDatacenter(final double timeZone) {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        final Datacenter dc = new DatacenterSimple(simulation, hostList);
        dc.setTimeZone(timeZone);
        return dc;
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        final Host host = new HostSimple(ram, bw, storage, peList);
        host.setId(++lastHostId);
        return host;
    }

    /**
     * Creates a list of VMs, setting the timezone they are expected to be placed.
     * This way, the broker will try to place each VM at the closest
     * Datacenter as possible.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS_TIMEZONES.length);
        for (final double timezone : VMS_TIMEZONES) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000).setTimeZone(timezone);
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }
}
