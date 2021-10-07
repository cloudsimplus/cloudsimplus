/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters.network;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.network.switches.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * NetworkDatacenter class is a {@link Datacenter} whose hosts have network support.
 * It contains all the information about internal network.
 * For example, which VM is connected to what switch, etc.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 */
public class NetworkDatacenter extends DatacenterSimple {

    /** @see #getSwitchMap() */
    private final List<Switch> switchMap;

    /**
     * Creates a NetworkDatacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity belongs to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     *
     * @throws IllegalArgumentException when this Host has zero number of PEs (Processing Elements).
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
     * Creates a NetworkDatacenter that uses a {@link VmAllocationPolicySimple}
     * as default.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity belongs to
     * @param hostList list of {@link Host}s that will compound the Datacenter
     *
     * @throws IllegalArgumentException when this Host has zero number of PEs (Processing Elements).
     */
    public NetworkDatacenter(
        final Simulation simulation,
        final List<? extends NetworkHost> hostList)
    {
        super(simulation, hostList);
        switchMap = new ArrayList<>();
    }

    /**
     * Gets a map of all Edge Switches in the Datacenter network, where each key is the switch id
     * and each value is the switch itself.
     * One can design similar functions for other type of Datacenter.
     *
     * @return
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
     * Gets a <b>read-only</b> list of network Datacenter's {@link Switch}es.
     * @return
     */
    public List<Switch> getSwitchMap() {
        return Collections.unmodifiableList(switchMap);
    }

    @Override
    public List<NetworkHost> getHostList() {
        return super.getHostList();
    }

}
