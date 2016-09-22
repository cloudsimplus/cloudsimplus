package org.cloudbus.cloudsim.IntegrationTests;

import java.util.Calendar;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
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
 * An Integration Test (IT) running a simulation scenario with 1 PM of 2 PEs, 
 * 1 VMs of 2 PEs and 4 cloudlet in that VM. 
 * The VM uses a {@link CloudletSchedulerTimeShared}. As the number of Cloudlets
 * is the double of VM's PEs, all cloudlets will spend the double of the
 * time to finish, because they will concur for CPU.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class CloudletSchedulerTimeSharedWithMoreCloudletsThanPEs {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 2;
    private static final int NUMBER_OF_VMS = 1;
    private static final int VM_MIPS = HOST_MIPS;
    private static final int VM_PES = HOST_PES;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = VM_MIPS*10;
    private static final int NUMBER_OF_CLOUDLETS = VM_PES*2;

    private SimulationScenarioBuilder scenario;
    private UtilizationModel utilizationModel;
    private DatacenterBroker broker;
    
    @Before
    public void setUp() {
        CloudSim.init(1, Calendar.getInstance(), false);
        utilizationModel = new UtilizationModelFull();
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().setSchedulingInterval(2).createDatacenter(
            new HostBuilder()
                .setVmSchedulerClass(VmSchedulerSpaceShared.class)
                .setRam(4000).setBw(400000)
                .setPes(HOST_PES).setMips(HOST_MIPS)
                .createOneHost()
                .getHosts()
        );


        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();
        broker = brokerBuilder.getBroker();
        brokerBuilder.getVmBuilder()
            .setRam(1000).setBw(100000)
            .setPes(VM_PES).setMips(VM_MIPS).setSize(50000)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .createAndSubmitVms(NUMBER_OF_VMS);

        brokerBuilder.getCloudletBuilder()
            .setLength(CLOUDLET_LENGTH)
            .setUtilizationModelCpu(utilizationModel)
            .setPEs(CLOUDLET_PES)
            .createAndSubmitCloudlets(NUMBER_OF_CLOUDLETS);
    }

    @Test
    public void integrationTest() {
        startSimulationAndWaitToStop();
        printCloudletsExecutionResults(broker);
        
        final double time = 20;
        for(Cloudlet c: broker.getCloudletsFinishedList()){
            assertEquals(String.format(
                "Cloudlet %d doesn't have the expected finish time.", 
                c.getId(), time), 
                time, c.getFinishTime(), 0.2);

            assertEquals(String.format(
                "Cloudlet %d doesn't have the expected exec time.", 
                c.getId(), time), 
                time, c.getActualCPUTime(), 0.2);
        }
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
