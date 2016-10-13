/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * CloudletSchedulerTimeShared implements a policy of scheduling performed by a
 * virtual machine to run its {@link Cloudlet Cloudlets}. Cloudlets execute in
 * time-shared manner in VM, i.e., it performs preemptive execution
 * of Cloudlets in the VM's PEs. Each VM has to have its own instance of a
 * CloudletScheduler.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class CloudletSchedulerTimeShared extends CloudletSchedulerAbstract {
	/**
	 * @see #getCloudletExecList()
	 */
	private Collection<? extends CloudletExecutionInfo> cloudletExecList;

    /**
     * Creates a new CloudletSchedulerTimeShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerTimeShared() {
        super();
	    this.cloudletExecList = new ArrayList<>();
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        return super.updateVmProcessing(currentTime, mipsShare);
    }

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>For time-shared schedulers, this list is always empty, once
	 * the VM PEs are shared across all Cloudlets running inside a VM.
	 * Each Cloudlet has the opportunity to use the PEs
	 * for a given timeslice.</b></p>
	 *
	 * @param <T> {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public <T extends CloudletExecutionInfo> List<T> getCloudletWaitingList() {
		return super.getCloudletWaitingList();
	}

	@Override
    public double cloudletResume(int cloudletId) {
	    Optional<CloudletExecutionInfo> optional =
		    getCloudletPausedList().stream()
		        .filter(c -> c.getCloudletId() == cloudletId)
		        .findFirst();

        if(!optional.isPresent()) {
	        return 0.0;
        }

        final CloudletExecutionInfo rcl = optional.get();
        getCloudletPausedList().remove(rcl);
        rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
        getCloudletExecList().add(rcl);

        // calculate the expected time for cloudlet completion
        // first: how many PEs do we have?
        double remainingLength = rcl.getRemainingCloudletLength();
        double estimatedFinishTime = CloudSim.clock()
                + (remainingLength / (getProcessor().getCapacity()
                * rcl.getNumberOfPes()));

        return estimatedFinishTime;
    }

	/**
	 * This time-shared scheduler shares the CPU time between all executing cloudlets,
	 * giving the same CPU timeslice for each Cloudlet to execute.
	 * It always allow any submitted Cloudlets to be imediately added to the execution list.
	 * By this way, doesn't matter what Cloudlet is being submitted, it always will
	 * include it in the execution list.
	 *
	 * @return always <b>true</b> to indicate that any submitted Cloudlet can be immediately added to the execution list
	 */
	@Override
	public boolean canAddCloudletToExecutionList(Cloudlet cloudlet) {
		return true;
	}

	/**
     * @todo If the method always return an empty list (that is created locally),
     * it doesn't make sense to exist. See other implementations such as
     * {@link CloudletSchedulerSpaceShared#getCurrentRequestedMips()}
     * @return
     */
    @Override
    public List<Double> getCurrentRequestedMips() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     * It in fact doesn't consider the parameters given
     * because in the Time Shared Scheduler, all the
     * CPU capacity from the VM that is managed by the scheduler
     * is made available for all VMs.
     *
     * @param rcl {@inheritDoc}
     * @param mipsShare {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare) {
        return getProcessor().getCapacity();
    }

    @Override
    public double getTotalCurrentAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        //@todo The method is not implemented, in fact
        return 0.0;
    }

    @Override
    public double getTotalCurrentRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        //@todo The method is not implemented, in fact
        return 0.0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfRam() {
	    final double time = CloudSim.clock();
        return getCloudletExecList().stream()
                .mapToDouble(rcl -> rcl.getCloudlet().getUtilizationOfRam(time))
                .sum();
    }

    @Override
    public double getCurrentRequestedUtilizationOfBw() {
	    final double time = CloudSim.clock();
        return getCloudletExecList().stream()
                .mapToDouble(rcl -> rcl.getCloudlet().getUtilizationOfBw(time))
                .sum();
    }

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CloudletExecutionInfo> Collection<T> getCloudletExecList() {
		return (Collection<T>) cloudletExecList;
	}

}
