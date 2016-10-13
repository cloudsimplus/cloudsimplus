package org.cloudbus.cloudsim.schedulers;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.resources.Pe;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 * that is the default scheduler used for most tasks on Linux Kernel.
 *
 * <p>
 * It is a basic implementation that covers that covers the following features:
 * <ul>
 *     <li>Defines one runqueue for each CPU core ({@link Pe})</li>
 *     <li>Computes process ({@link Cloudlet}) niceness based on its priority: {@code niceness = -priority}.
 *     The nice value (niceness) defines how nice a process is to the other ones.
 *     Lower niceness (negative values) represents higher priority and consequently higher weight.</li>
 *     <li>Computes process timeslice based on its weight, that in turn is computed based on its niceness.
 *     The timeslice is the amount of time that a process is allowed to use the CPU.
 *     The CFS scheduler uses a dynamic defined timeslice.</li>
 * </ul>
 *
 * And it currently <b>does not</b> implement the following features:
 * <ul>
 *     <li>Additional overhead for CPU context switch</li>
 * </ul>
 *
 * The implementation was based on the book of Robert Love: Linux Kernel Development, 3rd ed. Addison-Wesley, 2010.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 *
 * @see <a href="http://www.ibm.com/developerworks/library/l-completely-fair-scheduler/">Inside the Linux 2.6 Completely Fair Scheduler</a>
 * @see <a href="http://dx.doi.org/10.1145/1400097.1400102">Towards achieving fairness in the Linux scheduler</a>
 * @see <a href="http://dx.doi.org/10.1145/10.1145/2901318.2901326">The Linux scheduler</a>
 * @see <a href="http://www.ibm.com/developerworks/library/l-lpic1-103-6/index.html">Learn Linux, 101: Process execution priorities</a>
 */
public class CloudletSchedulerCompletelyFair extends CloudletSchedulerTimeShared {
	/**
	 * @see #getCloudletExecList()
	 */
	private TreeMap<Double, CloudletExecutionInfo> cloudletExecList;

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
	 * If there are 4 Cloudlets by just 2 PEs, the latency is devided
	 * by 2, because only 2 Cloudlets can be concurrently executed
	 * at the moment. However, the minimum amount of time allocated to each
	 * Cloudlet is defined by the {@link #getMininumGranularity()}.
	 *
	 * <p>As lower is the latency, more responsive a real operating
	 * system will be perceived by users, at the cost or more
	 * frequent CPU context switches (that reduces CPU throughput).
	 * <b>However, CPU context switch overhead is not being considered.</b></p>
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
	 * considering all Cloudlets in the runqueue.
	 *
	 * <p>The timeslice is computed considering the {@link #getCloudletWeight(Cloudlet) Cloudlet weight}
	 * and what it represents in percentage of the {@link #getWeightSumOfRunningCloudlets() weight sum} of
	 * all cloudlets in the runqueue.</p>
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
	 * <p>The formula used is based on the book referenced at the class documentation.</p>
	 *
	 * @param cloudlet Cloudlet to get the weight to use PEs
	 * @return the cloudlet weight to use PEs
	 */
	protected double getCloudletWeight(Cloudlet cloudlet){
		return 1024/(Math.pow(1.25, getCloudletNiceness(cloudlet)));
	}

	/**
	 * Gets the percentage (in scale from [0 to 1]) that the weight of a Cloudlet
	 * represents compared to the weight sum of all Cloudlets in the runqueue.
	 *
	 * @param cloudlet Cloudlet to get its weight percentage
	 * @return the cloudlet weight percentage between all Cloudlets in the runqueue
	 */
	private double getCloudletWeightPercentBetweenAllCloudlets(Cloudlet cloudlet) {
		return (getCloudletWeight(cloudlet)) / getWeightSumOfRunningCloudlets();
	}

	/**
	 * Gets the weight sum of all cloudlets in the executing list.
	 */
	private double getWeightSumOfRunningCloudlets() {
		return getCloudletExecList().stream().mapToDouble(c->getCloudletWeight(c.getCloudlet())).sum();
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

	/**
	 *  Gets a {@link TreeMap Red-Black Tree} that stores the list of running Cloudlets
	 * (the so called run queue), where each key is the virtual runtime (vruntime),
	 * which indicates the amount of time the Cloudlet has to run yet.
	 *
	 * @param <T> {@inheritDoc}
	 * @return {@inheritDoc} (the so called run queue)
	 */
	@Override
	public <T extends CloudletExecutionInfo> Collection<T> getCloudletExecList() {
		return (Collection<T>) cloudletExecList;
	}
}
