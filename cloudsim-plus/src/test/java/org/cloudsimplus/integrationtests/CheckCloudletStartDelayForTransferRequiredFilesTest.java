/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.integrationtests;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private DatacenterBroker broker;
	private List<File> files;
	private FileStorage storage;
    private CloudSim simulation;

	@BeforeEach
    public void setUp() {
		createStorage();

        this.simulation = new  CloudSim();
        final SimulationScenarioBuilder scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder()
	        .setSchedulingInterval(1)
	        .addStorageToList(storage)
	        .create(
	            new HostBuilder()
	                .setVmSchedulerSupplier(VmSchedulerSpaceShared::new)
	                .setPes(HOST_PES).setMips(HOST_MIPS)
	                .create()
	                .getHosts()
	        );


        final BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().create();
	    this.broker = brokerBuilder.getBroker();
        brokerBuilder.getVmBuilder()
                .setPes(VM_PES).setMips(VM_MIPS)
                .setCloudletSchedulerSupplier(CloudletSchedulerSpaceShared::new)
                .createAndSubmit();

		brokerBuilder.getVmBuilder()
            .setCloudletSchedulerSupplier(CloudletSchedulerTimeShared::new)
            .createAndSubmit();

        brokerBuilder.getCloudletBuilder()
                .setLength(CLOUDLET_LENGTH)
                .setUtilizationModelCpu(new UtilizationModelFull())
                .setPEs(CLOUDLET_PES)
	            .setRequiredFiles(getFileNames())
                .createAndSubmit(NUMBER_OF_CLOUDLETS);
    }

	private void createStorage() {
		createListOfFiles();
		storage = new SanStorage(100000, SAN_BANDWIDTH_MBITS_PER_SEC, 0.1);
		files.forEach(storage::addFile);
	}

	/**
	 * Gets the filenames from the list of {@link #files}.
	 */
	private List<String> getFileNames() {
		return files.stream().map(File::getName).collect(Collectors.toList());
	}

	/**
	 * List of files to be stored by the Datacenter and that will be used
	 * by the created cloudlet.
	 */
	private void createListOfFiles() {
		files = IntStream.range(0, NUMBER_OF_FILES_TO_CREATE)
                         .mapToObj(id -> new File(String.format("file%d", id), FILE_SIZE_MB))
                         .collect(Collectors.toList());
	}

	@Test
    public void integrationTest() {
        simulation.start();
		final List<Cloudlet> cloudlets = broker.getCloudletFinishedList();
		/* The expected finish time considers the delay to transfer the Cloudlet
		 * required files and the actual execution time.
		 */
		final long expectedFinishTime = 17+5;
		for(final Cloudlet c: cloudlets) {
			//Checks if each cloudlet finished at the expected time, considering the delay to transfer the required files
			assertEquals(expectedFinishTime, c.getFinishTime(), 0.3, c.toString() + " expected finish time");

			/* Checks if the cloudlet length is not being changed to simulate the
			 * delay to transfer the cloudlet required files to the Vm.
			 * The transfer time has to be implemented delaying the cloudlet processing
			 * not increasing the cloudlet length.
			 */
			assertEquals( CLOUDLET_LENGTH, c.getLength(), 0.1, c.toString());
		}

        //new CloudletsTableBuilder(broker.getCloudletFinishedList()).setTitle(broker.getName()).build();
    }

}
