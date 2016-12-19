package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelZero;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelPlanetLabInMemory;
import org.cloudbus.cloudsim.examples.power.Constants;

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
public class PlanetLabHelper {

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
		UtilizationModel utilizationModelNull = new UtilizationModelZero();

		File inputFolder = new File(inputFolderName);
		File[] files = inputFolder.listFiles();
        if(Objects.isNull(files)) {
            return list;
        }

		for (int i = 0; i < files.length; i++) {
			try {
                UtilizationModel utilizationModelCPU =
                        new UtilizationModelPlanetLabInMemory(
								files[i].getAbsolutePath(),
								Constants.SCHEDULING_INTERVAL);
				CloudletSimple cloudlet = new CloudletSimple(
                        i, Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES);
				cloudlet.setCloudletFileSize(fileSize)
                        .setCloudletOutputSize(outputSize)
						.setUtilizationModelCpu(utilizationModelCPU)
                        .setUtilizationModelRam(utilizationModelNull)
                        .setUtilizationModelBw(utilizationModelNull);
                cloudlet.setBroker(broker);
                //cloudlet.setVm(i);
                list.add(cloudlet);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		return list;
	}

}
