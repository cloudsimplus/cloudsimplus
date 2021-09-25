package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * A class that implements the Null Object Design Pattern for {@link ContinuousDistribution}
 * class.
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
