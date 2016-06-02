/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * Represents a processing task that can be executed by a {@link NetworkCloudlet}.
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
 * @author Manoel Campos da Silva Filho
 * 
 * @since CloudSim Toolkit 1.0
 * 
 * @todo @author manoelcampos
 * Classes {@link CloudletTask} and {@link org.cloudbus.cloudsim.Cloudlet}
 * and {@link org.cloudbus.cloudsim.ResCloudlet} share a common
 * set of attributes that would be defined by a common interface.
 */
public class CloudletExecutionTask extends CloudletTask {
    
    /**
     * @see #getLength() 
     */
    private long length;    
    
    /**
     * @see #getTotalExecutedLenght() 
     */
    private long totalExecutedLenght;
    
    /**
     * Creates a new task.
     * @param id task id
     * @param memory memory used by the task
     * @param executionLength the execution length of the task (in MI)
     * @param networkCloudlet the NetworkCloudlet that the task belongs to
     */
    public CloudletExecutionTask(int id, long memory, long executionLength, NetworkCloudlet networkCloudlet) {
        super(id, memory, networkCloudlet);
        this.length = executionLength;
    }

    /**
     * Creates a new task without assigning it to a {@link NetworkCloudlet}
     * (that has to be assigned further).
     * 
     * @param id task id
     * @param memory memory used by the task
     * @param executionLength the execution length of the task (in MI)
     * @see #setNetworkCloudlet(org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet) 
     */
    public CloudletExecutionTask(int id, long memory, long executionLength) {
        this(id, memory, executionLength, null);
    }

    /**
     * Gets the execution length of the task (in MI),
     * only used for tasks of the type {@link Type#EXECUTION}
     * 
     * @return 
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the execution length of the task (in MI),
     * only used for tasks of the type {@link Type#EXECUTION}
     * 
     * @param length 
     */
    public void setLength(long length) {
        this.length = length;
    }
    
    /**
     * Indicates if the task is finished or not, depending
     * on the number of MI {@link #getTotalExecutedLenght()  executed so far}.
     * 
     * @return true if the task executed all the MI
     * defined in its execution length, false otherwise
     */
    public boolean isFinished(){
        return totalExecutedLenght == length;
    }

    /**
     * Gets the length of this CloudletTask that has been executed so far (in MI).
     * @return 
     */
    public long getTotalExecutedLenght() {
        return totalExecutedLenght;
    }

    /**
     * Adds a given number of MI to the {@link #getTotalExecutedLenght() total
     * MI executed so far} by the cloudlet.
     * @param partialExecutionLength the partial number of MI just executed that has to be to added
     * to the total MI executed so far
     * @return {@inheritDoc}
     */
    public boolean increaseTaskProgress(long partialExecutionLength){
        return process(this.totalExecutedLenght+partialExecutionLength);
    }

    /**
     * Sets a given number of MI to the {@link #getTotalExecutedLenght() total
     * MI executed so far} by the cloudlet.
     * @param totalExecutedLenghtSoFar the number of MI executed so far
     * @return {@inheritDoc}
     */
    public boolean process(long totalExecutedLenghtSoFar) {
        if(totalExecutedLenghtSoFar <= 0)
            return false;
        
        this.totalExecutedLenght = Math.min(totalExecutedLenghtSoFar, length);
        return true;
    }

}
