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
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimEntity;
import org.cloudsimplus.examples.DynamicCloudletsArrival1;
import org.cloudsimplus.examples.DynamicCloudletsArrival2;
import org.cloudsimplus.examples.DynamicCreationOfVmsAndCloudletsExample;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * An example showing how to create simulation entities (a {@link DatacenterBroker} in
 * this example) in run-time (during simulation execution) using a {@link EntityManager}.
 * The {@link EntityManager} is a custom {@link CloudSimEntity} created for this example that dispatches
 * a message to schedule the creation of a {@link DatacenterBroker} with a set of VMs and Cloudlets
 * for a given simulation time.
 * When the requested time arrives, the {@link EntityManager} will be notified to
 * create the requested Broker, including its VMs and Cloudlets.
 *
 * <p>For a example of how to dynamically create VMs and Cloudlets
 * without the need to create a new broker, check the examples below:
 * <ul>
 *  <li>{@link DynamicCloudletsArrival1}</li>
 *  <li>{@link DynamicCloudletsArrival2}</li>
 *  <li>{@link DynamicCreationOfVmsAndCloudletsExample}</li>
 * </ul>
 * </p>
 */
public class CloudSimExample7 {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new CloudSimExample7();
    }

    public CloudSimExample7() {
        Log.printFormattedLine("Starting %s...", getClass().getSimpleName());
        // Initialize the CloudSim library
        simulation = new CloudSim();

        EntityManager entityManager = new EntityManager(simulation);

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim.
        //We need at list one of them to run a CloudSim simulation
        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
        @SuppressWarnings("unused")
        Datacenter datacenter1 = createDatacenter();

        //Third step: Create Broker
        DatacenterBroker staticBroker = createBroker("BrokerStaticallyCreated");

        //Fourth step: Create VMs and Cloudlets and send them to dynamicBroker
        vmList = createVM(staticBroker, 5, 0); //creating 5 vms
        cloudletList = createCloudlet(staticBroker, 10, 0); // creating 10 cloudlets

        staticBroker.submitVmList(vmList);
        staticBroker.submitCloudletList(cloudletList);

        // Fifth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        new CloudletsTableBuilder(staticBroker.getCloudletsFinishedList())
            .setTitle(staticBroker.getName())
            .build();
        new CloudletsTableBuilder(entityManager.getDynamicBroker()
            .getCloudletsFinishedList())
            .setTitle(entityManager.getDynamicBroker().getName())
            .build();
        Log.printFormattedLine("%s finished!", getClass().getSimpleName());
    }

    private List<Vm> createVM(DatacenterBroker broker, int vms, int idShift) {
        //Creates a container to store VMs. This list is passed to the dynamicBroker later
        List<Vm> list = new ArrayList<>(vms);

        //VM Parameters
        long size = 10000; //image size (MEGABYTE)
        int ram = 512; //vm memory (MEGABYTE)
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

    private List<Cloudlet> createCloudlet(DatacenterBroker broker, int cloudlets, int idShift) {
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
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker);
            list.add(cloudlet);
        }

        return list;
    }

    private Datacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();

        long mips = 1000;
        List<Pe> peList1 = new ArrayList<>();


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
        long ram = 16384; //host memory (MEGABYTE)
        long storage = 1000000; //host storage (MEGABYTE)
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

    //We strongly encourage users to develop their own dynamicBroker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    private DatacenterBroker createBroker(String name) {
        DatacenterBrokerSimple broker = new DatacenterBrokerSimple(simulation);
        broker.setName(name);
        return broker;
    }

    /**
     * A class that schedules the creation of a {@link DatacenterBroker}
     * and a set of VMs and Cloudlets for it.
     */
    private class EntityManager extends CloudSimEntity {
        /**
         * Simulation time when the a broker has to be dynamically created.
         */
        public static final int TIME_TO_CREATE_THE_BROKER = 200;

        /**
         * The message to be dispatched which indicates that a broker
         * has to be created.
         * @see #start()
         * @see #processEvent(SimEvent)
         */
        private static final int CREATE_BROKER_MSG = 0;

        /**
         * List of VMs that will be created when the broker is created.
         */
        private List<Vm> vmList;

        /**
         * List of Cloudlets that will be created when the broker is created.
         */
        private List<Cloudlet> cloudletList;

        /**
         * @see #getDynamicBroker()
         */
        private DatacenterBroker dynamicBroker;

        /**
         * Creates a EntityManager that will schedule the dynamic creation of a {@link DatacenterBroker}.
         * @param simulation
         */
        public EntityManager(CloudSim simulation) {
            super(simulation);
            this.cloudletList = new ArrayList<>();
            this.vmList = new ArrayList<>();
        }

        @Override
        public void processEvent(SimEvent ev) {
            switch (ev.getTag()) {
                case CREATE_BROKER_MSG:
                    this.dynamicBroker = createBroker("BrokerDynamicallyCreated");

                    //Create VMs and Cloudlets and send them to the broker created dynamically.
                    this.vmList = createVM(this.dynamicBroker, 5, 100);
                    this.cloudletList = createCloudlet(this.dynamicBroker, 10, 100);

                    this.dynamicBroker.submitVmList(this.vmList);
                    this.dynamicBroker.submitCloudletList(this.cloudletList);
                break;

                default:
                    Log.printLine(getName() + ": unknown event type");
                break;
            }
        }

        /**
         * Starts the DatacenterBroker entity and schedules the creation
         * of a broker for the time defined in {@link #TIME_TO_CREATE_THE_BROKER}.
         * The method schedule the dispatch of broker creation request that will be processed
         * by the {@link #processEvent(SimEvent)} method.
         */
        @Override
        protected void startEntity() {
            Log.printLine(super.getName() + " is starting...");
            schedule(getId(), TIME_TO_CREATE_THE_BROKER, CREATE_BROKER_MSG);
        }

        @Override public void shutdownEntity() {}
        public List<Vm> getVmList() {
            return vmList;
        }
        public List<Cloudlet> getCloudletList() {
            return cloudletList;
        }

        /**
         * Gets the broker that is dynamically create by this EntityManager.
         * @return the dynamically created broker or null if it wasn't created yet
         */
        public DatacenterBroker getDynamicBroker() { return dynamicBroker; }

    }

}
