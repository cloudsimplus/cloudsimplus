/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * An example showing how to create simulation entities (a DatacenterBroker in
 * this example) in run-time using a {@link GlobalBroker global entity manager}.
 */
public class CloudSimExample8 {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static CloudSim simulation;

    /**
     * Executes the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s...", CloudSimExample8.class.getSimpleName());
        // First step: Initialize the CloudSim package. It should be called
        // before creating any entities.
        int num_user = 2;   // number of grid users
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        simulation = new CloudSim(num_user, trace_flag);

        GlobalBroker globalBroker = new GlobalBroker("GlobalBroker");

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim.
        //We need at list one of them to run a CloudSim simulation
        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
        @SuppressWarnings("unused")
        Datacenter datacenter1 = createDatacenter();

        //Third step: Create Broker
        DatacenterBroker broker = createBroker("Broker_0");

        //Fourth step: Create VMs and Cloudlets and send them to broker
        vmList = createVM(broker, 5, 0); //creating 5 vms
        cloudletList = createCloudlet(broker, 10, 0); // creating 10 cloudlets

        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        // Fifth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        newList.addAll(globalBroker.getBroker().getCloudletsFinishedList());

        simulation.stop();

        new CloudletsTableBuilderHelper(newList).build();
        Log.printFormattedLine("%s finished!", CloudSimExample8.class.getSimpleName());
    }

    private static List<Vm> createVM(DatacenterBroker broker, int vms, int idShift) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> list = new ArrayList<>(vms);

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create VMs
        for (int i = 0; i < vms; i++) {
            Vm vm = new VmSimple(idShift+i, mips, pesNumber)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerTimeShared())
                .setBroker(broker);
            list.add(vm);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(DatacenterBroker broker, int cloudlets, int idShift) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new ArrayList<>(cloudlets);

        //cloudlet parameters
        long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(idShift + i, length, pesNumber)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker);
            list.add(cloudlet);
        }

        return list;
    }

    private static Datacenter createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        List<Pe> peList1 = new ArrayList<>();

        int mips = 1000;

        // 3. Create PEs and add these into the list.
        //for a quad-core machine, a list of 4 PEs is required:
        peList1.add(new PeSimple(0, new PeProvisionerSimple(mips)));
        peList1.add(new PeSimple(1, new PeProvisionerSimple(mips)));
        peList1.add(new PeSimple(2, new PeProvisionerSimple(mips)));
        peList1.add(new PeSimple(3, new PeProvisionerSimple(mips)));

        //Another list, for a dual-core machine
        List<Pe> peList2 = new ArrayList<>();

        peList2.add(new PeSimple(0, new PeProvisionerSimple(mips)));
        peList2.add(new PeSimple(1, new PeProvisionerSimple(mips)));

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int hostId = -1;
        long ram = 16384; //host memory (MB)
        long storage = 1000000; //host storage (MB)
        long bw = 10000; //Megabits/s

        Host host1 = new HostSimple(++hostId, storage, peList1)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host1);

        Host host2 = new HostSimple(++hostId, storage, peList2)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host2);

        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        // 6. Finally, we need to create a DatacenterSimple object.
        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    private static DatacenterBroker createBroker(String name) {
        return new DatacenterBrokerSimple(simulation);
    }

    public static class GlobalBroker extends CloudSimEntity {
        private static final int CREATE_BROKER = 0;
        private List<Vm> vmList;
        private List<Cloudlet> cloudletList;
        private DatacenterBroker broker;

        public GlobalBroker(String name) {
            super(simulation);
            setName(name);
        }

        @Override
        public void processEvent(SimEvent ev) {
            switch (ev.getTag()) {
                case CREATE_BROKER:
                    setBroker(createBroker(super.getName() + "_"));

                    //Create VMs and Cloudlets and send them to broker
                    //creating 5 vms
                    setVmList(createVM(getBroker(), 5, 100));
                    // creating 10 cloudlets
                    setCloudletList(createCloudlet(getBroker(), 10, 100));

                    broker.submitVmList(getVmList());
                    broker.submitCloudletList(getCloudletList());

                    simulation.resume();
                break;

                default:
                    Log.printLine(getName() + ": unknown event type");
                break;
            }
        }

        @Override
        public void startEntity() {
            Log.printLine(super.getName() + " is starting...");
            schedule(getId(), 200, CREATE_BROKER);
        }

        @Override
        public void shutdownEntity() {
        }

        public List<Vm> getVmList() {
            return vmList;
        }

        protected void setVmList(List<Vm> vmList) {
            this.vmList = vmList;
        }

        public List<Cloudlet> getCloudletList() {
            return cloudletList;
        }

        protected void setCloudletList(List<Cloudlet> cloudletList) {
            this.cloudletList = cloudletList;
        }

        public DatacenterBroker getBroker() {
            return broker;
        }

        protected void setBroker(DatacenterBroker broker) {
            this.broker = broker;
        }

    }

}
