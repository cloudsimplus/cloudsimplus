package org.cloudbus.cloudsim.distributions;

/**
 * A class that implements the Null Object Design Pattern for {@link ContinuousDistribution}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see ContinuousDistribution#NULL
 */
final class ContinuousDistributionNull implements ContinuousDistribution {
    @Override public double originalSample() { return 0; }
    @Override public double sample() { return 0.0; }
    @Override public long getSeed() {
        return 0;
    }
    @Override public boolean isApplyAntitheticVariates() { return false; }
    @Override public ContinuousDistribution setApplyAntitheticVariates(boolean applyAntitheticVariates) { return this; }
}
