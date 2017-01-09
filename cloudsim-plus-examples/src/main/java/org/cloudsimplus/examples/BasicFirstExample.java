/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
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
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A minimal but organized, structured and re-usable CloudSim Plus example
 * that even shows good coding practices for creating simulation scenarios.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class BasicFirstExample {
    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;
    private static final int VMS = 2;
    private static final int CLOUDLETS = 10;
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    public static void main(String[] args) {
        new BasicFirstExample();
    }

    public BasicFirstExample() {
        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLETS);

        simulation = new CloudSim();

        createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        createVms();
        createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(finishedCloudlets).build();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for(int h = 0; h < HOSTS; h++) {
            List<Pe> pesList = new ArrayList<>(HOST_PES);
            for (int p = 0; p < HOST_PES; p++) {
                pesList.add(new PeSimple(p, new PeProvisionerSimple(1000)));
            }

            ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple(new Ram(2048));
            ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple(new Bandwidth(10000));
            VmScheduler vmScheduler = new VmSchedulerTimeShared();
            Host host =
                new HostSimple(h, 1000000, pesList)
                    .setRamProvisioner(ramProvisioner)
                    .setBwProvisioner(bwProvisioner)
                    .setVmScheduler(vmScheduler);
            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);
        Datacenter dc0 = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    /**
     * Creates a list of VMs for the {@link #broker0}.
     */
    private void createVms() {
        for (int v = 0; v < VMS; v++) {
            Vm vm =
                new VmSimple(v, 1000, 1)
                    .setRam(512).setBw(1000).setSize(10000).setBroker(broker0)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());

            vmList.add(vm);
        }
    }

    /**
     * Creates a list of Cloudlets for the {@link #broker0}.
     */
    private void createCloudlets() {
        UtilizationModel utilization = new UtilizationModelFull();
        for (int c = 0; c < CLOUDLETS; c++) {
            Cloudlet cloudlet =
                new CloudletSimple(c, 10000, 2)
                    .setFileSize(1024)
                    .setOutputSize(1024)
                    .setUtilizationModel(utilization)
                    .setBroker(broker0);
            cloudletList.add(cloudlet);
        }
    }
}
