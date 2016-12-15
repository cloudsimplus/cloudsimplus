/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.migration;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyMigrationWorstFitStaticThreshold;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
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
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelArithmeticProgression;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

/**
 *
 * @author raysaoliveira
 */
public class ExampleMigrationVmWhenCpuMetricIsViolated {
    
    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;
    private static final double DATACENTER_COST_PER_CPU = 3.0;
    private static final double DATACENTER_COST_PER_RAM = 0.05;
    private static final double DATACENTER_COST_PER_STORAGE = 0.001;
    private static final double DATACENTER_COST_PER_BW = 0.0;

    private static final int    HOST_MIPS_BY_PE = 1000;
    private static final int    HOST_NUMBER_OF_PES = 2;
    private static final long   HOST_RAM = 500000; //host memory (MB)
    private static final long   HOST_STORAGE = 1000000; //host storage
    private static final long   HOST_BW = 100000000L;

    /**
     * The percentage of host CPU usage that trigger VM migration
     * due to over utilization (in scale from 0 to 1, where 1 is 100%).
     */
    private static final double HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION = 0.7;

    private static final int    VM_MIPS = 1000;
    private static final long   VM_SIZE = 1000; //image size (MB)
    private static final int    VM_RAM = 10000; //vm memory (MB)
    private static final long   VM_BW = 100000;
    private static final int    VM_PES_NUM = 1; //number of cpus

    private static final long   CLOUDLET_LENGHT = 20000;
    private static final long   CLOUDLET_FILESIZE = 300;
    private static final long   CLOUDLET_OUTPUTSIZE = 300;
    private static List<Cloudlet> list = new ArrayList<>();

    /**
     * The percentage of CPU that a cloudlet will use when
     * it starts executing (in scale from 0 to 1, where 1 is 100%).
     * For each cloudlet create, this value is used
     * as a base to define CPU usage.
     * @see #createAndSubmitCloudlets(DatacenterBroker)
     */
    private static final double CLOUDLET_INITIAL_CPU_UTILIZATION_PERCENTAGE = 0.6;

    /**
     * Defines the speed (in percentage) that CPU usage of a cloudlet
     * will increase during the simulation tie.
     * (in scale from 0 to 1, where 1 is 100%).
     * @see #createAndSubmitCloudletsWithDynamicCpuUtilization(double, double, Vm, DatacenterBroker)
     */
    public static final double CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND = 0.05;

    private static final int   NUMBER_OF_HOSTS_TO_CREATE = 3;
    private static final int   NUMBER_OF_VMS_TO_CREATE = NUMBER_OF_HOSTS_TO_CREATE + 1;
    private static final int   NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM = 1;

    private static final List<Vm> vmlist = new ArrayList<>();
    private static CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        Log.printConcatLine("Starting ", ExampleMigrationVmWhenCpuMetricIsViolated.class.getSimpleName(), "...");

        int num_user = 2;   // number of cloud users
        boolean trace_flag = false;  // mean trace events
        simulation = new CloudSim(trace_flag);

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        DatacenterBroker broker = createBroker();
        createAndSubmitVms(broker);

        createAndSubmitCloudlets(broker);
       
        simulation.start();
     
        new CloudletsTableBuilderHelper(broker.getCloudletsFinishedList()).build();

