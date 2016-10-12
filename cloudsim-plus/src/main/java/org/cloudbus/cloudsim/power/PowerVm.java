/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A class of VM that stores its CPU utilization percentage history. The history
 * is used by VM allocation and selection policies.
 *
 * <p>
 * If you are using any algorithms, policies or workload included in the power
 * package please cite the following paper:</p>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerVm extends VmSimple {

    /**
     * The maximum number of entries that will be stored.
     */
    public static final int MAX_HISTORY_ENTRIES = 30;

    /**
     * @see #getUtilizationHistory()
     */
    private final List<Double> utilizationHistory = new LinkedList<>();

    /**
     * @see #getPreviousTime()
     */
    private double previousTime;

    /**
     * @see #getSchedulingInterval()
     */
    private double schedulingInterval;

    /**
     * Instantiates a new PowerVm.
     *
     * @param id the id
     * @param userId the user id
     * @param mips the mips
     * @param pesNumber the pes number
     * @param ram the ram
     * @param bw the bw
     * @param size the size
     * @param priority the priority
     * @param vmm the vmm
     * @param cloudletScheduler the cloudlet scheduler
     * @param schedulingInterval the scheduling interval
     */
    public PowerVm(
            final int id,
            final int userId,
            final double mips,
            final int pesNumber,
            final int ram,
            final long bw,
            final long size,
            final int priority,
            final String vmm,
            final CloudletScheduler cloudletScheduler,
            final double schedulingInterval) {
        super(id, userId, mips, pesNumber, ram, bw, size, vmm, cloudletScheduler);
        setSchedulingInterval(schedulingInterval);
    }

    @Override
    public double updateVmProcessing(final double currentTime, final List<Double> mipsShare) {
        double time = super.updateVmProcessing(currentTime, mipsShare);
        if (currentTime > getPreviousTime() && (currentTime - 0.1) % getSchedulingInterval() == 0) {
            double utilization = getTotalUtilizationOfCpu(getCloudletScheduler().getPreviousTime());
            if (CloudSim.clock() != 0 || utilization != 0) {
                addUtilizationHistoryValue(utilization);
            }
            setPreviousTime(currentTime);
        }
        return time;
    }

    /**
     * Gets the utilization Median Absolute Deviation (MAD) in MIPS.
     */
    public double getUtilizationMad() {
        if (!getUtilizationHistory().isEmpty()) {
            int n = Math.min(MAX_HISTORY_ENTRIES, getUtilizationHistory().size());
            double median = MathUtil.median(getUtilizationHistory());
            double[] deviationSum = new double[n];
            for (int i = 0; i < n; i++) {
                deviationSum[i] = Math.abs(median - getUtilizationHistory().get(i));
            }
            return MathUtil.median(deviationSum);
        }

        return 0;
    }

    /**
     * Gets the utilization mean in MIPS.
     */
    public double getUtilizationMean() {
        if (!getUtilizationHistory().isEmpty()) {
	        double mean = 0;
	        int n = Math.min(MAX_HISTORY_ENTRIES, getUtilizationHistory().size());
            for (int i = 0; i < n; i++) {
                mean += getUtilizationHistory().get(i);
            }

            mean /= n;
	        return mean * getMips();
        }

        return 0;
    }

    /**
     * Gets the utilization variance in MIPS.
     *
     * @return the utilization variance in MIPS
     */
    public double getUtilizationVariance() {
        if (!getUtilizationHistory().isEmpty()) {
	        double mean = getUtilizationMean();
	        double variance = 0;
	        int n = Math.min(MAX_HISTORY_ENTRIES, getUtilizationHistory().size());
            for (int i = 0; i < n; i++) {
                double tmp = getUtilizationHistory().get(i) * getMips() - mean;
                variance += tmp * tmp;
            }

            return (variance / n);
        }

        return 0;
    }

    /**
     * Adds a CPU utilization percentage history value.
     *
     * @param utilization the CPU utilization percentage to add
     */
    public void addUtilizationHistoryValue(final double utilization) {
        getUtilizationHistory().add(0, utilization);
        if (getUtilizationHistory().size() > MAX_HISTORY_ENTRIES) {
            getUtilizationHistory().remove(MAX_HISTORY_ENTRIES);
        }
    }

    /**
     * Gets the CPU utilization percentage history.
     *
     */
    public List<Double> getUtilizationHistory() {
	    return Collections.unmodifiableList(utilizationHistory);
    }

    /**
     * Gets the previous time that cloudlets were processed.
     *
     */
    public double getPreviousTime() {
        return previousTime;
    }

    /**
     * Sets the previous time that cloudlets were processed.
     *
     * @param previousTime the new previous time
     */
    public void setPreviousTime(final double previousTime) {
        this.previousTime = previousTime;
    }

    /**
     * Gets the scheduling interval to update the processing of cloudlets running in
     * this VM.
     *
     * @return the schedulingInterval
     */
    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    /**
     * Sets the scheduling interval.
     *
     * @param schedulingInterval the schedulingInterval to set
     */
    protected final void setSchedulingInterval(final double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
    }

}
