/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.schedulers.vm.VmScheduler;

import java.io.Serial;
import java.util.List;

/// Implements a policy of scheduling performed by a
/// virtual machine to run its [Cloudlets][Cloudlet]. Cloudlets execute in
/// a time-shared manner in VM. Each VM has to have its own instance of a
/// CloudletScheduler. **This scheduler does not consider Cloudlets priorities
/// to define execution order. If actual priorities are defined for Cloudlets,
/// they are just ignored by the scheduler.**
/// Check [CloudletSchedulerCompletelyFair] for a more realistic, priority-aware scheduler,
/// but also more computationally complex.
///
/// It also does not perform a preemption process to move running
/// Cloudlets to the waiting list to make room for other already waiting
/// Cloudlets to run. It just imposes there is not waiting Cloudlet,
/// **oversimplifying** the problem considering that for a given simulation
/// second `t`, the total processing capacity of the processor cores (in
/// MIPS) is equally divided by the applications that are using them.
///
/// In processors enabled with [Hyper-threading technology (HT)](https://en.wikipedia.org/wiki/Hyper-threading),
/// it is possible to run up to 2 processes at the same physical CPU core.
/// However, usually just the Host operating system scheduler (a [VmScheduler] assigned to a Host)
/// has direct knowledge of HT to accordingly schedule up to 2 processes to the same physical CPU core.
/// Further, this scheduler implementation
/// oversimplifies a possible HT for the virtual PEs, allowing more than 2 processes to run at the same core.
///
/// Since this CloudletScheduler implementation does not account for the
/// [context switch](https://en.wikipedia.org/wiki/Context_switch)
/// overhead, this oversimplification impacts tasks completion by penalizing
/// equally all the Cloudlets that are running on the same CPU core.
/// Other impact is that, if there are
/// Cloudlets of the same length running in the same PEs, they will finish
/// exactly at the same time. On the other hand, on a real time-shared scheduler
/// these Cloudlets will finish almost at the same time.
///
///
/// As an example, consider a scheduler that has 1 PE that is able to execute
/// 1000 MI/S (MIPS) and is running Cloudlet 0 and Cloudlet 1, each one having
/// 5000 MI in length. These 2 Cloudlets will spend 5 seconds to finish. Now
/// consider that the time slice allocated to each Cloudlet to execute is 1
/// second. As at every 1 second a different Cloudlet is allowed to run, the
/// execution path will be as follows:
///
/// - Time (second): 00 01 02 03 04 05
/// - Cloudlet (id): C0 C1 C0 C1 C0 C1
///
/// As one can see, in a real time-shared scheduler that does not define priorities
/// for applications, the 2 Cloudlets will in fact finish at different times. In
/// this example, one Cloudlet will finish 1 second after the other.
///
/// **WARNING**: Time-shared schedulers drastically degrade the performance of large scale simulations.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
/// @see CloudletSchedulerCompletelyFair
/// @see CloudletSchedulerSpaceShared
public class CloudletSchedulerTimeShared extends CloudletSchedulerAbstract {
    @Serial
    private static final long serialVersionUID = 2115862129708036038L;

    /**
     * {@inheritDoc}
     *
     * <p>
     * <b>For this scheduler, this list is always empty, once the VM PEs
     * are shared across all Cloudlets running inside a VM. Each Cloudlet has
     * the opportunity to use the PEs for a given time-slice.</b></p>
     *
     * @return {@inheritDoc}
     */
    @Override
    public List<CloudletExecution> getCloudletWaitingList() {
        //The method was overridden here just to extend its JavaDoc.
        return super.getCloudletWaitingList();
    }

    /**
     * Moves a Cloudlet that was paused and has just been resumed to the
     * Cloudlet execution list.
     *
     * @param cloudlet the Cloudlet to move from the paused to the exec lit
     * @return the Cloudlet expected finish time
     */
    private double movePausedCloudletToExecListAndGetExpectedFinishTime(final CloudletExecution cloudlet) {
        getCloudletPausedList().remove(cloudlet);
        addCloudletToExecList(cloudlet);
        return cloudletEstimatedFinishTime(cloudlet, getVm().getSimulation().clock());
    }

    @Override
    public double cloudletResume(final Cloudlet cloudlet) {
        return findCloudletInList(cloudlet, getCloudletPausedList())
                .map(this::movePausedCloudletToExecListAndGetExpectedFinishTime)
                .orElse(0.0);
    }

    /**
     * This time-shared scheduler shares the CPU time between all executing
     * cloudlets, giving the same CPU time-slice for each Cloudlet to execute.
     * It always allows any submitted Cloudlets to be immediately added to the
     * execution list. By this way, it doesn't matter what Cloudlet is being
     * submitted, since it will always include it in the execution list.
     *
     * @param cloudlet the Cloudlet that will be added to the execution list.
     * @return always <b>true</b> to indicate that any submitted Cloudlet can be
     * immediately added to the execution list
     */
    @Override
    protected boolean canExecuteCloudletInternal(final CloudletExecution cloudlet) {
        return true;
    }
}
