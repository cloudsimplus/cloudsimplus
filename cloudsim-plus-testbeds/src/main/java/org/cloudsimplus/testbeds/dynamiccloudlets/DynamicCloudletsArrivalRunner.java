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
