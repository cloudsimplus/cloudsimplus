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
package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;
import org.cloudsimplus.builders.tables.PriorityCloudletsTableBuilder;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;

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
    public static final long VM_MIPS = 1000;
    public static final long VM_STORAGE = 10000; // vm image size (MEGABYTE)
    public static final long VM_RAM = 512; // vm memory (MEGABYTE)
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
            new PriorityCloudletsTableBuilder(broker.getCloudletsFinishedList()).build();
        } finally {
            Log.disable();
        }
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudsim());
    }

    @Override
    protected List<Host> createHosts()  {
        List<Host> list = new ArrayList<>(HOSTS_TO_CREATE);
        for (int i = 0; i < HOSTS_TO_CREATE; i++) {
            list.add(createHost(getHostList().size()+i));
        }
        return list;
    }

    private Host createHost(int id) {
        long mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long ram = 2048; // host memory (MEGABYTE)
        long storage = 1000000; // host storage (MEGABYTE)
        long bw = 10000; //Megabits/s
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(id, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    @Override
    protected List<Vm> createVms(DatacenterBroker broker) {
        final List<Vm> list = new ArrayList<>(VMS_TO_CREATE);
        for(int i = 0; i < VMS_TO_CREATE; i++) {
            list.add(createVm(broker));
        }
        return list;
    }

    /**
     * Gets a {@link Supplier} function that is able to create a new Vm.
     *
     * @param broker broker that the Vm to be created by the Supplier function will belong to
     * @return the Supplier function that can create a Vm when requested
     */
    protected abstract Vm createVm(DatacenterBroker broker);

    @Override
    protected List<Cloudlet> createCloudlets(DatacenterBroker broker) {
        List<Cloudlet> list = new ArrayList<>(numberOfCloudletsToCreate);
        for(int i = 0; i < numberOfCloudletsToCreate; i++) {
            list.add(createCloudlet(broker));
        }

        return list;
    }

    /**
     * Creates a Cloudlet with the given parameters.
     *
     * @param broker broker that the Cloudlet to be created by the Supplier function will belong to
     * @param cloudletPes number of PEs for the Cloudlet to be created by the Supplier function
     * @return the created Cloudlet
     */
    private Cloudlet createCloudlet(DatacenterBroker broker) {
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        final int cloudletPes = (int)cloudletPesPrng.sample();
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(CLOUDLET_LENGHT_MI, cloudletPes)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilization)
            .setBroker(broker);
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
