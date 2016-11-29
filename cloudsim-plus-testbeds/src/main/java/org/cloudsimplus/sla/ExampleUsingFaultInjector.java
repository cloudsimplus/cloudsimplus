/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * This simple example shows how to use a fault injector in the host.
 *
 * @author raysaoliveira
 */
public class ExampleUsingFaultInjector {
    private static final int HOSTS_NUMBER = 3;
    private static final int HOST_PES = 5;
    private static final int VM_PES1 = 2;
    private static final int VM_PES2 = 4;
    private static final int TOTAL_VM_PES = VM_PES1 + VM_PES2;
    private static final int CLOUDLETS_NUMBER = HOSTS_NUMBER * TOTAL_VM_PES;
    private static final int CLOUDLET_PES = 1;

    /**
     * The cloudlet list.
     */
    private final List<Cloudlet> cloudletList;

    private static List<Host> hostList;

    /**
     * The vmlist.
     */
    private final List<Vm> vmlist;
    private int lastCreatedVmId = 0;
    private final CloudSim cloudsim;

    /**
     * Creates Vms
     *
     * @param userId broker id
     * @param numberOfPes number of PEs for each VM to be created
     * @param numberOfVms number of VMs to create
     * @return list de vms
     */
    private List<Vm> createVM(DatacenterBroker broker, int numberOfPes, int numberOfVms) {
        //Creates a container to store VMs.
        List<Vm> list = new ArrayList<>(numberOfVms);

        //VM Parameters
        int vmid = 0;
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;

        //create VMs with differents configurations
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm  = new VmSimple(
                        this.lastCreatedVmId++, mips, numberOfPes)
                        .setRam(ram).setBw(bw).setSize(size)
                        .setCloudletScheduler(new CloudletSchedulerTimeShared())
                        .setBroker(broker);
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates cloudlets
     *
     * @param broker broker id
     * @param cloudlets to criate
     * @return list of cloudlets
     */
    private List<Cloudlet> createCloudlet(DatacenterBroker broker, int cloudlets) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new ArrayList<>(cloudlets);

        //Cloudlet Parameters
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(i, length, CLOUDLET_PES)
                    .setCloudletFileSize(fileSize)
                    .setCloudletOutputSize(outputSize)
                    .setUtilizationModel(utilizationModel)
                    .setBroker(broker);
            list.add(cloudlet);
        }
        return list;
    }

    /**
     * main()
     *
     * @param args the args
     */
    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        new ExampleUsingFaultInjector();
    }

    public ExampleUsingFaultInjector() {
        //  Initialize the CloudSim package.
        int num_user = 1; // number of cloud users

        this.cloudsim = new CloudSim(num_user);
        Log.disable();

        //Create Datacenters
        Datacenter datacenter0 = createDatacenter();

        //Inject Fault
        long seed = System.currentTimeMillis();
        PoissonProcess poisson = new PoissonProcess(0.2, seed);

        UniformDistr failurePesRand = new UniformDistr(seed);
        for (int i = 0; i < datacenter0.getHostList().size(); i++) {
            for (Host host : datacenter0.getHostList()) {
                if (poisson.haveKEventsHappened()) {
                    UniformDistr delayForFailureOfHostRandom = new UniformDistr(1, 10, seed + i);

                    //create a new intance of fault and start it.
                    HostFaultInjection fault = new HostFaultInjection(cloudsim);
                    fault.setNumberOfFailedPesRandom(failurePesRand);
                    fault.setDelayForFailureOfHostRandom(delayForFailureOfHostRandom);
                    fault.setHost(host);
                } else {
                    System.out.println("\t *** Host not failed. -> Id: " + host.getId() + "\n");
                }
                i++;
            }
        }

        //Create Broker
        DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);

        vmlist = new ArrayList<>();
        vmlist.addAll(createVM(broker, VM_PES1, 2));
        vmlist.addAll(createVM(broker, VM_PES2, 2));

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        cloudletList = createCloudlet(broker, CLOUDLETS_NUMBER);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        cloudsim.start();
        cloudsim.stop();

        System.out.println("\n");
        for (Cloudlet cloudlet : cloudletList) {
            System.out.println("--->Status Cloudlet: " + cloudlet.getStatus()
            + " in VM: " + cloudlet.getVm());

        }

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();

        Log.enable();
        new CloudletsTableBuilderHelper(newList).build();
        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the switches.
     *
     * @param name the name
     *
     * @return the switches
     */
    private Datacenter createDatacenter() {
        hostList = new ArrayList<>();

        int mips = 10000;
        int hostId = 0;
        int ram = 8192; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 100000;

        for (int i = 0; i < HOSTS_NUMBER; i++) {
            List<Pe> peList = createHostPesList(HOST_PES, mips);
            Host host = new HostSimple(hostId++, storage, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
                    .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
                    .setVmScheduler(new VmSchedulerTimeShared());

            getHostList().add(host);
        }// This is our machine

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(cloudsim, characteristics,
                new VmAllocationPolicySimple());
    }

    public List<Pe> createHostPesList(int hostPes, int mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < hostPes; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
        }
        return peList;
    }

    /**
     * @return the hostList
     */
    public List<Host> getHostList() {
        return hostList;
    }

    /**
     * @return the HOST_PES
     */
    public static int getHOST_PES() {
        return HOST_PES;
    }

}
