/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.util.ExpectedCloudletExecutionResults;

import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * An Integration Test (IT) to check a simulation scenario with 1 PM that has
 * capacity to host just 1 of the user's VMs. Two cloudlets are submitted to the
 * same VM and run using the SpaceShared CloudletScheduler.
 * The Integration Test performs several tests that aren't described here in
 * order to avoid the documentation being out-of-date in case the IT is changed.
 *
 * <p>To see what verifications are being performed, take a look at methods such as
 * {@link #assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(DatacenterBroker)}
 * and all the others that start with "assertThat". Those method names were
 * defined to give an exact notion of what is being tested.</p>
 *
 * <p>The IT uses the new VM listeners to get notified when a host is allocated,
 * deallocated for a given VM and when a VM fails to be created due to lack of
 * host resources. It also relies on the new CloudSim
 * {@link CloudSim#addOnEventProcessingListener(EventListener) process event listener} to be notified every time
 * when any event is processed by CloudSim. By this way, it is possible to
 * verify, for instance, if the resource usage of a given host at a given time
 * is as expected.</p>
 *
 * <i><b>NOTE</b>:See the profile section in the pom.xml for details of how to
 * run all tests, including Functional/Integration Tests in this package.</i>
 *
 * @see Vm#addOnHostAllocationListener(EventListener)
 * @see Vm#addOnHostDeallocationListener(EventListener)
 * @see Vm#addOnCreationFailureListener(EventListener)
 * @see CloudSim#addOnEventProcessingListener(EventListener)
 * @see Cloudlet#addOnFinishListener(EventListener)
 *
 * @author Manoel Campos da Silva Filho
 */
public final class VmCreationFailureIntegrationTest {
    /** The number of times a host was allocated to a VM. */
    private int numberOfHostAllocations = 0;

    /** The number of times a host was deallocated to a VM. */
    private int numberOfHostDeallocations = 0;

    /** The number of times a VM failed to be created due to lack of host resources. */
    private int numberOfVmCreationFailures = 0;

    private SimulationScenarioBuilder scenario;
    private CloudSim simulation;

    /**
     * A lambda function used by an {@link Vm#addOnHostAllocationListener(EventListener)}  }
     * that will be called every time a Host is
     * allocated to a given VM. It tries to assert that the Host 0 was allocated
     * to the Vm 0 at the expected time.
     *
     * @param evt
     */
    private void onHostAllocation(VmHostEventInfo evt) {
        numberOfHostAllocations++;
        Log.printFormattedLine("# Host %s allocated to Vm %s at time %3.0f",
                evt.getHost().getId(), evt.getVm().getId(), evt.getTime());
        if (scenario.getFirstHostFromFirstDatacenter().equals(evt.getHost())
                && scenario.getFirstVmFromFirstBroker().equals(evt.getVm())) {
            assertEquals(
                "Host wasn't allocated to Vm at the expected time",
                0, evt.getTime(), 0.2);
        }
    }

    /**
     * A lambda function used by an {@link Vm#addOnHostDeallocationListener(EventListener)}  }
     * that will be called every time a Host is
     * deallocated to a given VM. It tries to assert that the Host 0 was
     * deallocated to the Vm 0 at the expected time.
     *
     * @param evt
     */
    private void onHostDeallocation(VmHostEventInfo evt) {
        numberOfHostDeallocations++;
        Log.printFormattedLine(
                "# Vm %s moved/removed from Host %s at time %3.0f",
                evt.getVm().getId(), evt.getHost().getId(), evt.getTime());
        if (scenario.getFirstHostFromFirstDatacenter().equals(evt.getHost()) &&
            scenario.getFirstVmFromFirstBroker().equals(evt.getVm())) {
            assertEquals(
                    "Vm wasn't removed from the Host at the expected time",
                    20, evt.getTime(), 0.2);
        }
    }

    /**
     * A lambda function used by an {@link Vm#addOnCreationFailureListener(EventListener)}  }
     * that will be called every time a Vm failed to be created
     * due to lack of host resources.
     *
     * @param evt
     */
    private void onVmCreationFailure(VmDatacenterEventInfo evt) {
        numberOfVmCreationFailures++;
        final int expectedVmId = 1;

        String msg = String.format(
                "Only Vm %d should had failed to be created due to lack of resources",
                expectedVmId);
        assertEquals(msg, expectedVmId, evt.getVm().getId());
    }

    /**
     * A lambda function used by an {@link CloudSim#onEventProcessingListeners}
     * that will be called every time an event is processed by {@link CloudSim}.
     * @param evt
     */
    private void onEventProcessing(SimEvent evt) {
        Log.printFormattedLine("* onEventProcessing at time %3.0f: %s", evt.getTime(), evt);
        switch ((int) evt.getTime()) {
            case 10:
                assertEquals(200,
                        scenario.getFirstHostFromFirstDatacenter().getAvailableMips(), 0.1);
            break;
            case 20:
                assertEquals(200,
                        scenario.getFirstHostFromFirstDatacenter().getAvailableMips(), 0.1);
            break;
        }
    }


    /**
     * A lambda function used by an {@link Vm#addOnUpdateProcessingListener(EventListener)}  }
     * that will be called every time the processing of a Vm is updated inside its host.
     * Considering there is only one Host and only 1 VM where its cloudlets use a
     * {@link UtilizationModelFull} for CPU utilization model,
     * at any time, the amount of available Host CPU should be the same.
     *
     * @param evt
     */
    private void onUpdateVmProcessing(VmHostEventInfo evt) {
        Log.printConcatLine(
            "- onUpdateVmProcessing at time ", evt.getTime(), " - vm: ",
            evt.getVm().getId(), " host ", evt.getHost().getId(), " available mips: ",
            evt.getHost().getAvailableMips());
        assertEquals(200, evt.getHost().getAvailableMips(), 0);
    }

    @Before
    public void setUp() {
        simulation = new CloudSim();
        simulation.addOnEventProcessingListener((evt) -> onEventProcessing(evt));
        scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder().createDatacenter(
                new HostBuilder()
                .setVmSchedulerClass(VmSchedulerTimeShared.class)
                .setRam(2048).setBw(10000)
                .setPes(1).setMips(1200)
                .createOneHost()
                .getHosts()
        );

        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();

        brokerBuilder.getVmBuilder()
                .setRam(512).setBw(1000)
                .setPes(1).setMips(1000).setSize(10000)
                .setCloudletSchedulerSupplier(() -> new CloudletSchedulerSpaceShared())
                .setOnHostAllocationListener(evt -> onHostAllocation(evt))
                .setOnHostDeallocationListener(evt -> onHostDeallocation(evt))
                .setOnVmCreationFilatureListenerForAllVms(evt -> onVmCreationFailure(evt))
                .setOnUpdateVmProcessingListener(evt -> onUpdateVmProcessing(evt))
                /*try to create 2 VMs where there is capacity to only one,
                 thus, 1 will fail being created*/
                .createAndSubmitVms(2);

        brokerBuilder.getCloudletBuilder()
                .setLength(10000)
                .setUtilizationModelCpuRamAndBw(new UtilizationModelFull())
                .setPEs(1)
                .createAndSubmitCloudlets(2);
    }

    @Test
    public void integrationTest() {
        simulation.start();
        DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(broker);
        assertThatListenersWereCalledTheExpectedAmountOfTimes();

        printCloudletsExecutionResults(broker);
    }

    public void printCloudletsExecutionResults(DatacenterBroker broker) {
        new CloudletsTableBuilder(broker.getCloudletsFinishedList()).build();
    }

    public void assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(DatacenterBroker broker) {
        /*The array of expected results for each broker cloudlet*/
        final ExpectedCloudletExecutionResults expectedResults[]
                = new ExpectedCloudletExecutionResults[]{
                    new ExpectedCloudletExecutionResults(10, 0, 10),
                    new ExpectedCloudletExecutionResults(10, 10, 20)
                };

        final List<Cloudlet> cloudletList = broker.getCloudletsFinishedList();
        assertEquals(
                "The number of finished cloudlets was not as expected",
                cloudletList.size(), expectedResults.length);
        int i = -1;
        for (Cloudlet cloudlet : cloudletList) {
            expectedResults[++i].setCloudlet(cloudlet);
            assertThatOneGivenCloudletHasTheExpectedExecutionTimes(expectedResults[i]);
        }
    }

    private void assertThatOneGivenCloudletHasTheExpectedExecutionTimes(final ExpectedCloudletExecutionResults results) {
        assertEquals("cloudlet.getActualCPUTime", results.getExpectedExecTime(), results.getCloudlet().getActualCpuTime(), 0.2);
        assertEquals("cloudlet.getExecStartTime", results.getExpectedStartTime(), results.getCloudlet().getExecStartTime(), 0.2);
        assertEquals("cloudlet.getFinishTime", results.getExpectedFinishTime(), results.getCloudlet().getFinishTime(), 0.2);
        assertEquals(0, results.getCloudlet().getVm().getId(), 0);
        assertEquals("Cloudlet wasn't executed at the expected Datacenter",
                1, results.getCloudlet().getLastDatacenter().getId(), 0);
        assertEquals(Cloudlet.Status.SUCCESS, results.getCloudlet().getStatus());
    }

    private void assertThatListenersWereCalledTheExpectedAmountOfTimes() {
        assertEquals("The number of times a Host was allocated to a VM isn't as expected",
                1, numberOfHostAllocations);
        assertEquals("The number of times a Host was deallocated to a VM isn't as expected",
                1, numberOfHostDeallocations);
        assertEquals(
                "The number of times a Vm failed to be created due to lack of resources wasn't as expected",
                1, numberOfVmCreationFailures);
    }
}
