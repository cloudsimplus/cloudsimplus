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
package org.cloudsimplus.examples.traces;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.util.TraceReaderAbstract;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Comparator.comparingLong;

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
 * at the developer machine and can <b>spend a long time to finish</b>,
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
 * <p>Check important details at {@link SwfWorkloadFileReader}
 * and {@link TraceReaderAbstract}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0.0
 */
public class SwfWorkloadFormatExample1 {
    /**
     * The workload file to be read.
     */
    private static final String WORKLOAD_FILENAME = "NASA-iPSC-1993-3.1-cln.swf.gz";

    /**
     * The base dir inside the resource directory to get SWF workload files.
     */
    private static final String WORKLOAD_BASE_DIR = "workload/swf/";

    /**
     * A {@link Comparator} that sorts VMs submitted to a broker
     * by the VM's required PEs number in decreasing order.
     * This way, VMs requiring more PEs are created first.
     *
     * @see DatacenterBroker#setVmComparator(Comparator)
     */
    private static final Comparator<Vm> VM_COMPARATOR = comparingLong(Vm::getNumberOfPes).reversed();
    private final CloudSim simulation;

    /**
     * Defines the maximum number of cloudlets to be created
     * from the given workload file.
     * The value -1 indicates that every job inside the workload file
     * will be created as one cloudlet.
     */
    private int maximumNumberOfCloudletsToCreateFromTheWorkloadFile = -1;

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

    private SwfWorkloadFormatExample1() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName() + " in 5 seconds. Since it reads a workload file, it can take a long time to finish.");
        sleep(5);

        simulation = new CloudSim();
        try {
            broker = new DatacenterBrokerSimple(simulation);
            broker.setVmComparator(VM_COMPARATOR);

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
            new CloudletsTableBuilder(newList).build();

            System.out.println(getClass().getSimpleName() + " finished!");
        } catch (Exception e) {
            System.out.printf("Erro during simulation execution: %s\n", e.getMessage());
        }
    }

    private void sleep(final long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        System.out.printf("# Created %d VMs for the %s\n", vmlist.size(), broker);
    }

    private void createCloudletsFromWorkloadFile() {
        final String fileName = WORKLOAD_BASE_DIR + WORKLOAD_FILENAME;
        SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance(fileName, CLOUDLETS_MIPS);
        reader.setMaxLinesToRead(maximumNumberOfCloudletsToCreateFromTheWorkloadFile);
        this.cloudletList = reader.generateWorkload();

        System.out.printf("# Created %d Cloudlets for %s\n", this.cloudletList.size(), broker);
    }

    /**
     * Creates the Datacenter.
     *
     * @return the Datacenter
     */
    private Datacenter createDatacenterAndHostsBasedOnVmRequirements() {
        List<Host> hostList = createHostsAccordingToVmRequirements();
        Datacenter datacenter = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());

        System.out.printf("# Created %d Hosts at %s\n", hostList.size(), datacenter);
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
        for (Entry<Long, Long> entry : vmsPesCountMap.entrySet()) {
            numberOfPesRequiredByVms = entry.getKey();
            numberOfVms = entry.getValue();
            /*For VMs requiring MINIMUM_NUM_OF_PES_BY_HOST or less PEs,
            it will be created a set of Hosts which all of them contain
            this number of PEs.*/
            if (numberOfPesRequiredByVms <= MINIMUM_NUM_OF_PES_BY_HOST) {
                numberOfVmsRequiringUpToTheMinimumPesNumber += numberOfVms;
            } else {
                hostList.addAll(createHostsOfSameCapacity(numberOfVms, numberOfPesRequiredByVms));
                totalOfHosts += numberOfVms;
                totalOfPesOfAllHosts += numberOfVms * numberOfPesRequiredByVms;
            }
        }

        totalOfHosts += numberOfVmsRequiringUpToTheMinimumPesNumber;
        totalOfPesOfAllHosts += numberOfVmsRequiringUpToTheMinimumPesNumber * MINIMUM_NUM_OF_PES_BY_HOST;
        List<Host> subList =
            createHostsOfSameCapacity(
                numberOfVmsRequiringUpToTheMinimumPesNumber,
                MINIMUM_NUM_OF_PES_BY_HOST);
        hostList.addAll(subList);
        System.out.printf(
            "# Total of created hosts: %d Total of PEs of all hosts: %d\n\n", totalOfHosts, totalOfPesOfAllHosts);

        return hostList;
    }

    /**
     * Creates a specific number of PM's with the same capacity.
     *
     * @param numberOfHosts number of hosts to create
     * @param numberOfPes   number of PEs of the host
     * @return the created host
     */
    private List<Host> createHostsOfSameCapacity(long numberOfHosts, long numberOfPes) {
        final long ram = VM_RAM * NUMBER_OF_VMS_PER_HOST;
        final long storage = VM_SIZE * NUMBER_OF_VMS_PER_HOST;
        final long bw = VM_BW * NUMBER_OF_VMS_PER_HOST;

        List<Host> list = new ArrayList<>();
        for (int i = 0; i < numberOfHosts; i++) {
            List<Pe> peList = createPeList(numberOfPes, VM_MIPS);

            Host host =
                new HostSimple(ram, bw, storage, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());

            list.add(host);
        }

        System.out.printf("# Created %d hosts with %d PEs each one\n", numberOfHosts, numberOfPes);

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

        System.out.println();
        long totalOfVms = 0, totalOfPes = 0;
        for (Entry<Long, Long> entry : vmsPesCountMap.entrySet()) {
            totalOfVms += entry.getValue();
            totalOfPes += entry.getKey() * entry.getValue();
            System.out.printf("# There are %d VMs requiring %d PEs\n", entry.getValue(), entry.getKey());
        }
        System.out.printf("# Total of VMs: %d Total of required PEs of all VMs: %d\n", totalOfVms, totalOfPes);
        return vmsPesCountMap;
    }
}
