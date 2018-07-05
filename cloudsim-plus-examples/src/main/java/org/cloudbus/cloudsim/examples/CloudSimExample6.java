/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples;

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
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example creating a simulation with a higher number of VMs and Cloudlets.
 */
public class CloudSimExample6 {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new CloudSimExample6();
    }

    public CloudSimExample6(){
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());

        // First step: Initialize the CloudSim package.
        simulation = new CloudSim();

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
        @SuppressWarnings("unused")
        Datacenter datacenter1 = createDatacenter();

        //Third step: Create Broker
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);

        //Fourth step: Create VMs and Cloudlets and send them to broker
        vmlist = createVms(broker, 20); //creating 20 vms
        cloudletList = createCloudlets(broker, 40); // creating 40 cloudlets

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        // Fifth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletFinishedList();

        new CloudletsTableBuilder(newList).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private List<Vm> createVms(DatacenterBroker broker, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> list = new ArrayList<>(vms);

        //VM Parameters
        long size = 10000; //image size (Megabyte)
        int ram = 512; //vm memory (Megabyte)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create VMs
        for (int i = 0; i < vms; i++) {
            Vm vm = new VmSimple(i, mips, pesNumber)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());

            //for creating a VM with a space shared scheduling policy for cloudlets:
            //vm[i] = VmSimple(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

            list.add(vm);
        }

        return list;
    }

    private List<Cloudlet> createCloudlets(DatacenterBroker broker, int cloudlets) {
        List<Cloudlet> list = new ArrayList<>(cloudlets);

        //cloudlet parameters
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(i, length, pesNumber)
                            .setFileSize(fileSize)
                            .setOutputSize(outputSize)
                            .setUtilizationModel(utilizationModel);
            list.add(cloudlet);
        }

        return list;
    }

    private Datacenter createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        List<Pe> peList1 = new ArrayList<>();

        long mips = 1000;

        // 3. Create PEs and add these into the list.
        //for a quad-core machine, a list of 4 PEs is required:
        for(int i = 0; i < 4; i++)
            peList1.add(new PeSimple(mips, new PeProvisionerSimple()));

        //Another list, for a dual-core machine
        List<Pe> peList2 = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            peList2.add(new PeSimple(mips, new PeProvisionerSimple()));

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int hostId = -1;
        long ram = 2048; //host memory (Megabyte)
        long storage = 1000000; //host storage (Megabyte)
        long bw = 10000; //Megabits/s

        Host host1 = new HostSimple(ram, bw, storage, peList1)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host1);

        Host host2 = new HostSimple(ram, bw, storage, peList2)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host2);



        // 6. Finally, we need to create a DatacenterSimple object.
        DatacenterSimple dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.getCharacteristics()
            .setCostPerSecond(3.0)
            .setCostPerMem(0.05)
            .setCostPerStorage(0.1)
            .setCostPerBw(0.1);
        return dc;
    }

}
