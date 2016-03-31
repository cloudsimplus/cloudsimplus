package org.cloudbus.cloudsim.IntegrationTests;

import org.cloudbus.cloudsim.util.ExpectedCloudletExecutionResults;
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * An Integration Test (IT) to check a simulation scenario with 1 PM that has
 * capacity to host just 1 of the user's VMs. Two cloudlets are submitted to the
 * same VM and run using the SpaceShared CloudletScheduler.<p/>
 *
 * The Integration Test performs several tests that aren't describe here in
 * order to avoid the documentation being out-of-date in case the IT is changed.
 *
 * To see what verifications are being performed, take a look at methods such as
 * {@link #assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(org.cloudbus.cloudsim.DatacenterBroker)}
 * and all the others that start with "assertThat". Those method names were
 * defined to give an exact notion of what is being tested.<p/>
 *
 * The IT uses the new VM listeners to get notified when a host is allocated,
 * deallocated for a given VM and when a VM fails to be created due to lack of
 * host resources. It also relies on the new CloudSim
 * {@link CloudSim#eventProcessingListener} listener to be notified every time
 * when any event is processed by CloudSim. By this way, it is possible to
 * verify, for instance, if the resource usage of a given host at a given time
 * is as expected.<p/>
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

    private final SimulationScenarioBuilder scenario;

    /**
     * A lambda function used by an {@link EventListener} 
     * that will be notified every time a Host is
     * allocated to a given VM. It tries to assert that the Host 0 was allocated
     * to the Vm 0 at the expected time.
     * @param time
     * @param vm
     * @param host
     */
    private void onHostAllocation(double time, Vm vm, Host host) {
        numberOfHostAllocations++;
        Log.printFormattedLine("# Host %s allocated to Vm %s at time %3.0f",
                host.getId(), vm.getId(), time);
        if (scenario.getFirstHostOfFirstDatacenter().equals(host)
                && scenario.getFirstVmOfTheFirstBroker().equals(vm)) {
            assertEquals("Host wasn't allocated to Vm at the expected time", 0, time, 0.2);
        }
    }

    /**
     * A lambda function used by an {@link EventListener} 
     * that will be notified every time a Host is
     * deallocated to a given VM. It tries to assert that the Host 0 was
     * deallocated to the Vm 0 at the expected time.
     * @param time
     * @param vm
     * @param host
     */
    private void onHostDeallocation(double time, Vm vm, Host host) {
        numberOfHostDeallocations++;
        Log.printFormattedLine(
                "# Vm %s moved/removed from Host %s at time %3.0f",
                vm.getId(), host.getId(), time);
        if (scenario.getFirstHostOfFirstDatacenter().equals(host) && scenario.getFirstVmOfTheFirstBroker().equals(vm)) {
            assertEquals("Vm wasn't removed from the Host at the expected time", 20, time, 0.2);
        }
    }

    /**
     * A lambda function used by an {@link EventListener} 
     * that will be notified every time a Vm failed to be created
     * due to lack of host resources. 
     * @param time
     * @param vm
     * @param datacenter
     */
    private void onVmCreationFailure(double time, Vm vm, Datacenter datacenter) {
        numberOfVmCreationFailures++;
        final int expectedVmId = 1;

        String msg = String.format(
                "Only Vm %d should had failed to be created due to lack of resources",
                expectedVmId);
        assertEquals(msg, expectedVmId, vm.getId());
    }

    /**
     * A lambda function used by an {@link EventListener} 
     * that will be notified every time a event is processed by {@link CloudSim}. 
     * @param time
     * @param cloudsim
     * @param evt
     */
    private void onEventProcessing(double time, CloudSim cloudsim, SimEvent evt) {
        Log.printFormattedLine("* onEventProcessing at time %3.0f: %s", time, evt);
        switch ((int) time) {
            case 10:
                assertEquals(200,
                        scenario.getFirstHostOfFirstDatacenter().getAvailableMips(), 0.1);
            break;
            case 20:
                assertEquals(200,
                        scenario.getFirstHostOfFirstDatacenter().getAvailableMips(), 0.1);
            break;
        }
    }
    
    
    /**
     * Considering there is only one Host and only 1 VM
     * where its cloudlets use a {@link UtilizationModelFull} for CPU utilization 
     * model, at any time, the amount of available Host CPU should be the same.
     * 
     * @param time
     * @param vm
     * @param host 
     */
    private void onUpdateVmProcessing(double time, Vm vm, Host host) {
        Log.printConcatLine(
            "- onUpdateVmProcessing at time ", time, " - vm: ", 
            vm.getId(), " host ", host.getId(), " available mips: ", host.getAvailableMips());
        assertEquals(200, host.getAvailableMips(), 0);
    }

    /**
     * Default constructor that instantiates and initializes the required
     * objects for the Integration Test.
     */
    public VmCreationFailureIntegrationTest() {
        CloudSim.init(1, Calendar.getInstance(), false);
        CloudSim.setOnEventProcessingListener((t,c,e) -> onEventProcessing(t,c,e));
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

        brokerBuilder.getVmBuilderForTheCreatedBroker()
                .setRam(512).setBw(1000)
                .setPes(1).setMips(1000).setSize(10000)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared())
                .setOnHostAllocationListener((t,v,h) -> onHostAllocation(t,v,h))
                .setOnHostDeallocationListener((t,v,h) -> onHostDeallocation(t,v,h))
                .setOnVmCreationFilatureListenerForAllVms((t,v,d) -> onVmCreationFailure(t,v,d))
                .setOnUpdateVmProcessing((t,v,h) -> onUpdateVmProcessing(t, v, h))
                /*try to createBroker 2 Vm where there is capacity to only one,
                 thus, just 1 will be created*/
                .createAndSubmitVms(2);

        brokerBuilder.getCloudletBuilderForTheCreatedBroker()
                .setLength(10000)
                .setUtilizationModelCpuRamAndBw(new UtilizationModelFull())
                .setPEs(1)
                .createAndSubmitCloudlets(2);
    }

    @Test
    public void integrationTest() {
        startSimulationAndWaitToStop();

        final DatacenterBrokerSimple broker = scenario.getBrokerBuilder().getBrokers().get(0);
        assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(broker);
        assertThatListenersWereCalledTheExpectedAmountOfTimes();

        printCloudletsExecutionResults(broker);
    }

    public void startSimulationAndWaitToStop() throws RuntimeException, NullPointerException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults(DatacenterBrokerSimple broker) {
        TableBuilderHelper.print(new TextTableBuilder(), broker.getCloudletReceivedList());
    }

    public void assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(DatacenterBrokerSimple broker) {
        /*The array of expected results for each broker cloudlet*/
        final ExpectedCloudletExecutionResults expectedResults[]
                = new ExpectedCloudletExecutionResults[]{
                    new ExpectedCloudletExecutionResults(10, 0, 10),
                    new ExpectedCloudletExecutionResults(10, 10, 20)
                };

        final List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
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
        assertEquals(results.getExpectedExecTime(), results.getCloudlet().getActualCPUTime(), 0.2);
        assertEquals(results.getExpectedStartTime(), results.getCloudlet().getExecStartTime(), 0.2);
        assertEquals(results.getExpectedFinishTime(), results.getCloudlet().getFinishTime(), 0.2);
        assertEquals(0, results.getCloudlet().getVmId(), 0);
        assertEquals("Cloudlet wasn't executed at the expected Datacenter",
                2, results.getCloudlet().getResourceId(), 0);
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
