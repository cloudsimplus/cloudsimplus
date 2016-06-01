/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.ResCloudlet;

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
     * Creates a new CloudletSchedulerTimeShared object. This method must be
     * invoked before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerTimeShared() {
        super();
    }

    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        return super.updateVmProcessing(currentTime, mipsShare);
    }

    @Override
    public double cloudletResume(int cloudletId) {
        for (int i = 0; i < getCloudletPausedList().size(); i++) {
            ResCloudlet rcl = getCloudletPausedList().get(i);
            if (rcl.getCloudletId() == cloudletId) {
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
        }

        return 0.0;
    }

    @Override
    public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        ResCloudlet rcl = new ResCloudlet(cloudlet);
        rcl.setCloudletStatus(Cloudlet.Status.INEXEC);
        getCloudletExecList().add(rcl);

        // use the current capacity to estimate the extra amount of
        // time to file transferring. It must be added to the cloudlet length
        double extraSize = getProcessor().getCapacity() * fileTransferTime;
        long length = (long) (cloudlet.getCloudletLength() + extraSize);
        cloudlet.setCloudletLength(length);

        return cloudlet.getCloudletLength() / getProcessor().getCapacity();
    }

    /**
     * @todo If the method always return an empty list (that is created locally),
     * it doesn't make sense to exist. See other implementations such as
     * {@link CloudletSchedulerSpaceShared#getCurrentRequestedMips()}
     * @return
     */
    @Override
    public List<Double> getCurrentRequestedMips() {
        return new ArrayList<>();
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
    public double getTotalCurrentAvailableMipsForCloudlet(ResCloudlet rcl, List<Double> mipsShare) {
        return getProcessor().getCapacity();
    }

    @Override
    public double getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet rcl, double time) {
        //@todo The method is not implemented, in fact
        return 0.0;
    }

    @Override
    public double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl, double time) {
        //@todo The method is not implemented, in fact
        // TODO Auto-generated method stub
        return 0.0;
    }

    @Override
    public double getCurrentRequestedUtilizationOfRam() {
        return getCloudletExecList().stream()
                .mapToDouble(
                        rcl -> rcl.getCloudlet().getUtilizationOfRam(CloudSim.clock()))
                .sum();
    }

    @Override
    public double getCurrentRequestedUtilizationOfBw() {
        return getCloudletExecList().stream()
                .mapToDouble(
                        rcl -> rcl.getCloudlet().getUtilizationOfBw(CloudSim.clock()))
                .sum();
    }
}
