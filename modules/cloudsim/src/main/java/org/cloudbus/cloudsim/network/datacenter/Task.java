/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * Represents various tasks executed by a {@link NetworkCloudlet} during its
 * execution.
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
 */
public class Task {

    public static enum Stage {EXECUTION, WAIT_SEND, WAIT_RECV, FINISH}; 
    
    /**
     * @see #getId() 
     */
    private int id;

    /** @see #getCloudletId() */
    private int cloudletId;

    /**
     * The stage that defines the type of task.
     */
    private Stage stage;

    /**
     * The data length generated for the task (in bytes).
     */
    private double dataLenght;
    
    /**
     * @see #getStartTime() 
     */
    private double startTime;

    /**
     * @see #getExecutionTime() 
     */
    private double executionTime;
    
    /**
     * @see #getTaskExecutionLength() 
     */
    private double taskExecutionLength;    

    /**
     * Memory used by the task.
     */
    private long memory;

    /** @see #getVmId() */
    private int vmId;

    public Task(int id, Stage stage, double dataLength, 
            double taskExecutionLength, long memory, int vmId, int cloudletId) {
        super();
        this.id = id;
        this.stage = stage;
        this.dataLenght = dataLength;
        this.taskExecutionLength = taskExecutionLength;
        this.startTime = 0;
        this.executionTime = 0;
        this.memory = memory;
        this.vmId = vmId;
        this.cloudletId = cloudletId;
    }

    /**
     * Gets the id of the Task.
     * @return 
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the id of the cloudlet where to send of from which it is expected
     * to receive some data, according to the {@link #stage} of the Task.
     * 
     * @return 
     */
    public int getCloudletId() {
        return cloudletId;
    }

    public void setCloudletId(int cloudletId) {
        this.cloudletId = cloudletId;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public double getDataLenght() {
        return dataLenght;
    }

    public void setDataLenght(double dataLenght) {
        this.dataLenght = dataLenght;
    }

    /**
     * Gets the time spent to complete the task.
     * @return 
     */
    public double getExecutionTime() {
        return executionTime;
    }

    /**
     * Sets the time spent to complete the task.
     * @param executionTime 
     */
    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    /**
     * Gets the id of the sender or receiver VM, according to the {@link #stage} of the Task.
     * @return 
     */
    public int getVmId() {
        return vmId;
    }

    /**
     * Sets the id of the sender or receiver VM, according to the {@link #stage} of the Task.
     * @param vmId
     */
    public void setVmId(int vmId) {
        this.vmId = vmId;
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
     * Gets the execution length of the task (in MI),
     * only used for tasks of the type {@link Stage#EXECUTION}
     * 
     * @return 
     */
    public double getTaskExecutionLength() {
        return taskExecutionLength;
    }

    /**
     * Sets the execution length of the task (in MI),
     * only used for tasks of the type {@link Stage#EXECUTION}
     * 
     * @param taskExecutionLength 
     */
    public void setTaskExecutionLength(double taskExecutionLength) {
        this.taskExecutionLength = taskExecutionLength;
    }
    
    public boolean isFinished(){
        return executionTime > 0;
    }
    
}
