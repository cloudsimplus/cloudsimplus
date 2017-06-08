/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms.power;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.vms.VmSimple;

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
     * Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGABYTE of Storage Size.
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is not created inside a Host}, such values can be changed freely.
     *
     * @param id unique ID of the VM
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     *
     * @pre id >= 0
     * @pre numberOfPes > 0
     * @post $none
     */
    public PowerVm(int id, long mipsCapacity, int numberOfPes) {
        super(id, mipsCapacity, numberOfPes);
    }

    /**
     * Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and
     * 1024 MEGABYTE of Storage Size and no ID (which will be defined when the
     * VM is submitted to a {@link DatacenterBroker}).
     *
     * To change these values, use the respective setters. While the Vm {@link #isCreated()
     * is not created inside a Host}, such values can be changed freely.
     *
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     *
     * @pre numberOfPes > 0
     * @post $none
     */
    public PowerVm(long mipsCapacity, int numberOfPes) {
        this(-1, mipsCapacity, numberOfPes);
    }

    /**
     * Instantiates a new PowerVm.
     *
     * @param id unique ID of the VM
     * @param broker ID of the VM's owner, that is represented by the id of the {@link DatacenterBroker}
     * @param mipsCapacity the mips capacity of each Vm {@link Pe}
     * @param numberOfPes amount of {@link Pe} (CPU cores)
     * @param ramCapacity amount of ram in Megabytes
     * @param bwCapacity amount of bandwidth to be allocated to the VM (in Megabits/s)
     * @param size size the VM image in Megabytes (the amount of storage it will use, at least initially).
     * @param priority the priority
     * @param vmm Virtual Machine Monitor that manages the VM lifecycle
     * @param cloudletScheduler scheduler that defines the execution policy for Cloudlets inside this Vm
     * @param schedulingInterval not used anymore
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public PowerVm(
            final int id,
            final DatacenterBroker broker,
            final long mipsCapacity,
            final int numberOfPes,
            final int ramCapacity,
            final long bwCapacity,
            final long size,
            final int priority,
            final String vmm,
            final CloudletScheduler cloudletScheduler,
            final double schedulingInterval)
    {
        this(id, mipsCapacity, numberOfPes);
        setBroker(broker);
        setRam(ramCapacity);
        setBw(bwCapacity);
        setSize(size);
        setVmm(vmm);
        setCloudletScheduler(cloudletScheduler);
    }

    @Override
    public double updateProcessing(final double currentTime, final List<Double> mipsShare) {
        final double time = super.updateProcessing(currentTime, mipsShare);
        if (currentTime > getPreviousTime() && (currentTime - 0.1) % getHost().getDatacenter().getSchedulingInterval() == 0) {
            final double utilization = getCpuPercentUsage(getCloudletScheduler().getPreviousTime());
            if (currentTime != 0 || utilization != 0) {
                addUtilizationHistoryValue(utilization);
            }
            setPreviousTime(currentTime);
        }
        return time;
    }

    /**
     * Gets the utilization Median Absolute Deviation (MAD) in MIPS.
     * @return
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
     * @return
     */
    public double getUtilizationMean() {
        if (!getUtilizationHistory().isEmpty()) {
	        final int maxNumOfEntriesToAverage = Math.min(MAX_HISTORY_ENTRIES, getUtilizationHistory().size());
            final double usagePercentMean = getUtilizationHistory().stream()
                .limit(maxNumOfEntriesToAverage)
                .mapToDouble(usagePercent -> usagePercent)
                .average()
                .orElse(0);

            return usagePercentMean * getMips();
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
	        final double mean = getUtilizationMean();
	        final int maxNumOfEntriesToAverage = Math.min(MAX_HISTORY_ENTRIES, getUtilizationHistory().size());
            return getUtilizationHistory().stream()
                .limit(maxNumOfEntriesToAverage)
                .mapToDouble(usagePercent -> usagePercent * getMips())
                .map(usageValue -> usageValue - mean)
                .map(usageValue -> usageValue*usageValue)
                .average().orElse(0);
        }

        return 0;
    }

    /**
     * Adds a CPU utilization percentage history value to the begining of the History List.
     *
     * @param utilization the CPU utilization percentage to add
     */
    public void addUtilizationHistoryValue(final double utilization) {
        utilizationHistory.add(0, utilization);
        if (getUtilizationHistory().size() > MAX_HISTORY_ENTRIES) {
            utilizationHistory.remove(MAX_HISTORY_ENTRIES);
        }
    }

    /**
     * Gets a <b>read-only</b> CPU utilization percentage history (between [0 and 1], where 1 is 100%).
     * Each value into the returned array is the CPU utilization percentage for
     * a time interval equal to the {@link Datacenter#getSchedulingInterval()}.
     *
     * <p><b>The values are stored in the reverse chronological order.</b></p>
     *
     * @return
     */
    public List<Double> getUtilizationHistory() {
	    return Collections.unmodifiableList(utilizationHistory);
    }

    /**
     * Gets the previous time that cloudlets were processed.
     *
     * @return
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

}
