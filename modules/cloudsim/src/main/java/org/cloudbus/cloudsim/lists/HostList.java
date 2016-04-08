/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.lists;

import java.util.List;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * HostList is a collection of operations on lists of hosts (PMs).
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class HostList {

    /**
     * Gets a {@link Host} with a given id.
     *
     * @param <T> the generic type
     * @param hostList the list of existing hosts
     * @param id the host ID
     * @return a Host with the given ID or $null if not found
     *
     * @pre id >= 0
     * @post $none
     */
    public static <T extends Host> T getById(List<T> hostList, int id) {
        for (T host : hostList) {
            if (host.getId() == id) {
                return host;
            }
        }
        return null;
    }

    /**
     * Gets the total number of PEs for all Hosts.
     *
     * @param <T> the generic type
     * @param hostList the list of existing hosts
     * @return total number of PEs for all PMs
     * @pre $none
     * @post $result >= 0
     */
    public static <T extends Host> int getNumberOfPes(List<T> hostList) {
        return hostList.stream().mapToInt(h -> h.getPeList().size()).sum();
    }

    /**
     * Gets the total number of <tt>FREE</tt> (non-busy) PEs for all Hosts.
     *
     * @param <T> the generic type
     * @param hostList the list of existing hosts
     * @return total number of free PEs
     * @pre $none
     * @post $result >= 0
     */
    public static <T extends Host> int getNumberOfFreePes(List<T> hostList) {
        return hostList.stream().mapToInt(h -> PeList.getNumberOfFreePes(h.getPeList())).sum();
    }

    /**
     * Gets the total number of <tt>BUSY</tt> PEs for all Hosts.
     *
     * @param <T> the generic type
     * @param hostList the list of existing hosts
     * @return total number of busy PEs
     * @pre $none
     * @post $result >= 0
     */
    public static <T extends Host> int getNumberOfBusyPes(List<T> hostList) {
        return hostList.stream()
                .map((host) -> PeList.getNumberOfBusyPes(host.getPeList()))
                .reduce(0, Integer::sum);
    }

    /**
     * Gets the first host with free PEs.
     *
     * @param <T>
     * @param hostList the list of existing hosts
     * @return a Host object or <tt>null</tt> if not found
     * @pre $none
     * @post $none
     */
    public static <T extends Host> T getHostWithFreePe(List<T> hostList) {
        return getHostWithFreePe(hostList, 1);
    }

    /**
     * Gets the first Host with a specified number of free PEs.
     *
     * @param <T>
     * @param hostList the list of existing hosts
     * @param pesNumber the pes number
     * @return a Host object or <tt>null</tt> if not found
     * @pre $none
     * @post $none
     */
    public static <T extends Host> T getHostWithFreePe(List<T> hostList, int pesNumber) {
        for (Host host : hostList) {
            if (PeList.getNumberOfFreePes(host.getPeList()) >= pesNumber) {
                return (T) host;
            }
        }
        return null;
    }

    /**
     * Sets the status of a particular PE on a given Host.
     *
     * @param <T>
     * @param hostList the list of existing hosts
     * @param status the new PE status
     * @param hostId the host id
     * @param peId the id of the PE to set the status
     * @return <tt>true</tt> if the PE status has changed, <tt>false</tt>
     * otherwise (host id or PE id might not be exist)
     * @pre hostId >= 0
     * @pre peId >= 0
     * @post $none
     */
    public static <T extends Host> boolean setPeStatus(List<T> hostList, Pe.Status status, int hostId, int peId) {
        Host host = getById(hostList, hostId);
        if (host == null) {
            return false;
        }
        return host.setPeStatus(peId, status);
    }

}
