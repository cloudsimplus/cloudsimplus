/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;

import org.cloudbus.cloudsim.core.CloudSim;
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
     * Creates a new CloudletSchedulerSpaceShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerSpaceShared() {
        super();
        usedPes = 0;
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
        CloudletExecutionInfo foundRcl = searchForCloudletIntoList(getCloudletPausedList(), cloudletId);
        if (foundRcl == null) {
            // not found in the paused list: either it is in in the queue, executing or not exist
            return 0.0;
        }
        
        getCloudletPausedList().remove(foundRcl);

        // it can go to the exec list
        if ((getProcessor().getNumberOfPes() - usedPes) >= foundRcl.getNumberOfPes()) {
            foundRcl.setCloudletStatus(Cloudlet.Status.INEXEC);
            long remainingLenghtAcrossAllPes = foundRcl.getRemainingCloudletLength();
            remainingLenghtAcrossAllPes *= foundRcl.getNumberOfPes();
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
            foundRcl.getCloudlet().setCloudletLength(remainingLenghtAcrossAllPes);

            getCloudletExecList().add(foundRcl);
            usedPes += foundRcl.getNumberOfPes();

            // calculate the expected time for cloudlet completion
            long remainingLength = foundRcl.getRemainingCloudletLength();
            double estimatedFinishTime = CloudSim.clock()
                    + (remainingLength / (getProcessor().getCapacity() * foundRcl.getNumberOfPes()));

            return estimatedFinishTime;
        } else {// no enough free PEs: go to the waiting queue
            foundRcl.setCloudletStatus(Cloudlet.Status.QUEUED);

            long remainingLengthAcrossPes = foundRcl.getRemainingCloudletLength();
            remainingLengthAcrossPes *= foundRcl.getNumberOfPes();
            foundRcl.getCloudlet().setCloudletLength(remainingLengthAcrossPes);

            getCloudletWaitingList().add(foundRcl);
            return 0.0;
        }
    }

    /**
     * Search for a cloudlet into a given list.
     * @param cloudletList the cloudlet list 
     * @param cloudletId the id of the cloudlet to search
     * @return the cloudlet or null if not found
     */
    protected CloudletExecutionInfo searchForCloudletIntoList(List<CloudletExecutionInfo> cloudletList, int cloudletId) {
        for (CloudletExecutionInfo rcl : cloudletList) {
            if (rcl.getCloudletId() == cloudletId) {
                return rcl;
            }
        }
        return null;
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        // it can go to the exec list
        if ((getProcessor().getNumberOfPes() - usedPes) >= cloudlet.getNumberOfPes()) {
            CloudletExecutionInfo rcl = new CloudletExecutionInfo(cloudlet);
            rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
            getCloudletExecList().add(rcl);
            usedPes += cloudlet.getNumberOfPes();
        } else {// no enough free PEs: go to the waiting queue
            CloudletExecutionInfo rcl = new CloudletExecutionInfo(cloudlet);
            rcl.setCloudletStatus(Cloudlet.Status.QUEUED);
            getCloudletWaitingList().add(rcl);
            return 0.0;
        }

        // calculate the expected time for cloudlet completion
        // use the current capacity to estimate the extra amount of
        // time to transfer the cloudlet to the VM. It must be added to the cloudlet length
        double extraSize = getProcessor().getCapacity() * fileTransferTime;
        long length = cloudlet.getCloudletLength();
        length += extraSize;
        
        /**
         * @todo @author manoelcampos It is very strange to change
         * the length of the cloudlet, once it is 
         * a value defined by the user.
         * The execution length is one thing, 
         * the total execution time is other.
         * The length is being increased to include
         * the time the cloudlet spend to be transfered
         * to the VM (see comment above)
         */
        cloudlet.setCloudletLength(length);
        return cloudlet.getCloudletLength() / getProcessor().getCapacity();
    }

    /**
     * Returns the first cloudlet to migrate to another VM.
     *
     * @return the first running cloudlet
     * @pre $none
     * @post $none
     */
    @Override
    public Cloudlet migrateCloudlet() {
        Cloudlet cl = super.migrateCloudlet();
        if(cl != Cloudlet.NULL){
            usedPes -= cl.getNumberOfPes();
            return cl;
        }
        
        return null;
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
