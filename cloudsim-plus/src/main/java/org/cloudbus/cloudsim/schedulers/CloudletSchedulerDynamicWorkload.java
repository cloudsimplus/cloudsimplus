/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.CloudletExecutionInfo;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.Processor;

/**
 * CloudletSchedulerDynamicWorkload implements a policy of scheduling performed
 * by a virtual machine to run its {@link Cloudlet Cloudlets}, assuming there is
 * just one cloudlet which is working as an online service. It extends a
 * TimeShared policy, but in fact, considering that there is just one cloudlet
 * for the VM using this scheduler, the cloudlet will not compete for CPU with
 * other ones. Each VM has to have its own instance of a CloudletScheduler.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 * @todo @author manoelcampos The name of the class doesn't represent its goal. A clearer name would
 * be CloudletSchedulerSingleService as its Test Suite
 * @todo @author manoelcampos The class has some duplicated code from the
 * super class.
 */
public class CloudletSchedulerDynamicWorkload extends CloudletSchedulerTimeShared {

    /**
     * The individual MIPS capacity of each PE allocated to the VM using the
     * scheduler, considering that all PEs have the same capacity.
     *
     * @todo Despite of the class considers that all PEs have the same capacity,
     * it accepts a list of PEs with different MIPS at the method 
         * {@link #updateVmProcessing(double, java.util.List) }
     */
    private double mips;

    /**
     * The number of PEs allocated to the VM using the scheduler.
     */
    private int numberOfPes;

    /**
     * The under allocated MIPS.
     */
    private Map<String, Double> underAllocatedMips;

    /**
     * The cache of the previous time when the
     * {@link #getCurrentRequestedMips()} was called.
     */
    private double cachePreviousTime;

    /**
     * The cache of the last current requested MIPS.
     *
     * @see #getCurrentRequestedMips()
     */
    private List<Double> cacheCurrentRequestedMips;

    /**
     * Instantiates a new VM scheduler
     *
     * @param mips The individual MIPS capacity of each PE allocated to the VM
     * using the scheduler, considering that all PEs have the same capacity.
     * @param numberOfPes The number of PEs allocated to the VM using the
     * scheduler.
     */
    public CloudletSchedulerDynamicWorkload(double mips, int numberOfPes) {
        super();
        setMips(mips);
        setNumberOfPes(numberOfPes);
        setUnderAllocatedMips(new HashMap<>());
        setCachePreviousTime(-1);
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        setCurrentMipsShare(mipsShare);

        double nextEvent = Double.MAX_VALUE;
        List<CloudletExecutionInfo> cloudletsToFinish = new ArrayList<>();

        for (CloudletExecutionInfo rcl : getCloudletExecList()) {
            rcl.updateCloudletFinishedSoFar((long) (timeSpan(currentTime)
                    * getTotalCurrentAllocatedMipsForCloudlet(rcl, getPreviousTime()) * Consts.MILLION));

            if (rcl.getCloudlet().isFinished()) { // finished: remove from the list
                cloudletsToFinish.add(rcl);
            } else { // not finish: estimate the finish time
                double estimatedFinishTime = getEstimatedFinishTime(rcl, currentTime);
                if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
                    estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
                }
                if (estimatedFinishTime < nextEvent) {
                    nextEvent = estimatedFinishTime;
                }
            }
        }

        for (CloudletExecutionInfo rgl : cloudletsToFinish) {
            getCloudletExecList().remove(rgl);
            cloudletFinish(rgl);
        }

        setPreviousTime(currentTime);

        if (getCloudletExecList().isEmpty()) {
            return Double.MAX_VALUE;
        }

