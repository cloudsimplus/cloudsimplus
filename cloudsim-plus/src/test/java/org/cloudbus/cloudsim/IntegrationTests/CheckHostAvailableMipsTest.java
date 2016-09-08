package org.cloudbus.cloudsim.IntegrationTests;

import java.util.Calendar;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.schedulers.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * An Integration Test (IT) running a simulation scenario with 1 PM, 2 VMs
 * and 1 cloudlet in each VM. The cloudlets use a UtilizationModelFull for
 * CPU usage. The IT checks if the amount of available
 * CPU of the host is as expected along the simulation time.
 * It is created one broker for each VM and one VM finishes executing
 * prior to the other. By this way, the IT checks if the CPU used by the
 * finished VM is freed on the host. Creating the VMs for the same broker
 * doesn't make the finished VM to be automatically destroyed. 
 * In this case, only after all user VMs are finished that they are
 * destroyed in order to free resources.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class CheckHostAvailableMipsTest {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 5;
    private static final int NUMBER_OF_VMS = 2;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES/NUMBER_OF_VMS;
    private static final int CLOUDLET_PES = VM_PES;
    private static final int CLOUDLET_LENGTH = HOST_MIPS*10;
    private static final int NUMBER_OF_CLOUDLETS = NUMBER_OF_VMS;

    private SimulationScenarioBuilder scenario;
    private UtilizationModel utilizationModel;
    
    /**
     * A lambda function used by the {@link Host#setOnUpdateVmsProcessingListener(org.cloudbus.cloudsim.listeners.EventListener)}
     * that will be called every time a host updates the processing of its VMs.
     * It checks if the amount of available Host CPU is as expected,
     * every time a host updates the processing of all its VMs.
     * 
     * @param evt
     */
    private void onUpdateVmsProcessing(HostUpdatesVmsProcessingEventInfo evt) {
        double time = (int)evt.getTime();
        final double hostCapacity = HOST_MIPS * HOST_PES;
        final double usedHostMips = (NUMBER_OF_CLOUDLETS * CLOUDLET_PES * VM_MIPS 
                * utilizationModel.getUtilization(time));
        double expectedAvailableHostMips = hostCapacity - usedHostMips;
        
        /*After 10 seconds all VMs finish and 
        all host capacity will be free*/
        if(time > 10)
            expectedAvailableHostMips = hostCapacity;
        /*After 5 seconds, one VM finishes and
        its used capacity will be free*/
        else if(time > 5){
            expectedAvailableHostMips += VM_MIPS*VM_PES;
        }
        
        Log.printConcatLine(
            "- VMs processing at time ", time, " host ", evt.getHost().getId(), 
            " available mips: ", evt.getHost().getAvailableMips(), 
            " expected availability: ", expectedAvailableHostMips);
        
        assertEquals(
                String.format("Host available mips at time %.0f", time), 
                expectedAvailableHostMips, evt.getHost().getAvailableMips(), 0);
    }

    @Before
    public void setUp() {
        CloudSim.init(1, Calendar.getInstance(), false);
        utilizationModel = new UtilizationModelFull();
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().setSchedulingInterval(2).createDatacenter(
            new HostBuilder()
                .setVmSchedulerClass(VmSchedulerSpaceShared.class)
                .setRam(4000).setBw(400000)
                .setOnUpdateVmsProcessingListener((evt) -> onUpdateVmsProcessing(evt))
                .setPes(HOST_PES).setMips(HOST_MIPS)
                .createOneHost()
                .getHosts()
        );


        //create VMs and cloudlets for different brokers
        for(int i = 0; i < NUMBER_OF_VMS; i++){
            BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();
            brokerBuilder.getVmBuilder()
                    .setRam(1000).setBw(100000)
                    .setPes(VM_PES).setMips(VM_MIPS).setSize(50000)
                    .setCloudletScheduler(new CloudletSchedulerDynamicWorkload(VM_MIPS,VM_PES))
                    .createAndSubmitOneVm();

            final long cloudletLength = (i == 0 ? CLOUDLET_LENGTH : CLOUDLET_LENGTH/2);
            brokerBuilder.getCloudletBuilder()
                    .setLength(cloudletLength)
                    .setUtilizationModelCpu(utilizationModel)
                    .setPEs(CLOUDLET_PES)
                    .createAndSubmitOneCloudlet();
        }
    }

    @Test
    public void integrationTest() {
        startSimulationAndWaitToStop();
        scenario.getBrokerBuilder().getBrokers().stream().forEach(b->printCloudletsExecutionResults(b));
    }

    public void startSimulationAndWaitToStop() throws RuntimeException, NullPointerException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults(DatacenterBroker broker) {
        CloudletsTableBuilderHelper.print(
                new TextTableBuilder(broker.getName()), 
                broker.getCloudletsFinishedList());
    }

}
