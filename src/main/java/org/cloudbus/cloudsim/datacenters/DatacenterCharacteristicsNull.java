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
package org.cloudbus.cloudsim.datacenters;

/**
 * A class that implements the Null Object Design Pattern for {@link Datacenter}
 * class.
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
    @Override public void setCostPerSecond(double cost) { /**/ }
    @Override public void setVmm(String vmm) {/**/}
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
    @Override public String getArchitecture() {
        return "";
    }
    @Override public void setArchitecture(String arch) { /**/ }
    @Override public String getOs() {
        return "";
    }
    @Override public void setOs(String os) {/**/}
    @Override public long getId() {
        return 0;
    }
    @Override public double getMips() { return 0; }
    @Override public long getNumberOfFailedHosts() {
        return 0;
    }
    @Override public int getNumberOfPes() {
        return 0;
    }
    @Override public String getVmm() {
        return "";
    }
    @Override public boolean isWorking() {
        return false;
    }
    @Override public void setCostPerBw(double cost) {/**/}
    @Override public void setCostPerMem(double cost) { /**/ }
    @Override public void setCostPerStorage(double cost) { /**/ }
}
