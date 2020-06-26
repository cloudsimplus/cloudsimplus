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

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.ExpectedCloudletResults;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.BrokerBuilderDecorator;
import org.cloudsimplus.builders.HostBuilder;
import org.cloudsimplus.builders.SimulationScenarioBuilder;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.util.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * An Integration Test (IT) to check a simulation scenario with 1 PM that has
 * capacity to host just 1 of the user's VMs. Two cloudlets are submitted to the
 * same VM and run using the CloudletSchedulerSpaceShared.
 * The Integration Test performs several tests that aren't described here in
 * order to avoid the documentation being out-of-date in case the IT is changed.
 *
 * <p>To check which verifications are being performed, take a look at methods such as
 * {@link #assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(DatacenterBroker)}
 * and all the others that start with "assertThat". Those method names were
 * defined to give an exact notion of what is being tested.</p>
 *
 * <p>The IT uses the new VM listeners to get notified when a host is allocated,
 * deallocated for a given VM and when a VM fails to be created due to lack of
 * host resources. It also relies on the new CloudSim Plus
 * {@link CloudSim#addOnEventProcessingListener(EventListener) process event listener} to be notified every time
 * when any event is processed by CloudSim. By this way, it is possible to
 * verify, for instance, if the resource usage of a given host at a given time
 * is as expected.</p>
 *
 * <i><b>NOTE</b>:See the profile section in the pom.xml for details on how to
 * run all tests, including Functional/Integration Tests.</i>
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
    private static final Logger LOGGER = LoggerFactory.getLogger(VmCreationFailureIntegrationTest.class.getSimpleName());

    /** The number of times a host was allocated to a VM. */
    private int numberOfHostAllocations;

    /** The number of times a host was deallocated to a VM. */
    private int numberOfHostDeallocations;

    /** The number of times a VM failed to be created due to lack of host resources. */
    private int numberOfVmCreationFailures;

    private SimulationScenarioBuilder scenario;
    private CloudSim simulation;

    /**
     * A Listener that will be called every time a Host is
     * allocated to a given VM. It tries to assert that the Host 0 was allocated
     * to the Vm 0 at the expected time.
     *
     * @param evt
     */
    private void onHostAllocation(final VmHostEventInfo evt) {
        numberOfHostAllocations++;
        /*LOGGER.info(
                "# Host {} allocated to Vm {} at time {}",
                evt.getHost().getId(), evt.getVm().getId(), evt.getTime());*/
        if (scenario.getFirstHostFromFirstDatacenter().equals(evt.getHost())
                && scenario.getFirstVmFromFirstBroker().equals(evt.getVm())) {
            assertEquals(0, evt.getTime(), 0.2, "Host wasn't allocated to Vm at the expected time");
        }
    }

    /**
     * A Listener that will be called every time a Host is
     * deallocated to a given VM. It tries to assert that the Host 0 was
     * deallocated to the Vm 0 at the expected time.
     *
     * @param evt
     */
    private void onHostDeallocation(final VmHostEventInfo evt) {
        numberOfHostDeallocations++;
        LOGGER.info(
                "# {} moved/removed from {} at time {}",
                evt.getVm(), evt.getHost(), evt.getTime());
        if (scenario.getFirstHostFromFirstDatacenter().equals(evt.getHost()) &&
            scenario.getFirstVmFromFirstBroker().equals(evt.getVm())) {
            assertEquals(20, evt.getTime(), 0.2, "Vm wasn't removed from the Host at the expected time");
        }
    }

    /**
     * A listener that will be called every time a Vm failed to be created
     * due to lack of host resources.
     *
     * @param evt
     * @see Vm#addOnCreationFailureListener(EventListener)}
     */
    private void onVmCreationFailure(final VmDatacenterEventInfo evt) {
        numberOfVmCreationFailures++;
        final int expectedFailedVms = 1;

        final String msg = String.format(
                "Only %d VMs should had failed to be created due to lack of resources but %d failed",
                expectedFailedVms, numberOfVmCreationFailures);
        assertTrue(expectedFailedVms == numberOfVmCreationFailures, msg);
    }

    /**
     * A function used by an event processing listener
     * that will be called every time an event is processed by {@link CloudSim}.
     * @param evt
     * @see CloudSim#addOnEventProcessingListener(EventListener)
     */
    private void onEventProcessing(final SimEvent evt) {
        //LOGGER.info("* onEventProcessing at time {}: {}", evt.getTime(), evt);
        final int time = (int) evt.getTime();
        if (time == 10 || time == 20) {
            assertEquals(200, scenario.getFirstHostFromFirstDatacenter().getTotalAvailableMips(), 0.1);
        }
    }

    /**
     * A function used by a VM update processing listener
     * that will be called every time the processing of a Vm is updated inside its host.
     * Considering there is only one Host and only 1 VM where its cloudlets use a
     * {@link UtilizationModelFull} for CPU utilization model,
     * at any time, the amount of available Host CPU should be the same.
     *
     * @param evt
     * @see Vm#addOnUpdateProcessingListener(EventListener)}
     */
    private void onUpdateVmProcessing(final VmHostEventInfo evt) {
        /*LOGGER.info(
            "- onUpdateVmProcessing at time {} for {}: {} available mips: {}",
            evt.getTime(), evt.getVm(), evt.getHost(), evt.getHost().getAvailableMips());*/
        assertEquals(200, evt.getHost().getTotalAvailableMips());
    }

    @BeforeEach
    public void setUp() {
        Log.setLevel(Level.WARN);
        simulation = new CloudSim();
        simulation.addOnEventProcessingListener(this::onEventProcessing);
        scenario = new SimulationScenarioBuilder(simulation);
        scenario.getDatacenterBuilder().create(
                new HostBuilder()
                    .setVmSchedulerSupplier(VmSchedulerSpaceShared::new)
                    .setPes(1).setMips(1200)
                    .create()
                    .getHosts()
        );

        final BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().create();

        brokerBuilder.getVmBuilder()
                .setPes(1)
                .setMips(1000)
                .setVmCreationFunction(this::createVm)
                .setCloudletSchedulerSupplier(CloudletSchedulerSpaceShared::new)
                .setOnHostAllocationListener(this::onHostAllocation)
                .setOnHostDeallocationListener(this::onHostDeallocation)
                .setOnVmCreationFailureListener(this::onVmCreationFailure)
                .setOnUpdateVmProcessingListener(this::onUpdateVmProcessing)
                /*Try to create 2 VMs where there is capacity to only one,
                 thus, 1 will fail being created*/
                .createAndSubmit(2);

        brokerBuilder.getCloudletBuilder()
                .setLength(10000)
                .setUtilizationModelCpuRamAndBw(new UtilizationModelFull())
                .setPEs(1)
                .createAndSubmit(2);
    }

    private Vm createVm(final double mips, final long pes) {
        return new VmSimple(mips, pes)
                        .setRam(HostSimple.getDefaultRamCapacity())
                        .setBw(HostSimple.getDefaultBwCapacity())
                        .setSize(HostSimple.getDefaultStorageCapacity());
    }

    @Test
    public void integrationTest() {
        simulation.start();
        final DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        //new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
        assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(broker);
        assertThatListenersWereCalledTheExpectedAmountOfTimes();
    }

    public void assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(DatacenterBroker broker) {
        /*The array of expected results for each broker cloudlet*/
        final ExpectedCloudletResults[] expectedResults = {
                new ExpectedCloudletResults(10, 0, 10),
                new ExpectedCloudletResults(10, 10, 20)
        };

        final List<Cloudlet> cloudletList = broker.getCloudletFinishedList();
        assertEquals(expectedResults.length, cloudletList.size(), "The number of finished cloudlets was not as expected");

        int idx = -1;
        for (final Cloudlet cloudlet : cloudletList) {
            expectedResults[++idx].setCloudlet(cloudlet);
            assertThatOneGivenCloudletHasTheExpectedExecutionTimes(expectedResults[idx]);
        }
    }

    private void assertThatOneGivenCloudletHasTheExpectedExecutionTimes(final ExpectedCloudletResults results) {
        final double delta = 0.4;
        assertEquals(results.getExpectedExecTime(), results.getCloudlet().getActualCpuTime(), delta, results.getCloudlet()+" getActualCPUTime");
        assertEquals(results.getExpectedStartTime(), results.getCloudlet().getExecStartTime(), delta, results.getCloudlet()+" getExecStartTime");
        assertEquals(results.getExpectedFinishTime(), results.getCloudlet().getFinishTime(), delta, results.getCloudlet()+" getFinishTime");
        assertEquals(0, results.getCloudlet().getVm().getId());
        assertEquals(Cloudlet.Status.SUCCESS, results.getCloudlet().getStatus());
    }

    private void assertThatListenersWereCalledTheExpectedAmountOfTimes() {
        assertEquals(
            1, numberOfHostAllocations,
            "The number of times a Host was allocated to a VM isn't as expected");
        assertEquals(
                1, numberOfHostDeallocations,
                "The number of times a Host was deallocated to a VM isn't as expected");
        assertEquals(
                1, numberOfVmCreationFailures,
                "The number of times a Vm failed to be created due to lack of resources wasn't as expected");
    }
}
