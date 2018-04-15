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

import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudsimplus.testbeds.ExperimentRunner;

/**
 * Runs the {@link DynamicCloudletsArrivalExperiment}
 * the number of times defines by {@link #getSimulationRuns()}
 * and compute statistics.
 *
 * @author Manoel Campos da Silva Filho
 */
final class DynamicCloudletsArrivalRunner extends ExperimentRunner<DynamicCloudletsArrivalExperiment> {
    DynamicCloudletsArrivalRunner(boolean antitheticVariatesTechnique) {
        super(antitheticVariatesTechnique);
    }

    public DynamicCloudletsArrivalRunner(boolean antitheticVariatesTechnique, long baseSeed){
        super(antitheticVariatesTechnique, baseSeed);
    }

    @Override
	protected void setup() {}

    @Override
    protected Map<String, List<Double>> createMetricsMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	protected DynamicCloudletsArrivalExperiment createExperiment(int i) {
		return null;
	}

	@Override
	protected void printSimulationParameters() {

	}

	@Override
	protected SummaryStatistics computeFinalStatistics(List<Double> value) {
		return null;
	}

	@Override
	protected void printFinalResults(String metricName, SummaryStatistics stats) {

	}
}
