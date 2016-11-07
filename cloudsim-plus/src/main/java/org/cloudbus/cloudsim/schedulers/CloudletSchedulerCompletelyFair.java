package org.cloudbus.cloudsim.schedulers;

import java.util.Collection;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.Pe;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import org.cloudbus.cloudsim.Log;

/**
 * A <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 * that is the default scheduler used for most tasks on recent Linux Kernel. It is a time-shared
 * scheduler that shares CPU cores between running applications by preempting
 * them after a time period (timeslice) to allow other ones to start executing
 * during their timeslices.
 *
 * <p>
 * It is a basic implementation that covers that covers the following features:
 * <ul>
 *     <li>Defines a general runqueue for all CPU cores ({@link Pe}) instead
 *     of one for each core. More details in the listing below.</li>
 *     <li>Computes process ({@link Cloudlet}) niceness based on its priority: {@code niceness = -priority}.
 *     The nice value (niceness) defines how nice a process is to the other ones.
 *     Lower niceness (negative values) represents higher priority and consequently higher weight.</li>
 *     <li>Computes process timeslice based on its weight, that in turn is computed based on its niceness.
 *     The timeslice is the amount of time that a process is allowed to use the CPU.
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
 *     <p>Since this scheduler does not consider
 *     <a href="https://en.wikipedia.org/wiki/Context_switch">context switch</a>
 *     overhead, there is only one runqueue for all CPU cores because
 *     each application is not in fact assigned to a specific CPU core.
 *     The scheduler just computes how much computing power (in MIPS)
 *     and number of cores each application can use and that MIPS capacity
 *     is multiplied by the number of cores the application requires.
 *     This approach then enable the application to execute that number of instructions
 *     per second. Once the {@link Pe PEs} do not in fact run the application,
 *     (application running is simulated just computing the amount of instructions
 *     that can be executed), it doesn't matter which PEs are "running" the application.
 *     </p>
 *	   </li>
 * </ul>
 *
 * The implementation was based on the book of Robert Love: Linux Kernel Development, 3rd ed. Addison-Wesley, 2010
 * and some other references listed below.
 * </p>
 *
     * <p>
     * O scheduler é baseado no time-shared mas deve funcionar de maneira diferente.
     * <ul>
     * <li>
     *   <strike>
     *   A getCloudletExecutionList deve representar
     *   apenas as cloudlets que estão executando de fato no momento atual.
     *   A diferença é que no construtor, tal lista deve ser instanciada como um
     *   {@link Set} para manter a ordem dos elementos de acordo com o vruntime.
     * </strike>
     * <li>
     *
     * <li>
     *   <strike>Adicionar vruntime ao CloudletExecutionInfo para permitir
     *   ordenar a execution list por ele. Isso vai facilitar remover uma cloudlet
     *   desta lista, considerando aquela que tiver o maior vruntime (que já rodou mais
     *   que as outras).
     * </strike>
     * </li>
     *
     * <li><strike>A lista pode ser ordenada de forma descrescente para
     * permitir usar stream pra pegar o primeiro elemento.</strike></li>
     *
     * <li><strike>A waiting list é que será de fato a runqueue, contendo
     * a lista de cloudlets que devem rodar em seguida (conforme definição da wikipedia).</strike></li>
     *
     * <li>Ela, diferente do CloudletSchedulerTimeShared,
     * deve sim ter cloudlets. O CFS deve de fato implementar a preempção,
     * removendo processos na execution list para dar a vez
     * (mesmo que tais processos não tenhma terminado) para outros processos
     * na runqueue (waiting list).</li>
     *
     * <li><strike>A waiting list sim é que deve ser implementada como uma Red-Black tree.</strike></li>
     * </ul>
     * </p>
 *
 *
 * @author Manoel Campos da Silva Filho
 *
 * @see <a href="http://www.ibm.com/developerworks/library/l-completely-fair-scheduler/">Inside the Linux 2.6 Completely Fair Scheduler</a>
 * @see <a href="http://www.ibm.com/developerworks/library/l-lpic1-103-6/index.html">Learn Linux, 101: Process execution priorities</a>
 * @see <a href="http://dx.doi.org/10.1145/1400097.1400102">Towards achieving fairness in the Linux scheduler</a>
 * @see <a href="http://dx.doi.org/10.1145/10.1145/2901318.2901326">The Linux scheduler</a>
 * @see <a href="https://www.kernel.org/doc/Documentation/scheduler/sched-design-CFS.txt">kernel.org: CFS Design</a>
 */
