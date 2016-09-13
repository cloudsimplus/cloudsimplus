package org.cloudsimplus.experiments;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.builders.BrokerBuilderDecorator;
import org.cloudbus.cloudsim.builders.CloudletBuilder;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class DynamicCloudletArrival {
    private final SimulationScenarioBuilder scenario;
    private static final int HOST_PES = 4;
    private static final double HOST_MIPS = 1000;
    private static final int VM_PES = 1;
    private static final double VM_MIPS = HOST_MIPS;
    private static final int CLOUDLETS_NUMBER = 2;
    private static final int CLOUDLET_PES = (int)Math.ceil(VM_PES/(double)CLOUDLETS_NUMBER);
    private static final long CLOUDLET_LENGTH = (long)VM_MIPS*10;
    
    public DynamicCloudletArrival() {
        CloudSim.init(1, Calendar.getInstance(Locale.getDefault()), true);
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder()
            .createDatacenter(
                new HostBuilder()
                    .setPes(HOST_PES)
                    .setMips(HOST_MIPS)
                    .setVmSchedulerClass(VmSchedulerTimeShared.class)
                    .createHosts(1).getHosts()
            );
        
        BrokerBuilderDecorator brokerBuilder = scenario.getBrokerBuilder().createBroker();
        brokerBuilder.getVmBuilder()
            .setPes(VM_PES)
            .setMips(VM_MIPS)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .createAndSubmitOneVm();
        
        CloudletBuilder cloudletBuilder = brokerBuilder.getCloudletBuilder()
            .setPEs(CLOUDLET_PES)
            .setLength(CLOUDLET_LENGTH)
            .createCloudlets(CLOUDLETS_NUMBER);
        
        int i = -5;
        for(Cloudlet c: cloudletBuilder.getCloudlets()){
            c.setSubmissionDelay(i+=5);
        }
        cloudletBuilder.submitCloudlets();
        
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
        List<Cloudlet> newList = brokerBuilder.getBroker().getCloudletsFinishedList();
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), newList);
    }
    
    public static void main(String[] args) {
        new DynamicCloudletArrival();
    }
    
}
