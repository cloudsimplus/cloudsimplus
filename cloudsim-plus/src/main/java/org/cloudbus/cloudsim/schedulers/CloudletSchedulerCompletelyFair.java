package org.cloudbus.cloudsim.schedulers;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.resources.Pe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

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
 * @author Manoel Campos da Silva Filho
 *
 * @see <a href="http://www.ibm.com/developerworks/library/l-completely-fair-scheduler/">Inside the Linux 2.6 Completely Fair Scheduler</a>
 * @see <a href="http://dx.doi.org/10.1145/1400097.1400102">Towards achieving fairness in the Linux scheduler</a>
 * @see <a href="http://dx.doi.org/10.1145/10.1145/2901318.2901326">The Linux scheduler</a>
 * @see <a href="http://www.ibm.com/developerworks/library/l-lpic1-103-6/index.html">Learn Linux, 101: Process execution priorities</a>
 * @see <a href="https://www.kernel.org/doc/Documentation/scheduler/sched-design-CFS.txt">kernel.org: CFS Design</a>
 */
public final class CloudletSchedulerCompletelyFair extends CloudletSchedulerTimeShared {
    /**
     * @todo O scheduler é baseado no time-shared mas deve funcionar de maneira diferente.
     * A getCloudletExecutionList deve representar
     * apenas as cloudlets que estão executando de fato no momento atual.
     * A diferença é que no construtor tal lista pode ser instanciada como um 
     * Set para manter a ordem dos elementos de acordo com o vruntime.
     * Como o CloudletExecutionInfo não tem um atributo vruntime, uma classe
     * interna e filha dela pode ser criada para incluir tal campo e permitir
     * ordenar o Set por ele. Isso vai facilitar remover uma cloudlet
     * desta lista, considerando aquela que tiver o maior vruntime (que já rodou mais 
     * que as outras). A lista pode ser ordenada de forma descrescente para
     * permitir usar stream pra pegar o primeiro elemento.
     * 
     * O getCloudletWaitingList() é que será de fato a runqueue, contendo
     * a lista de cloudlets que devem rodar em seguida (conforme definição da wikipedia).
     * Ela, diferente do CloudletSchedulerTimeShared,
     * deve sim ter cloudlets. O CFS deve de fato implementar a preempção,
     * removendo processos na execution list para dar a vez
     * (mesmo que tais processos não tenhma terminado) para outros processos
     * na runqueue (waiting list).
     * 
     * A waiting list sim é que deve ser implementada como uma Red-Black tree.
     */
    
	/**
     * A {@link TreeMap Red-Black Tree} that stores the list of Cloudlets
     * that are active for running, the so called 
     * <a href="https://en.wikipedia.org/wiki/Run_queue">run queue</a>. 
     * Each key in this map is the virtual runtime (vruntime),
     * which indicates the amount of time the Cloudlet has run.
     * This runtime increases as the Cloudlet executes, what makes
     * it changes its position inside the map.
     * 
     * <p><b>NOTES:</b> 
     * <ul>
     * <li>This list <b>does not</b> contain the Cloudlets that are in fact
     * running at the current time. They are just Cloudlets that are sharing
     * the PEs with other actually running cloudlets in the
     * {@link #runningCloudletByPe running Cloudlets map}.</li>
     * <li>Do not confuse 
     * the executing list with the {@link #getCloudletWaitingList() waiting list}.
     * The executing list stores the cloudlets that are able to run
     * in a preemptive way, sharing the CPU cores between them.
     * The waiting list is the list of Cloudlets that have to wait
     * all the Cloudlets in the executing list to finish completely
     * to then start executing, what just happens in 
     * space-shared schedulers. In this scheduler, the waiting list
     * is always empty.
     * </li>
     * </ul>
     * </p>
     *
     * @see #getCloudletExecList()
     * @see #runningCloudletByPe
     * @see #getCloudletWaitingList() 
	 */
	private final Map<Double, CloudletExecutionInfo> cloudletExecList;
    
    /**
     * The map of Cloudlets that are in fact using
     * the PEs at the current time, that are got from the {@link #cloudletExecList}.
     * As each Pe can in fact run just one Cloudlet by time, 
     * this maps stores the Cloudlets that are really running
     * in the moment. 
     * 
     * <p>
     * Each key in this map is the virtual runtime (vruntime), 
     * which indicates the amount of time the Cloudlet has run.
     * Each value represents a Cloudlet
     * running into a group of Pe (defined by the number of
     * Pes the Cloudlet requires). 
     * </p>
     * 
     * <p>
     * Before starting executing, a Cloudlet is removed from the
     * {@link #cloudletExecList run queue} and added to this map.
     * When the Cloudlet vruntime reaches its timeslice (the amount of time
     * it can use the CPU), it is remove from this mapand added
     * back to the run queue.
     * </p>
     * 
     * <p>The sum of the PEs of Cloudlets into this map cannot exceeds 
     * the number of PEs available for the scheduler. If the sum of Cloudlets PEs
     * in this map is less than the number of existing PEs, there are 
     * idle PEs. Since the CPU context switch overhead is not regarded
     * in this implementation and as result, it doesn't matter which
     * PEs are running which Cloudlets, there is not such information
     * in this map. As an example, if the first Cloudlet on it requires 2 PEs,
     * then one can say that it is using the first 2 PEs.
     * But if at the next simulation time the same Cloudlet is
     * in the 3º position in this map, indicating that now it is using
     * the 3º and 4º Pe, it doesn't change anything. In real schedulers,
     * usually a process is pinned to a specific set of cores until it
     * finishes executing, to avoid the overhead to change to process from 
     * a run queue to another unnecessarily.</p>
     * 
     */
    private final Map<Double, CloudletExecutionInfo> runningCloudletByPe;

