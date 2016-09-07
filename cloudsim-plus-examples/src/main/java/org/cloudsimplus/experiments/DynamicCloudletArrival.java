package org.cloudsimplus.experiments;

import java.util.Calendar;
import java.util.Locale;
import org.cloudbus.cloudsim.builders.HostBuilder;
import org.cloudbus.cloudsim.builders.SimulationScenarioBuilder;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class DynamicCloudletArrival {
    private SimulationScenarioBuilder scenario;
    public DynamicCloudletArrival() {
        CloudSim.init(1, Calendar.getInstance(Locale.getDefault()), true);
        scenario = new SimulationScenarioBuilder();
        scenario.getDatacenterBuilder().createDatacenter(
                new HostBuilder().createHosts(1).getHosts()
        );
    }
    
    public static void main(String[] args) {
        new DynamicCloudletArrival();
    }
    
}
