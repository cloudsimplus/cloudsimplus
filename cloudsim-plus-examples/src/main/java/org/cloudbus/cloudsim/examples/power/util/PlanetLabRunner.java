package org.cloudbus.cloudsim.examples.power.util;

import org.cloudbus.cloudsim.util.Log;

/**
 * The example runner for the PlanetLab workload.
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
public final class PlanetLabRunner extends RunnerAbstract {
    private final static int NUMBER_OF_HOSTS = 800;

    /**
	 * Instantiates a new planet lab runner.
	 *
	 * @param enableOutput the enable output
	 * @param outputToFile the output to file
	 * @param inputFolder the input folder
	 * @param outputFolder the output folder
	 * @param workload the workload
	 * @param vmAllocationPolicy the vm allocation policy
	 * @param vmSelectionPolicy the vm selection policy
     * @param safetyParameterOrUtilizationThreshold a double value to be passed to the specific
     *                               PowerVmSelectionPolicy being created, which the meaning depends
     *                               on that policy.
	 */
	public PlanetLabRunner(
			boolean enableOutput,
			boolean outputToFile,
			String inputFolder,
			String outputFolder,
			String workload,
			String vmAllocationPolicy,
			String vmSelectionPolicy,
			double safetyParameterOrUtilizationThreshold) {
		super(
				enableOutput,
				outputToFile,
				inputFolder,
				outputFolder,
				workload,
				vmAllocationPolicy,
				vmSelectionPolicy,
            safetyParameterOrUtilizationThreshold);
	}

	@Override
	protected void init(String inputFolder) {
		try {
		    super.init(inputFolder);
			broker = Helper.createBroker(getSimulation());

			cloudletList = PlanetLabHelper.createCloudletListPlanetLab(broker, inputFolder);
			vmList = Helper.createVmList(broker, cloudletList.size());
			hostList = Helper.createHostList(NUMBER_OF_HOSTS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}
	}

}
