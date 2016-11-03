package org.cloudsimplus.testbeds;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudsimplus.testbeds.heuristics.DatacenterBrokerHeuristicExperiment;
import org.cloudsimplus.testbeds.heuristics.DatacenterBrokerHeuristicRunner;

import java.util.List;
import java.util.function.Consumer;

/**
 * An experiment that shows how the dynamic arrival of Cloudlets
 * and the policy used to map Cloudlets to VMs impact the number of required VMs.
 *
 * @author Manoel Campos da Silva Filho
 */
public class DynamicCloudletsArrivalExperiment extends SimulationExperiment {
	public static final int HOSTS_TO_CREATE = 100;

	/**
	 * Creates a simulation experiment.
	 *
	 * @param index  the index that identifies the current experiment run.
	 * @param runner The {@link ExperimentRunner} that is in charge
	 * of executing this experiment a defined number of times and to collect
	 * data for statistical analysis.
	 */
	public DynamicCloudletsArrivalExperiment(int index, DatacenterBrokerHeuristicRunner runner) {
		super(index, runner);
		 setHostsToCreate(HOSTS_TO_CREATE);
	}

	@Override
	public void printResults() {
		DatacenterBroker broker = getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
		List<Cloudlet> newList = broker.getCloudletsFinishedList();
		CloudletsTableBuilderHelper.print(new TextTableBuilder(), newList);
	}

	@Override
	protected DatacenterBroker createBroker() {
		return new DatacenterBrokerSimple("broker0");
	}

	/**
	 * Just a method to try a single run of the experiment.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		DynamicCloudletsArrivalExperiment exp = new DynamicCloudletsArrivalExperiment(0, null);
		exp.setVmPesArray(new int[]{2, 4, 6, 8}).setCloudletPesArray(new int[]{2, 4, 6, 8});
		exp.run();
	}

}
