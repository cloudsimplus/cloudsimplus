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
    private int maxHistoryEntires;

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
        if (!history.isEmpty()) {
            final int length = Math.min(getMaxHistoryEntries(), getHistory().size());
            final double median = MathUtil.median(getHistory().values());
            final double[] deviationSum = new double[length];
            for (int i = 0; i < length; i++) {
                deviationSum[i] = Math.abs(median - getHistory().get(i));
            }
            return MathUtil.median(deviationSum);
        }

        return 0;
    }

    @Override
    public double getUtilizationMean() {
        if (!history.isEmpty()) {
            final int maxEntries = Math.min(getMaxHistoryEntries(), getHistory().size());
            final double usagePercentMean = getHistory().values().stream()
                .limit(maxEntries)
                .mapToDouble(usagePercent -> usagePercent)
                .average()
                .orElse(0);

            return usagePercentMean * vm.getMips();
        }

        return 0;
    }

    @Override
    public double getUtilizationVariance() {
        if (!history.isEmpty()) {
            final double mean = getUtilizationMean();
            final int maxNumOfEntriesToAverage = Math.min(getMaxHistoryEntries(), getHistory().size());
            return getHistory().values().stream()
                .limit(maxNumOfEntriesToAverage)
                .mapToDouble(usagePercent -> usagePercent * vm.getMips())
                .map(usageValue -> usageValue - mean)
                .map(usageValue -> usageValue * usageValue)
                .average().orElse(0);
        }

        return 0;
    }

    @Override
    public void addUtilizationHistory(double time){
        if(!enabled) {
            return;
        }

        if (time > 0 && (time - previousTime >= 1 || vm.isIdle())) {
            final double utilization = vm.getCpuPercentUsage(vm.getCloudletScheduler().getPreviousTime());
            if(Math.floor(time) > previousTime || vm.isIdle()) {
                time = vm.isIdle() ? time : (int)time;
                addUtilizationHistoryValue(time, utilization);
                this.previousTime = time;
            }
        }
    }

    /**
     * Adds a CPU utilization percentage history value.
     *
     * @param time the time this utilization was collected
     * @param utilizationPercent the CPU utilization percentage to add
     */
    private void addUtilizationHistoryValue(final double time, final double utilizationPercent) {
        history.put(time, utilizationPercent);
        if (getHistory().size() > maxHistoryEntires) {
            history.remove(maxHistoryEntires);
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
        return maxHistoryEntires;
    }

    @Override
    public void setMaxHistoryEntries(final int maxHistoryEntries) {
        this.maxHistoryEntires = maxHistoryEntries;
    }

    @Override
    public Vm getVm() {
        return vm;
    }
}
