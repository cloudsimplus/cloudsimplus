package org.cloudsimplus.testbeds.dynamiccloudlets;

import org.cloudbus.cloudsim.Cloudlet;
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
		return new DatacenterBrokerSimple("broker0");
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
