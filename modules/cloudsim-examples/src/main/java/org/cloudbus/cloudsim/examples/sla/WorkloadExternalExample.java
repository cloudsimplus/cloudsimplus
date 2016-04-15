package org.cloudbus.cloudsim.examples.sla;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.util.WorkloadFileReader;

/**
 *
 * @author RaysaOliveira
 */

public class WorkloadExternalExample {
    
    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;
    
    /** The vmlist. */
    private static List<Vm> vmlist;
    
     /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
            
        try {
            // First step: Initialize the CloudSim package. It should be called before creating any entities.
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
            boolean trace_flag = false; // trace events
            
            CloudSim.init(num_user, calendar, trace_flag);
            
            // Second step: Create Datacenters
            Datacenter datacenter0 = createDatacenter("Datacenter_0");
        
            // Third step: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();
            
            // Fourth step: Create one virtual machine
            vmlist = new ArrayList<Vm>();

            // VM description
            int vmid = 0;
            int mips = 1000;
            long size = 10000; // image size (MB)
            int ram = 512; // vm memory (MB)
            long bw = 1000;
            int pesNumber = 1; // number of cpus
            String vmm = "Xen"; // VMM name
            
            // create VM
            Vm vm = new VmSimple(
                    vmid, brokerId, mips, pesNumber, ram, bw, size, 
                    vmm, new CloudletSchedulerTimeShared());

            // add the VM to the vmList
            vmlist.add(vm);
            
            // submit vm list to the broker
            broker.submitVmList(vmlist);
            
            // Fifth step: Read Cloudlets from workload external file in the swf format
	    WorkloadFileReader workloadFileReader = new WorkloadFileReader("src/main/java/org/cloudbus/cloudsim/examples/util/UniLu-Gaia-2014-2.swf", 1);
            cloudletList = workloadFileReader.generateWorkload().subList(0, 1000);
	    for (Cloudlet cloudlet : cloudletList) {
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmid);
	    }
            
            // submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            // Sixth step: Starts the simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            //Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            TableBuilderHelper.print(new TextTableBuilder(), newList);
            
            Log.printFormattedLine("... finished!");
        }catch (Exception e) {
                e.printStackTrace();
                Log.printLine("Unwanted errors happen");
        }
    }
           
/**
     * Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name) {

            // Here are the steps needed to create a PowerDatacenter:
            // 1. We need to create a list to store
            // our machine
            List<Host> hostList = new ArrayList<Host>();

            // 2. A Machine contains one or more PEs or CPUs/Cores.
            // In this example, it will have only one core.
            List<Pe> peList = new ArrayList<Pe>();

            int mips = 1000;

            // 3. Create PEs and add these into a list.
            peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

            // 4. Create Host with its id and list of PEs and add them to the list
            // of machines
            int hostId = 0;
            int ram = 2048; // host memory (MB)
            long storage = 1000000; // host storage
            long bw = 10000;

            hostList.add(new HostSimple(
                            hostId,
                            new ResourceProvisionerSimple<>(new Ram(ram)),
                            new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                            storage,
                            peList,
                            new VmSchedulerTimeShared(peList)
                    )
            ); // This is our machine

            // 5. Create a DatacenterCharacteristics object that stores the
            // properties of a data center: architecture, OS, list of
            // Machines, allocation policy: time- or space-shared, time zone
            // and its price (G$/Pe time unit).
            String arch = "x86"; // system architecture
            String os = "Linux"; // operating system
            String vmm = "Xen";
            double time_zone = 10.0; // time zone this resource located
            double cost = 3.0; // the cost of using processing in this resource
            double costPerMem = 0.05; // the cost of using memory in this resource
            double costPerStorage = 0.001; // the cost of using storage in this
                                                                            // resource
            double costPerBw = 0.0; // the cost of using bw in this resource
            LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN
                                                                                                    // devices by now

            DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple (
                            arch, os, vmm, hostList, time_zone, cost, costPerMem,
                            costPerStorage, costPerBw);

            // 6. Finally, we need to create a PowerDatacenter object.
            Datacenter datacenter = null;
            try {
                    datacenter = new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
            } catch (Exception e) {
                    e.printStackTrace();
            }

            return datacenter;
    }

      /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
            DatacenterBroker broker = null;
            try {
                    broker = new DatacenterBrokerSimple("Broker");
            } catch (Exception e) {
                    e.printStackTrace();
                    return null;
            }
            return broker;
    }
}


