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
/*
 */
package org.cloudsimplus.testbeds.hostfaultinjection;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.faultinjection.VmCloner;
import org.cloudsimplus.faultinjection.VmClonerSimple;
import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.Experiment;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.vmtemplates.AwsEc2Template;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.testbeds.hostfaultinjection.HostFaultInjectionRunner.CLOUDLET_LENGTHS;

/**
 * An experiment using a {@link HostFaultInjection} to generate random Host failures.
 * The experiment sets a {@link VmCloner}
 * linked to {@link DatacenterBroker} when all VMs associated with that broker are destroyed.
 * This way, new VMs are created for fault recovery.
 *
 * The experiment assesses if the SLA contract of the customer (represented by a {@link DatacenterBroker}
 * is being met or not.
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 */
final class HostFaultInjectionExperiment extends Experiment {
    /**
     * The list of SLA Contracts in JSON format which are used to assess
     * if the metrics in those contracts are being met.
     * This file is stored into the resources directory.
     */
    private static final String SLA_CONTRACTS_LIST = "sla-files.txt";

    /*The average number of failures expected to happen each hour
    in a Poisson Process, which is also called event rate or rate parameter.*/
    private static final double MEAN_FAILURE_NUMBER_PER_HOUR = 0.025;

    /**
     * The simulation time (in HOURS) after which, no failure will be generated,
     * allowing the simulation to finish any moment after this time.
     */
    private static final int MAX_TIME_TO_GENERATE_FAILURE_IN_HOURS = 800;

    /**
     * The time interval (in seconds) in which {@link Datacenter} events will be processed.
     */
    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 300; // (5 seconds * 60 (1 minute))

