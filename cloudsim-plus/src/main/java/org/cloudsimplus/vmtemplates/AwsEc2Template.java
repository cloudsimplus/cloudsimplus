package org.cloudsimplus.vmtemplates;

import com.google.gson.Gson;
import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Represents an
 * <a href="http://aws.amazon.com/ec2/">Amazon EC2 Instance</a> template.
 * This template contains actual configurations for VMs
 * available in <a href="http://aws.amazon.com/">Amazon Web Services</a>,
 * which is read from a JSON file.
 *
 * @author raysaoliveira
 * @see #getInstance(String)
 */
public class AwsEc2Template {
    private String name;
    private int cpus;
    private int memoryInMB;
    private double pricePerHour;
    private double maxNumberOfVmsForCustomer;

    /**
     * Default constructor used to create an {@link AwsEc2Template} instance.
     * If you want to get a template from a JSON file,
     * you shouldn't call the constructor directly.
     * Instead, use some methods such as the {@link #getInstance(String)}.
     */
    public AwsEc2Template(){}

    /**
     * A clone constructor which receives an {@link AwsEc2Template}
     * and creates a clone of it.
     * @param source the {@link AwsEc2Template} to be cloned
     */
    public AwsEc2Template(AwsEc2Template source){
        this.name = source.name;
        this.cpus = source.cpus;
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
    public static AwsEc2Template getInstance(final String jsonTemplateFilePath) throws FileNotFoundException {
        final FileReader fileReader = new FileReader(jsonTemplateFilePath);
        return new Gson().fromJson(fileReader, AwsEc2Template.class);
    }

    /**
     * Gets an AWS EC2 Instance from a JSON file inside the application's resource directory.
     * @param jsonFilePath the relative path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2Template getInstanceFromResourcesDir(final String jsonFilePath) throws FileNotFoundException {
        return getInstance(ResourceLoader.getResourcePath(AwsEc2Template.class, jsonFilePath));
    }

    public String getName() {return name; }

    public void setName(String name) {
        this.name = name;
    }

    public int getCpus() {
        return cpus;
    }

    public void setCpus(int cpus) {
        this.cpus = cpus;
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
    public AwsEc2Template setMaxNumberOfVmsForCustomer(double maxNumberOfVmsForCustomer) {
        this.maxNumberOfVmsForCustomer = maxNumberOfVmsForCustomer;
        return this;
    }

    @Override
    public String toString() {
        return "AwsEc2Template{name = " + name +
               ",  cpus = " + cpus +
               ",  memoryInMB = " + memoryInMB +
               ",  pricePerHour = " + pricePerHour +'}';
    }

    /**
     * A main method just to try the class implementation.
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        final AwsEc2Template template = AwsEc2Template.getInstanceFromResourcesDir("vmtemplates/aws/t2.nano.json");
        System.out.println(template);
    }
}
