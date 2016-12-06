package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;

/**
 * An interface to be implemented by Power-aware VM allocation policies that use
 * a dynamic over utilization threshold computed using some statistical method such as
 * Median absolute deviation (MAD), InterQuartileRange (IRQ), Local Regression, etc,
 * depending on the implementing class.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface PowerVmAllocationPolicyMigrationDynamicUpperThreshold extends PowerVmAllocationPolicyMigration {
    /**
     * Sets the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have
     * data to be computed.
     *
     * @param fallbackVmAllocationPolicy the new fallback vm allocation policy
     */
    void setFallbackVmAllocationPolicy(PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy);

    /**
     * Gets the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have
     * data to be computed.
     *
     * @return the fallback vm allocation policy
     */
    PowerVmAllocationPolicyMigration getFallbackVmAllocationPolicy();

    /**
     * Gets the safety parameter for the over utilization threshold in percentage (at scale from 0 to 1).
     * It is a tuning parameter used by the allocation policy to define
     * when a host is overloaded. The overload detection is based
     * on a dynamic defined host utilization threshold.
     *
     * <p>Such a threshold is computed based on the host's usage history using different statistical methods
     * (such as Median absolute deviation - MAD, that is similar to the Standard Deviation)
     * depending on the implementing class, as defined by the method
     * {@link #computeHostUtilizationMeasure(PowerHostUtilizationHistory)}.</p>
     *
     * <p>
     * This safety parameter is used to increase or decrease the utilization threshold.
     * As the safety parameter increases, the threshold decreases,
     * what may lead to less SLA violations. So, as higher is that parameter,
     * safer the algorithm will be when defining a host as overloaded.
     * </p>
     *
     * <p>Let's take an example of a class that uses the MAD to compute the
     * over utilization threshold. Considering a safety parameter of 1.5 (150%),
     * a host's resource usage mean of 0.6 (60%)
     * and a MAD of 0.2 (thus, the usage may vary from 0.4 to 0.8).
     * To compute the usage threshold, the MAD is increased by 60%, being equals to 0.32.
     * Finally, the threshold will be 1 - 0.32 = 0.68.
     * Thus, only when the host utilization threshold exceeds 68%,
     * the host is considered overloaded.
     * </p>
     *
     * <p>
     * Here, safer doesn't mean a more accurate overload detection but that the algorithm will use a lower host
     * utilization threshold that may lead to lower SLA violations but higher
     * resource wastage. <b>Thus this parameter has to be tuned in order to
     * trade-off between SLA violation and resource wastage.</b></p>
     */
    double getSafetyParameter();

    /**
     * Computes the measure used to generate the dynamic host over utilization threshold using some statistical method
     * (such as the Median absolute deviation - MAD, InterQuartileRange - IRQ, Local Regression, etc),
     * depending on the implementing class. The method uses Host utilization history to compute
     * such a metric.
     *
     * @param host the host to get the current utilization
     * @return
     * @throws IllegalArgumentException when the measure could not be computed
     * (for instance, because the Host doesn't have enought history to use)
     * @see #getOverUtilizationThreshold(PowerHost)
     */
    double computeHostUtilizationMeasure(PowerHostUtilizationHistory host) throws IllegalArgumentException;
}
