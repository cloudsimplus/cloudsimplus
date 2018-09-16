package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.util.MathUtil;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Stores resource utilization data for a specific {@link Vm}.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.4
 */
public class VmUtilizationHistory implements UtilizationHistory {

    private boolean enabled;
    private int maxHistoryEntries;

    /**
     * @see #getHistory()
     */
    private final SortedMap<Double, Double> history;
    private final Vm vm;

    /**
     * The previous time that cloudlets were processed.
     */
    private double previousTime;

    /**
     * Instantiates the class to store resource utilization history for a specific {@link Vm}.
     *
     * @param vm the vm to instantiates the object to store utilization history
     * @param enabled true if the history must be enabled by default, enabling usage
     *                history to be collected and stored;
     *                false if it must be disabled to avoid storing any history,
     *                in order to reduce memory usage
     */
    public VmUtilizationHistory(final Vm vm, final boolean enabled) {
        this.history = new TreeMap<>();
        this.vm = vm;
        this.enabled = enabled;
        this.setMaxHistoryEntries(Integer.MAX_VALUE);
    }

    public VmUtilizationHistory(final Vm vm) {
        this(vm, true);
    }

    @Override
    public double getUtilizationMad() {
        if (history.isEmpty()) {
            return 0;
        }

        final int length = Math.min(getMaxHistoryEntries(), getHistory().size());
        final double median = MathUtil.median(getHistory().values());
        final double[] deviationSum = new double[length];
        for (int i = 0; i < length; i++) {
            deviationSum[i] = Math.abs(median - getHistory().get(i));
        }

        return MathUtil.median(deviationSum);
    }

    @Override
    public double getUtilizationMean() {
        if (history.isEmpty()) {
            return 0;
        }

        final int maxEntries = Math.min(getMaxHistoryEntries(), getHistory().size());
        final double usagePercentMean = getHistory().values().stream()
            .limit(maxEntries)
            .mapToDouble(usagePercent -> usagePercent)
            .average()
            .orElse(0);

        return usagePercentMean * vm.getMips();
    }

    @Override
    public double getUtilizationVariance() {
        if (history.isEmpty()) {
            return 0;
        }

        final double mean = getUtilizationMean();
        final int maxNumOfEntriesToAverage = Math.min(getMaxHistoryEntries(), getHistory().size());
        return getHistory().values().stream()
            .limit(maxNumOfEntriesToAverage)
            .mapToDouble(usagePercent -> usagePercent * vm.getMips())
            .map(usageValue -> usageValue - mean)
            .map(usageValue -> usageValue * usageValue)
            .average().orElse(0);
    }

    @Override
    public void addUtilizationHistory(double time) {
        if (!enabled || isNotTimeToAddHistory(time)) {
            return;
        }

        final double utilization = vm.getCpuPercentUsage(vm.getCloudletScheduler().getPreviousTime());
        time = vm.isIdle() ? time : (int)time;
        addUtilizationHistoryValue(time, utilization);
        this.previousTime = time;
    }

    /**
     * Defines that it isn't time to add utilization history in any one of the following conditions:
     * - the simulation clock was not changed yet;
     * - the time passed is smaller than one second and the VM is not idle;
     * - the floor time is equal to the previous time and VM is not idle.
     *
     * If the time is smaller than one second and the VM became idle,
     * the history will be added so that we know what is the resource
     * utilization when the VM became idle.
     * This way, we can see clearly in the history when the VM was busy
     * and when it became idle.
     *
     * If the floor time is equal to the previous time and VM is not idle,
     * that means not even a second has passed. This way,
     * that utilization will not be stored.
     *
     * @param time the current simulation time
     * @return true if it's time to add utilization history, false otherwise
     */
    private boolean isNotTimeToAddHistory(final double time) {
        return time <= 0 ||
               (time - previousTime < 1 && !vm.isIdle()) ||
               (Math.floor(time) == previousTime && !vm.isIdle());
    }

    /**
     * Adds a CPU utilization percentage history value.
     *
     * @param time the time this utilization was collected
     * @param utilizationPercent the CPU utilization percentage to add
     */
    private void addUtilizationHistoryValue(final double time, final double utilizationPercent) {
        history.put(time, utilizationPercent);
        if (getHistory().size() > maxHistoryEntries) {
            history.remove(maxHistoryEntries);
        }
    }

    @Override
    public SortedMap<Double, Double> getHistory() {
        return Collections.unmodifiableSortedMap(history);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public int getMaxHistoryEntries() {
        return maxHistoryEntries;
    }

    @Override
    public void setMaxHistoryEntries(final int maxHistoryEntries) {
        this.maxHistoryEntries = maxHistoryEntries;
    }

    @Override
    public Vm getVm() {
        return vm;
    }
}
