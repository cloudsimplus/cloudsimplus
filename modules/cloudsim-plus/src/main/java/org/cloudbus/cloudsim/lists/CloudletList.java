/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.lists;

import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

/**
 * CloudletList is a collection of operations on lists of Cloudlets.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class CloudletList {
    
    /**
     * A index to indicate that an object was not found into a list.
     */
    public static final int NOT_FOUND_INDEX = -1;

    /**
     * Gets a {@link Cloudlet} with a given id.
     *
     * @param <T>
     * @param cloudletList the list of existing Cloudlets
     * @param id the Cloudlet id
     * @return a Cloudlet with the given ID or $null if not found
     */
    public static <T extends Cloudlet> T getById(List<T> cloudletList, int id) {
        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getId() == id) {
                return (T) cloudlet;
            }
        }
        /**
         * @todo @author manoelcampos Should return an empty object instead of
         * null, in order to avoid NullPointerExceptions. Check the same for the
         * other lists such as VmList and HostList.
         */
        return null;
    }

    /**
     * Gets the position of a cloudlet with a given id.
     *
     * @param <T>
     * @param cloudletList the list of existing cloudlets
     * @param id the cloudlet id
     * @return the position of the cloudlet with the given id or -1 if not found
     */
    public static <T extends Cloudlet> int getPositionById(List<T> cloudletList, int id) {
        int i = 0;
        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getId() == id) {
                return i;
            }
            i++;
        }
        return NOT_FOUND_INDEX;
    }

    /**
     * Sorts the Cloudlets in a list based on their lengths.
     *
     * @param <T> The generic type
     * @param cloudletList the cloudlet list
     * @pre $none
     * @post $none
     */
    public static <T extends Cloudlet> void sort(List<T> cloudletList) {
        Collections.sort(cloudletList, (T a, T b) -> {
            Double cla = Double.valueOf(a.getCloudletTotalLength());
            Double clb = Double.valueOf(b.getCloudletTotalLength());
            return cla.compareTo(clb);
        });
    }
    
}
