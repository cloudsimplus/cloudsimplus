/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.responsetime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.HorizontalVmScalingSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilderHelper;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.sla.readJsonFile.CpuUtilization;
import org.cloudsimplus.sla.readJsonFile.ResponseTime;
import org.cloudsimplus.sla.readJsonFile.SlaReader;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeMinimizationRunner.CLOUDLETS;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeMinimizationRunner.CLOUDLET_LENGTHS;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeMinimizationRunner.VMS;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeMinimizationRunner.VM_PES;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 * An experiment of dynamic creation of vm at runtime, respecting the 
 * cpu usage limit and the free number of each VM, thus  selecting 
 * an "ideal" VM for a given cloudlet, which will then minimize Cloudlet
 * response time.
 * 
 * @author raysaoliveira
 */
public final class CloudletResponseTimeMinimizationExperiment extends SimulationExperiment {

    private static final int SCHEDULING_INTERVAL = 5;
    
    /**
     * The interval to request the creation of new Cloudlets.
     */
    private static final int CLOUDLETS_CREATION_INTERVAL = SCHEDULING_INTERVAL * 5;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;

    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private final ContinuousDistribution randCloudlet, randVm;

    private int createdCloudlets;
    private int createsVms;
   
    /**
     * The file containing the SLA Contract in JSON format.
     */
    public static final String METRICS_FILE = ResourceLoader.getResourcePath(CloudletResponseTimeMinimizationExperiment.class, "SlaMetrics.json");
    private double cpuUtilizationSlaContract;
    private double responseTimeSlaContract;
    
