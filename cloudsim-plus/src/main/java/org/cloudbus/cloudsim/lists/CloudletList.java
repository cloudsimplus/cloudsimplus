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
import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * CloudletList is a collection of operations on lists of Cloudlets.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class CloudletList {

    /**
     * Gets a {@link Cloudlet} with a given id.
     *
     * @param <T>
     * @param cloudletList the list of existing Cloudlets
     * @param id the Cloudlet id
     * @return a Cloudlet with the given ID or $null if not found
     */
    public static <T extends Cloudlet> T getById(List<T> cloudletList, int id) {
        return cloudletList.stream().filter(c -> c.getId() == id).findFirst().orElse((T)Cloudlet.NULL);
    }

    /**
     * Sorts the Cloudlets in a list based on their lengths.
     *
     * @param cloudletList the cloudlet list
     * @pre $none
     * @post $none
     */
    public static void sort(List<? extends Cloudlet> cloudletList) {
        Collections.sort(cloudletList, (Cloudlet a, Cloudlet b) -> {
            Long aLen = a.getCloudletTotalLength();
            Long bLen = b.getCloudletTotalLength();
            return aLen.compareTo(bLen);
        });
    }

}
