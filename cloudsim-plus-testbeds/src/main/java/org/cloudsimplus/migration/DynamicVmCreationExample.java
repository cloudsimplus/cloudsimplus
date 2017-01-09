/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.migration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

/**
 * An example that creates VMs dinamically in runtime.
 * 
 * @author raysaoliveira
 */
public class DynamicVmCreationExample {
    
    private static final int HOSTS_NUMBER = 1;
    private static final int HOST_PES = 8;
    private static final int VM_PES1 = 4;
    private static final int VM_PES2 = 4;
    private static final int CLOUDLETS_NUMBER = 3;
    private static final int CLOUDLET_PES = 4;
  
    private int lastCreatedVmId = 0;

    private final List<Host> hostList;
    private final List<Cloudlet> cloudletList;
    private final List<Vm> vmlist;

    private double responseTimeCloudlet;
    private final CloudSim cloudsim;
    
 
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Log.printFormattedLine(" Starting... ");
        new DynamicVmCreationExample();
    }

    public DynamicVmCreationExample() throws FileNotFoundException, IOException {
        cloudsim = new CloudSim();

        hostList = new ArrayList<>();
        Datacenter datacenter0 = createDatacenter();

        DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);

        vmlist = new ArrayList<>();
        vmlist.addAll(createVM(broker, VM_PES1, 1));
        vmlist.addAll(createVM(broker, VM_PES2, 1));
        vmlist.addAll(createVM(broker, VM_PES2, 1));

        broker.submitVmList(vmlist);

        cloudletList = createCloudlet(broker, CLOUDLETS_NUMBER);

        
       /* WorkloadFileReader workloadFileReader = new WorkloadFileReader("/Users/raysaoliveira/Desktop/TeseMestradoEngInformatica/cloudsim-plus/cloudsim-plus-testbeds/src/main/java/org/cloudsimplus/sla/UniLu-Gaia-2014-2.swf", 1);
        cloudletList = workloadFileReader.generateWorkload().subList(0, 1000);
        for (Cloudlet cloudlet : cloudletList) {
            cloudlet.setBroker(broker);
            cloudlet.setLength(5000);
        }
        */
        
        broker.submitCloudletList(cloudletList);

        cloudsim.start();

        System.out.println("________________________________________________________________");
        System.out.println("\n\t\t - System Metrics - \n ");

        //responseTime
        responseTimeCloudlet = responseTimeCloudlet(cloudletList);
        System.out.printf("\t** Response Time of Cloudlets %.2f %n", responseTimeCloudlet);

        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(newList).build();

        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the Datacenter.
     * @return the datacenter
     */
    private Datacenter createDatacenter() {
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
                    .setVmScheduler(new VmSchedulerSpaceShared());

            getHostList().add(host);
        }// This is our machine

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList);

        return new DatacenterSimple(cloudsim, characteristics,
                new VmAllocationPolicySimple());
    }
    
    /**
     * Creates Vms
     *
     * @param broker broker
     * @param numberOfPes number of PEs for each VM to be created
     * @param numberOfVms number of VMs to create
     * @return list de vms
     */
    private List<Vm> createVM(DatacenterBroker broker, int numberOfPes, int numberOfVms) {
        //Creates a container to store VMs.
        List<Vm> list = new ArrayList<>(numberOfVms);

        //VM Parameters
        long size = 100; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;

        //create VMs with differents configurations
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = new VmSimple(
                    this.lastCreatedVmId++, mips, numberOfPes)
                    .setRam(ram).setBw(bw).setSize(size)
                    .setCloudletScheduler(new CloudletSchedulerSpaceShared())
                    .setBroker(broker);
            list.add(vm);
        }

        return list;
    }    

    public List<Pe> createHostPesList(int hostPes, int mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < hostPes; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips))); 
        }
        return peList;
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
        long fileSize = 500;
        long outputSize = 500;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(i, length, CLOUDLET_PES)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModel(utilizationModel)
                    .setBroker(broker);
            
            list.add(cloudlet);
        }
        return list;
    }

    /**
     * Shows the response time of cloudlets
     *
     * @param cloudlets to calculate the response time
     * @return responseTimeCloudlet
     */
    private double responseTimeCloudlet(List<Cloudlet> cloudlets) {

        double responseTime = 0;
        for (Cloudlet cloudlet : cloudlets) {
            responseTime = cloudlet.getFinishTime() - cloudlet.getLastDatacenterArrivalTime();
        }
        return responseTime;

    }
    

    /**
     * @return the responseTimeCloudlet
     */
    public double getResponseTime() {
        return responseTimeCloudlet;
    }

    /**
     * @return the hostLis
     */
    private List<Host> getHostList() {
        return hostList;
    }
}