public final class CloudletSchedulerCompletelyFair extends CloudletSchedulerTimeShared {
	/**
	 * @see #getMininumGranularity()
	 */
	private int mininumGranularity = 2; //The default value for linux schedueler is 0.001 s

	/**
	 * @see #getLatency()
	 */
	private int latency = 3; //The default value for linux schedueler is 0.02 s

	public CloudletSchedulerCompletelyFair(){
		super();
		setCloudletExecList(new TreeSet<>(this::executingCloudletsComparator));
        setCloudletWaitingList(new TreeSet<>(this::waitingCloudletsComparator));
	}

    /**
     * A comparator used to ascendingly sort Cloudlets into the waiting list
     * based on their virtual runtime. By this way, the Cloudlets in the beginning
     * of this list will be that ones which have run the least and have to be
     * prioritized when getting Cloudlets from this list to add to the execution
     * list.
     *
     * @param c1 first Cloudlet to compare
     * @param c2 second Cloudlet to compare
     * @return a negative value if c1 is lower than c2, zero if they are equals,
     * a positive value if c1 is greater than c2
     */
    private int waitingCloudletsComparator(CloudletExecutionInfo c1, CloudletExecutionInfo c2){
        if(c1.equals(c2))
            return 0;

        double diff = c1.getVirtualRuntime() - c2.getVirtualRuntime();

        /*If the difference between the virtual runtime is equal to zero, the comparator considers
        * that the objects are the same and this causes issues when adding or removing
        * elements into a TreeSet that accepts just unique objects.
        * By this way, if the diff is zero, uses the priorities difference.
        * If the diff is yet zero, uses the IDs difference.*/
        if(diff == 0)
            diff = c1.getCloudlet().getPriority()-c2.getCloudlet().getPriority();
        if(diff == 0)
            diff = c1.getCloudletId()-c2.getCloudletId();

        return (int)diff;
    }

    /**
     * A comparator used to descendingly sort running Cloudlets into the execution list
     * based on their virtual runtime. By this way, the Cloudlets in the beginning
     * of this list will be that ones which have run the most and thus, are the
     * first to be preempted to allow Cloudlets in the waiting list to run.
     *
     * @param c1 first Cloudlet to compare
     * @param c2 second Cloudlet to compare
     * @return a negative value if c2 is lower than c1, zero if they are equals,
     * a positive value if c2 is greater than c1
     */
    private int executingCloudletsComparator(CloudletExecutionInfo c1, CloudletExecutionInfo c2){
        /*Calling the other comparator just inverting the parameters order will change the sorting
        order.*/
        return waitingCloudletsComparator(c2, c1);
    }

	/**
	 * Gets the latency, that is the amount of time (in seconds)
	 * the scheduler will allow the execution of waiting Cloudlets
	 * in the available PEs, before checking which are the next
	 * Cloudlets to execute. The latency time is divided by the number of
	 * the number of Cloudlets that can be executed at the current time.
	 * If there are 4 Cloudlets by just 2 PEs, the latency is divided
	 * by 2, because only 2 Cloudlets can be concurrently executed
	 * at the moment. However, the minimum amount of time allocated to each
	 * Cloudlet is defined by the {@link #getMininumGranularity()}.
	 *
	 * <p>As lower is the latency, more responsive a real operating
	 * system will be perceived by users, at the cost or more
	 * frequent CPU context switches (that reduces CPU throughput).
	 * <b>However, CPU context switch overhead is not being considered.</b>
	 * </p>
	 * @return
	 */
	public int getLatency() {
		return latency;
	}

