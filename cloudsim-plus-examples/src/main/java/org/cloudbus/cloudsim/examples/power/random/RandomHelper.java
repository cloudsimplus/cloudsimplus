/*
 *
 */
package org.cloudbus.cloudsim.examples.power.random;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelZero;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.examples.power.Constants;

/**
 * The Helper class for the random workload.
 *
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 *
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 *
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class RandomHelper {

	/**
	 * Creates the cloudlet list.
	 *
	 * @param broker the broker
	 * @param cloudletsNumber the cloudlets number
	 *
	 * @return the list< cloudlet>
	 */
	public static List<Cloudlet> createCloudletList(DatacenterBroker broker, int cloudletsNumber) {
		List<Cloudlet> list = new ArrayList<>(cloudletsNumber);

		long fileSize = 300;
		long outputSize = 300;
		long seed = RandomConstants.CLOUDLET_UTILIZATION_SEED;
		UtilizationModel utilizationModelNull = new UtilizationModelZero();

		for (int i = 0; i < cloudletsNumber; i++) {
			CloudletSimple cloudlet = null;
			if (seed == -1) {
				cloudlet = new CloudletSimple(i,Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES);
                cloudlet
                        .setCloudletFileSize(fileSize)
                        .setCloudletOutputSize(outputSize)
						.setUtilizationModelCpu(new UtilizationModelStochastic())
						.setUtilizationModelRam(utilizationModelNull)
						.setUtilizationModelBw(utilizationModelNull);
			} else {
				cloudlet = new CloudletSimple(i,Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES);
                cloudlet
                        .setCloudletFileSize(fileSize)
                        .setCloudletOutputSize(outputSize)
						.setUtilizationModelCpu(new UtilizationModelStochastic(seed*i))
						.setUtilizationModelRam(utilizationModelNull)
						.setUtilizationModelBw(utilizationModelNull);
                
			}
			cloudlet.setBroker(broker);
			cloudlet.setVmId(i);
			list.add(cloudlet);
		}

		return list;
	}

}
