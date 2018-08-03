/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * CloudletSchedulerSpaceShared implements a policy of scheduling performed by a
 * virtual machine to run its {@link Cloudlet Cloudlets}. It considers there
 * will be only one Cloudlet per VM. Other Cloudlets will be in a waiting list.
 * It also considers that the time to transfer Cloudlets to the Vm happens
 * before Cloudlet starts executing. I.e., even though Cloudlets must wait for
 * CPU, data transfer happens as soon as Cloudlets are submitted.
 *
 * <p>
 * <b>This scheduler does not consider Cloudlets priorities to define execution
 * order. If actual priorities are defined for Cloudlets, they are just ignored
 * by the scheduler.</b></p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class CloudletSchedulerSpaceShared extends CloudletSchedulerAbstract {

    @Override
    public double cloudletResume(Cloudlet cloudlet) {
        return findCloudletInList(cloudlet, getCloudletPausedList())
            .map(this::movePausedCloudletToExecListOrWaitingList)
            .orElse(0.0);
    }

    /**
     * Moves a Cloudlet that is being resumed to the exec or waiting List.
     *
     * @param c the resumed Cloudlet to move
     * @return the time the cloudlet is expected to finish or zero if it was moved to the waiting list
     */
    private double movePausedCloudletToExecListOrWaitingList(CloudletExecution c) {
        getCloudletPausedList().remove(c);

        // it can go to the exec list
        if (isThereEnoughFreePesForCloudlet(c)) {
            return movePausedCloudletToExecList(c);
        }

        // No enough free PEs: go to the waiting queue
        /*
         * A resumed cloudlet is not immediately added to the execution list.
         * It is queued so that the next time the scheduler process VM execution,
         * the cloudlet may have the opportunity to run.
         * It goes to the end of the waiting list because other cloudlets
         * could be waiting longer and have priority to execute.
         */
        addCloudletToWaitingList(c);
        return 0.0;
    }

    /**
     * Moves a paused cloudlet to the execution list.
     *
     * @param c the cloudlet to be moved
     * @return the time the cloudlet is expected to finish
     */
    private double movePausedCloudletToExecList(CloudletExecution c) {
        addCloudletToExecList(c);
        return getEstimatedFinishTimeOfCloudlet(c, getVm().getSimulation().clock());
    }

    /**
     * The space-shared scheduler <b>does not</b> share the CPU time between
     * executing cloudlets. Each CPU ({@link Pe}) is used by another Cloudlet
     * just when the previous Cloudlet using it has finished executing
     * completely. By this way, if there are more Cloudlets than PEs, some
     * Cloudlet will not be allowed to start executing immediately.
     *
     * @param cloudlet {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean canExecuteCloudletInternal(final CloudletExecution cloudlet) {
        return isThereEnoughFreePesForCloudlet(cloudlet);
    }
}
