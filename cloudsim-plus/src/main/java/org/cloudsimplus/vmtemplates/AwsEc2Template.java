/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents an
 * <a href="http://aws.amazon.com/ec2/">Amazon EC2 Instance</a> template.
 * This class enables reading a template from a JSON file, containing actual configurations for VMs
 * available in <a href="http://aws.amazon.com/">Amazon Web Services</a>.
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
        this(getInstanceInternal(jsonFilePath, ResourceLoader.getFileReader(jsonFilePath)));
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
        final InputStreamReader reader = new InputStreamReader(ResourceLoader.getInputStream(jsonFilePath, AwsEc2Template.class));
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

    public String getName() {return name; }

    public void setName(final String name) {
        this.name = name;
    }

    public int getCpus() {
        return cpus;
    }

    public void setCpus(final int cpus) {
        this.cpus = cpus;
    }

    public int getMemoryInMB() {
        return memoryInMB;
    }

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

    public void setPricePerHour(final double pricePerHour) {
        this.pricePerHour = pricePerHour;
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
