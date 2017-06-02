package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

import com.google.gson.Gson;
import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Represents a JSON template file containing the configurations for an
 * <a href="http://aws.amazon.com/ec2/"> Amazon EC2 Instance </a>, which is
 * a Virtual Machine with specific configurations available
 * in <a href="http://aws.amazon.com/">Amazon Web Services</a>.
 *
 * @author raysaoliveira
 * @see #getInstance(String)
 */
public class AwsEc2Instance {
    private String instanceName;
    private int vCPU;
    private int memoryInMB;
    private double pricePerHour;

    /**
     * Gets an AWS EC2 Instance from a JSON file.
     * @param jsonFilePath the path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2Instance getInstance(final String jsonFilePath) throws FileNotFoundException {
        final FileReader fileReader = new FileReader(jsonFilePath);
        return new Gson().fromJson(fileReader, AwsEc2Instance.class);

    }

    /**
     * Gets an AWS EC2 Instance from a JSON file inside the application's resource directory.
     * @param jsonFilePath the path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2Instance getInstanceFromResourcesDir(final String jsonFilePath) throws FileNotFoundException {
        return getInstance(ResourceLoader.getResourcePath(AwsEc2Instance.class, jsonFilePath));
    }

    public String getName() {return instanceName; }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public int getvCPU() {
        return vCPU;
    }

    public void setvCPU(int vCPU) {
        this.vCPU = vCPU;
    }

    public int getMemoryInMB() {
        return memoryInMB;
    }

    public void setMemoryInMB(int memoryInMB) {
        this.memoryInMB = memoryInMB;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

}
