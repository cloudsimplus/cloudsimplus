package org.cloudsimplus.sla.awstemplates;

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
public class AwsEc2InstanceTemplate {
    private String instanceName;
    private int vCPU;
    private int memoryInMB;
    private double pricePerHour;
    private double maxNumberOfVmsForCustomer;

    public AwsEc2InstanceTemplate(){}

    /**
     * A clone constructor which receives an {@link AwsEc2InstanceTemplate}
     * and creates a clone of it.
     * @param source the {@link AwsEc2InstanceTemplate} to be cloned
     */
    public AwsEc2InstanceTemplate(AwsEc2InstanceTemplate source){
        this.instanceName = source.instanceName;
        this.vCPU = source.vCPU;
        this.memoryInMB = source.memoryInMB;
        this.pricePerHour = source.pricePerHour;
        this.maxNumberOfVmsForCustomer = source.maxNumberOfVmsForCustomer;
    }

    /**
     * Gets an AWS EC2 Instance from a JSON file.
     * @param jsonTemplateFilePath the full path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2InstanceTemplate getInstance(final String jsonTemplateFilePath) throws FileNotFoundException {
        final FileReader fileReader = new FileReader(jsonTemplateFilePath);
        return new Gson().fromJson(fileReader, AwsEc2InstanceTemplate.class);

    }

    /**
     * Gets an AWS EC2 Instance from a JSON file inside the application's resource directory.
     * @param jsonFilePath the relative path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2InstanceTemplate getInstanceFromResourcesDir(final String jsonFilePath) throws FileNotFoundException {
        return getInstance(ResourceLoader.getResourcePath(AwsEc2InstanceTemplate.class, jsonFilePath));
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

    /**
     * Gets the maximum number of VMs which can be created with this configuration
     * for a specific customer, considering the maximum price
     * the customer expects to pay hourly for all his/her running VMs.
     *
     * <p>This is not a field inside the JSON file and doesn't in fact represent
     * a AWS EC2 Instance attribute. It's a value which may be computed
     * externally and assigned to the attribute.
     * It's usage is optional and it's default value is zero.</p>
     * @return
     */
    public double getMaxNumberOfVmsForCustomer() {
        return maxNumberOfVmsForCustomer;
    }

    /**
     * Sets the maximum number of VMs which can be created with this configuration
     * for a specific customer, considering the maximum price
     * the customer expects to pay hourly for all his/her running VMs.
     *
     * <p>This is not a field inside the JSON file and doesn't in fact represent
     * a AWS EC2 Instance attribute. It's a value which may be computed
     * externally and assigned to the attribute.
     * It's usage is optional and it's default value is zero.</p>
     * @param maxNumberOfVmsForCustomer  the maximum number of VMs to set
     */
    public AwsEc2InstanceTemplate setMaxNumberOfVmsForCustomer(double maxNumberOfVmsForCustomer) {
        this.maxNumberOfVmsForCustomer = maxNumberOfVmsForCustomer;
        return this;
    }
}
