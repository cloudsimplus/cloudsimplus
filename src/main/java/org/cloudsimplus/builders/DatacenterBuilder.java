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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.SanStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A Builder class to createDatacenter {@link DatacenterSimple} objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
@Accessors(chain = true)
public class DatacenterBuilder implements Builder {
    private static final String DC_NAME_FORMAT = "Datacenter%d";
    private final SimulationScenarioBuilder scenario;

    @Getter @Setter private double costPerBwMegabit;
    @Getter @Setter private double costPerCpuSecond = 3.0;
    @Getter @Setter private double costPerStorage = 0.001;
    @Getter @Setter private double costPerMem = 0.05;
    @Getter @Setter private double schedulingInterval = -1;
    @Getter @Setter private int    timezone;
    @Getter private final List<Datacenter> datacenters;
    private int createdDatacenters;
    @Setter @NonNull private List<SanStorage> storageList;

    /**
     * A {@link Function} used to create Datacenters.
     * It must receive a list of {@link Host} for the Datacenter it will create.
     */
    @Setter @NonNull private Function<List<Host>, Datacenter> datacenterCreationFunction;

	public DatacenterBuilder(final SimulationScenarioBuilder scenario) {
	    super();
	    this.scenario = scenario;
        this.datacenters = new ArrayList<>();
		this.storageList = new ArrayList<>();
        this.createdDatacenters = 0;
        this.datacenterCreationFunction = this::defaultDatacenterCreationFunction;
    }

    public DatacenterBuilder create(@NonNull final List<Host> hosts) {
        if (hosts.isEmpty()) {
            throw new IllegalArgumentException("The hosts parameter has to have at least 1 host.");
        }

        final String name = DC_NAME_FORMAT.formatted(createdDatacenters++);
        final Datacenter datacenter = datacenterCreationFunction.apply(hosts);

        final var characteristics = datacenter.getCharacteristics();
        characteristics.setCostPerSecond(costPerCpuSecond);
        characteristics.setCostPerMem(costPerMem);
        characteristics.setCostPerStorage(costPerStorage);
        characteristics.setCostPerBw(costPerBwMegabit);

        datacenter.getDatacenterStorage().setStorageList(storageList);
        datacenter.setName(name);
        datacenter.setTimeZone(timezone);
        this.datacenters.add(datacenter);
        return this;
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

	public DatacenterBuilder addStorageToList(final SanStorage storage) {
		this.storageList.add(storage);
		return this;
	}
}
