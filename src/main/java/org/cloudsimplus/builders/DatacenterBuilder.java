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
package org.cloudsimplus.builders;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.SanStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A Builder class to createDatacenter {@link DatacenterSimple} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class DatacenterBuilder implements Builder {
    private static final String DC_NAME_FORMAT = "Datacenter%d";
    private final SimulationScenarioBuilder scenario;

    private double costPerBwMegabit;
    private double costPerCpuSecond = 3.0;
    private double costPerStorage = 0.001;
    private double costPerMem = 0.05;
    private double schedulingInterval = -1;
    private int    timezone;

    private final List<Datacenter> datacenters;
    private int createdDatacenters;
	private List<SanStorage> storageList;
	private Function<List<Host>, Datacenter> datacenterCreationFunction;

	public DatacenterBuilder(final SimulationScenarioBuilder scenario) {
	    super();
	    this.scenario = scenario;
        this.datacenters = new ArrayList<>();
		this.storageList = new ArrayList<>();
        this.createdDatacenters = 0;
        this.datacenterCreationFunction = this::defaultDatacenterCreationFunction;
    }

    public DatacenterBuilder create(final List<Host> hosts) {
        Objects.requireNonNull(hosts);
        if (hosts.isEmpty()) {
            throw new IllegalArgumentException("The hosts parameter has to have at least 1 host.");
        }

        final String name = String.format(DC_NAME_FORMAT, createdDatacenters++);
        final Datacenter datacenter = datacenterCreationFunction.apply(hosts);

        datacenter.getCharacteristics()
            .setCostPerSecond(costPerCpuSecond)
            .setCostPerMem(costPerMem)
            .setCostPerStorage(costPerStorage)
            .setCostPerBw(costPerBwMegabit);

        datacenter.getDatacenterStorage().setStorageList(storageList);
        datacenter.setName(name);
        datacenter.setTimeZone(timezone);
        this.datacenters.add(datacenter);
        return this;
    }

    public List<Datacenter> getDatacenters() {
        return datacenters;
    }

    public Datacenter get(final int index) {
        if(index >= 0 && index < datacenters.size())
            return datacenters.get(index);

        return Datacenter.NULL;
    }

    public Host getHostOfDatacenter(final int hostIndex, final int datacenterIndex){
        return get(datacenterIndex).getHost(hostIndex);
    }

    public Host getFirstHostFromFirstDatacenter(){
        return getHostOfDatacenter(0,0);
    }

    private Datacenter defaultDatacenterCreationFunction(final List<Host> hosts) {
        final DatacenterSimple dc = new DatacenterSimple(scenario.getSimulation(), hosts, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(schedulingInterval);
        return dc;
    }

    public double getCostPerBwMegabit() {
        return costPerBwMegabit;
    }

    public DatacenterBuilder setCostPerBwMegabit(final double defaultCostPerBwByte) {
        this.costPerBwMegabit = defaultCostPerBwByte;
        return this;
    }

    public double getCostPerCpuSecond() {
        return costPerCpuSecond;
    }

    public DatacenterBuilder setCostPerCpuSecond(final double defaultCostPerCpuSecond) {
        this.costPerCpuSecond = defaultCostPerCpuSecond;
        return this;
    }

    public double getCostPerStorage() {
        return costPerStorage;
    }

    public DatacenterBuilder setCostPerStorage(final double defaultCostPerStorage) {
        this.costPerStorage = defaultCostPerStorage;
        return this;
    }

    public double getCostPerMem() {
        return costPerMem;
    }

    public DatacenterBuilder setCostPerMem(final double defaultCostPerMem) {
        this.costPerMem = defaultCostPerMem;
        return this;
    }

    public int getTimezone() {
        return timezone;
    }

    public DatacenterBuilder setTimezone(final int defaultTimezone) {
        this.timezone = defaultTimezone;
        return this;
    }

    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    public DatacenterBuilder setSchedulingInterval(final double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
        return this;
    }

	public DatacenterBuilder setStorageList(final List<SanStorage> storageList) {
		this.storageList = storageList;
		return this;
	}

	public DatacenterBuilder addStorageToList(final SanStorage storage) {
		this.storageList.add(storage);
		return this;
	}

    /**
     * Sets a {@link Function} used to create Datacenters.
     * It must receive a list of {@link Host} for the Datacenter it will create.
     * @param datacenterCreationFunction
     */
    public void setDatacenterCreationFunction(final Function<List<Host>, Datacenter> datacenterCreationFunction) {
        this.datacenterCreationFunction = Objects.requireNonNull(datacenterCreationFunction);
    }
}
