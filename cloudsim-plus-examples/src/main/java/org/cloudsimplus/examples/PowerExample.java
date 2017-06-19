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
package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
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
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A example showing how to use the power module to
 * compute power consumption of Hosts.
 * Realize that for this goal, you must
 * use all power-related objects such as
 * {@link PowerDatacenter}, {@link PowerHost},
 * {@link PowerVm} and a {@link PowerModel}
 * for each Host.
 *
 * <p>It creates the number of cloudlets defined in
 * {@link #CLOUDLETS}. All cloudlets will required 100% of PEs they are using all the time.
 * Half of these cloudlets are created with the length defined by {@link #CLOUDLET_LENGTH}
 * and the other half will have the double of this length.
 * This way, it's possible to see that for the last half of the
 * simulation time, a Host doesn't use the entire CPU capacity,
 * and therefore doesn't consume the maximum power.</p>
 *
 * <p>However, you may notice in this case that the power usage isn't
 * half of the maximum consumption, because there is a minimum
 * amount of power to use, even if the Host is idle,
 * which is defined by {@link #STATIC_POWER_PERCENT}.
 * In the case of the {@link PowerModelLinear},
 * there is a constant power which is computed
 * and added to consumer power when it
 * is lower or equal to the minimum usage percentage.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.4
 */
public class PowerExample {
    /**
     * Defines, between other things, the time intervals
     * to keep Hosts CPU utilization history records.
     */
    private static final int SCHEDULING_INTERVAL = 10;
    private static final int HOSTS = 2;
    private static final int HOST_PES = 8;

    private static final int VMS = 4;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 8;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 50000;
    /**
     * Defines the minimum percentage of power a Host uses,
     * even it it's idle.
     */
    public static final double STATIC_POWER_PERCENT = 0.7;
    /**
     * The max number of watts/second of power a Host uses.
     */
    public static final int MAX_POWER = 1000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<PowerVm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private final List<PowerHostUtilizationHistory> hostList;

    public static void main(String[] args) {
        new PowerExample();
    }

    public PowerExample() {
        simulation = new CloudSim();
        hostList = new ArrayList<>(HOSTS);
        datacenter0 = createPowerDatacenter();
        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createPowerVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();

        new CloudletsTableBuilder(finishedCloudlets).build();
        printHostCpuUtilizationAndPowerConsumption();
    }

    private void printHostCpuUtilizationAndPowerConsumption() {
        System.out.println();
        for (PowerHostUtilizationHistory host : hostList) {
            System.out.printf("Host %4d CPU utilization and power consumption\n", host.getId());
            /*
            Since the utilization history are stored in the reverse chronological order,
            the values are presented in this way.
             */
            final double[] utilizationPercentHistory = host.getUtilizationHistory();
            double totalPower = 0;
            double time = simulation.clock();
            for (int i = 0; i < utilizationPercentHistory.length; i++) {
                final double utilizationPercent = utilizationPercentHistory[i];
                /**
                 * The power consumption is returned in Watts/Second,
                 * but it's measured only the instantaneous consumption for a given time,
                 * according to the time interval defined by {@link #SCHEDULING_INTERVAL} set to the Datacenter.
                 * For instance, for the time interval equal to 10,
                 * It is measured the power consumption for instants, 10, 20 and so on.
                 * That means it's not computed the power consumption for each time interval
                 * of 10 seconds, but the power consumption at the 10th, 20th second and so on.
                 * This way, to get the total power consumed for each 10 seconds interval,
                 * the power consumption is multipled by the time interval.
                */
                final double wattsPerInterval = host.getPowerModel().getPower(utilizationPercent)*SCHEDULING_INTERVAL;
                totalPower += wattsPerInterval;
                System.out.printf("\tTime %6.0f | CPU Utilization %6.2f%% | Power Consumption: %8.2f Watts in %d Seconds\n",
                    time, utilizationPercent*100, wattsPerInterval, SCHEDULING_INTERVAL);
                time -= SCHEDULING_INTERVAL;
            }
            System.out.printf(
                "\t    Total Host %4d Power Consumption in %6.0f seconds: %10.2f Watts (average of %.2f Watts/Second) \n\n",
                host.getId(), simulation.clock(), totalPower, totalPower/simulation.clock());
        }
    }

    /**
     * Creates a {@link PowerDatacenter} and its {@link PowerHost}s.
     */
    private PowerDatacenter createPowerDatacenter() {
        for(int i = 0; i < HOSTS; i++) {
            PowerHostUtilizationHistory host = createPowerHost();
            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);
        final PowerDatacenter dc = new PowerDatacenter(simulation, characteristics, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    private PowerHostUtilizationHistory createPowerHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final PowerModel powerModel = new PowerModelLinear(MAX_POWER, STATIC_POWER_PERCENT);

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        final ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        final ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        final VmScheduler vmScheduler = new VmSchedulerTimeShared();

        final PowerHostUtilizationHistory host = new PowerHostUtilizationHistory(ram, bw, storage, peList);
        host.setPowerModel(powerModel);
        host
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(vmScheduler);
        return host;
    }

    /**
     * Creates a list of VMs.
     */
    private List<PowerVm> createPowerVms() {
        final List<PowerVm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            PowerVm vm = new PowerVm(i, 1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000)
              .setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        UtilizationModel utilization = new UtilizationModelFull();
        for (int i = 0; i < CLOUDLETS; i++) {
            //Sets half of the cloudlets with the defined length and the other half with the double of it
            final long length = i < CLOUDLETS/2 ? CLOUDLET_LENGTH : CLOUDLET_LENGTH*2;
            Cloudlet cloudlet =
                new CloudletSimple(i, length, CLOUDLET_PES)
                    .setFileSize(1024)
                    .setOutputSize(1024)
                    .setUtilizationModel(utilization);
            list.add(cloudlet);
        }

        return list;
    }
}
