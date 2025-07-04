/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies.migration;

import org.cloudsimplus.hosts.Host;

/// An interface to be implemented by VM allocation policies that define
/// a dynamic over-utilization threshold, computed using some statistical method such as:
///
/// - [Median Absolute Deviation (MAD)](https://en.wikipedia.org/wiki/Median_absolute_deviation),
/// - [Inter-quartile Range (IQR)](https://en.wikipedia.org/wiki/Interquartile_range),
/// - [Local Regression (LR)](https://en.wikipedia.org/wiki/Local_regression), etc,
///
/// depending on the implementing class.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public sealed interface VmAllocationPolicyMigrationDynamicUpperThreshold
    extends VmAllocationPolicyMigration
    permits VmAllocationPolicyMigrationDynamicUpperThresholdAbstract
{
    /**
     * Sets the fallback VM allocation policy to be used when
     * the over-utilization host detection doesn't have
     * data to be computed.
     *
     * @param fallbackPolicy the new fallback vm allocation policy
     */
    VmAllocationPolicyMigrationDynamicUpperThreshold setFallbackVmAllocationPolicy(VmAllocationPolicyMigration fallbackPolicy);

    /**
     * @return the fallback VM allocation policy to be used when
     * the over-utilization host detection doesn't have
     * data to be computed
     */
    VmAllocationPolicyMigration getFallbackVmAllocationPolicy();

    /// Gets the safety parameter for the over utilization threshold in percentage, at scale from 0 to 1.
    /// For instance, a value 1 means 100% while 1.5 means 150%.
    /// It is a tuning parameter used by the allocation policy to define
    /// when a host is overloaded. The overload detection is based
    /// on a dynamic defined host utilization threshold.
    ///
    /// Such a threshold is computed based on the host's usage
    /// history, using different statistical methods
    /// (such as Median absolute deviation - MAD, that is similar to the Standard Deviation),
    /// depending on the implementing class (as defined by the
    /// [#computeHostUtilizationMeasure(Host)] method).
    ///
    /// This safety parameter is used to increase or decrease the utilization threshold.
    /// As the safety parameter increases, the threshold decreases,
    /// which may lead to fewer SLA violations. So, as higher is that parameter,
    /// safer the algorithm will be when defining a host as overloaded.
    /// **A value equal to 0 indicates that the safety parameter doesn't affect
    /// the computed CPU utilization threshold.**
    ///
    /// Let's take an example of a class that uses the MAD to compute the
    /// over utilization threshold. Considering a host's resource usage mean of 0.6 (60%)
    /// and a MAD of 0.2, meaning the usage may vary from 0.4 to 0.8.
    /// Now take a safety parameter of 0.5 (50%).
    /// To compute the usage threshold, the MAD is increased by 50%, being equals to 0.3.
    /// Finally, the threshold will be `1 - 0.3 = 0.7`.
    /// Therefore, only when the host utilization threshold exceeds 70%,
    /// the host is considered overloaded.
    ///
    /// Here, safer doesn't mean more accurate overload detection. Instead, it means the
    /// algorithm will use a lower host utilization threshold,
    /// which may lead to lower SLA violations but higher resource wastage.
    /// **Accordingly, this parameter has to be tuned to
    /// trade-off between SLA violation and resource wastage.**
    double getSafetyParameter();

    /**
     * Computes the measure used internally to generate the dynamic host over-utilization
     * threshold using some statistical method (such as the
     * Median absolute deviation - MAD, InterQuartileRange - IRQ, Local Regression, etc.),
     * depending on the implementing class.
     * The method uses Host utilization history to compute such a metric.
     *
     * @param host the host to get the current utilization
     * @return the computed measure for internal purposes
     * @throws IllegalArgumentException when the measure could not be computed
     * (for instance, because the Host doesn't have enough history to use)
     * @see #getOverUtilizationThreshold(Host)
     */
    double computeHostUtilizationMeasure(Host host) throws IllegalStateException;
}
