/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A VM allocation policy that uses Median Absolute Deviation (MAD) to compute
 * a dynamic threshold in order to detect host over utilization.
 * 
 * <br/>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br/>
 * 
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation extends
		PowerVmAllocationPolicyMigrationAbstract {

	/** The safety parameter in percentage (at scale from 0 to 1).
         * It is a tuning parameter used by the allocation policy to define
         * when a host is overloaded. The overload detection is based
         * on a dynamic defined host utilization threshold. This threshold 
         * is computed based on the host's usage history Median absolute deviation 
         * (MAD, that is similar to the Standard Deviation).
         * This safety parameter is used to increase or decrease the MAD value
         * when computing the utilization threshold.
         * As the safety parameter increases, the threshold decreases, 
         * what may lead to less SLA violations. So, as higher is that parameter, 
         * safer the algorithm will be when defining a host as overloaded. 
         * For instance, considering a safety parameter of 1.5 (150%),
         * a host's resource usage mean is 0.5 (50%) 
         * and a MAD of 0.2 (thus, the usage may vary from 0.3 to 0.7). 
         * To compute the usage threshold, the MAD is increased by 50%, being equals to 0.3. 
         * Finally, the threshold will be 1 - 0.3 = 0.7. 
         * Thus, only when the host utilization threshold exceeds 70%, 
         * the host is considered overloaded. 
         * Here, more safe or less safe doesn't means a more accurate or less accurate
         * overload detection. Safer means the algorithm will use a lower host
         * utilization threshold that may lead to lower SLA violations but higher
         * resource wastage. Thus this parameter has to be tuned in order to 
         * trade-off between SLA violation and resource wastage.
         */
	private double safetyParameter = 0;

	/** The fallback VM allocation policy to be used when
         * the MAD over utilization host detection doesn't have
         * data to be computed. */
	private PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy;

	/**
	 * Instantiates a new PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation.
	 * 
	 * @param hostList the host list
	 * @param vmSelectionPolicy the vm selection policy
	 * @param safetyParameter the safety parameter
         * @param fallbackVmAllocationPolicy
	 * @param utilizationThreshold the utilization threshold
	 */
	public PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation(
			List<PowerHost> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double safetyParameter,
			PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy); 
		setSafetyParameter(safetyParameter);
		setFallbackVmAllocationPolicy(fallbackVmAllocationPolicy);
	}

	/**
	 * Instantiates a new PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation.
	 * 
	 * @param hostList the host list
	 * @param vmSelectionPolicy the vm selection policy
	 * @param safetyParameter the safety parameter
         * @param fallbackVmAllocationPolicy
	 */
	public PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation(
			List<PowerHost> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double safetyParameter,
			PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy) {
		super(hostList, vmSelectionPolicy);
		setSafetyParameter(safetyParameter);
		setFallbackVmAllocationPolicy(fallbackVmAllocationPolicy);
	}

	/**
	 * Checks if a host is over utilized.
	 * 
	 * @param host the host
	 * @return true, if the host is over utilized; false otherwise
	 */
	@Override
	public boolean isHostOverUtilized(PowerHostSimple host) {
		PowerHostUtilizationHistory _host = (PowerHostUtilizationHistory) host;
		double upperThreshold = 0;
		try {
    			upperThreshold = 1 - getSafetyParameter() * getHostUtilizationMad(_host);
		} catch (IllegalArgumentException e) {
			return getFallbackVmAllocationPolicy().isHostOverUtilized(host);
		}
		addHistoryEntry(host, upperThreshold);
		double totalRequestedMips = 0;
		for (Vm vm : host.getVmList()) {
			totalRequestedMips += vm.getCurrentRequestedTotalMips();
		}
		double utilization = totalRequestedMips / host.getTotalMips();
		return utilization > upperThreshold;
	}

	/**
	 * Gets the host utilization MAD.
	 * 
	 * @param host the host
	 * @return the host utilization MAD
	 */
	protected double getHostUtilizationMad(PowerHostUtilizationHistory host) throws IllegalArgumentException {
		double[] data = host.getUtilizationHistory();
		if (MathUtil.countNonZeroBeginning(data) >= 12) { // 12 has been suggested as a safe value
			return MathUtil.mad(data);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Sets the safety parameter.
	 * 
	 * @param safetyParameter the new safety parameter
	 */
	protected final void setSafetyParameter(double safetyParameter) {
		if (safetyParameter < 0) {
                    throw new IllegalArgumentException(
                        "The safety parameter cannot be less than zero.");
		}
		this.safetyParameter = safetyParameter;
	}

	/**
	 * Gets the safety parameter.
	 * 
	 * @return the safety parameter
	 */
	protected double getSafetyParameter() {
		return safetyParameter;
	}

	/**
	 * Sets the fallback vm allocation policy.
	 * 
	 * @param fallbackVmAllocationPolicy the new fallback vm allocation policy
	 */
	public final void setFallbackVmAllocationPolicy(
			PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy) {
		this.fallbackVmAllocationPolicy = fallbackVmAllocationPolicy;
	}

	/**
	 * Gets the fallback vm allocation policy.
	 * 
	 * @return the fallback vm allocation policy
	 */
	public PowerVmAllocationPolicyMigration getFallbackVmAllocationPolicy() {
		return fallbackVmAllocationPolicy;
	}

}
