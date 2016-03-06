package org.cloudbus.cloudsim.IntegrationTests;

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
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * An Integration Test (IT) to check a simulation scenario 
 * with 1 PM that has capacity to host just one of the user's VMs.
 * Two cloudlets are submitted to the same VM and run using the SpaceShared 
 * CloudletScheduler.<p/>
 * 
 * The Integration Test checks if:
 * <ul>
 *  <li>cloudlets start, finish and execution time</li>
 *  <li>the VM that was assigned to the cloudlets</li>
 *  <li>if a given VM failed to be created due to lack of host resources</li>
 *  <li>the time a host was allocated to a given VM</li>
 *  <li>the time a host was deallocated for a given VM</li>
 * </ul>
 * 
 * The IT uses the new VM listeners to get notified when a host
 * is allocated, deallocated for a given VM
 * and when a VM fails to be created due to lack of host resources.<p/>
 * 
 * <i>See the profile section in the pom.xml to details of how to run all tests, 
 * including Functional/Integration Tests in this package.</i>
 * 
 * @see Vm#setOnHostAllocationListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * @see Vm#setOnHostDeallocationListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * @see Vm#setOnVmCreationFailureListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * 
 * @author Manoel Campos da Silva Filho
 */

public class VmCreationFailureIntegrationTest {
    private final SimulationScenarioBuilder scenario;
    
    private final EventListener<Vm, Host> onHostAllocationListener = new EventListener<Vm, Host>() {
        @Override
        public void update(double time, Vm vm, Host host) {
            Log.printFormattedLine("#Host %s allocated to Vm %s at time %.2f", host.getId(), vm.getId(), time);
            hostToVmAllocationTime = time;
        }
    };
    
    private final EventListener<Vm, Host> onHostDeallocationListener = new EventListener<Vm, Host>() {
        @Override
        public void update(double time, Vm vm, Host host) {
            Log.printFormattedLine("#Vm %s moved/removed from Host %s at time %.2f", vm.getId(), host.getId(), time);
            hostToVmDeallocationTime = time;
        }
    };
    
    private final EventListener<Vm, Integer> onVmCreationFailureListener = new EventListener<Vm, Integer>(){
        @Override
        public void update(double time, Vm vm, Integer datacenterId) {
            if(vm.getId() == 1)
                vm1FailedToBeCreatedDueToLackOfCPU = true;
        }
    };
    
    private double hostToVmAllocationTime = -1;
    private double hostToVmDeallocationTime = -1;
    private boolean vm1FailedToBeCreatedDueToLackOfCPU = false;
    
    public VmCreationFailureIntegrationTest() {
        CloudSim.init(1,  Calendar.getInstance(), false);
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().createDatacenter(
            new HostBuilder()
                .setDefaultVmSchedulerClass(VmSchedulerTimeShared.class)
                .setDefaultRam(2048).setDefaultBw(10000)
                .setDefaultPEs(1).setDefaultMIPS(1000)
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
            //try to createBroker 2 Vm where there is capacity to only one
            .createAndSubmitVms(2);
        
        brokerBuilder.getCloudletBuilderForTheCreatedBroker()
            .setDefaultLength(10000)
            .setDefaultPEs(1)
            .createAndSubmitCloudlets(2);
    }
        
    @Test
    public void testTwoCloudletInOneVmOneHostAndOneDatacenter(){
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
        
        DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        
        int i = 0;
        final double exec_time[] = new double[]{10,10};
        final double start_time[] = new double[]{0,10};
        final double finish_time[] = new double[]{10,20};
        List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
        for(Cloudlet cloudlet: cloudletList){
            assertEquals(exec_time[i], cloudlet.getActualCPUTime(), 0.2);
            assertEquals(start_time[i], cloudlet.getExecStartTime(), 0.2);
            assertEquals(finish_time[i], cloudlet.getFinishTime(), 0.2);
            assertEquals(0, cloudlet.getVmId(), 0);
            assertEquals("Cloudlet wasn't executed at the expected Datacenter", 
                    2, cloudlet.getResourceId(), 0);
            assertEquals(Cloudlet.Status.SUCCESS, cloudlet.getStatus());
            i++;
        }
        
        assertTrue("VM 1 should has failed to be created due to lack of CPU", vm1FailedToBeCreatedDueToLackOfCPU);
        assertEquals("Host wasn't allocated to Vm at the expected time", 
                0, hostToVmAllocationTime, 0.2);
        assertEquals("Vm wasn't removed from the Host at the expected time", 
                20, hostToVmDeallocationTime, 0.2);
        
        TableBuilderHelper.print(new TextTableBuilder(), cloudletList);
    }
}
