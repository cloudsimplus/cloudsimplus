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
import lombok.*;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.util.ResourceLoader;
import org.cloudsimplus.vms.Vm;

import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/// Represents an [Amazon EC2 VM Instance](http://aws.amazon.com/ec2/) template.
/// This class enables reading a template from a JSON file
/// containing actual configurations for VMs
/// available in [Amazon Web Services](http://aws.amazon.com).
/// Such templates can be used to create [Vm] instances.
///
/// For more details, check
/// [Raysa Oliveira's Master Thesis (only in Portuguese)](https://ubibliorum.ubi.pt/handle/10400.6/7839).
///
/// @author raysaoliveira
/// @see #getInstance(String)
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwsEc2Template implements Comparable<AwsEc2Template> {
    public static final AwsEc2Template NULL = new AwsEc2Template();

    @ToString.Exclude
    private Path path;

    /**
     * The name of the template.
     */
    private String name;

    /**
     * The number of CPUs {PEs} for the VM instance
     */
    private int cpus;

    /**
     * The VM RAM capacity (in MB)
     */
    private int memoryInMB;

    /**
     * The price per hour of a VM created from this template
     */
    private double pricePerHour;

    /// The AWS Region in which the instance is run.
    /// @link [AWS Regions, Availability Zones, and Local Zones](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html)
    private String region;

    /**
     * Default constructor used to create an {@link AwsEc2Template} instance.
     * If you want to get a template from a JSON file,
     * use some methods such as the {@link #getInstance(String)}.
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
     * Sets the price per hour of a VM created from this template
     * @param pricePerHour the price to set
     */
    public void setPricePerHour(final double pricePerHour) {
        this.pricePerHour = MathUtil.nonNegative(pricePerHour, "pricePerHour");
    }

    /**
     * @return the full path to the JSON template file used to create this template.
     */
    public String getFilePath() {
        return path.toAbsolutePath().toString();
    }

    /**
     * @return only the name of the JSON template file used to create this template,
     * without the path.
     */
    public String getFileName(){
        return path.getFileName().toString();
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
