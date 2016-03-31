package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;

/**
 * An Cloudlet {@link UtilizationModel} that uses Arithmetic Progression 
 * to increases the utilization of the related resource along the simulation time.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelArithmeticProgression implements UtilizationModel {
    /**
     * The value that represents 1%, taking a scale from 0 to 1, where 1 is 100%.
     */
    public static final double ONE_PERCENT = 0.1;
    
    /**@see #getUtilizationPercentageIncrementPerSecond() */
    private double utilizationPercentageIncrementPerSecond = ONE_PERCENT;
    
    /** @see #getInitialUtilization()  */
    private double initialUtilization = 0;
    
    /**
     * The id of the cloudlet using this UtilizationModel instance.
     */
    private int cloudletId;
    
    /**
     * The last time the {@link #getUtilization(double) } method was called.
     */
    private double previousTime = -1;
    
    /** @see #getMaxResourceUsagePercentage() */
    private double maxResourceUsagePercentage = 1;

    public UtilizationModelArithmeticProgression() {
    }
    
    public UtilizationModelArithmeticProgression(
            final int cloudletId,
            final double utilizationPercentageIncrementPerSecond) {
        this();
        this.cloudletId = cloudletId;
        this.utilizationPercentageIncrementPerSecond = utilizationPercentageIncrementPerSecond;
    }

    /**
     * Instantiates a UtilizationModelProgressive
     * that sets the {@link #setUtilizationPercentageIncrementPerSecond(double) utilization increment}
     * and the {@link #setInitialUtilization(double) initial utilization}
     * 
     * @param cloudletId
     * @param utilizationPercentageIncrementPerSecond
     * @param initialUtilization 
     */
    public UtilizationModelArithmeticProgression(
            final int cloudletId,
            final double utilizationPercentageIncrementPerSecond, 
            final double initialUtilization) {
        this(cloudletId, utilizationPercentageIncrementPerSecond);
        setInitialUtilization(initialUtilization);
    }

    @Override
    public double getUtilization(double time) {
        double utilization = initialUtilization + (utilizationPercentageIncrementPerSecond * time);
        
        if(utilization < 0)
            utilization = 0;
        else if(utilization > maxResourceUsagePercentage)
            utilization = maxResourceUsagePercentage;
        previousTime = time;
        return utilization;
    }
    /**
     * Gets the utilization percentage to be incremented
     * at the total utilization returned by {@link #getUtilization(double)}
     * at every simulation second. 
     * 
     * @return the utilization percentage increment
     * @see #setUtilizationPercentageIncrementPerSecond(double) 
     */
    public double getUtilizationPercentageIncrementPerSecond() {
        return utilizationPercentageIncrementPerSecond;
    }

    /**
     * Sets the utilization percentage to be incremented
     * at the total utilization returned by {@link #getUtilization(double)}
     * at every simulation second.
     * 
     * @param utilizationPercentageIncrementPerSecond the utilization percentage increment
     * to be set. For instance, if given the value 0.1, it means that every
     * simulation second, the total utilization will be incremented by 1%.
     * If set a negative value, the utilization will be decreased every second
     * by the given percentage. If the value is set as 0, it means
     * the utilization will be equals o the {@link #getInitialUtilization() initial utilization} 
     * all the time.
     */
    private void setUtilizationPercentageIncrementPerSecond(double utilizationPercentageIncrementPerSecond) {
        if(utilizationPercentageIncrementPerSecond > 1)
           throw new RuntimeException("Utilization percenrtage increment cannot be greater than 1 (100%)");
        this.utilizationPercentageIncrementPerSecond = utilizationPercentageIncrementPerSecond;
    }

    /**
     * Gets the initial percentage of resource  
     * that cloudlets using this UtilizationModel will require
     * when they start to execute.
     * @return the initial utilization percentage (in scale is from 0 to 1, where 1 is 100%)
     */
    public double getInitialUtilization() {
        return initialUtilization;
    }

    /**
     * Sets the initial percentage of resource  
     * that cloudlets using this UtilizationModel will require
     * when they start to execute.
     * @param the initial utilization percentage (in scale is from 0 to 1, where 1 is 100%)
     */
    private void setInitialUtilization(double initialUtilization) {
        if(initialUtilization < 0 || initialUtilization > 1)
           throw new RuntimeException("Initial utilization must to be a percentage value between [0 and 1] (0 to 100%)");
        
        this.initialUtilization = initialUtilization;
    }

    /**
     * Gets the maximum percentage of resource of resource that will be used.
     * @return the maximum resource usage percentage (in scale from [0 to 1], where 1 is equals 100%)
     */
    public double getMaxResourceUsagePercentage() {
        return maxResourceUsagePercentage;
    }

    /**
     * Sets the maximum percentage of resource of resource that will be used.
     * @param maxResourceUsagePercentage the maximum resource usage percentage (in scale from [0 to 1], where 1 is equals 100%)
     */
    public void setMaxResourceUsagePercentage(double maxResourceUsagePercentage) {
        this.maxResourceUsagePercentage = maxResourceUsagePercentage;
    }    
}
