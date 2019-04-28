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
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.testbeds.Experiment;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.ArrayList;
import java.util.List;


/**
 * An abstract class to provide the basic features for the classes that implement
 * the experiments to assess the {@link CloudletSchedulerCompletelyFair}
 * against the  {@link CloudletSchedulerTimeShared}.
 *
 * <p>All the extending experiment classes will create the exact same simulation scenario
 * once all seeds and configurations are shared among them. Such experiments will create
 * the number of Hosts and Vms defined by {@link #HOSTS} and {@link #VMS}.
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
abstract class CloudletSchedulerExperiment extends Experiment {
    protected static final int HOST_PES = 32;
    protected static final int VM_PES = HOST_PES;
    protected static final long VM_MIPS = 1000;
    protected static final long VM_STORAGE = 10000; // vm image size (MEGA)
    protected static final long VM_RAM = 512; // vm memory (MEGA)
    protected static final long VM_BW = 1000; // vm bandwidth
    protected static final int MAX_CLOUDLET_PES = VM_PES/8 + 1;
    protected static final int HOSTS = 1;
    protected static final int VMS = 1;
    protected static final long CLOUDLET_LENGHT_MI = 10000; //in Million Instructions (MI)

    private ContinuousDistribution cloudletPesPrng;
    private int numCloudletsToCreate;

    CloudletSchedulerExperiment(final int index, final ExperimentRunner runner) {
        super(index, runner);
        this.cloudletPesPrng = new UniformDistr(0, 1);
        setVmsByBrokerFunction(broker -> VMS);
        setHostsNumber(HOSTS);
    }

    @Override
    public void printResults() {
        System.out.printf("\nCloudlets: %d\n", numCloudletsToCreate);
        final DatacenterBroker broker = getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
        new CloudletsTableBuilder(broker.getCloudletFinishedList())
            .addColumn(2, new TextTableColumn("Priority"), Cloudlet::getPriority)
            .build();
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getSimulation());
    }

    @Override
    protected Host createHost(final int id) {
        final long mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        final long ram = 2048; // host memory (MEGA)
        final long storage = 1000000; // host storage (MEGA)
        final long bw = 10000; //Megabits/s
        final List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        final Host host = new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        host.setId(id);
        return host;
    }

    @Override
    protected List<Cloudlet> createCloudlets(final DatacenterBroker broker) {
        final List<Cloudlet> list = new ArrayList<>(numCloudletsToCreate);
        for (int id = getCloudletList().size(); id < getCloudletList().size() + numCloudletsToCreate; id++) {
            list.add(createCloudlet(broker));
        }

        return list;
    }

    @Override
    protected Cloudlet createCloudlet(final DatacenterBroker broker) {
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution
        final int cloudletPes = (int)cloudletPesPrng.sample();

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        final UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(nextCloudletId(), CLOUDLET_LENGHT_MI, cloudletPes)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilization);
    }

    public ContinuousDistribution getCloudletPesPrng() {
        return cloudletPesPrng;
    }

    public CloudletSchedulerExperiment setCloudletPesPrng(final ContinuousDistribution cloudletPesPrng) {
        this.cloudletPesPrng = cloudletPesPrng;
        return this;
    }

    public CloudletSchedulerExperiment setNumCloudletsToCreate(final int numCloudletsToCreate) {
        this.numCloudletsToCreate = numCloudletsToCreate;
        return this;
    }

    @Override
    protected Vm createVm(final DatacenterBroker broker, final int id) {
        return new VmSimple(id, VM_MIPS, VM_PES)
                      .setRam(VM_RAM)
                      .setBw(VM_BW)
                      .setSize(VM_STORAGE);
    }

    public int getNumCloudletsToCreate() {
        return numCloudletsToCreate;
    }
}
