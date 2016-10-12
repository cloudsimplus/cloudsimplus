package org.cloudbus.cloudsim.schedulers;

import java.util.List;

/**
 * A <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 * that is the default scheduler used for most tasks on Linux Kernel.
 *
 * <p>It is a basic implementation that covers that covers the following features:
 * <ul>
 *     <li>A</li>
 *     <li>B</li>
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
 */
public class CloudletSchedulerCompletelyFair extends CloudletSchedulerTimeShared {
	public static final int MIN_LATENCY = 1; //ms
	private int granularity;
	private int latency;

	public CloudletSchedulerCompletelyFair(){
		super();
	}

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		return super.updateVmProcessing(currentTime, mipsShare);
	}
}
