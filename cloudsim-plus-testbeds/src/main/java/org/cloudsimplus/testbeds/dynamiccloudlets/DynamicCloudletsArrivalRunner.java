package org.cloudsimplus.testbeds.dynamiccloudlets;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudsimplus.testbeds.ExperimentRunner;

/**
 * Runs the {@link DynamicCloudletsArrivalExperiment}
 * the number of times defines by {@link #numberOfSimulationRuns}
 * and compute statistics.
 *
 * @author Manoel Campos da Silva Filho
 */
final class DynamicCloudletsArrivalRunner extends ExperimentRunner<DynamicCloudletsArrivalExperiment> {
	@Override
	protected void setup() {

	}

	@Override
	protected DynamicCloudletsArrivalExperiment createExperiment(int i) {
		return null;
	}

	@Override
	protected void printSimulationParameters() {

	}

	@Override
	protected SummaryStatistics computeFinalStatistics() {
		return null;
	}

	@Override
	protected void printFinalResults(SummaryStatistics stats) {

	}
}
