/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
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

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

import java.util.List;

/**
 * An experiment that shows how the dynamic arrival of Cloudlets
 * and the policy used to map Cloudlets to VMs impact the number of required VMs.
 *
 * @author Manoel Campos da Silva Filho
 */
final class DynamicCloudletsArrivalExperiment extends SimulationExperiment {
	public static final int HOSTS_TO_CREATE = 100;

	/**
	 * Creates a simulation experiment.
	 *
	 * @param index  the index that identifies the current experiment run.
	 * @param runner The {@link ExperimentRunner} that is in charge
	 * of executing this experiment a defined number of times and to collect
	 * data for statistical analysis.
	 */
	public DynamicCloudletsArrivalExperiment(int index, DynamicCloudletsArrivalRunner runner) {
		super(index, runner);
	}

	@Override
	public void printResults() {
		DatacenterBroker broker = getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
		List<Cloudlet> newList = broker.getCloudletsFinishedList();
		new CloudletsTableBuilderHelper(newList).build();
	}

	@Override
	protected DatacenterBroker createBroker() {
		return new DatacenterBrokerSimple(getCloudsim());
	}

    @Override
    protected void createCloudlets(DatacenterBroker broker) {

    }

    @Override
    protected void createVms(DatacenterBroker broker) {

    }

    @Override
    protected void createHosts() {

    }

    /**
	 * Just a method to try a single run of the experiment.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		DynamicCloudletsArrivalExperiment exp = new DynamicCloudletsArrivalExperiment(0, null);
		exp.run();
	}

}
