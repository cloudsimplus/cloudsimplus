package org.cloudbus.cloudsim.IntegrationTests;

import org.cloudbus.cloudsim.util.UpdatesCountEventListener;
import org.cloudbus.cloudsim.util.ExpectedCloudletExecutionResults;
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * An Integration Test (IT) to check a simulation scenario 
 * with 1 PM that has capacity to host just 1 of the user's VMs.
 * Two cloudlets are submitted to the same VM and run using the SpaceShared 
 * CloudletScheduler.<p/>
 * 
 * The Integration Test performs several tests that aren't describe
 * here in order to avoid the documentation being out-of-date
 * in case the IT is changed.
 * 
 * To see what verifications are being performed, take a look at 
 * methods such as {@link #assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(org.cloudbus.cloudsim.DatacenterBroker)}
 * and all the others that start with "assertThat".
 * Those method names were defined to give an exact notion of what is being tested.<p/>
 * 
 * The IT uses the new VM listeners to get notified when a host
 * is allocated, deallocated for a given VM
 * and when a VM fails to be created due to lack of host resources.
 * It also relies on the new CloudSim {@link CloudSim#eventProcessingListener} listener
 * to be notified every time when any event is processed by CloudSim.
 * By this way, it is possible to verify, for instance, if the resource usage
 * of a given host at a given time is as expected.<p/>
 * 
 * <i><b>NOTE</b>:See the profile section in the pom.xml for details of how to run all tests, 
 * including Functional/Integration Tests in this package.</i>
 * 
 * @see Vm#setOnHostAllocationListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * @see Vm#setOnHostDeallocationListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * @see Vm#setOnVmCreationFailureListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * @see CloudSim#setEventProcessingListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * 
 * @author Manoel Campos da Silva Filho
 */
public class VmCreationFailureIntegrationTest {
    private final SimulationScenarioBuilder scenario;
    
    /**
     * A {@link EventListener} that will be notified every time
     * a Host is allocated to a given VM.
     * It tries to assert that the Host 0 was allocated to the Vm 0 at the expected time.
     */
    private final UpdatesCountEventListener<Vm, Host> onHostAllocationListener = new UpdatesCountEventListener<Vm, Host>() {
        @Override
        public void update(double time, Vm vm, Host host) {
            super.update(time, vm, host);
            
            Log.printFormattedLine("# Host %s allocated to Vm %s at time %3.0f", 
                    host.getId(), vm.getId(), time);
            assertThatGivenHostWasAllocatedToGivenVmAtTheExpectedTime(host, vm, time);
        }

        public void assertThatGivenHostWasAllocatedToGivenVmAtTheExpectedTime(Host host, Vm vm, double time) {
            if(getFirstHostOfFirstDatacenter().equals(host) && 
            getFirstVmOfTheFirstBroker().equals(vm)) {
                assertEquals("Host wasn't allocated to Vm at the expected time", 0, time, 0.2);
            }
        }
    };

    public Host getFirstHostOfFirstDatacenter() {
        return scenario.getDatacenterBuilder().getDatacenters().get(0).getHostList().get(0);
    }
    
    /**
     * A {@link EventListener} that will be notified every time
     * a Host is deallocated to a given VM.
     * It tries to assert that the Host 0 was deallocated to the Vm 0 at the expected time.
     */
    private final UpdatesCountEventListener<Vm, Host> onHostDeallocationListener = new UpdatesCountEventListener<Vm, Host>() {
        @Override
        public void update(double time, Vm vm, Host host) {
            super.update(time, vm, host);
            Log.printFormattedLine(
                    "# Vm %s moved/removed from Host %s at time %3.0f", 
                    vm.getId(), host.getId(), time);
            assertThatGivenVmWasRemovedFromGivenHostAtTheExpectedTime(host, vm, time);
        }
    };

    public void assertThatGivenVmWasRemovedFromGivenHostAtTheExpectedTime(Host host, Vm vm, double time) {
        if(getFirstHostOfFirstDatacenter().equals(host) && getFirstVmOfTheFirstBroker().equals(vm)){
            assertEquals("Vm wasn't removed from the Host at the expected time", 20, time, 0.2);
        }
    }

    public Vm getFirstVmOfTheFirstBroker() {
        return scenario.getBrokerBuilder().getBrokers().get(0).getVmList().get(0);
    }

    private final UpdatesCountEventListener<Vm, Integer> onVmCreationFailureListener = new UpdatesCountEventListener<Vm, Integer>(){
        @Override
        public void update(double time, Vm vm, Integer datacenterId) {
            super.update(time, vm, datacenterId);
            final int expectedVmId = 1;
            
            String msg = String.format(
                "Only Vm %d should had failed to be created due to lack of resources",
                expectedVmId);
            assertEquals(msg, expectedVmId, vm.getId());
        }
    };
    
    private final EventListener<CloudSim, SimEvent> eventProcessingListener = new EventListener<CloudSim, SimEvent>(){
        @Override
        public void update(double time, CloudSim observed, SimEvent data) {
            Log.printFormattedLine("* Event processed at time %3.0f: %s", time, data);
            switch((int)time){
                case 10:
                    assertEquals(200, getFirstHostOfFirstDatacenter().getAvailableMips(), 0.1);
                break;
                case 20:
                    assertEquals(200, getFirstHostOfFirstDatacenter().getAvailableMips(), 0.1);
                break;
            }            
        }
    };
    
    /**
     * Default constructor that instantiates and initializes 
     * the required objects for the Integration Test. 
     */
    public VmCreationFailureIntegrationTest() {
        CloudSim.init(1,  Calendar.getInstance(), false);
        CloudSim.setEventProcessingListener(eventProcessingListener);
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().createDatacenter(
            new HostBuilder()
                .setDefaultVmSchedulerClass(VmSchedulerTimeShared.class)
                .setDefaultRam(2048).setDefaultBw(10000)
                .setDefaultPEs(1).setDefaultMIPS(1200)
                .createOneHost()
                .getHosts()
        );
        
        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();
        
        brokerBuilder.getVmBuilderForTheCreatedBroker()
            .setDefaultRAM(512).setDefaultBW(1000)
            .setDefaultPEs(1).setDefaultMIPS(1000).setDefaultSize(10000)
            .setDefaultCloudletScheduler(CloudletSchedulerSpaceShared.class)
            .setDefaultOnHostAllocationListener(onHostAllocationListener)
            .setDefaultOnHostDeallocationListener(onHostDeallocationListener)
            .setOnVmCreationFilatureListenerForAllVms(onVmCreationFailureListener) 
            /*try to createBroker 2 Vm where there is capacity to only one,
              thus, just 1 will be created*/
            .createAndSubmitVms(2);
        
        brokerBuilder.getCloudletBuilderForTheCreatedBroker()
            .setDefaultLength(10000)
            .setDefaultPEs(1)
            .createAndSubmitCloudlets(2);
    }
        
    @Test
    public void testTwoCloudletInOneVmOneHostAndOneDatacenter(){
        startSimulationAndWaitToStop();

        final DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(broker);
        assertThatListenersWereCalledTheExpectedAmountOfTimes(); 
        
        printCloudletsExecutionResults(broker);
    }

    public void startSimulationAndWaitToStop() throws RuntimeException, NullPointerException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults(DatacenterBroker broker) {
        TableBuilderHelper.print(new TextTableBuilder(), broker.getCloudletReceivedList());
    }

    public void assertThatBrokerCloudletsHaveTheExpectedExecutionTimes(DatacenterBroker broker) {
        /*The array of expected results for each broker cloudlet*/
        final ExpectedCloudletExecutionResults expectedResults[] =
                new ExpectedCloudletExecutionResults[]{
                    new ExpectedCloudletExecutionResults(10,  0, 10),
                    new ExpectedCloudletExecutionResults(10, 10, 20)
                };
        
        final List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
        int i = -1;
        for(Cloudlet cloudlet: cloudletList){
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
                1, this.onHostAllocationListener.getUpdatesCount());
        assertEquals("The number of times a Host was deallocated to a VM isn't as expected", 
                1, this.onHostDeallocationListener.getUpdatesCount());
        assertEquals(
                "The number of times a Vm failed to be created due to lack of resources wasn't as expected", 
                1, this.onVmCreationFailureListener.getUpdatesCount());
    }
}
