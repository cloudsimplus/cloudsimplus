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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.tasktimecompletion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.WorkloadFileReader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.HorizontalVmScalingSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.sla.readJsonFile.CpuUtilization;
import org.cloudsimplus.sla.readJsonFile.TaskTimeCompletion;
import org.cloudsimplus.sla.readJsonFile.SlaReader;
import static org.cloudsimplus.sla.tasktimecompletion.CloudletTaskTimeCompletionWorkLoadRunner.VMS;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 *
 * @author raysaoliveira
 */
public class CloudletTaskTimeCompletionWorkLoadExperimet extends SimulationExperiment {

    private static final int SCHEDULING_INTERVAL = 5;

    /**
     * The interval to request the creation of new Cloudlets.
     */
    private static final int CLOUDLETS_CREATION_INTERVAL = SCHEDULING_INTERVAL * 3;

    private static final int HOSTS = 100;
    private static final int HOST_PES = 70;

    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private final ContinuousDistribution randCloudlet, randVm;

    private int createdCloudlets;
    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    public static final String METRICS_FILE = ResourceLoader.getResourcePath(CloudletTaskTimeCompletionWorkLoadExperimet.class, "SlaMetrics.json");
    private double cpuUtilizationSlaContract;
    private double taskTimeCompletionSlaContract;

    /**
     * Different lengths that will be randomly assigned to created Cloudlets.
     */
    private static final long[] CLOUDLET_LENGTHS = {20000, 40000, 14000, 10000, 10000};
    private static final int[] VM_PES = {2};

    /**
     * Sorts the Cloudlets before submitting them to the Broker, so that
     * Cloudlets with larger length will be mapped for a VM first than lower
     * ones.
     */
    private final Comparator<Cloudlet> sortCloudletsByLengthReversed = Comparator.comparingDouble((Cloudlet c) -> c.getLength()).reversed();


