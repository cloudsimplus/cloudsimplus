package org.cloudbus.cloudsim.IntegrationTests;

import java.util.Calendar;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.schedulers.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelArithmeticProgression;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * An Integration Test (IT) running a simulation scenario with 1 PM with 2 VMs
 * and 2 cloudlets in each VM. It checks if the amount of available
 * CPU of the host is as expected along the simulation time.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class CheckHostAvailableMips {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 4;
    private static final int NUMBER_OF_VMS = HOST_PES/2;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES/2;
    private static final int CLOUDLET_PES = VM_PES;
    private static final int CLOUDLET_LENGTH = HOST_MIPS*10;
    private static final int NUMBER_OF_CLOUDLETS = NUMBER_OF_VMS;

    private final SimulationScenarioBuilder scenario;
    private final UtilizationModelArithmeticProgression utilizationModel;
    
    /**
     * Checks if the amount of available Host CPU is as expected
     * for the given time.
     * 
     * @param time
     * @param vm
     * @param host 
     */
    private void onUpdateVmProcessing(double time, Vm vm, Host host) {
        final double totalHostMips = HOST_MIPS*HOST_PES;
        final double expectedAvailableHostMips = 
                totalHostMips -
                (NUMBER_OF_CLOUDLETS*CLOUDLET_PES*VM_MIPS*utilizationModel.getUtilization(time));
        Log.printConcatLine(
            "- onUpdateVmProcessing at time ", time, " - vm: ", 
            vm.getId(), " host ", host.getId(), 
            " available mips: ", host.getAvailableMips(), " expected availability: ", expectedAvailableHostMips);
        
        assertEquals("The amount of Host available MIPS was not as expected." +
            "WARNING: This IT was not accordingly validated to assure that the " +
            "expected value is correct. The test was left in this status to remember "
            + "that it has to be tested how UtilizationModels that define dynamic "
            + "cloudlet CPU utilization affect host CPU availability", 
                expectedAvailableHostMips, host.getAvailableMips(), 0);
    }

    /**
     * Default constructor that instantiates and initializes the required
     * objects for the Integration Test.
     */
    public CheckHostAvailableMips() {
        CloudSim.init(1, Calendar.getInstance(), false);
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().setSchedulingInterval(2).createDatacenter(new HostBuilder()
                .setVmSchedulerClass(VmSchedulerSpaceShared.class)
                .setRam(4000).setBw(400000)
                .setPes(HOST_PES).setMips(HOST_MIPS)
                .createOneHost()
                .getHosts()
        );

        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();

        brokerBuilder.getVmBuilderForTheCreatedBroker()
                .setRam(1000).setBw(100000)
                .setPes(VM_PES).setMips(VM_MIPS).setSize(50000)
                .setCloudletScheduler(new CloudletSchedulerDynamicWorkload(VM_MIPS,VM_PES))
                .setOnUpdateVmProcessing((t,v,h) -> onUpdateVmProcessing(t, v, h))
                .createAndSubmitVms(NUMBER_OF_VMS);

        utilizationModel = new UtilizationModelArithmeticProgression(0.1, 0.5);
        brokerBuilder.getCloudletBuilderForTheCreatedBroker()
                .setLength(CLOUDLET_LENGTH)
                .setUtilizationModelCpu(utilizationModel)
                .setPEs(CLOUDLET_PES)
                .createAndSubmitCloudlets(NUMBER_OF_CLOUDLETS);
    }

    @Test
    public void integrationTest() {
        startSimulationAndWaitToStop();

        final DatacenterBrokerSimple broker = scenario.getBrokerBuilder().getBrokers().get(0);

        printCloudletsExecutionResults(broker);
    }

    public void startSimulationAndWaitToStop() throws RuntimeException, NullPointerException {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
    }

    public void printCloudletsExecutionResults(DatacenterBrokerSimple broker) {
        TableBuilderHelper.print(new TextTableBuilder(), broker.getCloudletReceivedList());
    }

}
