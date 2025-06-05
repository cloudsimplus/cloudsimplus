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
package org.cloudsimplus.network.switches;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.SimEntityNullBase;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.network.HostPacket;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * A class that implements the Null Object Design Pattern for {@link Switch} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Switch#NULL
 */
final class SwitchNull implements Switch, SimEntityNullBase {
    private static final NetworkDatacenter DATACENTER = new NetworkDatacenter(Simulation.NULL, emptyList(), VmAllocationPolicy.NULL);

    @Override public double downlinkTransferDelay(HostPacket packet, int simultaneousPackets) { return 0; }
    @Override public double uplinkTransferDelay(HostPacket packet, int simultaneousPackets) { return 0; }
    @Override public double getUplinkBandwidth() {
        return 0;
    }
    @Override public void setUplinkBandwidth(double uplinkBandwidth) {/**/}
    @Override public double getDownlinkBandwidth() {
        return 0;
    }
    @Override public void setDownlinkBandwidth(double downlinkBandwidth) {/**/}
    @Override public int getPorts() {
        return 0;
    }
    @Override public void setPorts(int ports) {/**/}
    @Override public double getSwitchingDelay() {
        return 0;
    }
    @Override public void setSwitchingDelay(double switchingDelay) {/**/}
    @Override public List<Switch> getUplinkSwitches() {
        return emptyList();
    }
    @Override public List<Switch> getDownlinkSwitches() {
        return emptyList();
    }
    @Override public NetworkDatacenter getDatacenter() {
        return DATACENTER;
    }
    @Override public Switch setDatacenter(NetworkDatacenter datacenter) { return this; }
    @Override public int getLevel() {
        return 0;
    }
    @Override public int compareTo(SimEntity entity) { return 0; }
}
