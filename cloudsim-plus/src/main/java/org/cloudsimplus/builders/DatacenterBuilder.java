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
package org.cloudsimplus.builders;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private double schedulingInterval = 1;
    private double timezone = 10;

    private final List<Datacenter> datacenters;
    private int createdDatacenters;
	private List<FileStorage> storageList;

	public DatacenterBuilder(SimulationScenarioBuilder scenario) {
	    super();
	    this.scenario = scenario;
        this.datacenters = new ArrayList<>();
		this.storageList = new ArrayList<>();
        this.createdDatacenters = 0;
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

    public DatacenterBuilder createDatacenter(final List<Host> hosts) {
        Objects.requireNonNull(hosts);
        if (hosts.isEmpty()) {
            throw new IllegalArgumentException("The hosts parameter has to have at least 1 host.");
        }

        final String name = String.format(DC_NAME_FORMAT, createdDatacenters++);
        final Datacenter datacenter =
                new DatacenterSimple(scenario.getSimulation(), hosts, new VmAllocationPolicySimple())
                    .setSchedulingInterval(schedulingInterval);

        datacenter.getCharacteristics()
            .setTimeZone(timezone)
            .setCostPerSecond(costPerCpuSecond)
            .setCostPerMem(costPerMem)
            .setCostPerStorage(costPerStorage)
            .setCostPerBw(costPerBwMegabit);
        
        datacenter.getDatacenterStorage().setStorageList(storageList);
        datacenter.setName(name);
        this.datacenters.add(datacenter);
        return this;
    }

    public double getCostPerBwMegabit() {
        return costPerBwMegabit;
    }

    public DatacenterBuilder setCostPerBwMegabit(double defaultCostPerBwByte) {
        this.costPerBwMegabit = defaultCostPerBwByte;
        return this;
    }

    public double getCostPerCpuSecond() {
        return costPerCpuSecond;
    }

    public DatacenterBuilder setCostPerCpuSecond(double defaultCostPerCpuSecond) {
        this.costPerCpuSecond = defaultCostPerCpuSecond;
        return this;
    }

    public double getCostPerStorage() {
        return costPerStorage;
    }

    public DatacenterBuilder setCostPerStorage(double defaultCostPerStorage) {
        this.costPerStorage = defaultCostPerStorage;
        return this;
    }

    public double getCostPerMem() {
        return costPerMem;
    }

    public DatacenterBuilder setCostPerMem(double defaultCostPerMem) {
        this.costPerMem = defaultCostPerMem;
        return this;
    }

    public double getTimezone() {
        return timezone;
    }

    public DatacenterBuilder setTimezone(double defaultTimezone) {
        this.timezone = defaultTimezone;
        return this;
    }

    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    public DatacenterBuilder setSchedulingInterval(double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
        return this;
    }

	public DatacenterBuilder setStorageList(List<FileStorage> storageList) {
		this.storageList = storageList;
		return this;
	}

	public DatacenterBuilder addStorageToList(FileStorage storage) {
		this.storageList.add(storage);
		return this;
	}

}
