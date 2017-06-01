package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

/**
 * Represents a json template file containing the configurations for an
 * <a href="http://aws.amazon.com/ec2/"> Amazon EC2 Instance </a>, which is
 * a Virtual Machine with specific configurations available
 * in <a href="http://aws.amazon.com/">Amazon Web Services</a>.
 *
 * Created by raysaoliveira on 01/06/17.
 */
public class AwsEC2Instance {

    private String instanceName;
    private int vCPU;
    private int memoryInMB;
    private double pricePerHour;

    public String getInstanceName() {
        return instanceName;
    }

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
