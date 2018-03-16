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
package org.cloudsimplus.examples.workload;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.WorkloadFileReader;

/**
 * An example showing how to dynamically create cloudlets from a workload trace
 * file in the Standard Workload Format (.swf file) defined by the
 * <a href="http://www.cs.huji.ac.il/labs/parallel/workload/">Hebrew University
 * of Jerusalem</a>. This example uses the workload file
 * "<i>NASA-iPSC-1993-3.1-cln.swf.gz</i>", which was downloaded from the given
 * web page and is located at the resources folder of this project.
 * The workload file has 18239 jobs that will be created as Cloudlets.
 *
 * <p>Considering the large number of cloudlets that can have a workload file,
 * that can cause the simulation to consume a lot of resources
 * at the developer machine and can spend a long time to finish,
 * the example allow to limit the maximum number of cloudlets to be submitted
 * to the DatacenterBroker.
 * See the {@link #maximumNumberOfCloudletsToCreateFromTheWorkloadFile} attribute for more details.
 * </p>
 *
 * <p>
 * The workload file is compressed in <i>gz</i> format and the
 * <i>swf</i> file inside it is just a text file that can be opened in any text
 * editor. For more information about the workload format, check
 * <a href="http://www.cs.huji.ac.il/labs/parallel/workload/swf.html">this
 * page</a>.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class SwfWorkloadFormatExample1 {
    /**
     * The workload file to be read.
     */
    private static final String WORKLOAD_FILENAME = "NASA-iPSC-1993-3.1-cln.swf.gz";
    private final CloudSim simulation;

    /**
     * Defines the maximum number of cloudlets to be created
     * from the given workload file.
     * The value -1 indicates that every job inside the workload file
     * will be created as one cloudlet.
     */
    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

    private static final double DATACENTER_CPU_COST = 3.0;
    private static final double DATACENTER_MEM_COST = 0.05;
    private static final double DATACENTER_STORAGE_COST = 0.001;
    private static final double DATACENTER_BW_COST = 0.0;
    private static final int NUMBER_OF_VMS_PER_HOST = 10;

    /**
     * The minimum number of PEs to be created for each host.
     */
    private final int MINIMUM_NUM_OF_PES_BY_HOST = 8;

    private static final int CLOUDLETS_MIPS = 10000;
    private static final int VM_MIPS = CLOUDLETS_MIPS;
    private static final long VM_SIZE = 2000;
    private static final int VM_RAM = 1000;
    private static final long VM_BW = 50000;

    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private Datacenter datacenter0;
    private DatacenterBroker broker;

    private int lastCreatedHostId = 0;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        new SwfWorkloadFormatExample1();
    }

    public SwfWorkloadFormatExample1() {
        Log.printConcatLine("Starting ", SwfWorkloadFormatExample1.class.getSimpleName(), "...");

        simulation = new CloudSim();
        try {
            broker = new DatacenterBrokerVmsWithMorePesFirst(simulation);

            /*Vms and cloudlets are created before the Datacenter and host
            because the example is defining the hosts based on VM requirements
            and VMs are created based on cloudlet requirements.*/
            createCloudletsFromWorkloadFile();
            createOneVmForEachCloudlet(broker);

            datacenter0 = createDatacenterAndHostsBasedOnVmRequirements();

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            simulation.start();

            List<Cloudlet> newList = broker.getCloudletFinishedList();
            printCloudletList(newList);

            Log.printConcatLine(SwfWorkloadFormatExample1.class.getSimpleName(), " finished!");
        } catch (IOException e) {
            Log.printConcatLine(e.getMessage());
        }
    }

    private void createOneVmForEachCloudlet(DatacenterBroker broker) {
        int vmId = -1;
        vmlist = new ArrayList<>();
        for (Cloudlet cloudlet : this.cloudletList) {
            Vm vm = new VmSimple(++vmId, VM_MIPS, cloudlet.getNumberOfPes())
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
            vmlist.add(vm);
            cloudlet.setVm(vm);
        }

        Log.printConcatLine("#Created ", vmlist.size(), " VMs for the broker ", broker.getName());
    }

    private void createCloudletsFromWorkloadFile() throws IOException {
        String path = this.getClass().getClassLoader().getResource("workload/swf").getPath();
        if(Objects.isNull(path)){
            path = "";
        }

        String fileName = String.format("%s/%s", path, WORKLOAD_FILENAME);
        WorkloadFileReader reader =
                new WorkloadFileReader(fileName, CLOUDLETS_MIPS);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();

        Log.printConcatLine("#Created ", this.cloudletList.size(), " Cloudlets for broker ", broker.getName());
    }

    /**
     * Creates the Datacenter.
     *
     * @return the Datacenter
     */
    private Datacenter createDatacenterAndHostsBasedOnVmRequirements() {
        List<Host> hostList = createHostsAccordingToVmRequirements();
        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple (hostList)
                .setCostPerSecond(DATACENTER_CPU_COST)
                .setCostPerMem(DATACENTER_MEM_COST)
                .setCostPerStorage(DATACENTER_STORAGE_COST)
                .setCostPerBw(DATACENTER_BW_COST);

        Datacenter datacenter = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        Log.printConcatLine("#Created ", hostList.size(), " Hosts at ", datacenter.getName());
        return datacenter;
    }

    /**
     * Creates a list of hosts considering the requirements of the list of VMs.
     *
     * @return
     */
    private List<Host> createHostsAccordingToVmRequirements() {
        List<Host> hostList = new ArrayList<>();
        Map<Long, Long> vmsPesCountMap = getMapWithNumberOfVmsGroupedByRequiredPesNumber();
        long numberOfPesRequiredByVms, numberOfVms, numberOfVmsRequiringUpToTheMinimumPesNumber = 0;
        long totalOfHosts = 0, totalOfPesOfAllHosts = 0;
        for (Entry<Long, Long> entry: vmsPesCountMap.entrySet()) {
            numberOfPesRequiredByVms = entry.getKey();
            numberOfVms = entry.getValue();
            /*For VMs requiring MINIMUM_NUM_OF_PES_BY_HOST or less PEs,
            it will be created a set of Hosts which all of them contain
            this number of PEs.*/
            if(numberOfPesRequiredByVms <= MINIMUM_NUM_OF_PES_BY_HOST){
                numberOfVmsRequiringUpToTheMinimumPesNumber += numberOfVms;
            }
            else {
                hostList.addAll(createHostsOfSameCapacity(numberOfVms, numberOfPesRequiredByVms));
                totalOfHosts += numberOfVms;
                totalOfPesOfAllHosts += numberOfVms*numberOfPesRequiredByVms;
            }
        }

        totalOfHosts += numberOfVmsRequiringUpToTheMinimumPesNumber;
        totalOfPesOfAllHosts += numberOfVmsRequiringUpToTheMinimumPesNumber*MINIMUM_NUM_OF_PES_BY_HOST;
        List<Host> subList =
                createHostsOfSameCapacity(
                        numberOfVmsRequiringUpToTheMinimumPesNumber,
                        MINIMUM_NUM_OF_PES_BY_HOST);
        hostList.addAll(subList);
        Log.printConcatLine(
                "#Total of created hosts: ", totalOfHosts,
                " Total of PEs of all hosts: ", totalOfPesOfAllHosts);
        Log.printLine();

        return hostList;
    }

    /**
     * Creates a specific number of PM's with the same capacity.
     *
     * @param numberOfHosts number of hosts to create
     * @param numberOfPes number of PEs of the host
     * @return the created host
     */
    private List<Host> createHostsOfSameCapacity(long numberOfHosts, long numberOfPes) {
        final long ram = VM_RAM * NUMBER_OF_VMS_PER_HOST;
        final long storage = VM_SIZE * NUMBER_OF_VMS_PER_HOST;
        final long bw = VM_BW * NUMBER_OF_VMS_PER_HOST;

        List<Host> list = new ArrayList<>();
        for(int i = 0; i < numberOfHosts; i++){
            List<Pe> peList = createPeList(numberOfPes, VM_MIPS);

            Host host =
                new HostSimple(ram, bw, storage, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());

            list.add(host);
        }

        Log.printConcatLine("#Created ", numberOfHosts, " hosts with ", numberOfPes, " PEs each one");

        return list;
    }

    private List<Pe> createPeList(long numberOfPes, long mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return peList;
    }

    /**
     * Gets a map containing the number of PEs that existing VMs require and the
     * total of VMs that required the same number of PEs. This map is a way to
     * know how many PMs will be required to host the VMs.
     *
     * @return a map that counts the number of VMs that requires the same amount
     * of PEs. Each map key is number of PEs and each value is the number of VMs
     * that require that number of PEs. For instance, a key = 8 and a value = 5
     * means there is 5 VMs that require 8 PEs.
     */
    private Map<Long, Long> getMapWithNumberOfVmsGroupedByRequiredPesNumber() {
        Map<Long, Long> vmsPesCountMap = new HashMap<>();
        for (Vm vm : vmlist) {
            final long pesNumber = vm.getNumberOfPes();
            //checks if the map already has an entry to the given pesNumber
            Long numberOfVmsWithGivenPesNumber = vmsPesCountMap.get(pesNumber);
            if (numberOfVmsWithGivenPesNumber == null) {
                numberOfVmsWithGivenPesNumber = 0L;
            }
            //updates the number of VMs that have the given pesNumber
            vmsPesCountMap.put(pesNumber, ++numberOfVmsWithGivenPesNumber);

        }

        Log.printLine();
        long totalOfVms = 0, totalOfPes = 0;
        for(Entry<Long, Long> entry: vmsPesCountMap.entrySet()){
            totalOfVms += entry.getValue();
            totalOfPes += entry.getKey() * entry.getValue();
            Log.printConcatLine(
                    "#There is ", entry.getValue(),
                    " VMs requiring ", entry.getKey(), " PEs");
        }
        Log.printConcatLine(
                "#Total of VMs: ", totalOfVms,
                " Total of required PEs of all VMs: ", totalOfPes, "\n");
        return vmsPesCountMap;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("       #" + indent + "Cloudlet" + indent + "STATUS " + indent
                + "Datacenter" + indent + "      VM" + indent + "Exec. Time" + indent
                + "Start Time" + indent + "Finish Time");

        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            String line =
                String.format(
                    "%8d    %8d    %7s    %10d    %8d    %10.0f    %10.0f    %11.0f",
                    (i+1),
                    cloudlet.getId(),
                    cloudlet.getStatus().name(),
                    cloudlet.getLastDatacenter(),
                    cloudlet.getVm().getId(),
                    cloudlet.getActualCpuTime(),
                    cloudlet.getExecStartTime(),
                    cloudlet.getFinishTime()
                );
            Log.printLine(line);
        }
    }
}