    private static final int HOST_PES = 4;
    private static final long HOST_RAM = 500000; //host memory (MEGA)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;

    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MEGA)
    private static final long VM_BW = 100000;

    private static final int CLOUDLET_PES = 2;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * Number of Hosts to create for each Datacenter. The number of elements in
     * this array defines the number of Datacenters to be created.
     */
    private static final int HOSTS = 30;

    private List<Host> hostList;

    private HostFaultInjection faultInjection;

    /**
     * A random number generator used to define the number of cloudlets to create.
     */
    private final ContinuousDistribution randCloudlet;

    /**
     * The map of SLA Contracts for each customer represented by a {@link DatacenterBroker}.
     */
    private Map<DatacenterBroker, SlaContract> contractsMap;

    /**
     * The map of Amazon EC2 Templates which will be used to create VMs for
     * each customer represented by a {@link DatacenterBroker}.
     */
    private Map<DatacenterBroker, AwsEc2Template> templatesMap;

    private HostFaultInjectionExperiment(final long seed) {
        this(0, null, seed);
    }

    HostFaultInjectionExperiment(final int index, final ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private HostFaultInjectionExperiment(final int index, final ExperimentRunner runner, final long seed) {
        super(index, runner, seed);
        setBrokersNumber(readContractList().size());
        setAfterExperimentBuild(exp -> getDatacenterList().forEach(this::createFaultInjectionForHosts));
        setVmsByBrokerFunction(broker -> getContract(broker).getMinFaultToleranceLevel());
        setHostsNumber(HOSTS);
        this.randCloudlet = new UniformDistr(this.getSeed());
        contractsMap = new HashMap<>();
        templatesMap = new HashMap<>();
    }

    /**
     * Read all SLA contracts registered in the {@link #SLA_CONTRACTS_LIST}.
     * When the brokers are created, it is ensured the number of brokers
     * is equals to the number of SLA contracts in the {@link #SLA_CONTRACTS_LIST}.
     */
    private void readTheSlaContracts() throws IOException {
        final Iterator<DatacenterBroker> brokerIterator = getBrokerList().iterator();
        final List<AwsEc2Template> all = readAllAvailableAwsEc2Instances();
        for (final String file: readContractList()) {
            SlaContract contract = SlaContract.getInstance(file);
            DatacenterBroker b = brokerIterator.next();
            contractsMap.put(b, contract);
            templatesMap.put(b, getSuitableAwsEc2InstanceTemplate(b, all));
        }
    }

    private List<String> readContractList() {
        return ResourceLoader
                .newBufferedReader(SLA_CONTRACTS_LIST, getClass())
                .lines()
                .filter(l -> !l.startsWith("#"))
                .filter(l -> !l.trim().isEmpty())
                .collect(toList());
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
    private AwsEc2Template getSuitableAwsEc2InstanceTemplate(final DatacenterBroker broker, final List<AwsEc2Template> all) {
        if(all.isEmpty()){
            throw new RuntimeException("There aren't VM templates to create VMs for customer " + broker.getId());
        }

        final SlaContract contract = getContract(broker);
        AwsEc2Template selected = getMostPowerfulVmTemplateForCustomerPrice(contract, all);
        if (selected != AwsEc2Template.NULL) {
            return selected;
        }

        selected = getCheapestVmTemplate(broker, all);

        return selected;
    }

    /**
     * Try to find the most powerful VM which, running a number of instances equal to the
     * customer fault tolerance level, the total cost is lower or equal to
     * the maximum price the customer is willing to pay.
     * @return the most powerful VM according to customer contract or {@link AwsEc2Template#NULL}
     *         if a suitable template could not be found
     */
    private AwsEc2Template getMostPowerfulVmTemplateForCustomerPrice(final SlaContract contract, final List<AwsEc2Template> all) {
        final Comparator<AwsEc2Template> comparator = Comparator.naturalOrder();
        return all.stream()
            .filter(t -> getActualPriceForAllVms(contract, t) <= contract.getMaxPrice())
            .max(comparator)
            .orElse(AwsEc2Template.NULL);
    }

    /**
     * If a VM template matching the customer contract cannot be found,
     * gets the cheapest VM from the entire list and
     * computes the new k-fault-tolerance level which is possible using such a VM.
     * That is, computes the k number of VMs which can be created
     * from that template, that will not exceed the total price the customer
     * is willing to pay.
     *
     * At the end, updates the customer contract.
     *
     * @param broker the broker representing a customer to get the cheapest {@link AwsEc2Template}
     *               which maximizes the number of VMs for the customer's expected price
     * @param all the list of all existing {@link AwsEc2Template}s
     * @return the cheaper VM template
     */
    private AwsEc2Template getCheapestVmTemplate(final DatacenterBroker broker, final List<AwsEc2Template> all) {
        final SlaContract contract = getContract(broker);
        final AwsEc2Template instance =
            all.stream()
                .min(comparingDouble(AwsEc2Template::getPricePerHour))
                .orElseThrow(() ->
                    new RuntimeException(
                        "A VM template matching customer "+broker.getId() +
                        " contract could not be found and there isn't any cheaper one available."));

        final int faultToleranceLevel = getFaultToleranceLevelForTemplate(contract, instance);
        System.out.printf(
            "# There isn't any available VM template having an individual price of $%.2f, %n", contract.getExpectedMaxPriceForSingleVm());
        System.out.printf(
            "  which enables meeting the %d-fault-tolerance level defined by broker %d.%n",
            contract.getMinFaultToleranceLevel(), broker.getId());
        System.out.printf(
            "  The fault-tolerance level was reduced to %d (enabling %d VMs to run simultaneously).%n", faultToleranceLevel, faultToleranceLevel);
        /*
        After the k fault tolerance level was reduced because there isn't any VM that it's individual
        price multiplied by the k is lower or equal to the total price the customer is willing to pay.
        */
        contract.getFaultToleranceLevel().getMinDimension().setValue(faultToleranceLevel);
        return instance;
    }

    /**
     * Computes the fault-tolerance achieved by using of a given template,
     * considering the max price a customers is willing to pay for all VMs.
     * @param contract the customer contract
     * @param instance the instance type to compute the k-fault-tolerance level
     * @return the computed k-fault-tolerance level, where the minimum value for k will be 1
     */
    private int getFaultToleranceLevelForTemplate(final SlaContract contract, final AwsEc2Template instance) {
        final int faultToleranceLevel = (int)Math.floor(contract.getMaxPrice() / instance.getPricePerHour());
        return Math.max(faultToleranceLevel, 1);
    }

    private SlaContract getContract(final DatacenterBroker broker){
        return contractsMap.get(broker);
    }

    private List<AwsEc2Template> readAllAvailableAwsEc2Instances() throws IOException {
        final List<AwsEc2Template> instances = new ArrayList<>();
        //Lists the files into the given directory
        try (BufferedReader br = ResourceLoader.newBufferedReader("instance-files.txt", getClass())) {
            while (br.ready()) {
                final String file = br.readLine();
                final AwsEc2Template instance = AwsEc2Template.getInstance("vmtemplates/aws/"+file);
                instances.add(instance);
            }
        }
        return instances;
    }

    public Vm createVm(final DatacenterBroker broker, final int id) {
        final AwsEc2Template template = templatesMap.get(broker);
        final Vm vm = new VmSimple(id, VM_MIPS, template.getCpus());
        vm
            .setRam(template.getMemoryInMB()).setBw(VM_BW).setSize(VM_SIZE)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setDescription(template.getName());
        return vm;
    }

    @Override
    protected List<Cloudlet> createCloudlets(final DatacenterBroker broker) {
        final int cloudlets = getVmsByBrokerFunction().apply(broker)*2;
        final List<Cloudlet> list = new ArrayList<>(cloudlets);
        for (int id = getCloudletList().size(); id < getCloudletList().size() + cloudlets; id++) {
            list.add(createCloudlet(broker));
        }

        return list;
    }

    /**
     * Creates the number of Cloudlets defined in {@code createCloudlets #cloudlets} and submits
     * them to the given broker.
     *
     * @return the List of created Cloudlets
     */
    public Cloudlet createCloudlet(final DatacenterBroker broker) {
        final int i = (int) (randCloudlet.sample() * CLOUDLET_LENGTHS.length);
        final long length = CLOUDLET_LENGTHS[i];
        final UtilizationModel um = new UtilizationModelDynamic(UtilizationModel.Unit.ABSOLUTE, 50);

        final Cloudlet cloudlet
            = new CloudletSimple(nextCloudletId(), length, CLOUDLET_PES)
            .setFileSize(CLOUDLET_FILESIZE)
            .setOutputSize(CLOUDLET_OUTPUTSIZE)
            .setUtilizationModelCpu(new UtilizationModelFull())
            .setUtilizationModelRam(um)
            .setUtilizationModelBw(um);
        return cloudlet;
    }

    @Override
    protected Datacenter createDatacenter(final int index) {
        final Datacenter dc = super.createDatacenter(index);
        dc.setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    @Override
    protected Host createHost(final int id) {
        final List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(8000, new PeProvisionerSimple()));
        }

        final Host host = new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, pesList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());

        host.setId(id);
        return host;
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter
     */
    private void createFaultInjectionForHosts(final Datacenter datacenter) {
        PoissonDistr poisson = new PoissonDistr(MEAN_FAILURE_NUMBER_PER_HOUR, getSeed());

        faultInjection = new HostFaultInjection(datacenter, poisson);
        getFaultInjection().setMaxTimeToFailInHours(MAX_TIME_TO_GENERATE_FAILURE_IN_HOURS);

        for (DatacenterBroker broker : getBrokerList()) {
            getFaultInjection().addVmCloner (broker, new VmClonerSimple(this::cloneVm, this::cloneCloudlets));
        }

        System.out.printf(
            "\tFault Injection created for %s.%n\tMean Number of Failures per hour: %.6f (1 failure expected at each %.4f hours).%n",
            datacenter, MEAN_FAILURE_NUMBER_PER_HOUR, poisson.getInterArrivalMeanTime());
    }

    /**
     * Clones a VM by creating another one with the same configurations of a
     * given VM.
     *
     * @param vm the VM to be cloned
     * @return the cloned (new) VM.
     * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private Vm cloneVm(final Vm vm) {
        final Vm clone = new VmSimple(vm.getMips(), (int) vm.getNumberOfPes());
        /*
        It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the vm to its clone,
        since the clone ID will be 10 times the id of its
        source VM.
        */
        clone.setId(vm.getId() * 10);
        clone.setDescription("Clone of VM " + vm.getId());
        clone
            .setSize(vm.getStorage().getCapacity())
            .setBw(vm.getBw().getCapacity())
            .setRam(vm.getBw().getCapacity())
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        System.out.printf("%n%n#Cloning VM %d from Host %d%n\tMips %.2f Number of Pes: %d%n",
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
    private List<Cloudlet> cloneCloudlets(final Vm sourceVm) {
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
    private Cloudlet cloneCloudlet(final Cloudlet source, final long length) {
        final Cloudlet clone
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
        for (final DatacenterBroker broker : getBrokerList()) {
            new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
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
        return new DatacenterBrokerSimple(getSimulation());
    }

    public double getRatioVmsPerHost() {
        return getVmList().size() / (double) HOSTS;
    }

    /**
     * Computes the percentage of customers for whom the availability stated
     * in the SLA was met (in scale from 0 to 1, where 1 is 100%).
     *
     * @return
     */
    public double getPercentageOfAvailabilityMeetingSla() {
        final double totalOfAvailabilitySatisfied = getBrokerList()
            .stream()
            .filter(b -> faultInjection.availability(b) * 100 >= getCustomerMinAvailability(b))
            .count();
        return totalOfAvailabilitySatisfied / getBrokerList().size();
    }

    /**
     * Takes minimum customer availability.
     * @param broker
     * @return minimum customer availability
     */
    private double getCustomerMinAvailability(final DatacenterBroker broker) {
        return contractsMap.get(broker).getAvailabilityMetric().getMinDimension().getValue();

    }

    /**
     * Calculates the total cost of all VMs a given broker executed,
     * for the entire simulation time.
     */
    public double getTotalCost(final DatacenterBroker broker) {
        final SlaContract contract = getContract(broker);

        final AwsEc2Template template = templatesMap.get(broker);
        final double totalPriceForVmsInOneHour = getActualPriceForAllVms(contract, template);
        final double totalExecutionTimeForVmsInHours = getTotalExecutionTimeForVmsInHours(broker);

        return totalPriceForVmsInOneHour * totalExecutionTimeForVmsInHours;
    }

    /**
     * Gets the actual total price if a given VM template is used for a given customer.
     * @param contract the contract of the customer
     * @param template the template to compute the total price for that contract
     * @return
     */
    private double getActualPriceForAllVms(final SlaContract contract, final AwsEc2Template template) {
        return template.getPricePerHour()*contract.getMinFaultToleranceLevel();
    }

    /**
     * Gets the actual price of all customers VMs per hour, considering the entire simulation time.
     * It's the total VMs cost mean.
     * @param broker
     * @return
     */
    public double getCustomerActualPricePerHour(final DatacenterBroker broker) {
        return getTotalCost(broker)/getTotalExecutionTimeForVmsInHours(broker);
    }

    /**
     * Gets the total time all VMs from a given broker executed during the simulation (in hours).
     * @param broker
     * @return
     */
    private double getTotalExecutionTimeForVmsInHours(final DatacenterBroker broker) {
        return broker.getVmCreatedList().stream().mapToDouble(Vm::getTotalExecutionTime).sum()/3600.0;
    }

    /**
     * Gets the price per hour for the AWS EC2 Template to be used for a given customer.
     */
    public Double getTemplatesMap(final DatacenterBroker broker) {
        return templatesMap.get(broker).getPricePerHour();
    }

    /**
     * A main method just for test purposes.
     *
     * @param args
     */
    public static void main(String[] args) {
        final HostFaultInjectionExperiment exp = new HostFaultInjectionExperiment(System.currentTimeMillis());

        exp.setVerbose(true).run();
        exp.getBrokerList().forEach(b -> System.out.printf("%s - Availability %%: %.4f%n", b, exp.getFaultInjection().availability(b) * 100));

        System.out.println("Percentage of Brokers meeting the Availability Metric in SLA: " + exp.getPercentageOfAvailabilityMeetingSla() * 100);
        System.out.println("# Ratio VMS per HOST: " + exp.getRatioVmsPerHost());
        System.out.printf("%n# Number of Host faults: %d%n", exp.getFaultInjection().getNumberOfHostFaults());
        System.out.println("# Number of VM faults (VMs destroyed): " + exp.getFaultInjection().getNumberOfFaults());
        System.out.printf("# VMs MTBF average: %.2f minutes%n", exp.getFaultInjection().meanTimeBetweenVmFaultsInMinutes());
        System.out.printf("# Time the simulations finished: %.2f minutes%n", exp.getSimulation().clockInMinutes());
        System.out.printf("# Hosts MTBF: %.2f minutes%n", exp.getFaultInjection().meanTimeBetweenHostFaultsInMinutes());
        System.out.printf("%n# If the hosts are showing in the result equal to 0, it was because the vms ended before the failure was set.%n%n");
    }

    public HostFaultInjection getFaultInjection() {
        return faultInjection;
    }

    /**
     * A map containing the {@link SlaContract} associated to each
     * {@link DatacenterBroker} representing a customer.
     */
    public Map<DatacenterBroker, SlaContract> getContractsMap() {
        return contractsMap;
    }
}
