package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

import com.google.gson.Gson;
import hostFaultInjection.HostFaultInjectionExperiment;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.vms.Vm;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Reads a json template file containing the configurations for
 * an <a href="http://aws.amazon.com/ec2/">Amazon EC2 Instance</a>,
 * and then allow creating a {@link Vm} with such a configuration.
 * <p>
 * Created by raysaoliveira on 01/06/17.
 */
public class AwsEC2InstanceReader {

    private final AwsEC2Instance instance;


    public AwsEC2InstanceReader(String slaFileName) throws FileNotFoundException {
        Gson gson = new Gson();
        this.instance = gson.fromJson(
            new FileReader(slaFileName), AwsEC2Instance.class);
    }

    public static void main(String[] args) throws FileNotFoundException {
        final String file = ResourceLoader.getResourcePath(HostFaultInjectionExperiment.class, "AwsEC2Prices.json");
        AwsEC2InstanceReader reader = new AwsEC2InstanceReader(file);
        System.out.println(reader.getAwsEC2Instance().getInstanceName());


        if (reader.getAwsEC2Instance().getInstanceName().isEmpty()) {
            System.out.println("No instances found! \n");
        }
    }

    /**
     * @return the contract
     */
    public AwsEC2Instance getAwsEC2Instance() {
        return instance;
    }

    public Vm createVm() {
        return (Vm) this;
    }

}
