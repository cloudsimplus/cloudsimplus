/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.vmtemplates;

import com.google.gson.Gson;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.vms.Vm;

import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents an
 * <a href="http://aws.amazon.com/ec2/">Amazon EC2 VM Instance</a> template.
 * This class enables reading a template from a JSON file, containing actual configurations for VMs
 * available in <a href="http://aws.amazon.com/">Amazon Web Services</a>.
 * Such templates can be used to create {@link Vm} instances.
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 * @see #getInstance(String)
 */
public class AwsEc2Template implements Comparable<AwsEc2Template> {
    public static final AwsEc2Template NULL = new AwsEc2Template();

    private Path path;
    private String name;
    private int cpus;
    private int memoryInMB;
    private double pricePerHour;
    private String region;

    /**
     * Default constructor used to create an {@link AwsEc2Template} instance.
     * If you want to get a template from a JSON file,
     * you shouldn't call the constructor directly.
     * Instead, use some methods such as the {@link #getInstance(String)}.
     *
     * <p>This constructor is just provided to enable the {@link Gson} object
     * to use reflection to instantiate a AwsEc2Template.</p>
     * @see #getInstance(String)
     */
    public AwsEc2Template(){
        super();
    }

    /**
     * A clone constructor which receives an {@link AwsEc2Template}
     * and creates a clone of it.
     * @param source the {@link AwsEc2Template} to be cloned
     */
    public AwsEc2Template(final AwsEc2Template source){
        this.name = source.name;
        this.cpus = source.cpus;
        this.region = source.region;
        this.memoryInMB = source.memoryInMB;
        this.pricePerHour = source.pricePerHour;
        this.path = Paths.get(source.path.toUri());
    }

    /**
     * Instantiates an AWS EC2 Instance from a JSON file.
     *
     * @param jsonFilePath the full path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @see #getInstance(String)
     */
    public AwsEc2Template(final String jsonFilePath) {
        this(getInstanceInternal(jsonFilePath, ResourceLoader.newInputStreamReader(jsonFilePath)));
    }

    /**
     * Gets an AWS EC2 Instance from a JSON file inside the <b>application's resource directory</b>.
     * Use the available constructors if you want to load a file outside the resource directory.
     *
     * @param jsonFilePath the <b>relative path</b> to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2Template getInstance(final String jsonFilePath) {
        final InputStreamReader reader = ResourceLoader.newInputStreamReader(jsonFilePath, AwsEc2Template.class);
        return getInstanceInternal(jsonFilePath, reader);
    }

    /**
     * Gets an AWS EC2 Instance from a JSON file.
     * @param jsonFilePath the <b>relative path</b> to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @param reader a {@link InputStreamReader} to read the file
     * @return the AWS EC2 Instance from the JSON file
     */
    private static AwsEc2Template getInstanceInternal(final String jsonFilePath, final InputStreamReader reader) {
        final AwsEc2Template template = new Gson().fromJson(reader, AwsEc2Template.class);
        template.path = Paths.get(jsonFilePath);
        return template;
    }

    /**
     * Gets the name of the template.
     * @return
     */
    public String getName() {return name; }

    /**
     * Sets the name of the template.
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the number of CPUs {PEs} for the VM instance
     * @return
     */
    public int getCpus() {
        return cpus;
    }

    /**
     * Sets the number of CPUs {PEs} for the VM instance
     * @param cpus number of CPUs to set
     */
    public void setCpus(final int cpus) {
        this.cpus = cpus;
    }

    /**
     * Gets the VM RAM capacity (in MB)
     */
    public int getMemoryInMB() {
        return memoryInMB;
    }

    /**
     * Sets the VM RAM capacity (in MB)
     * @param memoryInMB RAM capacity to set
     */
    public void setMemoryInMB(final int memoryInMB) {
        this.memoryInMB = memoryInMB;
    }

    /**
     * Gets the price per hour of a VM created from this template
     * @return
     */
    public double getPricePerHour() {
        return pricePerHour;
    }

    /**
     * Sets the price per hour of a VM created from this template
     * @param pricePerHour the price to set
     */
    public void setPricePerHour(final double pricePerHour) {
        if(pricePerHour < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.pricePerHour = pricePerHour;
    }

    /**
     * Gets the AWS Region in which the instance is run.
     * @return
     * @see <a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html">AWS Regions, Availability Zones, and Local Zones</a>
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the AWS Region in which the instance is run.
     * @param region the region to set
     * @see <a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html">AWS Regions, Availability Zones, and Local Zones</a>
     */
    public void setRegion(final String region) {
        this.region = region;
    }

    /**
     * Gets the full path to the JSON template file used to create this template.
     * @return
     */
    public String getFilePath() {
        return path.toAbsolutePath().toString();
    }

    /**
     * Gets only the name of the JSON template file used to create this template,
     * without the path.
     * @return
     */
    public String getFileName(){
        return path.getFileName().toString();
    }

    @Override
    public String toString() {
        return "AwsEc2Template {name = " + name +
            ",  cpus = " + cpus +
            ",  memoryInMB = " + memoryInMB +
            ",  pricePerHour = " + pricePerHour +'}';
    }


    @Override
    public int compareTo(final AwsEc2Template template) {
        int comparison;
        comparison = Double.compare(this.cpus, template.cpus);
        if(comparison != 0){
            return comparison;
        }

        comparison = Double.compare(this.memoryInMB, template.memoryInMB);
        if(comparison != 0){
            return comparison;
        }

        comparison = Double.compare(this.pricePerHour, template.pricePerHour);
        return comparison;
    }
}
