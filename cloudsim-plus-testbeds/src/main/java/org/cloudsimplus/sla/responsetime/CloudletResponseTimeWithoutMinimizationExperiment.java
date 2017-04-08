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
import java.util.function.Predicate;
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
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
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
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.sla.readJsonFile.CpuUtilization;
import org.cloudsimplus.sla.readJsonFile.ResponseTime;
import org.cloudsimplus.sla.readJsonFile.SlaReader;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeWithoutMinimizationRunner.CLOUDLETS;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeWithoutMinimizationRunner.CLOUDLET_LENGTHS;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeWithoutMinimizationRunner.VMS;
import static org.cloudsimplus.sla.responsetime.CloudletResponseTimeWithoutMinimizationRunner.VM_PES;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 *
 * @author raysaoliveira
 */
public class CloudletResponseTimeWithoutMinimizationExperiment extends SimulationExperiment{

    private static final int SCHEDULING_INTERVAL = 5;

    /**
     * The interval to request the creation of new Cloudlets.
     */
    private static final int CLOUDLETS_CREATION_INTERVAL = SCHEDULING_INTERVAL * 3;

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
    public static final String METRICS_FILE = ResourceLoader.getResourcePath(CloudletResponseTimeWithoutMinimizationExperiment.class, "SlaMetrics.json");
    private double cpuUtilizationSlaContract;
    private double responseTimeSlaContract;

    public CloudletResponseTimeWithoutMinimizationExperiment(ContinuousDistribution randCloudlet, ContinuousDistribution randVm) {
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

          //  getCloudsim().addOnClockTickListener(this::createNewCloudlets);
            getCloudsim().addOnClockTickListener(this::printVmsCpuUsage);
        } catch (IOException ex) {
            Logger.getLogger(CloudletResponseTimeWithoutMinimizationExperiment.class.getName()).log(Level.SEVERE, null, ex);
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
        cloudletList = new ArrayList<>(CLOUDLETS);
        DatacenterBroker broker0 = getFirstBroker();
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudletList.add(createCloudlet(broker0));
        }

        return cloudletList;
    }

    private Cloudlet createCloudlet(DatacenterBroker broker) {
        final int id = createdCloudlets++;
        final int i = (int) (randCloudlet.sample() * CLOUDLET_LENGTHS.length);
        final long length = CLOUDLET_LENGTHS[i];

        UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(id, length, 2)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModel(utilization)
                .setBroker(broker);
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
        final int i = (int) (randVm.sample() * VM_PES.length);
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
     * on CPU usage. A reference to this method is assigned to
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

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(20480, 100000, 100000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    @Override
    protected DatacenterBroker createBroker() {
        DatacenterBroker broker0;
        broker0 = new DatacenterBrokerSimple(getCloudsim());
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

    double getPercentageOfCloudletsMeetingResponseTime() {
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);

        double totalOfcloudletSlaSatisfied = broker.getCloudletsFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .filter(rt -> rt <= responseTimeSlaContract)
                .count();

        System.out.printf("\n ** Percentage of cloudlets that complied with "
                + "the SLA Agreement:  %.2f %%",
                ((totalOfcloudletSlaSatisfied * 100) /broker.getCloudletsFinishedList().size()));
        System.out.printf("\nTotal of cloudlets SLA satisfied: %.0f de %d", totalOfcloudletSlaSatisfied, broker.getCloudletsFinishedList().size());
        return (totalOfcloudletSlaSatisfied * 100 )/broker.getCloudletsFinishedList().size();
    }

    double getSumPesVms() {
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);

        double sumPesVms = broker.getVmsCreatedList().stream()
                .mapToLong(vm -> vm.getNumberOfPes())
                .sum();

        return sumPesVms;
    }

    double getSumPesCloudlets() {
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);

        double sumPesCloudlet = broker.getCloudletsCreatedList().stream()
                .mapToLong(c -> c.getNumberOfPes())
                .sum();

        return sumPesCloudlet;
    }

    double getDivPesVmsByPesCloudlets() {
        double sumPesVms = getSumPesVms();
        double sumPesCloudlets = getSumPesCloudlets();

        return sumPesVms / sumPesCloudlets;
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
        CloudletResponseTimeWithoutMinimizationExperiment exp =
                new CloudletResponseTimeWithoutMinimizationExperiment(randCloudlet, randVm);
        exp.setVerbose(true);
        exp.run();
        exp.getCloudletsResponseTimeAverage();
        exp.getPercentageOfCloudletsMeetingResponseTime();
        exp.getDivPesVmsByPesCloudlets();
    }
}