	/**
	 * Sets the latency time (in seconds)
	 * @param latency the latency to set
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}

	/**
	 * Computes the timeslice for a Cloudlet, that is, the amount
	 * of time (in seconds) that such a Cloudlet will have to use the PEs,
	 * considering all Cloudlets in the {@link #getCloudletExecList() executing list}.
	 *
	 * <p>The timeslice is computed considering the {@link #getCloudletWeight(CloudletExecutionInfo) Cloudlet weight}
	 * and what it represents in percentage of the {@link #getWeightSumOfRunningCloudlets() weight sum} of
	 * all cloudlets in the execution list.</p>
	 *
	 * @param cloudlet Cloudlet to get the timeslice
	 * @return Cloudlet timeslice (in seconds)
	 *
	 * @see #getCloudletWeight(CloudletExecutionInfo)
	 * @see #getWeightSumOfRunningCloudlets()
	 */
	protected double computeCloudletTimeSlice(CloudletExecutionInfo cloudlet){
		final double timeslice = getLatency() * getCloudletWeightPercentBetweenAllCloudlets(cloudlet);
		return Math.min(timeslice, getMininumGranularity());
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
	 */
	protected double getCloudletNiceness(CloudletExecutionInfo cloudlet){
		return -cloudlet.getCloudlet().getPriority();
	}

	/**
	 * Gets the weight of the Cloudlet to use the CPU, that is
	 * defined based on its niceness. As greater is the weight,
	 * more time the Cloudlet will have to use the PEs.
	 *
     * <p>As the {@link #computeCloudletTimeSlice(CloudletExecutionInfo) timelice} assigned to a Cloudlet to use the CPU is defined
     * exponentially instead of linearly according to its niceness,
     * this method is used as the base to correctly compute the timeslice.
     * </p>
	 * <p><b>NOTICE</b>: The formula used is based on the book referenced at the class documentation.</p>
	 *
	 * @param cloudlet Cloudlet to get the weight to use PEs
	 * @return the cloudlet weight to use PEs
     * @see #getCloudletNiceness(CloudletExecutionInfo)
	 */
	protected double getCloudletWeight(CloudletExecutionInfo cloudlet){
		return 1024.0/(Math.pow(1.25, getCloudletNiceness(cloudlet)));
	}

	/**
	 * Gets the percentage (in scale from [0 to 1]) that the weight of a Cloudlet
	 * represents compared to the weight sum of all Cloudlets in the execution list.
	 *
	 * @param cloudlet Cloudlet to get its weight percentage
	 * @return the cloudlet weight percentage between all Cloudlets in the execution list
	 */
	private double getCloudletWeightPercentBetweenAllCloudlets(CloudletExecutionInfo cloudlet) {
		return getCloudletWeight(cloudlet) / getWeightSumOfRunningCloudlets();
	}

	/**
	 * Gets the weight sum of all cloudlets in the executing list.
	 */
	private double getWeightSumOfRunningCloudlets() {
		return getCloudletExecList().stream()
            .mapToDouble(this::getCloudletWeight)
            .sum();
	}

	/**
	 * Gets the minimum granularity that is the minimum amount of
	 * time (in seconds) that is assigned to each
	 * Cloudlet to execute.
	 *
	 * <p>This minimum value is used to reduce the frequency
	 * of CPU context switches, that degrade CPU throughput.
	 * <b>However, CPU context switch overhead is not being considered.</b>
	 * By this way, it just ensures that each Cloudlet will not use the CPU
	 * for less than the minimum granularity.</p>
	 *
	 * @return
	 * @see #getLatency()
	 */
	public int getMininumGranularity() {
		return mininumGranularity;
	}

	/**
	 * Sets the minimum granularity that is the minimum amount of
	 * time (in seconds) that is assigned to each
	 * Cloudlet to execute.
	 *
	 * @param mininumGranularity the minimum granularity to set
	 */
	public void setMininumGranularity(int mininumGranularity) {
		this.mininumGranularity = mininumGranularity;
	}

    /**
     * {@inheritDoc}
     *
     * <p>It also sets the initial virtual runtime for the given Cloudlet
     * in order to define how long the Cloudlet has executed yet.<br>
     *
     * See {@link #computeCloudletInitialVirtualRuntime(org.cloudbus.cloudsim.CloudletExecutionInfo)}
     * for more details.</p>
     *
     * @param rcl {@inheritDoc}
     * @param fileTransferTime {@inheritDoc}
     */
    @Override
    public double processCloudletSubmit(CloudletExecutionInfo rcl, double fileTransferTime) {
        rcl.setVirtualRuntime(computeCloudletInitialVirtualRuntime(rcl));
        rcl.setTimeSlice(computeCloudletTimeSlice(rcl));
        if(rcl.getCloudletId() == 5) {
            System.out.println("\tCloudlets submitted:");
            Stream.concat(getCloudletExecList().stream(), getCloudletWaitingList().stream())
                .forEach(c-> System.out.printf("\t\t%d vruntime: %16.2f timeslice: %.2f\n",
                    c.getCloudletId(), c.getVirtualRuntime(), c.getTimeSlice()));

        }
        double result = super.processCloudletSubmit(rcl, fileTransferTime);
        return result;
    }

    /**
     * {@inheritDoc}
     * @param currentTime {@inheritDoc}
     * @param mipsShare {@inheritDoc}
     * @return the shorter timeslice assigned to the running cloudlets, or Double.MAX_VALUE if there is no next events
     */
    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        super.updateVmProcessing(currentTime, mipsShare);
        double nextExpiringTimeSlice = getCloudletExecList().stream()
                .mapToDouble(CloudletExecutionInfo::getTimeSlice)
                .min().orElse(Double.MAX_VALUE);
        System.out.printf("\tTime %.2f updateVmProcessing - Next expiring timeslice: %.2f\n", currentTime, nextExpiringTimeSlice);
        return nextExpiringTimeSlice;
    }

