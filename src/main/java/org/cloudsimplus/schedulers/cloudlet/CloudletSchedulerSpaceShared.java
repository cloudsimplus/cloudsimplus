/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;

import java.io.Serial;

/**
 * Implements a policy of scheduling performed by a
 * {@link Vm} to run its {@link Cloudlet}s. It considers there
 * will be only one Cloudlet per VM. Other Cloudlets will be in a waiting list.
 * It also considers that the time to transfer Cloudlets to the Vm happens
 * before the Cloudlets start executing. That is, even though Cloudlets must wait for
 * CPU, data transfer happens as soon as Cloudlets are submitted.
 *
 * <p>
 * <b>This scheduler does not consider Cloudlets priorities to define execution
 * order. If actual priorities are defined for Cloudlets, they are just ignored
 * by the scheduler.</b>
 * Check {@link CloudletSchedulerCompletelyFair} for a more realistic, priority-aware scheduler,
 * but also more computationally complex.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class CloudletSchedulerSpaceShared extends CloudletSchedulerAbstract {
    @Serial
    private static final long serialVersionUID = 4699085761507163349L;

    @Override
    public double cloudletResume(Cloudlet cloudlet) {
        return findCloudletInList(cloudlet, getCloudletPausedList())
            .map(this::movePausedCloudletToExecListOrWaitingList)
            .orElse(0.0);
    }

    /**
     * Moves a Cloudlet that is being resumed to the exec or waiting List.
     *
     * @param cle the resumed Cloudlet to move
     * @return the time the cloudlet is expected to finish or zero if it was moved to the waiting list
     */
    private double movePausedCloudletToExecListOrWaitingList(final CloudletExecution cle) {
        getCloudletPausedList().remove(cle);

        // it can go to the exec list
        if (isThereEnoughFreePesForCloudlet(cle)) {
            return movePausedCloudletToExecList(cle);
        }

        // No enough free PEs: go to the waiting queue
        /*
         * A resumed cloudlet is not immediately added to the execution list.
         * It is queued so that the next time the scheduler process VM execution,
         * the cloudlet may have the opportunity to run.
         * It goes to the end of the waiting list because other cloudlets
         * could be waiting longer and have priority to execute.
         */
        addCloudletToWaitingList(cle);
        return 0.0;
    }

    /**
     * Moves a paused cloudlet to the execution list.
     *
     * @param cle the cloudlet to be moved
     * @return the time the cloudlet is expected to finish
     */
    private double movePausedCloudletToExecList(final CloudletExecution cle) {
        addCloudletToExecList(cle);
        return cloudletEstimatedFinishTime(cle, getVm().getSimulation().clock());
    }

    /**
     * The space-shared scheduler <b>does not</b> share the CPU time between
     * executing cloudlets. Each CPU ({@link Pe}) is used by another Cloudlet
     * just when the previous Cloudlet using it has finished executing
     * completely. By this way, if there are more Cloudlets than PEs, some
     * Cloudlet will not be allowed to start executing immediately.
     *
     * @param cle {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean canExecuteCloudletInternal(final CloudletExecution cle) {
        return isThereEnoughFreePesForCloudlet(cle);
    }
}
