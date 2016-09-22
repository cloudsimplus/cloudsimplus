package org.cloudbus.cloudsim.IntegrationTests;

import java.util.Calendar;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Host;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelArithmeticProgression;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * An Integration Test (IT) running a simulation scenario with 1 PM, 2 VMs
 * and 2 cloudlets in each VM. It checks if the amount of available
 * CPU of the host is as expected along the simulation time.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class CheckHostAvailableMipsDynamicUtilizationTest {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 2;
    private static final int NUMBER_OF_VMS = HOST_PES;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES/NUMBER_OF_VMS;
    private static final int CLOUDLET_PES = VM_PES;
    private static final int CLOUDLET_LENGTH = HOST_MIPS*10;
    private static final int NUMBER_OF_CLOUDLETS = 2;

    private SimulationScenarioBuilder scenario;
    private UtilizationModelArithmeticProgression utilizationModel;
    
    /**
     * A lambda function used by the {@link Host#setOnUpdateVmsProcessingListener(org.cloudbus.cloudsim.listeners.EventListener)}
     * that will be called every time a host updates the processing of its VMs.
     * It checks if the amount of available Host CPU is as expected,
     * every time a host updates the processing of all its VMs.
     * 
     * @param evt 
     */
    private void onUpdateVmsProcessing(HostUpdatesVmsProcessingEventInfo evt) {
        final double expectedAvailableHostMips = 
               HOST_MIPS * HOST_PES * utilizationModel.getUtilization(evt.getTime());
        
        Log.printConcatLine(
            "- onUpdateVmProcessing at time ", evt.getTime(), " host ", evt.getHost().getId(), 
            " available mips: ", evt.getHost().getAvailableMips(), 
            " expected availability: ", expectedAvailableHostMips);
        
        assertEquals("The amount of Host available MIPS was not as expected.", 
                 expectedAvailableHostMips, evt.getHost().getAvailableMips(), 0);
    }

    @Before
    public void setUp() {
        CloudSim.init(1, Calendar.getInstance(), false);
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().setSchedulingInterval(2).createDatacenter(
                new HostBuilder()
                    .setVmSchedulerClass(VmSchedulerSpaceShared.class)
                    .setRam(4000).setBw(400000)
                    .setPes(HOST_PES).setMips(HOST_MIPS)
                    .setOnUpdateVmsProcessingListener((evt) -> onUpdateVmsProcessing(evt))
                    .createOneHost()
                    .getHosts()
        );

        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();

        brokerBuilder.getVmBuilder()
                .setRam(1000).setBw(100000)
                .setPes(VM_PES).setMips(VM_MIPS).setSize(50000)
                .setCloudletScheduler(new CloudletSchedulerDynamicWorkload(VM_MIPS,VM_PES))
                .createAndSubmitVms(NUMBER_OF_VMS);

        utilizationModel = new UtilizationModelArithmeticProgression(0.0, 0.25);
        brokerBuilder.getCloudletBuilder()
                .setLength(CLOUDLET_LENGTH)
                .setUtilizationModelCpu(utilizationModel)
                .setPEs(CLOUDLET_PES)
                .createAndSubmitCloudlets(NUMBER_OF_CLOUDLETS);
    }

    @Test @Ignore("WARNING: It has to be checked if it is really required to use the "
                + " PowerDatacenter, PowerHostUtilizationHistory, PowerVm"
                + " and CloudletSchedulerDynamicWorkload to make the host CPU usage"
                + " to be correctly updated.")
    public void integrationTest() {
        startSimulationAndWaitToStop();
        DatacenterBroker broker = scenario.getBrokerBuilder().getBrokers().get(0);
        printCloudletsExecutionResults(broker);
    }

    public void startSimulationAndWaitToStop() throws RuntimeException, NullPointerException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults(DatacenterBroker broker) {
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), broker.getCloudletsFinishedList());
    }

}
