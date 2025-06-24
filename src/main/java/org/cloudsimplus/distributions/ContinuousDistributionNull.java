/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.distributions;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * A class that implements the Null Object Design Pattern for {@link ContinuousDistribution} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see ContinuousDistribution#NULL
 */
final class ContinuousDistributionNull implements ContinuousDistribution {
    @Override public double originalSample() { return 0; }
    @Override public double probability(double val) { return 0; }
    @Override public double density(double val) { return 0; }
    @Override public double cumulativeProbability(double val) { return 0; }
    @Override public double cumulativeProbability(double val1, double val2) throws NumberIsTooLargeException { return 0; }
    @Override public double inverseCumulativeProbability(double val) throws OutOfRangeException { return 0; }
    @Override public double getNumericalMean() { return 0; }
    @Override public double getNumericalVariance() { return 0; }
    @Override public double getSupportLowerBound() { return 0; }
    @Override public double getSupportUpperBound() { return 0; }
    @Override public boolean isSupportLowerBoundInclusive() { return false; }
    @Override public boolean isSupportUpperBoundInclusive() { return false; }
    @Override public boolean isSupportConnected() { return false; }
    @Override public void reseedRandomGenerator(long val) {/**/}
    @Override public double sample() { return 0.0; }
    @Override public double[] sample(int val) { return new double[0]; }
    @Override public long getSeed() { return 0; }
    @Override public boolean isApplyAntitheticVariates() { return false; }
    @Override public ContinuousDistribution setApplyAntitheticVariates(boolean applyAntitheticVariates) { return this; }
}
