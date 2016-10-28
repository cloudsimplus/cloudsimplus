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

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * CloudletSchedulerTimeShared implements a policy of scheduling performed by a
 * virtual machine to run its {@link Cloudlet Cloudlets}. Cloudlets execute in
 * time-shared manner in VM, i.e., it performs preemptive execution
 * of Cloudlets in the VM's PEs. Each VM has to have its own instance of a
 * CloudletScheduler.
 * 
 * <p>CPU context switch is the process of removing an application (Cloudlets) that is using 
 * a CPU core ({@link Pe}) from the {@link #getCloudletExecList() run queue}, 
 * to allow other one in the {@link #getCloudletWaitingList() waiting queue} 
 * to start executing in the same CPU.
 * This process enables sharing the CPU time between different applications.  
 * </p>
 * 
 * <p>
 * <b>NOTE</b>: This implementation simplifies the context switch process, not 
 * in fact switching cloudlets between the run queue and the waiting queue.
 * It just considers there is not waiting Cloudlet, oversimplifying the 
 * problem as if for a simulation second <tt>t</tt>, the total processing capacity 
 * of the processor cores (in MIPS) is equally divided by the applications that are using them.
 * This in fact is not possible, once just one application can use
 * a CPU core at a time. However, since the basic CloudletScheduler implementations
 * do not account the context switch overhead, there is no difference in
 * Cloudlets finish time and this simplification works well. 
 * </p>
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class CloudletSchedulerTimeShared extends CloudletSchedulerAbstract {
	/**
	 * @see #getCloudletExecList()
	 */
	private final List<CloudletExecutionInfo> cloudletExecList;

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

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>For time-shared schedulers, this list is always empty, once
	 * the VM PEs are shared across all Cloudlets running inside a VM.
	 * Each Cloudlet has the opportunity to use the PEs
	 * for a given timeslice.</b></p>
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	public List<CloudletExecutionInfo> getCloudletWaitingList() {
		return super.getCloudletWaitingList();
	}

    /**
     * Moves a Cloudlet that was paused and has just been resumed
     * to the Cloudlet execution list.
     *
     * @param cloudlet the Cloudlet to move from the paused to the exec lit
     * @return the Cloudlet expected finish time
     */
    private double movePausedCloudletToExecListAndGetExpectedFinishTime(CloudletExecutionInfo cloudlet){
        getCloudletPausedList().remove(cloudlet);
        cloudlet.setCloudletStatus(Cloudlet.Status.INEXEC);
        addCloudletToExecList(cloudlet);

        // calculate the expected time for cloudlet completion
        // first: how many PEs do we have?
        double remainingLength = cloudlet.getRemainingCloudletLength();
        double estimatedFinishTime = CloudSim.clock()
            + (remainingLength / (getProcessor().getCapacity()
            * cloudlet.getNumberOfPes()));

        return estimatedFinishTime;
    };

	@Override
    public double cloudletResume(int cloudletId) {
	    return
		    getCloudletPausedList().stream()
		        .filter(c -> c.getCloudletId() == cloudletId)
		        .findFirst()
                .map(this::movePausedCloudletToExecListAndGetExpectedFinishTime)
                .orElse(0.0);
    }

	/**
	 * This time-shared scheduler shares the CPU time between all executing cloudlets,
	 * giving the same CPU timeslice for each Cloudlet to execute.
	 * It always allow any submitted Cloudlets to be imediately added to the execution list.
	 * By this way, it doesn't matter what Cloudlet is being submitted, since it will
	 * always include it in the execution list.
	 *
	 * @param cloudlet the Cloudlet that will be added to the execution list.
	 * @return always <b>true</b> to indicate that any submitted Cloudlet can be immediately added to the execution list
	 */
	@Override
	public boolean canAddCloudletToExecutionList(CloudletExecutionInfo cloudlet) {
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

	@Override
	public List<CloudletExecutionInfo> getCloudletExecList() {
		return Collections.unmodifiableList(cloudletExecList);
	}

    @Override
    protected void removeCloudletFromExecList(CloudletExecutionInfo cloudlet) {
        cloudletExecList.remove(cloudlet);
    }

    @Override
    public void addCloudletToExecList(CloudletExecutionInfo cloudlet) {
        cloudletExecList.add(cloudlet);
    }

}
