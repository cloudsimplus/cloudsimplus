package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

/**
 * This class represents the instances aws.
 *
 * Created by raysaoliveira on 01/06/17.
 */
public class InstancesAws {

    private static final String T2_NANO = "t2.nano";
    private static final String T2_MICRO = "t2.micro";
    private static final String T2_MEDIUM = "t2.medium";
    private static final String T2_LARGE = "t2.large";
    private static final String M4_LARGE = "m4.large";
    private static final String M4_2xLARGE = "m4.2xlarge";
    private static final String P2_xLARGE = "p2.xlarge";

    private String instanceName;
    private int vCPU;
    private int memoryInMB;
    private double pricePerHour;

    public static String getP2xlarge() {
        return P2_xLARGE;
    }

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

    /**
     * Checks if this is a t2.nano instance.
     *
     * @return
     */
    public boolean isT2_Nano() {
        return this.instanceName.trim().equals("T2_NANO");
    }

    /**
     * Checks if this is a t2.micro instance.
     *
     * @return
     */
    public boolean isT2_Micro() {
        return this.instanceName.trim().equals("T2_MICRO");
    }

    /**
     * Checks if this is a t2.medium instance.
     *
     * @return
     */
    public boolean isT2_Medium() {
        return this.instanceName.trim().equals("T2_MEDIUM");
    }

    /**
     * Checks if this is a t2.large instance.
     *
     * @return
     */
    public boolean isT2_Large() {
        return this.instanceName.trim().equals("T2_LARGE");
    }

    /**
     * Checks if this is a m4.large instance.
     *
     * @return
     */
    public boolean isM4_Large() {
        return this.instanceName.trim().equals("M4_LARGE");
    }

    /**
     * Checks if this is a m4.2xlarge instance.
     *
     * @return
     */
    public boolean isM4_2xLarge() {
        return this.instanceName.trim().equals("M4_2xLARGE");
    }

    /**
     * Checks if this is a p2.xlarge instance.
     *
     * @return
     */
    public boolean isP2_xLarge() {
        return this.instanceName.trim().equals("P2_xLARGE");
    }
}