        return nextEvent;
    }

    @Override
    public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(cl);
        rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
        getCloudletExecList().add(rcl);
        return getEstimatedFinishTime(rcl, getPreviousTime());
    }

    /**
     * Get the estimated completion time of a given cloudlet.
     *
     * @param rcl the cloudlet
     * @param time the time
     * @return the estimated finish time
     */
    public double getEstimatedFinishTime(CloudletExecutionInfo rcl, double time) {
        return time + 
               ((rcl.getRemainingCloudletLength()) / 
                getTotalCurrentAllocatedMipsForCloudlet(rcl, time));
    }

    @Override
    public List<Double> getCurrentRequestedMips() {
        if (getCachePreviousTime() == getPreviousTime()) {
            return getCacheCurrentRequestedMips();
        }
        List<Double> currentMips = new ArrayList<>();
        double totalMips = getTotalUtilizationOfCpu(getPreviousTime()) * getTotalMips();
        double mipsForPe = totalMips / getNumberOfPes();

        for (int i = 0; i < getNumberOfPes(); i++) {
            currentMips.add(mipsForPe);
        }

        setCachePreviousTime(getPreviousTime());
        setCacheCurrentRequestedMips(currentMips);

        return currentMips;
    }

    @Override
    public double getTotalCurrentRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        return rcl.getCloudlet().getUtilizationOfCpu(time) * getTotalMips();
    }

    @Override
    public double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare) {
        double totalCurrentMips = 0.0;
        if (mipsShare != null) {
            int neededPEs = rcl.getNumberOfPes();
            for (double mips : mipsShare) {
                totalCurrentMips += mips;
                neededPEs--;
                if (neededPEs <= 0) {
                    break;
                }
            }
        }
        return totalCurrentMips;
    }

    @Override
    public double getTotalCurrentAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        double totalCurrentRequestedMips = getTotalCurrentRequestedMipsForCloudlet(rcl, time);
        double totalCurrentAvailableMips = getTotalCurrentAvailableMipsForCloudlet(rcl, getCurrentMipsShare());
        if (totalCurrentRequestedMips > totalCurrentAvailableMips) {
            return totalCurrentAvailableMips;
        }
        return totalCurrentRequestedMips;
    }

    /**
     * Update under allocated mips for cloudlet.
     *
     * @param rcl the rgl
     * @param mips the mips
     * @todo It is not clear the goal of this method. The related test case
     * doesn't make it clear too. The method doesn't appear to be used anywhere.
     */
    public void updateUnderAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double mips) {
        if (getUnderAllocatedMips().containsKey(rcl.getUid())) {
            mips += getUnderAllocatedMips().get(rcl.getUid());
        }
        getUnderAllocatedMips().put(rcl.getUid(), mips);
    }

    /**
     * Gets the total current mips available for the VM using the scheduler. The
     * total is computed from the {@link #getCurrentMipsShare()}
     *
     * @return the total current mips
     */
    public double getTotalCurrentMips() {
        Processor p = Processor.fromMipsList(getCurrentMipsShare());
        return p.getTotalMipsCapacity();
    }

    /**
     * Gets the total mips considering all PEs.
     *
     * @return the total mips capacity
     */
    public double getTotalMips() {
        return getNumberOfPes() * getMips();
    }

    /**
     * Sets the pes number.
     *
     * @param pesNumber the new pes number
     */
    public final void setNumberOfPes(int pesNumber) {
        numberOfPes = pesNumber;
    }

    /**
     * Gets the pes number.
     *
     * @return the pes number
     */
    public final int getNumberOfPes() {
        return numberOfPes;
    }

    /**
     * Sets the mips.
     *
     * @param mips the new mips
     */
    public final void setMips(double mips) {
        this.mips = mips;
    }

    /**
     * Gets the mips.
     *
     * @return the mips
     */
    public final double getMips() {
        return mips;
    }

    /**
     * Sets the under allocated mips.
     *
     * @param underAllocatedMips the under allocated mips
     */
    public final void setUnderAllocatedMips(Map<String, Double> underAllocatedMips) {
        this.underAllocatedMips = underAllocatedMips;
    }

    /**
     * Gets the under allocated mips.
     *
     * @return the under allocated mips
     */
    public Map<String, Double> getUnderAllocatedMips() {
        return underAllocatedMips;
    }

    /**
     * Gets the cache of previous time.
     *
     * @return the cache previous time
     */
    protected double getCachePreviousTime() {
        return cachePreviousTime;
    }

    /**
     * Sets the cache of previous time.
     *
     * @param cachePreviousTime the new cache previous time
     */
    protected final void setCachePreviousTime(double cachePreviousTime) {
        this.cachePreviousTime = cachePreviousTime;
    }

    /**
     * Gets the cache of current requested mips.
     *
     * @return the cache current requested mips
     */
    protected List<Double> getCacheCurrentRequestedMips() {
        return cacheCurrentRequestedMips;
    }

    /**
     * Sets the cache of current requested mips.
     *
     * @param cacheCurrentRequestedMips the new cache current requested mips
     */
    protected void setCacheCurrentRequestedMips(List<Double> cacheCurrentRequestedMips) {
        this.cacheCurrentRequestedMips = cacheCurrentRequestedMips;
    }

}
