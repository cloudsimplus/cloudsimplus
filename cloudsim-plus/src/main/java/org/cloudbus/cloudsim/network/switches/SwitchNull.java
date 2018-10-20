package org.cloudbus.cloudsim.network.switches;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEntityNullBase;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.HostPacket;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A class that implements the Null Object Design Pattern for {@link Switch}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Switch#NULL
 */
final class SwitchNull implements Switch, SimEntityNullBase {
    private static final NetworkDatacenter DATACENTER = new NetworkDatacenter(Simulation.NULL, Collections.emptyList(), VmAllocationPolicy.NULL);

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
        return Collections.emptyList();
    }
    @Override public List<NetworkHost> getHostList() {
        return Collections.emptyList();
    }
    @Override public void connectHost(NetworkHost host) {/**/}
    @Override public boolean disconnectHost(NetworkHost host) {
        return false;
    }
    @Override public Map<NetworkHost, List<HostPacket>> getPacketToHostMap() {
        return Collections.emptyMap();
    }
    @Override public List<Switch> getDownlinkSwitches() {
        return Collections.emptyList();
    }
    @Override public List<HostPacket> getDownlinkSwitchPacketList(Switch swt) { return Collections.emptyList(); }
    @Override public List<HostPacket> getUplinkSwitchPacketList(Switch swt) {
        return Collections.emptyList();
    }
    @Override public List<HostPacket> getHostPacketList(NetworkHost host) {
        return Collections.emptyList();
    }
    @Override public Map<Switch, List<HostPacket>> getUplinkSwitchPacketMap() {
        return Collections.emptyMap();
    }
    @Override public void addPacketToBeSentToDownlinkSwitch(Switch downlinkSwitch, HostPacket packet) {/**/}
    @Override public void addPacketToBeSentToUplinkSwitch(Switch uplinkSwitch, HostPacket packet) {/**/}
    @Override public void addPacketToBeSentToHost(NetworkHost host, HostPacket packet) {/**/}
    @Override public NetworkDatacenter getDatacenter() {
        return DATACENTER;
    }
    @Override public void setDatacenter(NetworkDatacenter datacenter) {/**/}
    @Override public List<HostPacket> getPacketList() {
        return Collections.emptyList();
    }
    @Override public int getLevel() {
        return 0;
    }
    @Override public int compareTo(SimEntity entity) { return 0; }
}
