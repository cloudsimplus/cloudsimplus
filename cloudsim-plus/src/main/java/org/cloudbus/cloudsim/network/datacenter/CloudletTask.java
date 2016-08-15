/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

import org.cloudbus.cloudsim.core.Identificable;

/**
 * Represents one of many tasks that can be executed by a {@link NetworkCloudlet}.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 * 
 * @todo @author manoelcampos
 * Classes {@link CloudletTask} and {@link org.cloudbus.cloudsim.Cloudlet}
 * and {@link org.cloudbus.cloudsim.ResCloudlet} share a common
 * set of attributes that would be defined by a common interface.
 */
public abstract class CloudletTask implements Identificable {
    
    /**
     * @see #getId() 
     */
    private int id;

    /**
     * @see #getStartTime() 
     */
    private double startTime;

    /**
     * @see #getExecutionTime() 
     */
    private double executionTime;
    
    /**
     * @see #getMemory() 
     */
    private long memory;
    
    /**
     * @see #getNetworkCloudlet() 
     */
    private NetworkCloudlet networkCloudlet;

    /**
     * Creates a new task.
     * @param id task id
     */
    public CloudletTask(int id) {
        super();
        this.id = id;
        this.startTime = 0;
        this.executionTime = 0;
        this.memory = 0;
    }

    /**
     * Gets the id of the CloudletTask.
     * @return 
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the CloudletTask.
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the time spent to complete the task.
     * @return 
     */
    public double getExecutionTime() {
        return executionTime;
    }

    /**
     * Computes and sets the time spent to complete the task.
     * @param currentSimulationTime the current simulation time
     * @return the computed execution time
     */
    public double computeExecutionTime(double currentSimulationTime) {
        this.executionTime = currentSimulationTime - getStartTime();
        return this.executionTime;
    }

    /**
     * Gets the memory amount used by the task.
     * @return 
     */
    public long getMemory() {
        return memory;
    }

    /**
     * Sets the memory amount used by the task.
     * @param memory 
     */
    public void setMemory(long memory) {
        this.memory = memory;
    }

    /**
     * Gets the time the task started executing.
     * @return 
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Sets the time the task started executing.
     * @param startTime 
     */
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    } 

    /**
     * Gets the NetworkCloudlet that the task belongs to.
     * @return 
     */
    public NetworkCloudlet getNetworkCloudlet() {
        return networkCloudlet;
    }    

    public void setNetworkCloudlet(NetworkCloudlet networkCloudlet) {
        this.networkCloudlet = networkCloudlet;
    }
    
}
