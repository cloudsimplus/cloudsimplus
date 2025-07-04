/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.datacenters.network;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.switches.EdgeSwitch;
import org.cloudsimplus.network.switches.Switch;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.network.NetworkVm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/// A [Datacenter] whose [Host]s have network support.
/// It contains all the information about the internal network.
/// For example, which [NetworkVm] is connected to which [Switch], etc.
///
/// Please refer to the following publication for more details:
///
/// - [Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
///   Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
///   International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
///   Press, USA), Melbourne, Australia, December 5-7, 2011.](https://doi.org/10.1109/UCC.2011.24)
///
/// @author Saurabh Kumar Garg
/// @author Manoel Campos da Silva Filho
public class NetworkDatacenter extends DatacenterSimple {

    /** @see #getSwitchMap() */
    private final List<Switch> switchMap;

    /**
     * Creates a NetworkDatacenter with the given parameters.
     *
     * @param simulation The {@link CloudSimPlus} instance that represents the simulation the Entity belongs to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate {@link Vm}s into hosts
     *
     * @throws IllegalArgumentException when this Host has zero {@link Pe}s
     */
    public NetworkDatacenter(
        final Simulation simulation,
        final List<? extends NetworkHost> hostList,
        final VmAllocationPolicy vmAllocationPolicy)
    {
        this(simulation, hostList);
        setVmAllocationPolicy(vmAllocationPolicy);
    }

    /**
     * Creates a NetworkDatacenter that uses a {@link VmAllocationPolicySimple} as default.
     *
     * @param simulation The {@link CloudSimPlus} instance that represents the simulation the Entity belongs to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     *
     * @throws IllegalArgumentException when this Host has zero {@link Pe}s
     */
    public NetworkDatacenter(
        final Simulation simulation,
        final List<? extends NetworkHost> hostList)
    {
        super(simulation, hostList);
        switchMap = new ArrayList<>();
    }

    /**
     * @return a list of all {@link EdgeSwitch}s in the Datacenter network.
     * @see #getSwitchMap()
     */
    public List<Switch> getEdgeSwitch() {
        return switchMap.stream()
                .filter(swt -> swt.getLevel() == EdgeSwitch.LEVEL)
                .collect(toList());
    }

    /**
     * Adds a {@link Switch} to the Datacenter.
     * @param swt the Switch to be added
     */
    public void addSwitch(final Switch swt){
        switchMap.add(swt);
    }

    /**
     * @return a <b>read-only</b> list of network Datacenter's {@link Switch}es.
     */
    // TODO This method and attribute must be renamed to switchList
    public List<Switch> getSwitchMap() {
        return Collections.unmodifiableList(switchMap);
    }

    @Override
    public List<NetworkHost> getHostList() {
        return super.getHostList();
    }

}
