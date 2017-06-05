package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.examples.power.util.PlanetLabRunner;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.util.Constants;
import org.cloudbus.cloudsim.examples.power.util.Helper;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenterNonPowerAware;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicySimple;

/**
 * A simulation of a heterogeneous non-power aware data center: all hosts consume maximum power all
 * the time.
 *
 * This example uses a real PlanetLab workload: 20110303.
 *
 * The remaining configuration parameters are in the Constants and PlanetLabConstants classes.
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
public class NonPowerAware {
    private final static int NUMBER_OF_HOSTS = 800;
    private static CloudSim simulation;

    /**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		final String experimentName = "planetlab_npa";
		final String outputFolder = "output";
        String inputFolder =  ResourceLoader.getResourcePath(NonPowerAware.class,"workload/planetlab/20110303");
        if(Objects.isNull(inputFolder)){
            inputFolder = "";
        }

		Log.setDisabled(!Constants.ENABLE_OUTPUT);
		Log.printLine("Starting " + experimentName);

		try {
            simulation = new CloudSim();
            Helper helper = new Helper(simulation, experimentName, Constants.OUTPUT_CSV, outputFolder);

			DatacenterBroker broker = helper.createBroker(simulation);

			List<Cloudlet> cloudletList = PlanetLabRunner.createCloudletListPlanetLab(broker, inputFolder);
			List<Vm> vmList = helper.createVmList(broker, cloudletList.size());
			List<PowerHost> hostList = helper.createHostList(NUMBER_OF_HOSTS);

			PowerDatacenterNonPowerAware datacenter = (PowerDatacenterNonPowerAware) helper.createDatacenter(
                PowerDatacenterNonPowerAware.class, hostList,
                new PowerVmAllocationPolicySimple());

			datacenter.setMigrationsEnabled(false);

			broker.submitVmList(vmList);
			broker.submitCloudletList(cloudletList);

			simulation.terminateAt(Constants.SIMULATION_LIMIT);
			double lastClock = simulation.start();

			List<Cloudlet> newList = broker.getCloudletFinishedList();
			Log.printLine("Received " + newList.size() + " cloudlets");

            helper.printResults(datacenter, vmList, lastClock);
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}

		Log.printLine("Finished " + experimentName);
	}

}