	/**
	 * @see #getMininumGranularity()
	 */
	private int mininumGranularity = 1;

	/**
	 * @see #getLatency()
	 */
	private int latency = 20;

	public CloudletSchedulerCompletelyFair(){
		super();
		cloudletExecList = new TreeMap<>();
        runningCloudletByPe = new HashMap<>();
	}

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		return super.updateVmProcessing(currentTime, mipsShare);
	}

	/**
	 * Gets the latency, that is the amount of time (in milliseconds)
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
	 * Sets the latency time (in milliseconds)
	 * @param latency the latency to set
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}

	/**
	 * Computes the timeslice for a Cloudlet, that is, the amount
	 * of time (in milliseconds) that such a Cloudlet will have to use the PEs,
	 * considering all Cloudlets in the {@link #getCloudletExecList() executing list}.
	 *
	 * <p>The timeslice is computed considering the {@link #getCloudletWeight(Cloudlet) Cloudlet weight}
	 * and what it represents in percentage of the {@link #getWeightSumOfRunningCloudlets() weight sum} of
	 * all cloudlets in the execution list.</p>
	 *
	 * @param cloudlet Cloudlet to get the timeslice
	 * @return Cloudlet timeslice (in milliseconds)
	 *
	 * @see #getCloudletWeight(Cloudlet)
	 * @see #getWeightSumOfRunningCloudlets()
	 */
	protected double getTimeSlice(Cloudlet cloudlet){
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
	protected double getCloudletNiceness(Cloudlet cloudlet){
		return -cloudlet.getPriority();
	}

	/**
	 * Gets the weight of the Cloudlet to use the CPU, that is
	 * defined based on its niceness. As greater is the weight,
	 * more time the Cloudlet will have to use the PEs.
	 *
     * <p>As the {@link #getTimeSlice(Cloudlet) timelice} assigned to a Cloudlet to use the CPU is defined
     * exponentially instead of linearly according to its niceness,
     * this method is used as the base to correctly compute the timeslice.
     * </p>
	 * <p><b>NOTICE</b>: The formula used is based on the book referenced at the class documentation.</p>
	 *
	 * @param cloudlet Cloudlet to get the weight to use PEs
	 * @return the cloudlet weight to use PEs
     * @see #getCloudletNiceness(Cloudlet)
	 */
	protected double getCloudletWeight(Cloudlet cloudlet){
		return 1024.0/(Math.pow(1.25, getCloudletNiceness(cloudlet)));
	}

	/**
	 * Gets the percentage (in scale from [0 to 1]) that the weight of a Cloudlet
	 * represents compared to the weight sum of all Cloudlets in the execution list.
	 *
	 * @param cloudlet Cloudlet to get its weight percentage
	 * @return the cloudlet weight percentage between all Cloudlets in the execution list
	 */
	private double getCloudletWeightPercentBetweenAllCloudlets(Cloudlet cloudlet) {
		return getCloudletWeight(cloudlet) / getWeightSumOfRunningCloudlets();
	}

	/**
	 * Gets the weight sum of all cloudlets in the executing list.
	 */
	private double getWeightSumOfRunningCloudlets() {
		return getCloudletExecList().stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .mapToDouble(this::getCloudletWeight)
            .sum();
	}

	/**
	 * Gets the minimum granularity that is the minimum amount of
	 * time (in milliseconds) that is assigned to each
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
	 * time (in milliseconds) that is assigned to each
	 * Cloudlet to execute.
	 *
	 * @param mininumGranularity the minimum granularity to set
	 */
	public void setMininumGranularity(int mininumGranularity) {
		this.mininumGranularity = mininumGranularity;
	}

	@Override
	public List<CloudletExecutionInfo> getCloudletExecList() {
        return Stream.concat(
                    runningCloudletByPe.values().stream(), 
                    cloudletExecList.values().stream()
                ).collect(toList());
	}

    @Override
    public void addCloudletToExecList(CloudletExecutionInfo c) {
		//A negative virtual runtime indicates the cloudlet has never been executed yet.
		//This math was used just to ensure that the first added cloudlets
		//will have the lower vruntime, depending of their priorites.
		//If all cloudlets have the same priority, the first
		//addedd will start executing first.
		double vruntime = -(c.getCloudlet().getPriority() * 1.0/c.getCloudletId());
        cloudletExecList.put(vruntime, c);
    }

    @Override
    protected boolean removeCloudletFromExecList(CloudletExecutionInfo c) {
        return cloudletExecList.remove(getTimeSlice(c.getCloudlet())) != null;
    }

}

