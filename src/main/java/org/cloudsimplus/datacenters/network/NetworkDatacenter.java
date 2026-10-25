/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.datacenters.network;

import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.placementpolicies.VmPlacementPolicySimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.switches.EdgeSwitch;
import org.cloudsimplus.network.switches.Switch;
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

    /// @see #getSwitchMap()
    private final List<Switch> switchMap;

    /// Creates a NetworkDatacenter with the given parameters.
    /// This constructor is only for subclasses: use [#of(Simulation, List, VmPlacementPolicy)] to create a Datacenter.
    ///
    /// @param simulation The [CloudSimPlus] instance that represents the simulation the Entity belongs to
    /// @param hostList list of [Host]s that will compound the Datacenter
    /// @param vmPlacementPolicy the policy to be used to allocate [Vm]s into hosts
    ///
    /// @throws IllegalArgumentException when this Host has zero cores
    protected NetworkDatacenter(
        final Simulation simulation,
        final List<? extends NetworkHost> hostList,
        final VmPlacementPolicy vmPlacementPolicy)
    {
        this(simulation, hostList);
        setVmPlacementPolicy(vmPlacementPolicy);
    }

    /// Creates a NetworkDatacenter with the given parameters.
    /// @see #NetworkDatacenter(Simulation, List, VmPlacementPolicy)
    /// @throws IllegalArgumentException when some Host isn't a [NetworkHost]
    public static NetworkDatacenter of(final Simulation simulation, final List<? extends Host> hostList, final VmPlacementPolicy vmPlacementPolicy) {
        return new NetworkDatacenter(simulation, networkHosts(hostList), vmPlacementPolicy);
    }

    /// Creates a NetworkDatacenter that uses a [VmPlacementPolicySimple] as default.
    /// This constructor is only for subclasses: use [#of(Simulation, List)] to create a Datacenter.
    ///
    /// @param simulation The [CloudSimPlus] instance that represents the simulation the Entity belongs to
    /// @param hostList list of [Host]s that will compound the Datacenter
    ///
    /// @throws IllegalArgumentException when this Host has zero cores
    protected NetworkDatacenter(
        final Simulation simulation,
        final List<? extends NetworkHost> hostList)
    {
        super(simulation, hostList);
        switchMap = new ArrayList<>();
    }

    /// Creates a NetworkDatacenter that uses a [VmPlacementPolicySimple] as default.
    /// @see #NetworkDatacenter(Simulation, List)
    /// @throws IllegalArgumentException when some Host isn't a [NetworkHost]
    public static NetworkDatacenter of(final Simulation simulation, final List<? extends Host> hostList) {
        return new NetworkDatacenter(simulation, networkHosts(hostList));
    }

    /// {@return the given list as a list of [NetworkHost]s}
    /// The factory methods receive a list of [Host]s, since they hide the ones in [DatacenterSimple],
    /// which must have the same parameter types.
    ///
    /// @param hostList the list of Hosts to check
    /// @throws IllegalArgumentException when some Host isn't a [NetworkHost]
    @SuppressWarnings("unchecked")
    private static List<? extends NetworkHost> networkHosts(final List<? extends Host> hostList) {
        if (hostList.stream().allMatch(NetworkHost.class::isInstance)) {
            return (List<? extends NetworkHost>) hostList;
        }

        throw new IllegalArgumentException("A NetworkDatacenter can only have NetworkHosts");
    }

    /// @return a list of all [EdgeSwitch]s in the Datacenter network.
    /// @see #getSwitchMap()
    public List<Switch> getEdgeSwitch() {
        return switchMap.stream()
                .filter(swt -> swt.getLevel() == EdgeSwitch.LEVEL)
                .collect(toList());
    }

    /// Adds a [Switch] to the Datacenter.
    /// @param swt the Switch to be added
    public void addSwitch(final Switch swt){
        switchMap.add(swt);
    }

    /// @return a **read-only** list of network Datacenter's [Switch]es.
    // TODO This method and attribute must be renamed to switchList
    // See https://github.com/cloudsimplus/cloudsimplus/issues/589
    public List<Switch> getSwitchMap() {
        return Collections.unmodifiableList(switchMap);
    }

    @Override
    public List<NetworkHost> getHostList() {
        return super.getHostList();
    }

}
