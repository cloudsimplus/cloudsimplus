/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents an
 * <a href="http://aws.amazon.com/ec2/">Amazon EC2 Instance</a> template.
 * This class enables reading a template from a JSON file, containing actual configurations for VMs
 * available in <a href="http://aws.amazon.com/">Amazon Web Services</a>.
 *
 * @author raysaoliveira
 * @see #getInstance(String)
 */
public class AwsEc2Template implements Comparable<AwsEc2Template> {
    public static final AwsEc2Template NULL = new AwsEc2Template();

    private String name;
    private Path path;
    private int cpus;
    private int memoryInMB;
    private double pricePerHour;

    /**
     * Default constructor used to create an {@link AwsEc2Template} instance.
     * If you want to get a template from a JSON file,
     * you shouldn't call the constructor directly.
     * Instead, use some methods such as the {@link #getInstance(String)}.
     */
    public AwsEc2Template(){
    }

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
        this.path = Paths.get(source.path.toUri());
    }

    /**
     * Gets an AWS EC2 Instance from a JSON file.
     * @param jsonTemplateFilePath the full path to the JSON file representing the template with
     *                     configurations for an AWS EC2 Instance
     * @return the AWS EC2 Instance from the JSON file
     */
    public static AwsEc2Template getInstance(final String jsonTemplateFilePath) throws FileNotFoundException {
        final FileReader fileReader = new FileReader(jsonTemplateFilePath);
        final AwsEc2Template template = new Gson().fromJson(fileReader, AwsEc2Template.class);
        template.path = Paths.get(jsonTemplateFilePath);
        return template;
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

    /**
     * Gets the price per hour of a VM created from this template
     * @return
     */
    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    @Override
    public String toString() {
        return "AwsEc2Template {name = " + name +
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
    public int compareTo(AwsEc2Template o) {
        int comparison = Double.compare(this.cpus, o.cpus);
        if(comparison != 0){
            return comparison;
        }

        comparison = Double.compare(this.memoryInMB, o.memoryInMB);
        if(comparison != 0){
            return comparison;
        }

        comparison = Double.compare(this.pricePerHour, o.pricePerHour);
        return comparison;
    }
}