    public CloudletTaskTimeCompletionWorkLoadExperimet(ContinuousDistribution randCloudlet, ContinuousDistribution randVm) {
        super();
        this.randCloudlet = randCloudlet;
        this.randVm = randVm;
        try {
            SlaReader slaReader = new SlaReader(METRICS_FILE);
            TaskTimeCompletion rt = new TaskTimeCompletion(slaReader);
            rt.checkTaskTimeCompletionSlaContract();
            taskTimeCompletionSlaContract = rt.getMaxValueTaskTimeCompletion();

            CpuUtilization cpu = new CpuUtilization(slaReader);
            cpu.checkCpuUtilizationSlaContract();
            cpuUtilizationSlaContract = cpu.getMaxValueCpuUtilization();

            // getCloudsim().addOnClockTickListener(this::createNewCloudlets);
            //getCloudsim().addOnClockTickListener(this::printVmsCpuUsage);

        } catch (IOException ex) {
            Logger.getLogger(CloudletTaskTimeCompletionWorkLoadExperimet.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private DatacenterBroker getFirstBroker() {
        return getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
    }

    private void printVmsCpuUsage(EventInfo eventInfo) {
        DatacenterBroker broker0 = getFirstBroker();
        broker0.getVmsCreatedList().sort(Comparator.comparingInt(Vm::getId));

        broker0.getVmsCreatedList().forEach(vm
                -> Log.printFormattedLine("####Time %.0f: Vm %d CPU usage: %.2f. SLA: %.2f.\n",
                        eventInfo.getTime(), vm.getId(),
                        vm.getCurrentCpuPercentUse(), cpuUtilizationSlaContract)
        );
    }

    @Override
    public final void printResults() {
        DatacenterBroker broker0 = getFirstBroker();
        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        Comparator<Cloudlet> sortByStartTime = comparingDouble(c -> c.getExecStartTime());
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    @Override
    protected List<Cloudlet> createCloudlets(DatacenterBroker broker) {
       WorkloadFileReader workloadFileReader;
       cloudletList = new ArrayList<>();
        try {
            workloadFileReader = new WorkloadFileReader("/Users/raysaoliveira/Desktop/Mestrado/cloudsim-plus/cloudsim-plus-testbeds/src/main/resources/METACENTRUM-2009-2.swf", 1);
            cloudletList = workloadFileReader.generateWorkload().subList(0, 1000);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CloudletTaskTimeCompletionWorkLoadExperimet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CloudletTaskTimeCompletionWorkLoadExperimet.class.getName()).log(Level.SEVERE, null, ex);
        }

        cloudletList.forEach((cloudlet) -> {
            cloudlet.setBroker(broker);
        });

        return cloudletList;
    }

    /**
     * Selects a VM to run a Cloudlet that will minimize the Cloudlet response
     * time.
     *
     * @param cloudlet the Cloudlet to select a VM to
     * @return the selected Vm
     */
    private Vm selectVmForCloudlet(Cloudlet cloudlet) {
        List<Vm> createdVms = cloudlet.getBroker().getVmsCreatedList();
    //    Log.printLine("\t\tCreated VMs: " + createdVms);
        Comparator<Vm> sortByNumberOfFreePes
                = Comparator.comparingLong(vm -> getExpectedNumberOfFreeVmPes(vm));
        Comparator<Vm> sortByExpectedCloudletTaskTimeCompletion
                = Comparator.comparingDouble(vm -> getExpectedCloudletTaskTimeCompletion(cloudlet, vm));
        createdVms.sort(
                sortByNumberOfFreePes
                        .thenComparing(sortByExpectedCloudletTaskTimeCompletion)
                        .reversed());
        Vm mostFreePesVm = createdVms.stream().findFirst().orElse(Vm.NULL);

        Vm selectedVm = createdVms.stream()
                .filter(vm -> getExpectedNumberOfFreeVmPes(vm) >= cloudlet.getNumberOfPes())
                .filter(vm -> getExpectedCloudletTaskTimeCompletion(cloudlet, vm) <= taskTimeCompletionSlaContract)
                .findFirst().orElse(mostFreePesVm);

        return selectedVm;
    }

    private double getExpectedCloudletTaskTimeCompletion(Cloudlet cloudlet, Vm vm) {
        final double expectedTaskTimeCompletion = cloudlet.getLength() / vm.getMips();
        return expectedTaskTimeCompletion;
    }

    /**
     * Gets the expected amount of free PEs for a VM
     *
     * @param vm the VM to get the amount of free PEs
     * @return the number of PEs that are free or a negative value that indicate
     * there aren't free PEs (this negative number indicates the amount of
     * overloaded PEs)
     */
    private long getExpectedNumberOfFreeVmPes(Vm vm) {
        final long totalPesNumberForCloudletsOfVm
                = vm.getBroker().getCloudletsCreatedList().stream()
                        .filter(c -> c.getVm().equals(vm))
                        .mapToLong(Cloudlet::getNumberOfPes)
                        .sum();

        final long numberOfVmFreePes
                = vm.getNumberOfPes() - totalPesNumberForCloudletsOfVm;

  /*      Log.printFormattedLine(
                "\t\tTotal pes of cloudlets in VM " + vm.getId() + ": "
                + totalPesNumberForCloudletsOfVm + " -> vm pes: "
                + vm.getNumberOfPes() + " -> vm free pes: " + numberOfVmFreePes);*/
        return numberOfVmFreePes;
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    @Override
    protected List<Vm> createVms(DatacenterBroker broker) {
        vmList = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm();
           // createHorizontalVmScaling(vm);
            vmList.add(vm);
        }
        return vmList;
    }

    /**
     * Creates a Vm object.
     *
     * @return the created Vm
     */
    private Vm createVm() {
        DatacenterBroker broker0 = getFirstBroker();
        final int id = createsVms++;
        final int i = (int) (randVm.sample() * VM_PES.length);
        final int pes = VM_PES[i];

        Vm vm = new VmSimple(id, 1000, pes)
                .setRam(512).setBw(1000).setSize(10000).setBroker(broker0)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    @Override
    protected List<Host> createHosts() {
        hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }
        return hostList;
    }

    private Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        Host h = new HostSimple(20480, 1000000, 1000000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
        h.setId(id);
        return h;
    }

    @Override
    protected DatacenterBroker createBroker() {
        DatacenterBroker broker0;
        broker0 = new DatacenterBrokerSimple(getCloudsim());
        broker0.setVmMapper(this::selectVmForCloudlet);
        broker0.setCloudletComparator(sortCloudletsByLengthReversed);
        return broker0;
    }

    /**
     * Computes the TaskTimeCompletion average for all finished Cloudlets on this
     * experiment.
     *
     * @return the TaskTimeCompletion average
     */
    double getCloudletsTaskTimeCompletionAverage() {
        SummaryStatistics cloudletTaskTimeCompletion = new SummaryStatistics();
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);
        broker.getCloudletsFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .forEach(cloudletTaskTimeCompletion::addValue);

       /* Log.printFormattedLine(
                "\t\t\n TaskTimeCompletion simulation: %.2f \n TaskTimeCompletion contrato SLA: %.2f \n",
                cloudletTaskTimeCompletion.getMean(), taskTimeCompletionSlaContract);*/
        return cloudletTaskTimeCompletion.getMean();
    }

    double getPercentageOfCloudletsMeetingTaskTimeCompletion() {
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);

        double totalOfcloudletSlaSatisfied = broker.getCloudletsFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .filter(rt -> rt <= taskTimeCompletionSlaContract)
                .count();
    /*    System.out.printf("\n ** Percentage of cloudlets that complied with "
                + "the SLA Agreement:  %.2f %%",
                ((totalOfcloudletSlaSatisfied * 100) / broker.getCloudletsFinishedList().size()));
        System.out.printf("\nTotal of cloudlets SLA satisfied: %.0f de %d", totalOfcloudletSlaSatisfied, broker.getCloudletsFinishedList().size());*/
        return (totalOfcloudletSlaSatisfied * 100) / broker.getCloudletsFinishedList().size();
    }
    
      double getSumPesVms() {
        return vmList.stream()
                .mapToDouble(vm -> vm.getNumberOfPes())
                .sum();
    }

    double getSumPesCloudlets() {
        return cloudletList.stream()
                .mapToDouble(c -> c.getNumberOfPes())
                .sum();
    }

    /**
     * Gets the ratio of existing vPEs (VM PEs) divided by the number
     * of required PEs of all Cloudlets, which indicates
     * the mean number of vPEs that are available for each PE required 
     * by a Cloudlet, considering all the existing Cloudlets.
     * For instance, if the ratio is 0.5, in average, two Cloudlets
     * requiring one vPE will share that same vPE.
     * @return the average of vPEs/CloudletsPEs ratio
     */
    double getRatioOfExistingVmPesToRequiredCloudletPes() {
        double sumPesVms = getSumPesVms();
        double sumPesCloudlets = getSumPesCloudlets();
    //    System.out.println("\n\t -> Pe cloudlets / PE vm: " + sumPesVms/sumPesCloudlets);

        return sumPesVms / sumPesCloudlets;
    }
    
     /**
     * Shows the wait time of cloudlets
     *
     * @param cloudlet list of cloudlets
     */
    public void waitTimeAverage(List<Cloudlet> cloudlet) {
        double waitTime = 0, quant = 0;
        for (Cloudlet cloudlets : cloudlet) {
            waitTime += cloudlets.getWaitingTime();
            quant++;
        }
        System.out.println("\n# The wait time is: " + waitTime/quant);
    }
      

    /**
     * A main method just for test purposes.
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        final long seed = System.currentTimeMillis();
        ContinuousDistribution randCloudlet = new UniformDistr(seed);
        ContinuousDistribution randVm = new UniformDistr(seed);
        CloudletTaskTimeCompletionWorkLoadExperimet exp
                = new CloudletTaskTimeCompletionWorkLoadExperimet(randCloudlet, randVm);
        exp.setVerbose(true);
        exp.run();
        exp.getCloudletsTaskTimeCompletionAverage();
        exp.getPercentageOfCloudletsMeetingTaskTimeCompletion();
        exp.getRatioOfExistingVmPesToRequiredCloudletPes();
        exp.waitTimeAverage(exp.getCloudletList());
    }
}
