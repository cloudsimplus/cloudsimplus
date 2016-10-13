/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Processor;

/**
 * CloudletSchedulerSpaceShared implements a policy of scheduling performed by a
 * virtual machine to run its {@link Cloudlet Cloudlets}. It consider there will
 * be only one cloudlet per VM. Other cloudlets will be in a waiting list. We
 * consider that file transfer from cloudlets waiting happens before cloudlet
 * execution. I.e., even though cloudlets must wait for CPU, data transfer
 * happens as soon as cloudlets are submitted.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class CloudletSchedulerSpaceShared extends CloudletSchedulerAbstract {
	/**
	 * @see #getCloudletExecList()
	 */
	private Collection<? extends CloudletExecutionInfo> cloudletExecList;

    /**
     * Creates a new CloudletSchedulerSpaceShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerSpaceShared() {
        super();
        usedPes = 0;
	    this.cloudletExecList = new ArrayList<>();
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        return super.updateVmProcessing(currentTime, mipsShare);
    }

    @Override
    public void cloudletFinish(CloudletExecutionInfo rcl) {
        super.cloudletFinish(rcl);
        usedPes -= rcl.getNumberOfPes();
    }

    @Override
    public double cloudletResume(int cloudletId) {
	    Optional<CloudletExecutionInfo> optional = findCloudletInList(cloudletId, getCloudletPausedList());
        if (!optional.isPresent()) {
            // not found in the paused list: either it is in in the queue, executing or not exist
            return 0.0;
        }

        getCloudletPausedList().remove(optional.get());
	    CloudletExecutionInfo c = optional.get();

        // it can go to the exec list
        if ((getProcessor().getNumberOfPes() - usedPes) >= c.getNumberOfPes()) {
	        return movePausedCloudletToExecList(c);
        }

        // no enough free PEs: go to the waiting queue
		/*
		* @todo @author manoelcampos The cloudlet length is the lenght in MI
		* to be executed by each cloudlet PE. However, this code inherited from CloudSim
		* changes to length to the total length across all PEs, what is very strange
		* and has to be investigated.*/
	    long remainingLengthAcrossPes = c.getRemainingCloudletLength();
	    remainingLengthAcrossPes *= c.getNumberOfPes();
	    c.getCloudlet().setCloudletLength(remainingLengthAcrossPes);
	    moveCloudletToWaitingList(c);
        return 0.0;
    }

	/**
	 * Moves a paused cloudlet to the execution list.
	 *
	 * @param c the cloudlet to be moved
	 * @return the time the cloudlet is expected to finish
	 */
	private double movePausedCloudletToExecList(CloudletExecutionInfo c) {
		long remainingLenghtAcrossAllPes = c.getRemainingCloudletLength();
		remainingLenghtAcrossAllPes *= c.getNumberOfPes();

		/**
		 * @todo @author manoelcampos It's very strange
		 * to change the cloudlet length that is
		 * defined by the user. And in the documentation
		 * of the attribute, it is supposed to be the length
		 * that will be executed in each cloudlet PE,
		 * not the length sum across all existing PEs,
		 * as it is being changed here
		 * (you can see that the size is being multiplied by the
		 * number of PEs).
		 */
		c.getCloudlet().setCloudletLength(remainingLenghtAcrossAllPes);

		c.setCloudletStatus(Cloudlet.Status.INEXEC);
		getCloudletExecList().add(c);
		usedPes += c.getNumberOfPes();

		// calculate the expected time for cloudlet completion
		long remainingLength = c.getRemainingCloudletLength();
		double estimatedFinishTime = CloudSim.clock()
		        + (remainingLength / (getProcessor().getCapacity() * c.getNumberOfPes()));

		return estimatedFinishTime;
	}

	@Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
		double cloudletExpectedFinishTime = super.cloudletSubmit(cloudlet, fileTransferTime);
		//if the expected finish time is greater than 0, the Cloudlet was added to the execution list
		if (cloudletExpectedFinishTime > 0) {
	        usedPes += cloudlet.getNumberOfPes();
        }

        return cloudletExpectedFinishTime;
    }

	/**
	 * The space-shared scheduler <b>does not</b> share the CPU time between executing cloudlets.
	 * Each CPU ({@link Pe}) is used by another Cloudlet just when the previous Cloudlet
	 * using it has finished executing completely.
	 * By this way, if there are more Cloudlets than PEs, some Cloudlet
	 * will not be allowed to start executing immediately.
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean canAddCloudletToExecutionList(Cloudlet cloudlet) {
		return (getProcessor().getNumberOfPes() - usedPes) >= cloudlet.getNumberOfPes();
	}

	/**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet getCloudletToMigrate() {
        Cloudlet cl = super.getCloudletToMigrate();
        if(cl != Cloudlet.NULL){
            usedPes -= cl.getNumberOfPes();
        }

        return cl;
    }

    @Override
    public List<Double> getCurrentRequestedMips() {
        List<Double> mipsShare = new ArrayList<>();
        if (getCurrentMipsShare() != null) {
            for (Double mips : getCurrentMipsShare()) {
                mipsShare.add(mips);
            }
        }
        return mipsShare;
    }

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CloudletExecutionInfo> Collection<T> getCloudletExecList() {
		return (Collection<T>) cloudletExecList;
	}

    @Override
    public double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare) {
        /*@todo The param rcl is not being used.*/
        Processor p = Processor.fromMipsList(mipsShare);
        return p.getCapacity();
    }

    @Override
    public double getTotalCurrentAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        //@todo the method isn't in fact implemented
        // TODO Auto-generated method stub
        return 0.0;
    }

    @Override
    public double getTotalCurrentRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time) {
        //@todo the method isn't in fact implemented
        // TODO Auto-generated method stub
        return 0.0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfRam() {
        //@todo the method isn't in fact implemented
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfBw() {
        //@todo the method isn't in fact implemented
        // TODO Auto-generated method stub
        return 0;
    }

}
