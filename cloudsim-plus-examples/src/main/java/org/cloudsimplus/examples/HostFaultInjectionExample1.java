/**
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

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.faultinjection.HostFaultInjection;

/**
 * Example which shows how to inject random {@link Pe} faults into Hosts
 * using {@link HostFaultInjection} objects.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 */
public final class HostFaultInjectionExample1 {

    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;
    private static final double DATACENTER_COST_PER_CPU = 3.0;
    private static final double DATACENTER_COST_PER_RAM = 0.05;
    private static final double DATACENTER_COST_PER_STORAGE = 0.001;
    private static final double DATACENTER_COST_PER_BW = 0.0;

    private static final int  HOST_MIPS_BY_PE = 1000;
    private static final int  HOST_PES = 6;
    private static final long HOST_RAM = 500000; //host memory (MEGABYTE)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;
    private List<Host> hostList;

    /**
     * The percentage of host CPU usage that trigger VM migration due to over
     * utilization (in scale from 0 to 1, where 1 is 100%).
     */
    private static final double HOST_USAGE_THRESHOLD_VM_MIGRATION = 0.5;

    private static final int  VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MEGABYTE)
    private static final int  VM_RAM = 10000; //vm memory (MEGABYTE)
    private static final long VM_BW = 100000;
    private static final int  VM_PES = 2; //number of cpus
    
    private static final int  CLOUDLET_PES = 2; 
    private static final long CLOUDLET_LENGHT = 200000;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * Number of Hosts to create for each Datacenter.
     * The number of elements in this array defines the number of Datacenters to be created.
     */
    private static final int HOSTS = 2;
    private static final int VMS = 4;
            
    private static final int CLOUDLETS_BY_VM = 2;

    private final List<Vm> vmlist = new ArrayList<>();
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new HostFaultInjectionExample1();
    }

    public HostFaultInjectionExample1() {
        Log.printConcatLine("Starting ", getClass().getSimpleName(), "...");

        simulation = new CloudSim();

        Datacenter datacenter = createDatacenter(HOSTS);
        createFaultInjectionForHosts(datacenter);

        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        createAndSubmitVms(broker);
        createAndSubmitCloudlets(broker);

        simulation.start();

        new CloudletsTableBuilder(broker.getCloudletsFinishedList()).build();

        Log.printConcatLine(getClass().getSimpleName(), " finished!");
        //@todo ver a mensagem abaixo
        System.out.println("A cloudlet 2 deveria terminar no segundo 40, pois ela iniciou em 10, mas ela não termina");
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm(broker);
            vmlist.add(vm);
        }
        broker.submitVmList(vmlist);
    }

    /**
     *
     * @param broker
     * @return
     *
     * @todo @author manoelcampos The use of other CloudletScheduler instead of
     * CloudletSchedulerDynamicWorkload makes the Host CPU usage not be updated
     * (and maybe VM CPU usage too).
     */
    public Vm createVm(DatacenterBroker broker) {
        Vm vm = new VmSimple(vmlist.size(), VM_MIPS, VM_PES);
        vm
            .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
            .setBroker(broker)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates the number of Cloudlets defined in
     * {@link #CLOUDLETS_BY_VM} and submits them to the
     * given broker.
     *
     * @param broker the broker that the created Cloudlets belong to
     * @return the List of created Cloudlets
     */
    public List<Cloudlet> createAndSubmitCloudlets(
            DatacenterBroker broker) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS_BY_VM);
        UtilizationModel utilizationModel = new UtilizationModelFull();
        for (int i = 0; i < CLOUDLETS_BY_VM; i++) {
            Cloudlet c
                    = new CloudletSimple(CLOUDLET_LENGHT, CLOUDLET_PES)
                            .setFileSize(CLOUDLET_FILESIZE)
                            .setOutputSize(CLOUDLET_OUTPUTSIZE)
                            .setUtilizationModel(utilizationModel);
            c.setBroker(broker);
            list.add(c);
        }

        broker.submitCloudletList(list);

        return list;
    }

    private Datacenter createDatacenter(int numberOfHosts) {
        hostList = new ArrayList<>();
        for (int i = 0; i < numberOfHosts; i++) {
            hostList.add(createHost());
            Log.printConcatLine("#Created host ", i, " with ", HOST_MIPS_BY_PE, " mips x ", HOST_PES);
        }
        Log.printLine();

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                        .setCostPerSecond(DATACENTER_COST_PER_CPU)
                        .setCostPerMem(DATACENTER_COST_PER_RAM)
                        .setCostPerStorage(DATACENTER_COST_PER_STORAGE)
                        .setCostPerBw(DATACENTER_COST_PER_BW);

        Datacenter dc = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        dc
          .setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS)
          .setLog(false);
        return dc;
    }

    /**
     * Creates a Host.
     * @return
     */
    public Host createHost() {
      List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(HOST_MIPS_BY_PE, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }    

    public List<Pe> createPeList(int numberOfPEs, long mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter
     */
    private void createFaultInjectionForHosts(Datacenter datacenter) {
        final int MAX_FAILURE_DELAY_SECONDS = 10;
        //final long seed = System.currentTimeMillis();
        long seed = 3412125;
        final double meanFailureNumberPerMinute = 0.4;
        
        for (Host host: datacenter.getHostList()) {
            HostFaultInjection fault = new HostFaultInjection(host, meanFailureNumberPerMinute, seed++);
            fault.setMaxFailureDelay(MAX_FAILURE_DELAY_SECONDS);
            fault.setVmCloner(this::cloneVm);
            fault.setCloudletsCloner(this::cloneCloudlets);
            Log.printFormattedLine("\tFault Injection created for %s.", host);
            break; //cria apenas para o primeiro host, so pra teste
        }
    }
    
    /**
     * Clones a VM by creating another one with the same configurations of a given VM.
     * @param vm the VM to be cloned
     * @return the cloned (new) VM.
     * 
     * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter) 
     */
    private Vm cloneVm(Vm vm){
        Vm clone = new VmSimple((long)vm.getMips(), (int)vm.getNumberOfPes());
        clone.setDescription("Clone of VM " + vm.getId());
        clone
            .setSize(vm.getStorage().getCapacity())
            .setBw(vm.getBw().getCapacity())
            .setRam(vm.getBw().getCapacity())
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        Log.printFormattedLine("\n\n#Cloning VM %d\n\tMips %.2f Number of Pes: %d ", vm.getId(), clone.getMips(), clone.getNumberOfPes());
        
        return clone;
    }
    
    /**
     * Clones each Cloudlet associated to a given VM.
     * The method is called when a VM is destroyed due to a
     * Host failure and a snapshot from that VM (a clone)
     * is started into another Host.
     * In this case, all the Cloudlets which were running inside
     * the destroyed VM will be recreated from scratch into the VM clone,
     * re-starting their execution from the beginning.
     * 
     * @param sourceVm the VM to clone its Cloudlets
     * @return the List of cloned Cloudlets.
     * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter) 
     */
    private List<Cloudlet> cloneCloudlets(Vm sourceVm){
        final List<Cloudlet> sourceVmCloudlets = sourceVm.getCloudletScheduler().getCloudletList();
        final List<Cloudlet> clonedCloudlets = new ArrayList<>(sourceVmCloudlets.size());
        for(Cloudlet cl: sourceVmCloudlets){
            clonedCloudlets.add(cloneCloudlet(cl));
        }
        
        return clonedCloudlets;
    }

    /**
     * Creates a clone from a given Cloudlet.
     * @param sourceCloudlet the Cloudlet to be cloned.
     * @return the cloned (new) cloudlet
     */
    private Cloudlet cloneCloudlet(Cloudlet sourceCloudlet) {
        Cloudlet clone = new CloudletSimple(sourceCloudlet.getLength(), (int)sourceCloudlet.getNumberOfPes());
        clone
                .setUtilizationModelBw(sourceCloudlet.getUtilizationModelBw())
                .setUtilizationModelCpu(sourceCloudlet.getUtilizationModelCpu())
                .setUtilizationModelRam(sourceCloudlet.getUtilizationModelRam());
        return clone;
        
    }
    
}
