package org.cloudbus.cloudsim.IntegrationTests;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 *
 * An Integration Test (IT) running a simulation scenario with 2 PMs, 2 VMs
 * (one using {@link CloudletSchedulerSpaceShared} and other using
 * {@link CloudletSchedulerTimeShared}) and 2 cloudlets with a list of required files.
 * The test checks if the end of cloudlet execution was
 * correctly delayed by the time to transfer the file list
 * to the VM.
 *
 * <p>It is created a Storage Area Network (SAN) for the Datacenter and
 * a list of {@link File Files} is stored on it.
 * The name of these files are then added to the list
 * of required files of the created Cloudlet.
 * Thus, the time to transfer these files from the SAN
 * to the Vm has to be added to cloudlet finish time.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CheckCloudletStartDelayForTransferRequiredFilesTest {
	private static final int NUMBER_OF_CLOUDLETS = 2;
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 2;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES/2;
    private static final int CLOUDLET_PES = VM_PES;
    private static final int CLOUDLET_LENGTH = HOST_MIPS*5;
	private static final int FILE_SIZE_MB = 100;
	private static final int NUMBER_OF_FILES_TO_CREATE = 2;
	private static final double SAN_BANDWIDTH_MBITS_PER_SEC = 100;

	private SimulationScenarioBuilder scenario;
    private UtilizationModel utilizationModel;
	private DatacenterBroker broker;
	private List<File> files;
	private FileStorage storage;

	@Before
    public void setUp() {
		createStorage();

        CloudSim.init(1, Calendar.getInstance(), false);
        utilizationModel = new UtilizationModelFull();
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder()
	        .setSchedulingInterval(1)
	        .addStorageToList(storage)
	        .createDatacenter(
	            new HostBuilder()
	                .setVmSchedulerClass(VmSchedulerSpaceShared.class)
	                .setRam(4000).setBw(400000)
	                .setPes(HOST_PES).setMips(HOST_MIPS)
	                .createOneHost()
	                .getHosts()
	        );


        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();
	    this.broker = brokerBuilder.getBroker();
        brokerBuilder.getVmBuilder()
                .setRam(1000).setBw(100000)
                .setPes(VM_PES).setMips(VM_MIPS).setSize(50000)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared())
                .createAndSubmitOneVm();

		brokerBuilder.getVmBuilder().setCloudletScheduler(new CloudletSchedulerTimeShared()).createAndSubmitOneVm();

        brokerBuilder.getCloudletBuilder()
                .setLength(CLOUDLET_LENGTH)
                .setUtilizationModelCpu(utilizationModel)
                .setPEs(CLOUDLET_PES)
	            .setRequiredFiles(getFileNames())
                .createAndSubmitCloudlets(NUMBER_OF_CLOUDLETS);
    }

	private void createStorage() {
		createListOfFiles();
		storage = new SanStorage(100000, SAN_BANDWIDTH_MBITS_PER_SEC, 0.1);
		files.stream().forEach(storage::addFile);
	}

	/**
	 * Gets the filenames from the list of {@link #files}.
	 */
	private List<String> getFileNames() {
		return files.stream().map(File::getName).collect(Collectors.toList());
	}

	/**
	 * List of files to be stored by the datacenter and that will be used
	 * by the created cloudlet.
	 */
	private void createListOfFiles() {
		files = new ArrayList<>();
		for(int i = 0; i < NUMBER_OF_FILES_TO_CREATE; i++) {
			files.add(new File(String.format("file%d", i), FILE_SIZE_MB));
		}
	}

	@Test
    public void integrationTest() {
        startSimulationAndWaitToStop();
		List<Cloudlet> cloudlets = broker.getCloudletsFinishedList();
		/* The expected finish time considers the delay to transfer the Cloudlet
		 * required files and the actual execution time.*/
		final long expectedFinishTime = 7;
		for(Cloudlet c: cloudlets) {
			//Checks if each cloudlet finished at the expectd time, considering the delay to transfer the required files
			assertEquals(String.format("Cloudlet %d", c.getId()), expectedFinishTime, c.getFinishTime(), 0.1);

			/* Checks if the cloudlet length is not being changed to simulate the
			 * delay to transfer the cloudlet required files to the Vm.
			 * The transfer time has to be implemented delaying the cloudlet processing
			 * not increasing the cloudlet length.*/
			assertEquals(String.format("Cloudlet %d", c.getId()), CLOUDLET_LENGTH, c.getCloudletLength(), 0.1);
		}
	    printCloudletsExecutionResults();
    }

    public void startSimulationAndWaitToStop() throws RuntimeException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults() {
        CloudletsTableBuilderHelper.print(
                new TextTableBuilder(broker.getName()),
                broker.getCloudletsFinishedList());
    }

}
