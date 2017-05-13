package org.cloudbus.cloudsim.examples.power.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelPlanetLab;

/**
 * A helper class for the running examples for the PlanetLab workload.
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
public final class PlanetLabHelper {

	/**
	 * Creates the cloudlet list planet lab.
	 *
	 * @param broker the broker
	 * @param inputFolderName the input folder name
	 * @return the list
	 */
	public static List<Cloudlet> createCloudletListPlanetLab(DatacenterBroker broker, String inputFolderName) {
		List<Cloudlet> list = new ArrayList<>();

		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModelNull = UtilizationModel.NULL;

		File inputFolder = new File(inputFolderName);
		File[] files = inputFolder.listFiles();
        if(Objects.isNull(files)) {
            return list;
        }

		for (int i = 0; i < files.length; i++) {
			try {
                UtilizationModel utilizationModelCPU =
                        new UtilizationModelPlanetLab(
								files[i].getAbsolutePath(),
								Constants.SCHEDULING_INTERVAL);
				CloudletSimple cloudlet = new CloudletSimple(
                        i, Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES);
				cloudlet.setFileSize(fileSize)
                        .setOutputSize(outputSize)
						.setUtilizationModelCpu(utilizationModelCPU)
                        .setUtilizationModelRam(utilizationModelNull)
                        .setUtilizationModelBw(utilizationModelNull);
                //cloudlet.setVm(i);
                list.add(cloudlet);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		return list;
	}

    /**
     * A private constructor to avoid class instantiation.
     */
	private PlanetLabHelper(){}

}
