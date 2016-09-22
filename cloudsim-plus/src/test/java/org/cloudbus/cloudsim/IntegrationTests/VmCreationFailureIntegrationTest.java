package org.cloudbus.cloudsim.IntegrationTests;

import org.cloudbus.cloudsim.util.ExpectedCloudletExecutionResults;
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * <p>An Integration Test (IT) to check a simulation scenario with 1 PM that has
 * capacity to host just 1 of the user's VMs. Two cloudlets are submitted to the
 * same VM and run using the SpaceShared CloudletScheduler.</p>
 *
 * The Integration Test performs several tests that aren't describe here in
 * order to avoid the documentation being out-of-date in case the IT is changed.
 *
 * <p>To see what verifications are being performed, take a look at methods such as
 * {@link #assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(org.cloudbus.cloudsim.DatacenterBroker)}
 * and all the others that start with "assertThat". Those method names were
 * defined to give an exact notion of what is being tested.</p>
 *
 * <p>The IT uses the new VM listeners to get notified when a host is allocated,
 * deallocated for a given VM and when a VM fails to be created due to lack of
 * host resources. It also relies on the new CloudSim
 * {@link CloudSim#eventProcessingListener} listener to be notified every time
 * when any event is processed by CloudSim. By this way, it is possible to
 * verify, for instance, if the resource usage of a given host at a given time
 * is as expected.</p>
 *
 * <i><b>NOTE</b>:See the profile section in the pom.xml for details of how to
 * run all tests, including Functional/Integration Tests in this package.</i>
 *
 * @see Vm#setOnHostAllocationListener(org.cloudbus.cloudsim.listeners.EventListener)
 * @see Vm#setOnHostDeallocationListener(org.cloudbus.cloudsim.listeners.EventListener)
 * @see Vm#setOnVmCreationFailureListener(org.cloudbus.cloudsim.listeners.EventListener)
 * @see CloudSim#setOnEventProcessingListener(org.cloudbus.cloudsim.listeners.EventListener)
 * @see Cloudlet#setOnCloudletFinishEventListener(org.cloudbus.cloudsim.listeners.EventListener) 
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

    /**
     * A lambda function used by an {@link Vm#setOnHostAllocationListener(org.cloudbus.cloudsim.listeners.EventListener) } 
     * that will be called every time a Host is
     * allocated to a given VM. It tries to assert that the Host 0 was allocated
     * to the Vm 0 at the expected time.
     * 
     * @param evt
     */
    private void onHostAllocation(HostToVmEventInfo evt) {
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
     * A lambda function used by an {@link Vm#setOnHostDeallocationListener(org.cloudbus.cloudsim.listeners.EventListener) } 
     * that will be called every time a Host is
     * deallocated to a given VM. It tries to assert that the Host 0 was
     * deallocated to the Vm 0 at the expected time.
     * 
     * @param evt
     */
    private void onHostDeallocation(HostToVmEventInfo evt) {
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
     * A lambda function used by an {@link Vm#setOnVmCreationFailureListener(org.cloudbus.cloudsim.listeners.EventListener) } 
     * that will be called every time a Vm failed to be created
     * due to lack of host resources. 
     * 
     * @param evt
     */
    private void onVmCreationFailure(DatacenterToVmEventInfo evt) {
        numberOfVmCreationFailures++;
        final int expectedVmId = 1;

        String msg = String.format(
                "Only Vm %d should had failed to be created due to lack of resources",
                expectedVmId);
        assertEquals(msg, expectedVmId, evt.getVm().getId());
    }

    /**
     * A lambda function used by an {@link CloudSim#onEventProcessingListener} 
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
     * A lambda function used by an {@link Vm#setOnUpdateVmProcessingListener(org.cloudbus.cloudsim.listeners.EventListener) } 
     * that will be called every time the processing of a Vm is updated inside its host.
     * Considering there is only one Host and only 1 VM where its cloudlets use a 
     * {@link UtilizationModelFull} for CPU utilization model, 
     * at any time, the amount of available Host CPU should be the same.
     * 
     * @param evt
     */
    private void onUpdateVmProcessing(HostToVmEventInfo evt) {
        Log.printConcatLine(
            "- onUpdateVmProcessing at time ", evt.getTime(), " - vm: ", 
            evt.getVm().getId(), " host ", evt.getHost().getId(), " available mips: ", 
            evt.getHost().getAvailableMips());
        assertEquals(200, evt.getHost().getAvailableMips(), 0);
    }

    @Before
    public void setUp() {
        CloudSim.init(1, Calendar.getInstance(), false);
        CloudSim.setOnEventProcessingListener((evt) -> onEventProcessing(evt));
        scenario = new SimulationScenarioBuilder();
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
                .setCloudletScheduler(new CloudletSchedulerSpaceShared())
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
        startSimulationAndWaitToStop();

        DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(broker);
        assertThatListenersWereCalledTheExpectedAmountOfTimes();

        printCloudletsExecutionResults(broker);
    }

    public void startSimulationAndWaitToStop() throws RuntimeException, NullPointerException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults(DatacenterBroker broker) {
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), broker.getCloudletsFinishedList());
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
        assertEquals("cloudlet.getActualCPUTime", results.getExpectedExecTime(), results.getCloudlet().getActualCPUTime(), 0.2);
        assertEquals("cloudlet.getExecStartTime", results.getExpectedStartTime(), results.getCloudlet().getExecStartTime(), 0.2);
        assertEquals("cloudlet.getFinishTime", results.getExpectedFinishTime(), results.getCloudlet().getFinishTime(), 0.2);
        assertEquals(0, results.getCloudlet().getVmId(), 0);
        assertEquals("Cloudlet wasn't executed at the expected Datacenter",
                2, results.getCloudlet().getDatacenterId(), 0);
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
