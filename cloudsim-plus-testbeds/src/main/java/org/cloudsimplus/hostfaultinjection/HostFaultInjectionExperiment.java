/*
 */
package org.cloudsimplus.hostfaultinjection;

import static org.cloudsimplus.hostfaultinjection.HostFaultInjectionRunner.CLOUDLETS;
import static org.cloudsimplus.hostfaultinjection.HostFaultInjectionRunner.CLOUDLET_LENGTHS;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
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
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.vmtemplates.AwsEc2Template;
import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 * An experiment using a {@link HostFaultInjection} which it set a VM cloner
 * linked to Broker when all VMs associated with it are destroyed.
 *
 * @author raysaoliveira
 */
public final class HostFaultInjectionExperiment extends SimulationExperiment {
    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 300; // (5 seconds * 60 (1 minute))

    private static final int HOST_PES = 4;
    private static final long HOST_RAM = 500000; //host memory (MEGABYTE)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;

    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MEGABYTE)
    private static final long VM_BW = 100000;

    private static final int CLOUDLET_PES = 2;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * Number of Hosts to create for each Datacenter. The number of elements in
     * this array defines the number of Datacenters to be created.
     */
    private static final int HOSTS = 50;
    public static final String SLA_CONTRACTS_LIST = "sla-files.txt";

    private List<Host> hostList;

    private HostFaultInjection faultInjection;

    private final ContinuousDistribution randCloudlet;
    /**
     * A map containing the {@link SlaContract} associated to each
     * {@link DatacenterBroker} representing a customer.
     */
    private Map<DatacenterBroker, SlaContract> contractsMap;
    /**
     * A map of AWS EC2 Template to be used for each customer.
     */
    private Map<DatacenterBroker, AwsEc2Template> templatesMap;

    private HostFaultInjectionExperiment(final long seed) {
        this(0, null, seed);
    }

    HostFaultInjectionExperiment(int index, ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private HostFaultInjectionExperiment(int index, ExperimentRunner runner, long seed) {
        super(index, runner, seed);
        setNumBrokersToCreate((int)numberSlaContracts());
        setAfterScenarioBuild(exp -> createFaultInjectionForHosts(getDatacenter0()));
        this.randCloudlet = new UniformDistr(this.getSeed());
        contractsMap = new HashMap<>();
        templatesMap = new HashMap<>();
    }

    /**
     * Read all SLA contracts registered in the {@link #SLA_CONTRACTS_LIST}.
     */
    private void readTheSlaContracts() throws IOException {
        Iterator<DatacenterBroker> brokerIterator = getBrokerList().iterator();
        final List<AwsEc2Template> all = readAllAvailableAwsEc2Instances();
        try (BufferedReader br = slaContractsListReader()) {
            while (br.ready() || brokerIterator.hasNext()) {
                final String file =  br.readLine();
                SlaContract contract = SlaContract.getInstanceFromResourcesDir(getClass(), file);
                DatacenterBroker b = brokerIterator.next();
                contractsMap.put(b, contract);
                templatesMap.put(b, getSuitableAwsEc2InstanceTemplate(b, all));
            }
        }
    }

    private BufferedReader slaContractsListReader() throws FileNotFoundException {
        return ResourceLoader.getBufferedReader(getClass(), SLA_CONTRACTS_LIST);
    }

    private long numberSlaContracts()  {
        try {
            return slaContractsListReader().lines().count();
        } catch (FileNotFoundException e) {
            return 0;
        }
    }

    /**
     * Gets a suitable {@link AwsEc2Template}
     * for which it will be possible for
     * the customer to get the maximum number of VMs,
     * considering the price he/she expects to pay.
     *
     * @param broker the broker representing a customer to get a suitable {@link AwsEc2Template}
     *               which maximizes the number of VMs for the customer's expected price
     * @param all the list of all existing {@link AwsEc2Template}s
     * @return the selected {@link AwsEc2Template} which will allow the customer
     * to run the maximum number of VMs
     */
    private AwsEc2Template getSuitableAwsEc2InstanceTemplate(DatacenterBroker broker, List<AwsEc2Template> all) throws IOException {
        final List<AwsEc2Template> suitableTemplates = all.stream()
            .filter(t -> t.getPricePerHour() <= getCustomerMaxPrice(broker))
            .collect(toList());

        final double maxPrice = getCustomerMaxPrice(broker);
        final AwsEc2Template template = suitableTemplates
            .stream()
            .max(comparingDouble(t -> getMaxNumberOfVmsForCustomerExpectedPrice(broker, t)))
            .orElseThrow(() -> new RuntimeException("No AWS EC2 Instance found with price lower or equal to " + maxPrice));

        final AwsEc2Template selected = new AwsEc2Template(template);
        selected.setMaxNumberOfVmsForCustomer(getMaxNumberOfVmsForCustomerExpectedPrice(broker, selected));
        System.out.println(
            "AWS EC2 Template selected for broker " + broker + ": " + selected + " maxNumberOfVMs: " +
            selected.getMaxNumberOfVmsForCustomer());
        return selected;
    }

    /**
     * The maximum price a customer expects to pay hourly for his/her running VMs.
     * @param broker
     * @return
     */
    private double getCustomerMaxPrice(DatacenterBroker broker) {
        return contractsMap.get(broker).getPriceMetric().getMaxDimension().getValue();
    }

    /**
     * Gets the maximum number of VMs which can be created from
     * a given {@link AwsEc2Template}, considering
     * the price the customer expects to pay.
     *
     * @param broker the broker to get the maximum number of VMs which can be created from a given template
     * @param template an {@link AwsEc2Template}
     * @return the maxinum number of VMs which can be created from the given
     * {@link AwsEc2Template} for the customer's expected price
     */
    private double getMaxNumberOfVmsForCustomerExpectedPrice(DatacenterBroker broker, AwsEc2Template template) {
        return getCustomerMaxPrice(broker) / template.getPricePerHour();
    }

    private List<AwsEc2Template> readAllAvailableAwsEc2Instances() throws IOException {
        List<AwsEc2Template> instances = new ArrayList<>();
        //Lists the files into the given directory
        try (BufferedReader br = ResourceLoader.getBufferedReader(getClass(), "instances-files.txt")) {
            while (br.ready()) {
                final String file = br.readLine();
                final AwsEc2Template instance = AwsEc2Template.getInstanceFromResourcesDir("vmtemplates/aws/"+file);
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    protected List<Vm> createVms(DatacenterBroker broker) {
        final int numVms = (int)templatesMap.get(broker).getMaxNumberOfVmsForCustomer();
        List<Vm> list = new ArrayList<>(numVms);
        final int id = getVmList().size();
        for (int i = 0; i < numVms; i++) {
            Vm vm = createVm(broker, id + i, templatesMap.get(broker));
            list.add(vm);
        }
        return list;

    }

    public Vm createVm(DatacenterBroker broker, int id, AwsEc2Template template) {
        Vm vm = new VmSimple(id, VM_MIPS, template.getCpus());
        vm
            .setRam(template.getMemoryInMB()).setBw(VM_BW).setSize(VM_SIZE)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates the number of Cloudlets defined in {@link HostFaultInjectionRunner#CLOUDLETS} and submits
     * them to the given broker.
     *
     * @return the List of created Cloudlets
     */
    public Cloudlet createCloudlet(int id) {
        final int i = (int) (randCloudlet.sample() * CLOUDLET_LENGTHS.length);
        final long length = CLOUDLET_LENGTHS[i];

        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet c
            = new CloudletSimple(id, length, CLOUDLET_PES)
            .setFileSize(CLOUDLET_FILESIZE)
            .setOutputSize(CLOUDLET_OUTPUTSIZE)
            .setUtilizationModel(utilizationModel);
        return c;
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        final int id = getCloudletList().size();
        for (int i = 0; i < CLOUDLETS; i++) {
            list.add(createCloudlet(id + i));
        }

        return list;
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
        dc.setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    @Override
    protected List<Host> createHosts() {
        hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }
        return hostList;

    }

    /**
     * Creates a Host.
     *
     * @return
     */
    public Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(8000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
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
        /*The average number of failures expected to happen each minute
        in a Poisson Process, which is also called event rate or rate parameter.*/
        final double meanFailureNumberPerMinute = 0.009;
        //System.out.printf("Experiment %d seed: %d\n", getIndex(), getSeed());
        PoissonDistr poisson = new PoissonDistr(meanFailureNumberPerMinute, getSeed());
        //System.out.println("\n\t seed: " + getSeed());

        faultInjection = new HostFaultInjection(datacenter, poisson);
        getFaultInjection().setMaxTimeToGenerateFailure(100_000L);

        for (DatacenterBroker broker : getBrokerList()) {
            Vm lastVmFromBroker = broker.getWaitingVm(broker.getVmsWaitingList().size() - 1);
            getFaultInjection().addVmCloner(broker, this::cloneVm);
            getFaultInjection().addCloudletsCloner(broker, this::cloneCloudlets);
        }

        Log.printFormattedLine(
            "\tFault Injection created for %s.\n\tMean Number of Failures per Minute: %.6f (1 failure expected at each %.2f minutes).",
            datacenter, meanFailureNumberPerMinute, poisson.getInterarrivalMeanTime());
    }

    /**
     * Clones a VM by creating another one with the same configurations of a
     * given VM.
     *
     * @param vm the VM to be cloned
     * @return the cloned (new) VM.
     * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private Vm cloneVm(Vm vm) {
        Vm clone = new VmSimple((long) vm.getMips(), (int) vm.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the vm to its clone,
        since the clone ID will be 10 times the id of its
        source VM.*/
        clone.setId(vm.getId() * 10);
        clone.setDescription("Clone of VM " + vm.getId());
        clone
            .setSize(vm.getStorage().getCapacity())
            .setBw(vm.getBw().getCapacity())
            .setRam(vm.getBw().getCapacity())
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        Log.printFormattedLine("\n\n#Cloning VM %d from Host %d\n\tMips %.2f Number of Pes: %d ",
            vm.getId(), vm.getHost().getId(), clone.getMips(), clone.getNumberOfPes());

        return clone;
    }

    /**
     * Clones each Cloudlet associated to a given VM. The method is called when
     * a VM is destroyed due to a Host failure and a snapshot from that VM (a
     * clone) is started into another Host. In this case, all the Cloudlets
     * which were running inside the destroyed VM will be recreated from scratch
     * into the VM clone, re-starting their execution from the beginning.
     *
     * @param sourceVm the VM to clone its Cloudlets
     * @return the List of cloned Cloudlets.
     * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private List<Cloudlet> cloneCloudlets(Vm sourceVm) {
        final List<Cloudlet> sourceVmCloudlets = sourceVm.getCloudletScheduler().getCloudletList();
        final List<Cloudlet> clonedCloudlets = new ArrayList<>(sourceVmCloudlets.size());
        for (Cloudlet cl : sourceVmCloudlets) {
            clonedCloudlets.add(cloneCloudlet(cl, cl.getLength() - cl.getFinishedLengthSoFar()));
        }

        return clonedCloudlets;
    }

    /**
     * Creates a clone from a given Cloudlet.
     *
     * @param source the Cloudlet to be cloned.
     * @param length
     * @return the cloned (new) cloudlet
     */
    private Cloudlet cloneCloudlet(Cloudlet source, long length) {
        Cloudlet clone
            = new CloudletSimple(length, (int) source.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the cloudlet to its clone,
        since the clone ID will be 10 times the id of its
        source cloudlet.*/
        clone.setId(source.getId() * 10);
        clone
            .setUtilizationModelBw(source.getUtilizationModelBw())
            .setUtilizationModelCpu(source.getUtilizationModelCpu())
            .setUtilizationModelRam(source.getUtilizationModelRam());
        return clone;
    }

    @Override
    public void printResults() {
        for (DatacenterBroker broker : getBrokerList()) {
            new CloudletsTableBuilder(broker.getCloudletsFinishedList()).build();
        }
    }

    @Override
    protected void createBrokers() {
        super.createBrokers();
        try {
            readTheSlaContracts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudSim());
    }

    public double getRatioVmsPerHost() {
        double vmsize = getVmList().size();
        double hostsize = getHostList().size();
        return vmsize / hostsize;
    }

    /**
     * Computes the percentage of customers for whom the availability stated
     * in the SLA was met (in scale from 0 to 1, where 1 is 100%).
     *
     * @return
     */
    public double getPercentageOfAvailabilityMeetingSla() {
        double total = 0;
        double totalOfAvailabilitySatisfied = getBrokerList()
            .stream()
            .filter(b -> faultInjection.availability(b) >= getCustomerMinAvailability(b))
            .count();
        total = totalOfAvailabilitySatisfied / getBrokerList().size();

        return total;
    }

    private double getCustomerMinAvailability(DatacenterBroker b) {
        return contractsMap.get(b).getAvailabilityMetric().getMinDimension().getValue();
    }

    /**
     * Calculates the cost price of resources (processing, bw, memory, storage)
     * of each or all of the Datacenter VMs()
     */
    double getTotalCost(DatacenterBroker broker) {
        double convertMonth = 0;

        final List<Vm> vmList = broker.getVmsCreatedList();
        for (Vm vm : vmList) {
            double price = templatesMap.get(broker).getPricePerHour();
            double priceVm = price * vmList.size(); //price * vms allocated for this broker
            convertMonth = priceVm * 744; //price * a month
            double time = (getCloudSim().clockInHours()) / 24;
            System.out.println(" price VM " + vm.getId() + " = " + price + " clock: " + time);
        }

        return convertMonth;
    }

    /**
     * A main method just for test purposes.
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        HostFaultInjectionExperiment exp = new HostFaultInjectionExperiment(System.currentTimeMillis());

        exp.setVerbose(true).run();
        exp.getBrokerList().stream().forEach(b -> System.out.printf("%s - Availability %%: %.4f\n", b, exp.getFaultInjection().availability(b) * 100));

        System.out.println("Percentagem of Brokers that meeting the Availability Metric in SLA: " + exp.getPercentageOfAvailabilityMeetingSla() * 100);
        System.out.println("# Ratio VMS per HOST: " + exp.getRatioVmsPerHost());
        System.out.println("\n# Number of Host faults: " + exp.getFaultInjection().getNumberOfHostFaults());
        System.out.println("# Number of VM faults (VMs destroyed): " + exp.getFaultInjection().getNumberOfFaults());
        System.out.printf("# VMs MTBF average: %.2f minutes\n", exp.getFaultInjection().meanTimeBetweenVmFaultsInMinutes());
        Log.printFormattedLine("# Time that the simulations finished: %.2f minutes", exp.getCloudSim().clockInMinutes());
        Log.printFormattedLine("# Hosts MTBF: %.2f minutes", exp.getFaultInjection().meanTimeBetweenHostFaultsInMinutes());
        Log.printFormattedLine("\n# If the hosts are showing in the result equal to 0, it was because the vms ended before the failure was set.\n");

        for(DatacenterBroker b: exp.getBrokerList()){
            System.out.println(" VM COST :  " + exp.getTotalCost(b));

        }
    }

    public HostFaultInjection getFaultInjection() {
        return faultInjection;
    }

}