        Log.printConcatLine(ExampleMigrationVmWhenCpuMetricIsViolated.class.getSimpleName(), " finished!");
    }
    
    public static void createAndSubmitCloudlets(DatacenterBroker broker) {
        double initialCloudletCpuUtilizationPercentage = CLOUDLET_INITIAL_CPU_UTILIZATION_PERCENTAGE;
        final int numberOfCloudlets = NUMBER_OF_VMS_TO_CREATE-1;
        for(int i = 0; i < numberOfCloudlets; i++){
            createAndSubmitCloudletsWithStaticCpuUtilization(
                    initialCloudletCpuUtilizationPercentage, vmlist.get(i), broker);
            initialCloudletCpuUtilizationPercentage += 0.15;
        }
        //Create one last cloudlet which CPU usage increases dynamically
        Vm lastVm = vmlist.get(vmlist.size()-1);
        createAndSubmitCloudletsWithDynamicCpuUtilization(0.2, 1, lastVm, broker);
    }

    public static void createAndSubmitVms(DatacenterBroker broker) {
        for(int i = 0; i < NUMBER_OF_VMS_TO_CREATE; i++){
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
     * @todo @author manoelcampos The use of other CloudletScheduler instead
     * of {@link CloudletSchedulerDynamicWorkload} makes the Host CPU usage
     * not be updated (and maybe VM CPU usage too).
     */
    public static PowerVm createVm(DatacenterBroker broker) {
        PowerVm vm = new PowerVm(vmlist.size(), VM_MIPS, VM_PES_NUM);
        vm.setSchedulingInterval(1)
          .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
          .setBroker(broker)
          .setCloudletScheduler(new CloudletSchedulerDynamicWorkload(VM_MIPS, VM_PES_NUM));

        Log.printConcatLine(
                "#Requested creation of VM ", vm.getId(), " with ", VM_MIPS, " MIPS x ", VM_PES_NUM);
        return vm;
    }

    public static List<Cloudlet> createAndSubmitCloudlets(
            double cloudletInitialCpuUtilizationPercentage,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker,
            boolean progressiveCpuUsage) {
        list = new ArrayList<>(NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM);
        UtilizationModel utilizationModelFull = new UtilizationModelFull();
        int cloudletId;
        for(int i = 0; i < NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM; i++){
            cloudletId = hostingVm.getId() + i;
            UtilizationModelArithmeticProgression cpuUtilizationModel;
            if(progressiveCpuUsage){
                cpuUtilizationModel =
                        new UtilizationModelArithmeticProgression(
                                CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND,
                                cloudletInitialCpuUtilizationPercentage);
            } else {
                cpuUtilizationModel =
                        new UtilizationModelArithmeticProgression(0,
                                cloudletInitialCpuUtilizationPercentage);
            }
            cpuUtilizationModel.setMaxResourceUsagePercentage(maxCloudletCpuUtilizationPercentage);

            Cloudlet c =
                new CloudletSimple(
                    cloudletId, CLOUDLET_LENGHT, VM_PES_NUM)
                    .setCloudletFileSize(CLOUDLET_FILESIZE)
                    .setCloudletOutputSize(CLOUDLET_OUTPUTSIZE)
                    .setUtilizationModelCpu(cpuUtilizationModel)
                    .setUtilizationModelRam(utilizationModelFull)
                    .setUtilizationModelBw(utilizationModelFull);
            c.setBroker(broker);
            list.add(c);
        }

        broker.submitCloudletList(list);
        for(Cloudlet c: list) {
            broker.bindCloudletToVm(c, hostingVm);
        }

        return list;
    }

    public static List<Cloudlet> createAndSubmitCloudletsWithDynamicCpuUtilization (
            double initialCloudletCpuUtilizationPercentage,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                maxCloudletCpuUtilizationPercentage, hostingVm, broker, true);
    }

    public static List<Cloudlet> createAndSubmitCloudletsWithStaticCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                initialCloudletCpuUtilizationPercentage,
                hostingVm, broker, false);
    }

    private static Datacenter createDatacenter() {
        ArrayList<PowerHost> hostList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_HOSTS_TO_CREATE; i++){
            hostList.add(createHost(i, HOST_NUMBER_OF_PES, HOST_MIPS_BY_PE));
            Log.printConcatLine("#Created host ", i, " with ", HOST_MIPS_BY_PE, " mips x ", HOST_NUMBER_OF_PES);
        }
        Log.printLine();

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(DATACENTER_COST_PER_CPU)
                .setCostPerMem(DATACENTER_COST_PER_RAM)
                .setCostPerStorage(DATACENTER_COST_PER_STORAGE)
                .setCostPerBw(DATACENTER_COST_PER_BW);
                
        VmAllocationPolicyMigrationWorstFitStaticThreshold allocationPolicy =
            new VmAllocationPolicyMigrationWorstFitStaticThreshold(
                new ExamplePowerVmSelectionPolicyMinimumUtilization(),HOST_UTILIZATION_THRESHOLD_FOR_VM_MIGRATION);

        PowerDatacenter dc = new PowerDatacenter(simulation, characteristics, allocationPolicy);
        dc.setDisableMigrations(false).setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    /**
     *
     * @param id
     * @param numberOfPes
     * @param mipsByPe
     * @return
     *
     * @todo @author manoelcampos Using the {@link VmSchedulerSpaceShared} its getting NullPointerException,
     * probably due to lack of CPU for all VMs. It has to be created
     * an IT test to check this problem.
     *
     * @todo @author manoelcampos The method {@link DatacenterBroker#getCloudletsFinishedList()}
     * returns an empty list when using  {@link PowerDatacenter},
     * {@link PowerHost} and {@link PowerVm}.
     */
    public static PowerHostUtilizationHistory createHost(int id, int numberOfPes, double mipsByPe) {
            List<Pe> peList = createPeList(numberOfPes, mipsByPe);
            PowerHostUtilizationHistory host = new PowerHostUtilizationHistory(id, HOST_STORAGE, peList);
            host.setPowerModel(new PowerModelLinear(1000, 0.7))
                .setRamProvisioner(new ResourceProvisionerSimple(new Ram(HOST_RAM)))
                .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(HOST_BW)))
                .setVmScheduler(new VmSchedulerTimeShared());
            return host;
    }

    public static List<Pe> createPeList(int numberOfPEs, double mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for(int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        return list;
    }

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    private static DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }
 
}
