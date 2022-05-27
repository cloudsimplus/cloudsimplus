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
package org.cloudsimplus.slametrics;

import com.google.gson.Gson;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudsimplus.vmtemplates.AwsEc2Template;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an SLA Contract containing a list of metrics.
 * It follows the standard used by
 * <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/viewing_metrics_with_cloudwatch.html">Amazon Cloudwatch</a>.
 * <p>The constants inside the class define the names
 * of SLA Metrics supported in the JSON SLA Contract format.</p>
 *
 * <p>Instances of this class can be created from a JSON file
 * using the {@link #getInstanceInternal(InputStream)} or
 * {@link #getInstance(String)} methods.
 * This way, one doesn't need to create instances
 * of this class using its default constructor.
 * This one is just used by the JSON parsing library.</p>
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 */
public class SlaContract {
    private static final String AVAILABILITY = "Availability";
    private static final String TASK_COMPLETION_TIME = "TaskCompletionTime";
    private static final String CPU_UTILIZATION = "CpuUtilization";
    private static final String WAIT_TIME = "WaitTime";
    private static final String PRICE = "Price";
    private static final String FAULT_TOLERANCE_LEVEL = "FaultToleranceLevel";

    private List<SlaMetric> metrics;

    /**
     * Creates a {@link SlaContract}.
     * If you want to get a contract from a JSON file,
     * you shouldn't call the constructor directly.
     * Instead, use some methods of the class methods.
     *
     * <p>This constructor is just provided to enable the {@link Gson} object
     * to use reflection to instantiate a SlaContract.</p>
     *
     * @see #getInstance(String)
     *
     */
    public SlaContract() {
        this.metrics = new ArrayList<>();
    }

    /**
     * Gets an {@link SlaContract} from a JSON file inside the <b>application's resource directory</b>.
     * @param jsonFilePath the <b>relative path</b> to the JSON file representing the
     *                     SLA contract to read
     * @return a {@link SlaContract} read from the JSON file
     */
    public static SlaContract getInstance(final String jsonFilePath) {
        return getInstanceInternal(ResourceLoader.newInputStream(jsonFilePath, SlaContract.class));
    }

    /**
     * Gets an {@link SlaContract} from a JSON file.
     * Use the available constructors if you want to load a file outside the resource directory.
     *
     * @param inputStream a {@link InputStream} to read the file
     * @return a {@link SlaContract} read from the JSON file
     */
    private static SlaContract getInstanceInternal(final InputStream inputStream) {
        return new Gson().fromJson(new InputStreamReader(inputStream), SlaContract.class);
    }

    /**
     * @return the metrics list
     */
    public List<SlaMetric> getMetrics() {
        return metrics;
    }

    /**
     * @param metrics the metrics to set
     */
    public void setMetrics(final List<SlaMetric> metrics) {
        /* Since the contract can be read from a file, the metrics
        *  can be in fact null. This way, instantiates an empty list
        *  instead of using Objects.requiredNonNull(). */
        this.metrics = metrics == null ? new ArrayList<>() : metrics;
    }

    private SlaMetric getSlaMetric(final String metricName) {
        return metrics
                .stream()
                .filter(metric -> metricName.equals(metric.getName()))
                .findFirst()
                .orElse(SlaMetric.NULL);
    }

    public SlaMetric getAvailabilityMetric() {
        return getSlaMetric(AVAILABILITY);
    }

    public SlaMetric getCpuUtilizationMetric() {
        return getSlaMetric(CPU_UTILIZATION);
    }

    public SlaMetric getPriceMetric() {
        return getSlaMetric(PRICE);
    }

    public SlaMetric getWaitTimeMetric() {
        return getSlaMetric(WAIT_TIME);
    }

    public SlaMetric getTaskCompletionTimeMetric() {
        return getSlaMetric(TASK_COMPLETION_TIME);
    }

    public SlaMetric getFaultToleranceLevel() {
        return getSlaMetric(FAULT_TOLERANCE_LEVEL);
    }

    /**
     * Gets the maximum price a customer expects to pay hourly for all his/her running VMs.
     * @return
     */
    public double getMaxPrice() {
        return getPriceMetric().getMaxDimension().getValue();
    }

    /**
     * Gets the expected maximum price a single VM can cost, considering
     * the {@link #getMinFaultToleranceLevel() Fault Tolerance Level}.
     *
     * @return the expected maximum price a single VM can cost for the given customer
     * {@link AwsEc2Template} for the customer's expected price
     */
    public double getExpectedMaxPriceForSingleVm() {
        return getMaxPrice() / getFaultToleranceLevel().getMinDimension().getValue();
    }

    public int getMinFaultToleranceLevel() {
        return (int)Math.floor(getFaultToleranceLevel().getMinDimension().getValue());
    }

    @Override
    public String toString() {
        return metrics.toString();
    }
}
