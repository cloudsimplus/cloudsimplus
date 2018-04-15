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
package org.cloudsimplus.testbeds.dynamiccloudlets;

import java.util.ArrayList;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.List;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An experiment that shows how the dynamic arrival of Cloudlets and the policy
 * used to map Cloudlets to VMs impact the number of required VMs and application completion time.
 *
 * @author Manoel Campos da Silva Filho
 */
final class DynamicCloudletsArrivalExperiment extends SimulationExperiment {

    public static final int HOSTS_TO_CREATE = 100;

    private DynamicCloudletsArrivalExperiment(final long seed) {
        this(0, null, seed);
    }

    /**
     * Creates a simulation experiment.
     *
     * @param index the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge of executing
     * this experiment a defined number of times and to collect data for
     * statistical analysis.
     */
    DynamicCloudletsArrivalExperiment(int index, DynamicCloudletsArrivalRunner runner) {
        this(index, runner, -1);
    }

    private DynamicCloudletsArrivalExperiment(int index, DynamicCloudletsArrivalRunner runner, long seed) {
        super(index, runner, seed);
    }

    @Override
    public void printResults() {
        final DatacenterBroker broker = getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
        new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudSim());
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        return new ArrayList<>();
    }

    @Override
    protected List<Vm> createVms(DatacenterBroker broker) {
        return new ArrayList<>();
    }

    @Override
    protected List<Host> createHosts() {
        return new ArrayList<>();
    }

    /**
     * Just a method to try a single run of the experiment.
     *
     * @param args
     */
    public static void main(String[] args) {
        DynamicCloudletsArrivalExperiment exp = new DynamicCloudletsArrivalExperiment(1);
        exp.run();
    }

}
