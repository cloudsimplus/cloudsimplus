package org.cloudbus.cloudsim.examples.power.util;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelPlanetLab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The example runner for the PlanetLab workload.
 * <p>
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <p>
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
     * The input folder given to the constructor constains lots of
     * planet lab workload files. One cloudlet is created
     * for each file into this directory.
     * Each file is used as input to the cloudlet's CPU UtilizationModel.
     * This constant limits the number of files to read,
     * limiting the number of cloudlets to create.
     * The value {@link Integer#MAX_VALUE} indicates no limit.
     */
    private final static int MAX_NUMBER_OF_WORLOAD_FILES_TO_READ = Integer.MAX_VALUE;

    /**
     * Instantiates a new planet lab runner.
     *
     * @param enableOutput                          the enable output
     * @param outputToFile                          the output to file
     * @param inputFolder                           the input folder
     * @param outputFolder                          the output folder
     * @param workload                              the workload
     * @param vmAllocationPolicy                    the vm allocation policy
     * @param vmSelectionPolicy                     the vm selection policy
     * @param safetyParameterOrUtilizationThreshold a double value to be passed to the specific
     *                                              PowerVmSelectionPolicy being created, which the meaning depends
     *                                              on that policy.
     */
    public PlanetLabRunner(
        final boolean enableOutput,
        final boolean outputToFile,
        final String inputFolder,
        final String outputFolder,
        final String workload,
        final String vmAllocationPolicy,
        final String vmSelectionPolicy,
        final double safetyParameterOrUtilizationThreshold)
    {
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

    /**
     * Creates the cloudlet list planet lab.
     *
     * @param inputFolderName the input folder name
     * @return the list
     */
    public static List<Cloudlet> createCloudletListPlanetLab(final String inputFolderName) {
        final long fileSize = 300;
        final long outputSize = 300;

        final List<String> planetLabFiles = getFileList(inputFolderName);

        final int filesToRead = Math.min(planetLabFiles.size(), MAX_NUMBER_OF_WORLOAD_FILES_TO_READ);
        final List<Cloudlet> list = new ArrayList<>(filesToRead);
        for (int i = 0; i < filesToRead; i++) {
            try {
                UtilizationModel utilizationModelCPU =
                    UtilizationModelPlanetLab.getInstance(planetLabFiles.get(i), Constants.SCHEDULING_INTERVAL);
                CloudletSimple cloudlet = new CloudletSimple(
                    i, Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES);
                cloudlet.setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModelCpu(utilizationModelCPU);
                list.add(cloudlet);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        return list;
    }

    private static List<String> getFileList(final String inputFolderName) {
        final List<String> files = ResourceLoader.getResourceList(PlanetLabRunner.class, inputFolderName);
        if (files == null || files.isEmpty()) {
            Log.printLine("No workload files found in " + inputFolderName);
            return new ArrayList<>();
        }

        return files;
    }

    @Override
    protected void init(final String inputFolder) {
        try {
            super.init(inputFolder);
            broker = Helper.createBroker(getSimulation());
            cloudletList = createCloudletListPlanetLab(inputFolder);
            vmList = Helper.createVmList(broker, cloudletList.size());
            hostList = Helper.createHostList(NUMBER_OF_HOSTS);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
            System.exit(0);
        }
    }

}
