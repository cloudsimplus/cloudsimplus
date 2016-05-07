/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.datacenter;

/**
 * TaskStage represents various stages a {@link NetworkCloudlet} can have during
 * execution. Four stage types which are possible: {@link NetworkConstants#EXECUTION},
 * {@link NetworkConstants#WAIT_SEND}, {@link NetworkConstants#WAIT_RECV},
 * {@link NetworkConstants#FINISH}.
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
 * @todo Attributes should be defined as private.
 */
public class TaskStage {

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
     * Execution executionTime for this stage.
     */
    private double executionTime;

    /**
     * Memory used by the task.
     */
    private long memory;

    /** @see #getVmId() */
    private int vmId;

    public TaskStage(int id, Stage stage, double dataLength, double executionTime, 
            long memory, int vmId, int cloudletId) {
        super();
        this.id = id;
        this.stage = stage;
        this.dataLenght = dataLength;
        this.executionTime = executionTime;
        this.memory = memory;
        this.vmId = vmId;
        this.cloudletId = cloudletId;
    }

    /**
     * Gets the id of the TaskStage.
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

    public double getExecutionTime() {
        return executionTime;
    }

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
}
