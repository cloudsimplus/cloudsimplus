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

import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.network.HostPacket;

import java.util.List;

/**
 * Represents a Network Switch.
 *
 * @author Manoel Campos da Silva Filho
 */
public sealed interface Switch extends SimEntity permits AbstractSwitch, SwitchNull {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Switch} objects.
     */
    Switch NULL = new SwitchNull();

    /**
     * Considering a list of packets to be sent simultaneously,
     * computes the expected time to transfer each packet through the downlink,
     * assuming that the bandwidth is shared equally between all packets.
     *
     * @param simultaneousPackets number of packets to be simultaneously sent
     * @return the expected transmission time in seconds
     */
    double downlinkTransferDelay(HostPacket packet, int simultaneousPackets);

    /**
     * Considering a list of packets to be sent simultaneously,
     * computes the expected time to transfer each packet through the uplink,
     * assuming that the bandwidth is shared equally between all packets.
     *
     * @param simultaneousPackets number of packets to be simultaneously sent
     * @return the expected transmission time in seconds
     */
    double uplinkTransferDelay(HostPacket packet, int simultaneousPackets);

    /**
     * Gets the bandwidth this Switch has to communicate with Switches in the upper layer.
     * @return Bandwidth of uplink (in Megabits/s).
     * @see #getUplinkSwitches()
     */
    double getUplinkBandwidth();

    /**
     * Sets the bandwidth this Switch has to communicate with Switches in the upper layer.
     * @param uplinkBandwidth uplink bandwidth to set (in Megabits/s).
     * @see #getUplinkSwitches()
     */
    void setUplinkBandwidth(double uplinkBandwidth);

    /**
     * Gets the bandwidth this Switch has to communicate with Switches in the lower layer.
     * @return Bandwidth of downlink (in Megabits/s).
     * @see #getDownlinkSwitches()
     */
    double getDownlinkBandwidth();

    /**
     * Sets the bandwidth this Switch has to communicate with Switches in the lower layer.
     * @param downlinkBandwidth downlink bandwidth to set (in Megabits/s).
     * @see #getDownlinkSwitches()
     */
    void setDownlinkBandwidth(double downlinkBandwidth);

    /**
     * @return the number of ports the switch has.
     */
    int getPorts();

    /**
     * Sets the number of ports the switch has.
     * @param ports the number of ports to set
     */
    void setPorts(int ports);

    /**
     * Gets the latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process (in seconds).
     * @return the switching delay (in seconds)
     */
    double getSwitchingDelay();

    /**
     * Sets the latency time the switch spends to process a received packet. This time is
     * considered constant no matter how many packets the switch have to
     * process (in seconds).
     * @param switchingDelay the switching delay to set
     */
    void setSwitchingDelay(double switchingDelay);

    /**
     * @return the list of Switches in the upper layer that this Switch is connected to.
     */
    List<Switch> getUplinkSwitches();

    /**
     * @return the list of Switches in the lower layer that this Switch is connected to.
     */
    List<Switch> getDownlinkSwitches();

    /**
     * @return the Datacenter where the switch is connected to.
     */
    NetworkDatacenter getDatacenter();

    /**
     * Sets the Datacenter where the switch is connected to.
     * @param datacenter the Datacenter to set
     */
    Switch setDatacenter(NetworkDatacenter datacenter);

    /**
     * Gets the level (layer) of the Switch in the network topology,
     * depending on whether it is a {@link RootSwitch} (layer 0), {@link AggregateSwitch} (layer 1)
     * or {@link EdgeSwitch} (layer 2).
     *
     * @return the switch network level
     */
    int getLevel();
}
