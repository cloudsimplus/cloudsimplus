package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;
import org.cloudsimplus.util.tablebuilder.PriorityCloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerCompletelyFair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * An abstract class to provide the basic features for the classes that implement
 * the experiments to assess the {@link CloudletSchedulerCompletelyFair}
 * against the  {@link CloudletSchedulerTimeShared}.
 *
 * <p>All the extending experiment classes will create the exact same simulation scenario
 * once all seeds and configurations are shared among them. Such experiments will create
 * the number of Hosts and Vms defined by {@link #HOSTS_TO_CREATE} and {@link #VMS_TO_CREATE}.
 * The number of Cloudlets is defined randomly for each experiment. The {@link CloudletSchedulerRunner} is in charge
 * to instantiate the experiments and set the general parameters (such as the number of Cloudlets).
 * All Cloudlets will have the same length defined by {@link #CLOUDLET_LENGHT_MI}, but the number
 * of PEs is defined randomly using the {@link CloudletSchedulerExperiment#getCloudletPesPrng()},
 * that in turn is set by each {@link CloudletSchedulerRunner} extending class.
 * </p>
 *
 * <p>All these configurations, including the number of Cloudlets and PEs of each one, that are
 * defined randomly, are shared between all sub-classes of this one. Each sub-class just
 * uses the same scenario with a different {@link CloudletScheduler} implementation
 * to compare results.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletSchedulerTimeSharedExperiment
 * @see CompletelyFairSchedulerExperiment
 */
abstract class CloudletSchedulerExperiment extends SimulationExperiment {
    public static final int HOST_PES = 32;
    public static final int VM_PES = HOST_PES;
    public static final double VM_MIPS = 1000;
    public static final long VM_STORAGE = 10000; // vm image size (MB)
    public static final int VM_RAM = 512; // vm memory (MB)
    public static final long VM_BW = 1000; // vm bandwidth
    public static final int MAX_CLOUDLET_PES = VM_PES/8 + 1;
    public static final int HOSTS_TO_CREATE = 1;
    public static final int VMS_TO_CREATE = 1;
    public static final long CLOUDLET_LENGHT_MI = 10000; //in Million Instructions (MI)

    private ContinuousDistribution cloudletPesPrng;
    private int numberOfCloudletsToCreate;

    public CloudletSchedulerExperiment(int index, ExperimentRunner runner) {
        super(index, runner);
        this.cloudletPesPrng = new UniformDistr(0, 1);
    }

    @Override
    public void printResults() {
        Log.enable();
        try {
            System.out.printf("\nCloudlets: %d\n", numberOfCloudletsToCreate);
            DatacenterBroker broker = getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
            new PriorityCloudletsTableBuilderHelper(broker.getCloudletsFinishedList()).build();
        } finally {
            Log.disable();
        }
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudsim());
    }

    @Override
    protected void createHosts()  {
        for (int i = 0; i < HOSTS_TO_CREATE; i++) {
            addNewHostToList(this::getHostSupplier);
        }
    }

    private Host getHostSupplier() {
        int mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage (MB)
        long bw = 10000; //Megabits/s
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }

        return new HostSimple(getNumberOfCreatedHosts(), storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    @Override
    protected void createVms(DatacenterBroker broker) {
        for(int i = 0; i < VMS_TO_CREATE; i++) {
            addNewVmToList(getVmSupplier(broker));
        }
    }

    /**
     * Gets a {@link Supplier} function that is able to create a new Vm.
     *
     * @param broker broker that the Vm to be created by the Supplier function will belong to
     * @return the Supplier function that can create a Vm when requested
     */
    protected abstract Supplier<Vm> getVmSupplier(DatacenterBroker broker);

    @Override
    protected void createCloudlets(DatacenterBroker broker) {
        for(int i = 0; i < numberOfCloudletsToCreate; i++) {
            addNewCloudletToList(getCloudletSupplier(broker));
        }
    }

    /**
     * Gets a {@link Supplier} function that is able to create a Cloudlet.
     *
     * @param broker broker that the Cloudlet to be created by the Supplier function will belong to
     * @return the Supplier function that can create a Cloudlet when requested
     */
    private Supplier<Cloudlet> getCloudletSupplier(DatacenterBroker broker) {
        return () -> {
            long fileSize = 300; //Size (in bytes) before execution
            long outputSize = 300; //Size (in bytes) after execution
            final int cloudletPes = (int)cloudletPesPrng.sample();
            //Defines how CPU, RAM and Bandwidth resources are used
            //Sets the same utilization model for all these resources.
            UtilizationModel utilization = new UtilizationModelFull();
            return new CloudletSimple(getNumberOfCreatedCloudlets(), CLOUDLET_LENGHT_MI, cloudletPes)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilization)
                .setBroker(broker);
        };
    }

    public ContinuousDistribution getCloudletPesPrng() {
        return cloudletPesPrng;
    }

    public CloudletSchedulerExperiment setCloudletPesPrng(ContinuousDistribution cloudletPesPrng) {
        this.cloudletPesPrng = cloudletPesPrng;
        return this;
    }

    public CloudletSchedulerExperiment setNumberOfCloudletsToCreate(int numberOfCloudletsToCreate) {
        this.numberOfCloudletsToCreate = numberOfCloudletsToCreate;
        return this;
    }

    public int getNumberOfCloudletsToCreate() {
        return numberOfCloudletsToCreate;
    }
}
