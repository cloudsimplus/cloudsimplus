/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.util.MathUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * A simplified implementation of the <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 * that is the default scheduler used for most tasks on recent Linux Kernel. It is a time-shared
 * scheduler that shares CPU cores between running applications by preempting
 * them after a time period (timeslice) to allow other ones to start executing
 * during their timeslices.
 *
 * <p><b>This scheduler supposes that Cloudlets priorities are in the range from [-20 to 19],
 * as used in <a href="http://man7.org/linux/man-pages/man1/nice.1.html">Linux Kernel</a></b>. Despite setting
 * Cloudlets priorities with values outside this interval will work as well, one has to
 * realize that lower priorities are defined by negative values.
 * </p>
 *
 * <p>
 * It is a basic implementation that covers the following features:
 * <ul>
 *     <li>Defines a general runqueue (the waiting list which defines which Cloudlets to run next) for all CPU cores ({@link Pe}) instead
 *     of one for each core. More details in the listing below.</li>
 *     <li>Computes process ({@link Cloudlet}) niceness based on its priority: {@code niceness = -priority}.
 *     The nice value (niceness) defines how nice a process is to the other ones.
 *     Lower niceness (negative values) represents higher priority and consequently higher weight, while
 *     higher niceness (positive values) represent lower priority and lower weight.</li>
 *     <li>Computes process timeslice based on its weight, that in turn is computed based on its niceness.
 *     The timeslice is the amount of time that a process is allowed to use the CPU before be preempted to make
 *     room for other process to run.
 *     The CFS scheduler uses a dynamic defined timeslice.</li>
 * </ul>
 *
 * And it currently <b>DOES NOT</b> implement the following features:
 * <ul>
 *     <li>Additional overhead for CPU context switch: the context switch
 *     is the process of removing an application that is using a CPU core
 *     to allow another one to start executing. This is the task preemption
 *     process that allows a core to be shared between several applications.
 *
 *     <li>Since this scheduler does not consider
 *     <a href="https://en.wikipedia.org/wiki/Context_switch">context switch</a>
 *     overhead, there is only one runqueue (waiting list) for all CPU cores because
 *     each application is not in fact assigned to a specific CPU core.
 *     The scheduler just computes how much computing power (in MIPS)
 *     and number of cores each application can use and that MIPS capacity
 *     is multiplied by the number of cores the application requires.
 *     Such an approach then enables the application to execute that number of instructions
 *     per second. Once the {@link Pe PEs} do not in fact run the application,
 *     (application execution is simulated just computing the amount of instructions
 *     that can be run), it doesn't matter which PEs are "running" the application.
 *     </li>
 *	   </li>
 *	   <li>It doesn't use a Red-Black tree (such as the TreeSet), as in real implementations of CFS,
 *	   to sort waiting Cloudlets (runqueue list) increasingly, based on their virtual runtime (vruntime or VRT)
 *	   (placing the Cloudlets that have run the least at the top of the tree).
 *	   Furthermore, the use of such a data structure added some complexity to the implementation.
 *	   Since different Cloudlets may have the same virtual runtime, this introduced some issues when adding or
 *	   removing elements in a structure such as the TreeSet, that requires
 *	   each value (the virtual runtime in this case) used to sort the Set to be unique.</li>
 * </ul>
 * </p>
 *
 * <p>
 *     <b>NOTES:</b>
 *     <ul>
 *         <li>The time interval for updating cloudlets execution in this scheduler is not primarily defined by the
 *         {@link Datacenter#getSchedulingInterval()}, but by the {@link #computeCloudletTimeSlice(CloudletExecution) timeslice}
 *         computed based on the defined {@link #getLatency()}. Each time the computed timeslice is greater than
 *         the Datacenter scheduling interval, then the next update of Cloudlets processing will follow the {@link Datacenter#getSchedulingInterval()}.</li>
 *         <li>The implementation was based on the book of Robert Love: Linux Kernel Development, 3rd ed. Addison-Wesley, 2010
 *             and some other references listed below.</li>
 *     </ul>
 * </p>
 *
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see <a href="http://www.ibm.com/developerworks/library/l-completely-fair-scheduler/">Inside the Linux 2.6 Completely Fair Scheduler</a>
 * @see <a href="http://www.ibm.com/developerworks/library/l-lpic1-103-6/index.html">Learn Linux, 101: Process execution priorities</a>
 * @see <a href="https://doi.org/10.1145/1400097.1400102">Towards achieving fairness in the Linux scheduler</a>
 * @see <a href="https://doi.org/10.1145/10.1145/2901318.2901326">The Linux scheduler</a>
 * @see <a href="https://www.kernel.org/doc/Documentation/scheduler/sched-design-CFS.txt">kernel.org: CFS Scheduler Design</a>
 * @see <a href="https://oakbytes.wordpress.com/linux-scheduler/">Linux Scheduler FAQ</a>
 */
public final class CloudletSchedulerCompletelyFair extends CloudletSchedulerTimeShared {
	/**
	 * @see #getMinimumGranularity()
	 */
	private int minimumGranularity = 2;

	/**
	 * @see #getLatency()
	 */
	private int latency = 3;

    /**
     * A comparator used to ascendingly sort Cloudlets into the waiting list
     * based on their virtual runtime (vruntime or VRT). By this way, the Cloudlets in the beginning
     * of such a list will be that ones which have run the least and have to be
     * prioritized when getting Cloudlets from this list to add to the execution
     * list.
     *
     * @param c1 first Cloudlet to compare
     * @param c2 second Cloudlet to compare
     * @return a negative value if c1 is lower than c2, zero if they are equals,
     * a positive value if c1 is greater than c2
     */
    private int waitingCloudletsComparator(final CloudletExecution c1, final CloudletExecution c2){
        final double vRuntimeDiff = c1.getVirtualRuntime() - c2.getVirtualRuntime();
        if (vRuntimeDiff != 0) {
            return MathUtil.doubleToInt(vRuntimeDiff);
        }

        final long priorityDiff = c1.getCloudlet().getPriority() - c2.getCloudlet().getPriority();
        final long idDiff = c1.getCloudletId() - c2.getCloudletId();
        //Since the computed value is long but the comparator return must be int, rounds the value to the closest int
        return Math.round(priorityDiff == 0 ? idDiff : priorityDiff);
    }

    /**
	 * Gets the latency, which is the amount of time (in seconds)
	 * the scheduler will allow the execution of running Cloudlets
	 * in the available PEs, before checking which are the next
	 * Cloudlets to execute. The latency time is divided by the number of
	 * the number of Cloudlets that can be executed at the current time.
	 * If there are 4 Cloudlets by just 2 PEs, the latency is divided
	 * by 2, because only 2 Cloudlets can be concurrently executed
	 * at the moment. However, the minimum amount of time allocated to each
	 * Cloudlet is defined by the {@link #getMinimumGranularity()}.
	 *
	 * <p>As lower is the latency, more responsive a real operating
	 * system will be perceived by users, at the cost or more
	 * frequent CPU context Datacenter (that reduces CPU throughput).
	 * <b>However, CPU context switch overhead is not being considered.</b>
	 * </p>
     *
     * NOTE: The default value for linux scheduler is 0.02s.
	 * @return
	 */
	public int getLatency() {
		return latency;
	}

	/**
	 * Sets the latency time (in seconds).
	 * @param latency the latency to set
     * @throws IllegalArgumentException when latency is lower than minimum granularity
     * @see #getLatency()
	 */
	public void setLatency(final int latency) {
		if(latency < minimumGranularity){
            throw new IllegalArgumentException("Latency cannot be lower than the mininum granularity.");
        }

        this.latency = latency;
	}

	/**
	 * Computes the timeslice for a Cloudlet, which is the amount
	 * of time (in seconds) that it will have to use the PEs,
	 * considering all Cloudlets in the {@link #getCloudletExecList() executing list}.
	 *
	 * <p>The timeslice is computed considering the {@link #getCloudletWeight(CloudletExecution) Cloudlet weight}
	 * and what it represents in percentage of the {@link #getWeightSumOfRunningCloudlets() weight sum} of
	 * all cloudlets in the execution list.</p>
	 *
	 * @param cloudlet Cloudlet to get the timeslice
	 * @return Cloudlet timeslice (in seconds)
	 *
	 * @see #getCloudletWeight(CloudletExecution)
	 * @see #getWeightSumOfRunningCloudlets()
	 */
	protected double computeCloudletTimeSlice(final CloudletExecution cloudlet){
		final double timeslice = getLatency() * getCloudletWeightPercentBetweenAllCloudlets(cloudlet);
		return Math.min(timeslice, getMinimumGranularity());
	}

    /**
     * Gets a <b>read-only</b> list of Cloudlets which are waiting to run, the so called
     * <a href="https://en.wikipedia.org/wiki/Run_queue">run queue</a>.
     *
     * <p>
     * <b>NOTE:</b> Different from real implementations, this scheduler uses just one run queue
     * for all processor cores (PEs). Since CPU context switch is not concerned,
     * there is no point in using different run queues.
     * </p>
     *
     * @return
     */
    @Override
    public List<CloudletExecution> getCloudletWaitingList() {
        return super.getCloudletWaitingList();
    }

    /**
     * {@inheritDoc}
     * The cloudlet waiting list (runqueue) is sorted according to the virtual runtime (vruntime or VRT),
     * which indicates the amount of time the Cloudlet has run.
     * This runtime increases as the Cloudlet executes.
     *
     * @return {@inheritDoc}
     */
    @Override
    protected Optional<CloudletExecution> findSuitableWaitingCloudlet() {
        sortCloudletWaitingList(this::waitingCloudletsComparator);
        return super.findSuitableWaitingCloudlet();
    }

	/**
	 * Gets the weight of the Cloudlet to use the CPU, that is
	 * defined based on its niceness. As greater is the weight,
	 * more time the Cloudlet will have to use the PEs.
	 *
     * <p>As the {@link #computeCloudletTimeSlice(CloudletExecution) timelice} assigned to a Cloudlet to use the CPU is defined
     * exponentially instead of linearly according to its niceness,
     * this method is used as the base to correctly compute the timeslice.
     * </p>
	 * <p><b>NOTICE</b>: The formula used is based on the book referenced at the class documentation.</p>
	 *
	 * @param cloudlet Cloudlet to get the weight to use PEs
	 * @return the cloudlet weight to use PEs
     * @see #getCloudletNiceness(CloudletExecution)
	 */
	protected double getCloudletWeight(final CloudletExecution cloudlet){
		return 1024.0/(Math.pow(1.25, getCloudletNiceness(cloudlet)));
	}

    /**
     * Gets the nice value from a Cloudlet based on its priority.
     * The nice value is the opposite of the priority.
     *
     * <p>As "niceness" is a terminology defined by specific schedulers
     * (such as Linux Schedulers), it is not defined inside the Cloudlet.</p>
     *
     * @param cloudlet Cloudlet to get the nice value
     * @return the cloudlet niceness
     * @see <a href="http://man7.org/linux/man-pages/man1/nice.1.html">Man Pages: Nice values for Linux processes</a>
     */
    protected double getCloudletNiceness(final CloudletExecution cloudlet){
        return -cloudlet.getCloudlet().getPriority();
    }

    /**
	 * Gets the percentage (in scale from [0 to 1]) that the weight of a Cloudlet
	 * represents, compared to the weight sum of all Cloudlets in the execution list.
	 *
	 * @param cloudlet Cloudlet to get its weight percentage
	 * @return the cloudlet weight percentage between all Cloudlets in the execution list
	 */
	private double getCloudletWeightPercentBetweenAllCloudlets(final CloudletExecution cloudlet) {
		return getCloudletWeight(cloudlet) / getWeightSumOfRunningCloudlets();
	}

	/**
	 * Gets the weight sum of all cloudlets in the executing list.
	 */
	private double getWeightSumOfRunningCloudlets() {
		return getCloudletExecList()
            .stream()
            .mapToDouble(this::getCloudletWeight)
            .sum();
	}

	/**
	 * Gets the minimum granularity that is the minimum amount of
	 * time (in seconds) that is assigned to each
	 * Cloudlet to execute.
	 *
	 * <p>This minimum value is used to reduce the frequency
	 * of CPU context Datacenter, that degrade CPU throughput.
	 * <b>However, CPU context switch overhead is not being considered.</b>
	 * By this way, it just ensures that each Cloudlet will not use the CPU
	 * for less than the minimum granularity.</p>
     *
     * <p>The default value for linux scheduler is 0.001s</p>
	 *
	 * @return
	 * @see #getLatency()
	 */
	public int getMinimumGranularity() {
		return minimumGranularity;
	}

	/**
	 * Sets the minimum granularity that is the minimum amount of
	 * time (in seconds) that is assigned to each
	 * Cloudlet to execute.
	 *
	 * @param minimumGranularity the minimum granularity to set
     * @throws IllegalArgumentException when minimum granularity is greater than latency
	 */
	public void setMinimumGranularity(final int minimumGranularity) {
        if(minimumGranularity > latency){
            throw new IllegalArgumentException("Minimum granularity cannot be greater than latency.");
        }
		this.minimumGranularity = minimumGranularity;
	}

    /**
     * {@inheritDoc}
     *
     * <p>It also sets the initial virtual runtime for the given Cloudlet
     * in order to define how long the Cloudlet has executed yet.<br>
     *
     * See {@link #computeCloudletInitialVirtualRuntime(CloudletExecution)}
     * for more details.</p>
     *
     * @param cle {@inheritDoc}
     * @param fileTransferTime {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected double cloudletSubmitInternal(final CloudletExecution cle, final double fileTransferTime) {
        cle.setVirtualRuntime(computeCloudletInitialVirtualRuntime(cle));
        cle.setTimeSlice(computeCloudletTimeSlice(cle));
        return super.cloudletSubmitInternal(cle, fileTransferTime);
    }

    /**
     * {@inheritDoc}
     * @param currentTime {@inheritDoc}
     * @param mipsShare {@inheritDoc}
     * @return the shorter timeslice assigned to the running cloudlets (which defines
     * the time of the next expiring Cloudlet, enabling the preemption process), or Double.MAX_VALUE if there is no next events
     */
    @Override
    public double updateProcessing(final double currentTime, final List<Double> mipsShare) {
        super.updateProcessing(currentTime, mipsShare);
        return getCloudletExecList().stream()
                .mapToDouble(CloudletExecution::getTimeSlice)
                .min().orElse(Double.MAX_VALUE);
    }

    @Override
    public long updateCloudletProcessing(final CloudletExecution cle, final double currentTime) {
        /*
        Cloudlet has never been executed yet and it will start executing now,
        sets its actual virtual runtime. The negative value was used so far
        just to sort Cloudlets in the waiting list according to their priorities.
        */
        if(cle.getVirtualRuntime() < 0){
            cle.setVirtualRuntime(0);
        }

        final double cloudletTimeSpan = currentTime - cle.getLastProcessingTime();
        final long partialFinishedMI = super.updateCloudletProcessing(cle, currentTime);

        cle.addVirtualRuntime(cloudletTimeSpan);
        return partialFinishedMI;
    }

    /**
     * Computes the initial virtual runtime for a Cloudlet that will be added to the execution list.
     * This virtual runtime is updated as long as the Cloudlet is executed.
     * The initial value is negative to indicate the Cloudlet hasn't started
     * executing yet. The virtual runtime is computed based on the Cloudlet priority.
     *
     * @param cloudlet Cloudlet to compute the initial virtual runtime
     * @return the computed initial virtual runtime as a negative value
     * to indicate that the Cloudlet hasn't started executing yet
     */
    private double computeCloudletInitialVirtualRuntime(final CloudletExecution cloudlet) {
        /*
        A negative virtual runtime indicates the cloudlet has never been executed yet.
        This math was used just to ensure that the first added cloudlets
        will have the lower vruntime, depending of their priorities.
        If all cloudlets have the same priority, the first
        added will start executing first.
        */

        /*
        Inverses the Cloudlet ID dividing the Integer.MAX_VALUE by it,
        because the ID is in fact int. This will make that the lower
        ID return higher values. It ensures that as lower is the ID,
        lower is the negative value returned.
        Inverting the cloudlet ID to get a higher value for lower IDs
        can be understood as resulting in "higher negative" values, that is,
        extreme negative values.
        */
        final double inverseOfCloudletId = Integer.MAX_VALUE/(cloudlet.getCloudletId()+1.0);

        return -Math.abs(cloudlet.getCloudlet().getPriority() + inverseOfCloudletId);
    }

    /**
     * Checks if a Cloudlet can be submitted to the execution list.
     *
     * This scheduler, different from its time-shared parent, only adds
     * submitted Cloudlets to the execution list if there is enough free PEs.
     * Otherwise, such Cloudlets are added to the waiting list,
     * really enabling time-sharing between running Cloudlets.
     * By this way, some Cloudlets have to be preempted to allow other ones
     * to be executed.
     *
     * @param cloudlet {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean canExecuteCloudletInternal(final CloudletExecution cloudlet) {
        return isThereEnoughFreePesForCloudlet(cloudlet);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Prior to start executing, a Cloudlet is added to this list.
     * When the Cloudlet vruntime reaches its timeslice (the amount of time
     * it can use the CPU), it is removed from this list and added
     * back to the {@link #getCloudletWaitingList()}.</p>
     *
     * <p>The sum of the PEs of Cloudlets into this list cannot exceeds
     * the number of PEs available for the scheduler. If the sum of PEs of such Cloudlets
     * is less than the number of existing PEs, there are
     * idle PEs. Since the CPU context switch overhead is not regarded
     * in this implementation and as result, it doesn't matter which
     * PEs are running which Cloudlets, there is not such information
     * in anywhere. As an example, if the first Cloudlet requires 2 PEs,
     * then one can say that it is using the first 2 PEs.
     * But if at the next simulation time the same Cloudlet can be
     * at the 3º position in this Collection, indicating that now it is using
     * the 3º and 4º Pe, which doesn't change anything. In real schedulers,
     * usually a process is pinned to a specific set of cores until it
     * finishes executing, to avoid the overhead of changing processes from
     * a run queue to another unnecessarily.</p>
     *
     * @return
     */
    @Override
    public List<CloudletExecution> getCloudletExecList() {
        //The method was overridden here just to extend its JavaDoc.
        return super.getCloudletExecList();
    }

    /**
     * Checks which Cloudlets in the execution list have the virtual runtime
     * equals to their allocated time slice and preempt them, getting
     * the most priority Cloudlets in the waiting list (i.e., those ones
     * in the beginning of the list).
     *
     * @see #preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList()
     */
    @Override
    protected void moveNextCloudletsFromWaitingToExecList() {
        final List<CloudletExecution> preemptedCloudlets = preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList();
        super.moveNextCloudletsFromWaitingToExecList();

        /*After preempted Cloudlets are moved to the waiting list
        and next Cloudlets on the beginning of this list are moved
        to the execution list, the virtual runtime of these preempted Cloudlets
        is reset so that they can compete with other waiting Cloudlets to use
        the processor again.*/
        for(final CloudletExecution c: preemptedCloudlets) {
            c.setVirtualRuntime(computeCloudletInitialVirtualRuntime(c));
        }
    }

    /**
     * Checks which Cloudlets in the execution list have an expired virtual
     * runtime (that have reached the execution time slice) and
     * preempts its execution, moving them to the waiting list.
     *
     * @return The list of preempted Cloudlets, that were removed from the execution list
     * and must have their virtual runtime (VRT) reset after the next cloudlets are put into
     * the execution list.
     *
     */
    private List<CloudletExecution> preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList() {
        final Predicate<CloudletExecution> vrtReachedTimeSlice = cle -> cle.getVirtualRuntime() >= cle.getTimeSlice();
        final List<CloudletExecution> expiredVrtCloudlets =
            getCloudletExecList()
                .stream()
                .filter(vrtReachedTimeSlice)
                .collect(toList());

        expiredVrtCloudlets.forEach(cle -> addCloudletToWaitingList(removeCloudletFromExecList(cle)));
        return expiredVrtCloudlets;
    }

}