    public CloudletResponseTimeMinimizationExperiment(ContinuousDistribution randCloudlet, ContinuousDistribution randVm) {
        super();
        this.randCloudlet = randCloudlet;
        this.randVm = randVm;
        try {            
       
            SlaReader slaReader = new SlaReader(METRICS_FILE);
            ResponseTime rt = new ResponseTime(slaReader);
            rt.checkResponseTimeSlaContract();
            responseTimeSlaContract = rt.getMaxValueResponseTime();
            
            CpuUtilization cpu = new CpuUtilization(slaReader);
            cpu.checkCpuUtilizationSlaContract();
            cpuUtilizationSlaContract = cpu.getMaxValueCpuUtilization();
            
            getCloudsim().addOnClockTickListener(this::createNewCloudlets);
            getCloudsim().addOnClockTickListener(this::printVmsCpuUsage); 

        } catch (IOException ex) {
            Logger.getLogger(CloudletResponseTimeMinimizationExperiment.class.getName()).log(Level.SEVERE, null, ex);
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

        new CloudletsTableBuilderHelper(finishedCloudlets).build();
    }

    @Override
    protected List<Cloudlet> createCloudlets(DatacenterBroker broker) {
        cloudletList = new ArrayList<>(CLOUDLETS);
        DatacenterBroker broker0 = getFirstBroker(); 
        for(int i =0; i < CLOUDLETS; i++){
            cloudletList.add(createCloudlet(broker0));
        }
        return cloudletList;     
    }
    
    private Cloudlet createCloudlet(DatacenterBroker broker) {
        final int id = createdCloudlets++;
        final int i = (int) (randCloudlet.sample()*CLOUDLET_LENGTHS.length);
        final long length = CLOUDLET_LENGTHS[i];
      
        UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(id, length, 2)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModel(utilization)
                .setBroker(broker);
    }
    
      /**
     * Creates new Cloudlets at every 10 seconds up to the 50th simulation
     * second. A reference to this method is set as the {@link EventListener} to
     * the {@link Simulation#addOnClockTickListener(EventListener)}. The method
     * is then called every time the simulation clock advances.
     *
     * @param eventInfo the information about the OnClockTick event that has
     * happened
     */
    private void createNewCloudlets(EventInfo eventInfo) {
        final long time = (long) eventInfo.getTime();
        DatacenterBroker broker0 = getFirstBroker(); 
        if (time > 0 && time % CLOUDLETS_CREATION_INTERVAL == 0 && time <= 50) {
            final int numberOfCloudlets = 4;
            Log.printFormattedLine("\t#Creating %d Cloudlets at time %d.", numberOfCloudlets, time);
            List<Cloudlet> newCloudlets = new ArrayList<>(numberOfCloudlets);
            for (int i = 0; i < numberOfCloudlets; i++) {
                Cloudlet cloudlet = createCloudlet(broker0);
                cloudletList.add(cloudlet);
                newCloudlets.add(cloudlet);
            }

            broker0.submitCloudletList(newCloudlets);
        }
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
        Log.printLine("\t\tCreated VMs: " + createdVms);
        Comparator<Vm> sortByNumberOfFreePes
                = Comparator.comparingInt(vm -> getExpectedNumberOfFreeVmPes(vm));
        Comparator<Vm> sortByExpectedCloudletResponseTime
                = Comparator.comparingDouble(vm -> getExpectedCloudletResponseTime(cloudlet, vm));
        createdVms.sort(
                sortByNumberOfFreePes
                        .thenComparing(sortByExpectedCloudletResponseTime)
                        .reversed());
        Vm mostFreePesVm = createdVms.stream().findFirst().orElse(Vm.NULL);

        Vm selectedVm = createdVms.stream()
                .filter(vm -> getExpectedNumberOfFreeVmPes(vm) >= cloudlet.getNumberOfPes())
                .filter(vm -> getExpectedCloudletResponseTime(cloudlet, vm) <= responseTimeSlaContract)
                .findFirst().orElse(mostFreePesVm);

        return selectedVm;
    }
    
    private double getExpectedCloudletResponseTime(Cloudlet cloudlet, Vm vm) {
        final double expectedResponseTime = cloudlet.getLength() / vm.getMips();
        return expectedResponseTime;
    }

    /**
     * Gets the expected amount of free PEs for a VM
     *
     * @param vm the VM to get the amount of free PEs
     * @return the number of PEs that are free or a negative value that indicate
     * there aren't free PEs (this negative number indicates the amount of
     * overloaded PEs)
     */
    private int getExpectedNumberOfFreeVmPes(Vm vm) {
        final int totalPesNumberForCloudletsOfVm
                = vm.getBroker().getCloudletsCreatedList().stream()
                        .filter(c -> c.getVm().equals(vm))
                        .mapToInt(Cloudlet::getNumberOfPes)
                        .sum();

        final int numberOfVmFreePes
                = vm.getNumberOfPes() - totalPesNumberForCloudletsOfVm;

        Log.printFormattedLine(
                "\t\tTotal pes of cloudlets in VM " + vm.getId() + ": " + 
                totalPesNumberForCloudletsOfVm + " -> vm pes: " + 
                vm.getNumberOfPes() + " -> vm free pes: " + numberOfVmFreePes);
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
        for(int i =0; i < VMS; i++){
            Vm vm = createVm();
            createHorizontalVmScaling(vm);
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
        final int i = (int) (randVm.sample()*VM_PES.length);
        final int pes = VM_PES[i];
      

        Vm vm = new VmSimple(id, 1000, pes)
                .setRam(512).setBw(1000).setSize(10000).setBroker(broker0)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }
    
    /**
     * Creates a {@link HorizontalVmScaling} object for a given VM.
     *
     * @param vm the VM in which the Horizontal Scaling will be created
     * @see #createListOfScalableVms(int)
     */
    private void createHorizontalVmScaling(Vm vm) {
        HorizontalVmScaling horizontalScaling = new HorizontalVmScalingSimple();
        horizontalScaling
                .setVmSupplier(this::createVm)
                .setOverloadPredicate(this::isVmOverloaded);
        vm.setHorizontalScaling(horizontalScaling);
    }
    
     /**
     * A {@link Predicate} that checks if a given VM is overloaded or not based
     * on response time max value. A reference to this method is assigned to
     * each Horizontal VM Scaling created.
     *
     * @param vm the VM to check if it is overloaded
     * @return true if the VM is overloaded, false otherwise
     * @see #createHorizontalVmScaling(Vm)
     */
    private boolean isVmOverloaded(Vm vm) {
        return vm.getCurrentCpuPercentUse() > cpuUtilizationSlaContract;
    }
    
    @Override
    protected List<Host> createHosts() {
        hostList = new ArrayList<>(HOSTS);
        for(int i =0; i < HOSTS; i++){
            hostList.add(createHost());
        }
        return hostList;     
    }
    
    private Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple(new Ram(20480));
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple(new Bandwidth(10000));
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(id, 10000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    @Override
    protected DatacenterBroker createBroker() {
        DatacenterBroker broker0;
        broker0 = new DatacenterBrokerSimple(getCloudsim());
        broker0.setVmMapper(this::selectVmForCloudlet);
        return broker0;
    }

    /**
     * Computes the response time average for all finished Cloudlets
     * on this experiment.
     * @return the response time average
     */
    double getCloudletsResponseTimeAverage() {
        SummaryStatistics cloudletResponseTime = new SummaryStatistics();
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);
        broker.getCloudletsFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .forEach(cloudletResponseTime::addValue);
        
        Log.printFormattedLine(
                "\t\t\n Response Time simulation: %.2f \n Response Time contrato SLA: %.2f \n",
                 cloudletResponseTime.getMean(), responseTimeSlaContract);
        return cloudletResponseTime.getMean();
    }
   
    /**
     * A main method just for test purposes.
     * @param args
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        final long seed = System.currentTimeMillis();
        ContinuousDistribution randCloudlet = new UniformDistr(seed);
        ContinuousDistribution randVm = new UniformDistr(seed);
        CloudletResponseTimeMinimizationExperiment exp = 
                new CloudletResponseTimeMinimizationExperiment(randCloudlet, randVm);
        exp.setVerbose(true);
        exp.run();
        exp.getCloudletsResponseTimeAverage();
    }  
}
