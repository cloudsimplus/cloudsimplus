package org.cloudsimplus.migration;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationWorstFitStaticThreshold;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelArithmeticProgression;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudsimplus.sla.HostFaultInjection;
import org.cloudsimplus.sla.PoissonProcess;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

/**
 * This example migrates the vm when a fault happens.
 *
 * @author raysaoliveira
 */
public final class MigrationWhenHostFaultHappensExample {

    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;

    private static final int HOST_MIPS_BY_PE = 1000;
    private static final int HOST_NUMBER_OF_PES = 2;
    private static final long HOST_RAM = 500000; //host memory (MB)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;
    private ArrayList<PowerHost> hostList;
    /**
     * The percentage of host CPU usage that trigger VM migration due to over
     * utilization (in scale from 0 to 1, where 1 is 100%).
     */
    private static final double HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION = 0.7;

    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MB)
    private static final int VM_RAM = 10000; //vm memory (MB)
    private static final long VM_BW = 100000;
    private static final int VM_PES_NUM = 1; //number of cpus

    private static final long CLOUDLET_LENGHT = 20000;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * The percentage of CPU that a cloudlet will use when it starts executing
     * (in scale from 0 to 1, where 1 is 100%). For each cloudlet create, this
     * value is used as a base to define CPU usage.
     *
     * @see #createAndSubmitCloudlets(DatacenterBroker)
     */
    private static final double CLOUDLET_INITIAL_CPU_UTILIZATION_PERCENTAGE = 0.6;

    /**
     * Defines the speed (in percentage) that CPU usage of a cloudlet will
     * increase during the simulation tie. (in scale from 0 to 1, where 1 is
     * 100%).
     *
     * @see #createAndSubmitCloudletsWithDynamicCpuUtilization(double, double,
     * Vm, DatacenterBroker)
     */
    public static final double CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND = 0.05;

    private static final int NUMBER_OF_HOSTS_TO_CREATE = 3;
    private static final int NUMBER_OF_VMS_TO_CREATE = NUMBER_OF_HOSTS_TO_CREATE + 1;
    private static final int NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM = 1;

    private List<Vm> vmlist = new ArrayList<>();
    private CloudSim simulation;

    public MigrationWhenHostFaultHappensExample() {
        Log.printConcatLine("Starting ", VmMigrationWhenCpuMetricIsViolatedExample.class.getSimpleName(), "...");

        simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        createFaultInjectionForHosts(datacenter0);

        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        createAndSubmitVms(broker);

        createAndSubmitCloudlets(broker);

        simulation.start();

        new CloudletsTableBuilderHelper(broker.getCloudletsFinishedList()).build();

        Log.printConcatLine(VmMigrationWhenCpuMetricIsViolatedExample.class.getSimpleName(), " finished!");

    }

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new MigrationWhenHostFaultHappensExample();
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter0
     */
    private void createFaultInjectionForHosts(Datacenter datacenter0) {
        //Inject Fault
        long seed = System.currentTimeMillis();
        PoissonProcess poisson = new PoissonProcess(0.2, seed);

        UniformDistr failurePesRand = new UniformDistr(seed);
        for (int i = 0; i < datacenter0.getHostList().size(); i++) {
            for (Host host : datacenter0.getHostList()) {
                if (poisson.haveKEventsHappened()) {
                    UniformDistr delayForFailureOfHostRandom = new UniformDistr(1, 10, seed++);

                    //create a new intance of fault and start it.
                    HostFaultInjection fault = new HostFaultInjection(simulation);
                    fault.setNumberOfFailedPesRandom(failurePesRand);
                    fault.setDelayForFailureOfHostRandom(delayForFailureOfHostRandom);
                    fault.setHost(host);
                } else {
                    System.out.println("\t *** Host not failed. -> Id: " + host.getId() + "\n");
                }
                i++;
            }
        }
    }

    public void createAndSubmitCloudlets(DatacenterBroker broker) {
        double initialCloudletCpuUtilizationPercentage = CLOUDLET_INITIAL_CPU_UTILIZATION_PERCENTAGE;
        final int numberOfCloudlets = NUMBER_OF_VMS_TO_CREATE - 1;
        for (int i = 0; i < numberOfCloudlets; i++) {
            createAndSubmitCloudletsWithStaticCpuUtilization(
                    initialCloudletCpuUtilizationPercentage, vmlist.get(i), broker);
            initialCloudletCpuUtilizationPercentage += 0.15;
        }
        //Create one last cloudlet which CPU usage increases dynamically
        Vm lastVm = vmlist.get(vmlist.size() - 1);
        createAndSubmitCloudletsWithDynamicCpuUtilization(0.2, 1, lastVm, broker);
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        for (int i = 0; i < NUMBER_OF_VMS_TO_CREATE; i++) {
            PowerVm vm = createVm(broker);
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
     * {@link CloudletSchedulerDynamicWorkload} makes the Host CPU usage not be
     * updated (and maybe VM CPU usage too).
     */
    public PowerVm createVm(DatacenterBroker broker) {
        PowerVm vm = new PowerVm(vmlist.size(), VM_MIPS, VM_PES_NUM);
        vm.setSchedulingInterval(1)
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setBroker(broker)
                .setCloudletScheduler(new CloudletSchedulerDynamicWorkload(VM_MIPS, VM_PES_NUM));

        Log.printConcatLine(
                "#Requested creation of VM ", vm.getId(), " with ", VM_MIPS, " MIPS x ", VM_PES_NUM);
        return vm;
    }

    public List<Cloudlet> createAndSubmitCloudlets(
            double cloudletInitialCpuUtilizationPercentage,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker,
            boolean progressiveCpuUsage) {
        final List<Cloudlet> list = new ArrayList<>(NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM);
        UtilizationModel utilizationModelFull = new UtilizationModelFull();
        int cloudletId;
        for (int i = 0; i < NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM; i++) {
            cloudletId = hostingVm.getId() + i;
            UtilizationModelArithmeticProgression cpuUtilizationModel;
            if (progressiveCpuUsage) {
                cpuUtilizationModel
                        = new UtilizationModelArithmeticProgression(
                                CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND,
                                cloudletInitialCpuUtilizationPercentage);
            } else {
                cpuUtilizationModel
                        = new UtilizationModelArithmeticProgression(0,
                                cloudletInitialCpuUtilizationPercentage);
            }
            cpuUtilizationModel.setMaxResourceUsagePercentage(maxCloudletCpuUtilizationPercentage);

            Cloudlet c
                    = new CloudletSimple(
                            cloudletId, CLOUDLET_LENGHT, VM_PES_NUM)
                    .setFileSize(CLOUDLET_FILESIZE)
                    .setOutputSize(CLOUDLET_OUTPUTSIZE)
                    .setUtilizationModelCpu(cpuUtilizationModel)
                    .setUtilizationModelRam(utilizationModelFull)
                    .setUtilizationModelBw(utilizationModelFull);
            c.setBroker(broker);
            list.add(c);
        }

        broker.submitCloudletList(list);
        for (Cloudlet c : list) {
            broker.bindCloudletToVm(c, hostingVm);
        }

        return list;
    }

    public List<Cloudlet> createAndSubmitCloudletsWithDynamicCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                maxCloudletCpuUtilizationPercentage, hostingVm, broker, true);
    }

    public List<Cloudlet> createAndSubmitCloudletsWithStaticCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                initialCloudletCpuUtilizationPercentage,
                hostingVm, broker, false);
    }

    private Datacenter createDatacenter() {
        hostList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS_TO_CREATE; i++) {
            hostList.add(createHost(i, HOST_NUMBER_OF_PES, HOST_MIPS_BY_PE));
            Log.printConcatLine("#Created host ", i, " with ", HOST_MIPS_BY_PE, " mips x ", HOST_NUMBER_OF_PES);
        }
        Log.printLine();

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList);

        PowerVmAllocationPolicyMigrationWorstFitStaticThreshold allocationPolicy
                = new PowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
                        new PowerVmSelectionPolicyMinimumUtilization(),
                        HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION);

        PowerDatacenter dc = new PowerDatacenter(simulation, characteristics, allocationPolicy);
        dc.setMigrationsEnabled(true).setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    /**
     *
     * @param id
     * @param numberOfPes
     * @param mipsByPe
     * @return
     *
     * @todo @author manoelcampos Using the {@link VmSchedulerSpaceShared} its
     * getting NullPointerException, probably due to lack of CPU for all VMs. It
     * has to be created an IT test to check this problem.
     *
     * @todo @author manoelcampos The method
     * {@link DatacenterBroker#getCloudletsFinishedList()} returns an empty list
     * when using null     {@link PowerDatacenter},
     * {@link PowerHost} and {@link PowerVm}.
     */
    public PowerHostUtilizationHistory createHost(int id, int numberOfPes, double mipsByPe) {
        List<Pe> peList = createPeList(numberOfPes, mipsByPe);
        PowerHostUtilizationHistory host = new PowerHostUtilizationHistory(id, HOST_STORAGE, peList);
        host.setPowerModel(new PowerModelLinear(1000, 0.7))
                .setRamProvisioner(new ResourceProvisionerSimple(new Ram(HOST_RAM)))
                .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(HOST_BW)))
                .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    public List<Pe> createPeList(int numberOfPEs, double mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        return list;
    }
}
