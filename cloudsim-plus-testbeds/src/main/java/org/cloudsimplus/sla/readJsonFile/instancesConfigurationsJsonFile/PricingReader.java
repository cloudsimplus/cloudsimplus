package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

import com.google.gson.Gson;
import hostFaultInjection.HostFaultInjectionExperiment;
import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class was created to read a json file where is
 * the instance and configurations about this.
 *
 * Created by raysaoliveira on 01/06/17.
 */
public class PricingReader {

    private final AwsInstancesConfigurations configurations;


    public PricingReader(String slaFileName) throws FileNotFoundException {
        Gson gson = new Gson();
        this.configurations = gson.fromJson(
            new FileReader(slaFileName), AwsInstancesConfigurations.class);
    }

    public static void main(String[] args) throws FileNotFoundException {
        final String file = ResourceLoader.getResourcePath(HostFaultInjectionExperiment.class, "AwsEC2Prices.json");
        PricingReader reader = new PricingReader(file);
        for(InstancesAws m: reader.getInstancesConfigurations().getInstances()){
            System.out.println(m);
        }

        if(reader.getInstancesConfigurations().getInstances().isEmpty()){
            System.out.println("No instances found! \n");
        }
    }

    /**
     * @return the contract
     */
    public AwsInstancesConfigurations getInstancesConfigurations() {
        return configurations;
    }

}
