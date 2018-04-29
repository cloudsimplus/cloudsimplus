package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.util.MathUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    public final List<Double> history;
    private final Vm vm;

    /**
     * @see #getPreviousTime()
     */
    public double previousTime;

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
        this.history = new LinkedList<>();
        this.vm = vm;
        this.enabled = enabled;
        this.setMaxHistoryEntries(DEF_MAX_HISTORY_ENTRIES);
    }

    public VmUtilizationHistory(final Vm vm) {
        this(vm, true);
    }

    @Override
    public double getUtilizationMad() {
        if (!history.isEmpty()) {
            final int n = Math.min(getMaxHistoryEntries(), getHistory().size());
            final double median = MathUtil.median(getHistory());
            final double[] deviationSum = new double[n];
            for (int i = 0; i < n; i++) {
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
            final double usagePercentMean = getHistory().stream()
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
            return getHistory().stream()
                .limit(maxNumOfEntriesToAverage)
                .mapToDouble(usagePercent -> usagePercent * vm.getMips())
                .map(usageValue -> usageValue - mean)
                .map(usageValue -> usageValue * usageValue)
                .average().orElse(0);
        }

        return 0;
    }

    @Override
    public void addUtilizationHistory(final double time){
        if(!enabled) {
            return;
        }

        if (time > getPreviousTime() && (time - 0.1) % vm.getHost().getDatacenter().getSchedulingInterval() == 0) {
            final double utilization = vm.getCpuPercentUsage(vm.getCloudletScheduler().getPreviousTime());
            if (time != 0 || utilization != 0) {
                addUtilizationHistoryValue(utilization);
            }
            setPreviousTime(time);
        }
    }

    /**
     * Adds a CPU utilization percentage history value to the beginning of the History List.
     *
     * @param utilization the CPU utilization percentage to add
     */
    private void addUtilizationHistoryValue(final double utilization) {
        history.add(0, utilization);
        if (getHistory().size() > getMaxHistoryEntries()) {
            history.remove(getMaxHistoryEntries());
        }
    }

    @Override
    public List<Double> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Override
    public double getPreviousTime() {
        return previousTime;
    }

    @Override
    public void setPreviousTime(final double previousTime) {
        this.previousTime = previousTime;
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
}
