/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.lists;

import java.util.List;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * PeList is a collection of operations on lists of PEs.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public final class PeList {
    /**
     * A private constructor to avoid class instantiation.
     */
    private PeList(){}

    /**
     * Gets a {@link Pe} with a given id.
     *
     * @param <T>
     * @param peList the PE list where to get a given PE
     * @param id the id of the PE to be get
     * @return the PE with the given id or null if not found
     * @pre id >= 0
     * @post $none
     */
    public static <T extends Pe> T getById(List<T> peList, int id) {
        return peList.stream().filter(pe -> pe.getId() == id).findFirst().orElse((T)Pe.NULL);
    }

    /**
     * Gets MIPS Rating of a PE with a given ID.
     *
     * @param peList the PE list where to get a given PE
     * @param id the id of the PE to be get
     * @return the MIPS rating of the PE or -1 if the PE was not found
     * @pre id >= 0
     * @post $none
     */
    public static long getMips(List<? extends Pe> peList, int id) {
        Pe pe = getById(peList, id);
        return (pe == Pe.NULL ? -1 : pe.getCapacity());
    }

    /**
     * Gets total MIPS capacity for a list of PEs.
     *
     * @param peList the pe list
     * @return the total MIPS capacity
     * @pre $none
     * @post $none
     */
    public static long getTotalMips(List<? extends Pe> peList) {
        return peList.stream().mapToLong(Pe::getCapacity).sum();
    }

    /**
     * Gets the max utilization percentage (between [0 and 1]) among all PEs.
     *
     * @param peList the pe list
     * @return the max utilization percentage (between [0 and 1])
     */
    public static double getMaxUtilization(List<? extends Pe> peList) {
        return peList.stream()
            .map(Pe::getPeProvisioner)
            .mapToDouble(PeProvisioner::getUtilization)
            .max().orElse(0);
    }

    /**
     * Gets the max utilization percentage among all PEs allocated to a VM.
     *
     * @param vm the vm to get the maximum utilization percentage
     * @param peList the pe list
     * @return the max utilization percentage
     */
    public static double getMaxUtilizationAmongVmsPes(List<? extends Pe> peList, Vm vm) {
        return peList.stream()
            .map(Pe::getPeProvisioner)
            .filter(pv -> pv.getAllocatedResourceForVm(vm) > 0)
            .mapToDouble(PeProvisioner::getUtilization)
            .max().orElse(0);
    }

    /**
     * Gets the first <tt>FREE</tt> PE.
     *
     * @param <T> the class of PEs inside the List
     * @param peList the PE list
     * @return the first free PE or null if not found
     * @pre $none
     * @post $none
     */
    public static <T extends Pe> T getFreePe(List<T> peList) {
        return peList.stream().filter(Pe::isFree).findFirst().orElse((T)Pe.NULL);
    }

    /**
     * Sets a PE status.
     *
     * @param status the new PE status
     * @param id the id of the PE to be set
     * @param peList the PE list
     * @return <tt>true</tt> if the PE status has been changed, <tt>false</tt>
     * otherwise (PE id might not be exist)
     * @pre peID >= 0
     * @post $none
     */
    public static boolean setPeStatus(List<? extends Pe> peList, int id, Pe.Status status) {
        Pe pe = getById(peList, id);
        return pe.setStatus(status);
    }

    /**
     * Gets the number of <tt>BUSY</tt> PEs.
     *
     * @param peList the PE list
     * @return number of busy PEs
     * @pre $none
     * @post $result >= 0
     */
    public static int getNumberOfBusyPes(List<? extends Pe> peList) {
        return (int)peList.stream()
            .filter(Pe::isBuzy)
            .count();
    }

    /**
     * Gets the number of <tt>FREE</tt> (non-busy) PEs.
     *
     * @param peList the PE list
     * @return number of free PEs
     * @pre $none
     * @post $result >= 0
     */
    public static int getNumberOfFreePes(List<? extends Pe> peList) {
        return (int)peList.stream()
            .filter(Pe::isFree)
            .count();
    }

    /**
     * Sets the status of PEs of a host to FAILED or FREE. NOTE:
     * <tt>hostId</tt> are used for debugging purposes, which is <b>ON</b> by
     * default. Use {@link #setStatusFailed(List, boolean)} if you do not want this
     * information.
     *
     * @param peList the host's PE list to be set as failed or free
     * @param hostId the id of the host
     * @param failed true if the host's PEs have to be set as FAILED, false if
     * they have to be set as FREE.
     * @see #setStatusFailed(java.util.List, boolean)
     */
    public static void setStatusFailed(List<? extends Pe> peList, int hostId, boolean failed) {
        String status = (failed ? "FAILED" : "WORKING");
        setStatusFailed(peList, failed);
    }

    /**
     * Sets the status of PEs of a host to FAILED or FREE.
     *
     * @param <T> the generic type
     * @param peList the host's PE list to be set as failed or free
     * @param failed true if the host's PEs have to be set as FAILED, false if
     * they have to be set as FREE.
     */
    public static <T extends Pe> void setStatusFailed(List<T> peList, boolean failed) {
        final Pe.Status status = (failed ? Pe.Status.FAILED : Pe.Status.FREE);
        peList.forEach(pe -> pe.setStatus(status));
    }

}
