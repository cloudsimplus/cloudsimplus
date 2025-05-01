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
package org.cloudsimplus.datacenters;

/**
 * A class that implements the Null Object Design Pattern for {@link Datacenter} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see DatacenterCharacteristics#NULL
 */
final class DatacenterCharacteristicsNull implements DatacenterCharacteristics {
    @Override public double getCostPerBw() {
        return 0;
    }
    @Override public double getCostPerMem() {
        return 0;
    }
    @Override public double getCostPerSecond() {
        return 0;
    }
    @Override public double getCostPerStorage() {
        return 0;
    }
    @Override public DatacenterCharacteristics setCostPerSecond(double cost) { return this;}
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
    @Override public Distribution getDistribution() { return Distribution.PRIVATE; }
    @Override public DatacenterCharacteristics setDistribution(Distribution distribution) { return this; }
    @Override public long getId() {
        return 0;
    }
    @Override public double getMips() { return 0; }
    @Override public long getNumberOfFailedHosts() {
        return 0;
    }
    @Override public int getPesNumber() {
        return 0;
    }
    @Override public boolean isWorking() {
        return false;
    }
    @Override public DatacenterCharacteristics setCostPerBw(double cost) { return this; }
    @Override public DatacenterCharacteristics setCostPerMem(double cost) { return this; }
    @Override public DatacenterCharacteristics setCostPerStorage(double cost) { return this; }
}
