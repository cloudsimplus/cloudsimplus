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
package org.cloudsimplus.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.util.MathUtil;

/**
 * A base class for {@link CustomerEntity} implementations.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.3
 */
@Accessors @Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class CustomerEntityAbstract implements CustomerEntity {
    @EqualsAndHashCode.Include
    private long id;

    @NonNull
    @EqualsAndHashCode.Include
    private DatacenterBroker broker;

    private double arrivedTime;
    private double creationTime;

    private Datacenter lastTriedDatacenter;

    protected CustomerEntityAbstract(){
        lastTriedDatacenter = Datacenter.NULL;
        creationTime = -1;
    }

    @Override
    public String getUid() {
        return UniquelyIdentifiable.getUid(broker.getId(), id);
    }

    @Override
    public void setArrivedTime(final double time) {
        this.arrivedTime = MathUtil.nonNegative(time, "Arrived time");
    }

    public void setCreationTime() {
        setCreationTime(getSimulation().clock());
    }

    public void setCreationTime(final double time) {
        this.creationTime = MathUtil.nonNegative(time, "Creation time");
    }

    @Override
    public double getWaitTime() {
        return creationTime < 0 ? getSimulation().clock() - arrivedTime : creationTime - arrivedTime;
    }

    @Override
    public Simulation getSimulation() {
        return broker.getSimulation();
    }
}
