/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.core.Identificable;

/**
 * AppCloudlet class represents an application that an user submits for execution
 * within a datacenter and is consisted of several {@link NetworkCloudlet NetworkCloudlets}.
 * An AppCloudlet can represent, for instance: 
 * <ul>
 * <li>a Multi-tier Web Application 
 * compounded of Web Tier, Application Tier and a Database Tier where each
 * tier is represented by a {@link NetworkCloudlet} inside the AppCloudlet.
 * </li>
 * <li>
 * A <a href="https://en.wikipedia.org/wiki/Message_Passing_Interface">MPI application</a>  
 * compounded of several communicating processes 
 * represented b {@link NetworkCloudlet NetworkCloudlets} inside the AppCloudlet.
 * </li>
 * </ul>
 *
 * <br/>Please refer to following publication for more details:<br/>
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 *
 * @todo @author manoelcampos If it is an application/cloudlet, it would extend the Cloudlet interface.
 * In the case of such interface has more attributes and methods than required
 * by this class, a common interface would be created.
 *
 * @todo @author manoelcampos The attributes have to be defined as private.
 */
public class AppCloudlet implements Identificable {
    private final int id;
    
    /**
     * The list of {@link NetworkCloudlet} that this AppCloudlet represents.
     */
    private List<NetworkCloudlet> networkCloudletList;

    /**
     * This attribute doesn't appear to be used.
     */
    public double accuracy;

    public double execTime;
    
    
    public AppCloudlet(int id) {
        super();
        this.id = id;
        this.networkCloudletList = new ArrayList<>();
    }

    /**
     * Gets the list of network cloudlets that are part of the AppCloudlet.
     * 
     * @return 
     */
    public List<NetworkCloudlet> getNetworkCloudletList() {
        return networkCloudletList;
    }

    /**
     * Sets a list of network cloudlets that will be part of the AppCloudlet.
     * 
     * @param networkCloudletList 
     */
    public void setNetworkCloudletList(List<NetworkCloudlet> networkCloudletList) {
        this.networkCloudletList = networkCloudletList;
    }

    @Override
    public int getId() {
        return id;
    }
}