    @Override
    public void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {
        /*
        Cloudlet has never been executed yet and it will start executing now,
        sets its actual virtual runtime. The negative value was used so far
        just to sort Cloudlets in the waiting list according to their priorities.
        */
        if(rcl.getVirtualRuntime() < 0){
            rcl.setVirtualRuntime(0);
        }

        double cloudletTimeSpan = currentTime - rcl.getLastProcessingTime();
        super.updateCloudletProcessing(rcl, currentTime);

        //System.out.printf("\tCloudlet %d time span: %f - curTime %f last %f vruntime %f\n", rcl.getCloudletId(), cloudletTimeSpan, currentTime, rcl.getLastProcessingTime(), rcl.getVirtualRuntime());
        rcl.addVirtualRuntime(cloudletTimeSpan);
    }

    /**
     * Computes the initial virtual runtime for a Cloudlet that will be added to the execution list.
     * This virtual runtime is updated as long as the Cloudlet is executed.
     * The initial value is negative to indicate the Cloudlet hasn't started
     * executing yet. The virtual runtime is computed based on the Cloudlet priority.
     *
     * @param rcl Cloudlet to compute the initial virtual runtime
     * @return the computed initial virtual runtime as a negative value
     * to indicate that the Cloudlet hasn't started executing yet
     */
    private double computeCloudletInitialVirtualRuntime(CloudletExecutionInfo rcl) {
        /*
        A negative virtual runtime indicates the cloudlet has never been executed yet.
        This math was used just to ensure that the first added cloudlets
        will have the lower vruntime, depending of their priorites.
        If all cloudlets have the same priority, the first
        addedd will start executing first.
        */

        /*
        Inverses the Cloudlet ID dividing the Integer.MAX_VALUE by it,
        because the ID is in fact int. This will make that the lower
        ID return higher values. It ensures that as lower is the ID,
        lower is the negative value returned.
        Inversing the cloudlet ID to get a higher value for lower IDs
        can be understood as resulting in "higher negative" values, that is,
        extreme negative values.
        */
        double inverseOfCloudletId = Integer.MAX_VALUE/(rcl.getCloudletId()+1.0);

        return -Math.abs(rcl.getCloudlet().getPriority() + inverseOfCloudletId);
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
    public boolean canAddCloudletToExecutionList(CloudletExecutionInfo cloudlet) {
        return isThereEnoughFreePesForCloudlet(cloudlet);
    }

    @Override
    protected boolean removeCloudletFromWaitingList(CloudletExecutionInfo cloudlet) {
        return super.removeCloudletFromWaitingList(cloudlet);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Prior to start executing, a Cloudlet is added to this Collection.
     * When the Cloudlet vruntime reaches its timeslice (the amount of time
     * it can use the CPU), it is removed from this Collection and added
     * back to the {@link #getCloudletWaitingList()}.</p>
     *
     * <p>The sum of the PEs of Cloudlets into this Collection cannot exceeds
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
    public Collection<CloudletExecutionInfo> getCloudletExecList() {
        return super.getCloudletExecList();
    }

    /**
     * Gets a <b>read-only</b> {@link TreeSet} (a implementation of a Red-Black Tree) that stores the list of Cloudlets
     * that are waiting to run, the so called
     * <a href="https://en.wikipedia.org/wiki/Run_queue">run queue</a>.
     * Each key in this map is the virtual runtime (vruntime),
     * which indicates the amount of time the Cloudlet has run.
     * This runtime increases as the Cloudlet executes, what makes
     * it to change its position inside the map.
     * Each value represents a Cloudlet
     * running into a group of PE (defined by the number of
     * PEs the Cloudlet requires).
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
    public Collection<CloudletExecutionInfo> getCloudletWaitingList() {
        return super.getCloudletWaitingList();
    }

    /**
     * Checks which Cloudlets in the execution list has the virtual runtime
     * equals to its allocated time slice and preempt them and gets
     * the most priority Cloudlets in the waiting list (that is those ones
     * in the beginning of the list).
     *
     * @see #preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList()
     */
    @Override
    protected void moveNextCloudletsFromWaitingToExecList() {
        List<CloudletExecutionInfo> preemptedCloudlets = preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList();
        super.moveNextCloudletsFromWaitingToExecList();

        /*After preempted Cloudets are moved to the waitingn list
        and next Cloudlets on the beginning of this list are moved
        to the execution list, the virtual runtime of these preempted Cloudlets
        is reseted so that they can compete with other waiting Cloudlets to use
        the processor again.*/
        for(CloudletExecutionInfo c: preemptedCloudlets) {
            c.setVirtualRuntime(computeCloudletInitialVirtualRuntime(c));
        }
    }

    /**
     * Checks which Cloudlets in the execution list have an expired virtual
     * runtime (that have reached the execution time slice) and move
     * preempt its execution, moving them to the waiting list.
     *
     * @return The list of preempted Cloudlets, that were removed from the execution list
     * and must have their virtual runtime reseted after the next cloudlets are put into
     * the execution list.
     *
     */
    private List<CloudletExecutionInfo> preemptExecCloudletsWithExpiredVRuntimeAndMoveToWaitingList() {
        Predicate<CloudletExecutionInfo> cloudletThatVirtualRuntimeHasReachedItsTimeSlice =
                c -> c.getVirtualRuntime() >= c.getTimeSlice();

        Consumer<CloudletExecutionInfo> printCloudlet =
            c->System.out.printf("\t\tid %d vruntime %.2f timeslice: %.2f\n",
                                 c.getCloudletId(), c.getVirtualRuntime(), c.getTimeSlice());

        List<CloudletExecutionInfo> expiredVRuntimeCloudlets = getCloudletExecList().stream()
                .filter(cloudletThatVirtualRuntimeHasReachedItsTimeSlice)
                .collect(toList());

        if(!getCloudletExecList().isEmpty()){
            System.out.printf("\tTime %.2f - Running cloudlets: \n", CloudSim.clock());
            getCloudletExecList().stream().forEach(printCloudlet);
            System.out.println();
        }

        if(!expiredVRuntimeCloudlets.isEmpty()){
            System.out.println("\tExpired cloudlets: ");
            expiredVRuntimeCloudlets.stream().forEach(printCloudlet);
            System.out.println();
        }

        for(CloudletExecutionInfo c: expiredVRuntimeCloudlets) {
            if(!removeCloudletFromExecList(c)){
                System.out.printf("Cloudlet %d was not removed from exec list\n", c.getCloudletId());
            }
            addCloudletToWaitingList(c);
        }

        return expiredVRuntimeCloudlets;
    }


}

