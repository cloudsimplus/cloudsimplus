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
 *
 * @todo @author manoelcampos It should be used an enum for stage types.
 */
public class TaskStage {

    public static enum Stage {EXECUTION, WAIT_SEND, WAIT_RECV, FINISH}; 
    
    /**
     * The id of the TaskStage.
     */
    private int id;

    private int cloudletId;

    /**
     * The stage of the task.
     */
    private Stage stage;

    /**
     * The data length generated for the task (in bytes).
     */
    private double dataLenght;

    /**
     * Execution time for this stage.
     */
    private double time;

    /**
     * Memory used by the task.
     */
    private long memory;

    /**
     * The VM from whom data needed to be received or sent.
     */
    private int vmId;

    public TaskStage(int id, Stage stage, double dataLength, double time, 
            long memory, int vmId, int cloudletId) {
        super();
        this.id = id;
        this.stage = stage;
        this.dataLenght = dataLength;
        this.time = time;
        this.memory = memory;
        this.vmId = vmId;
        this.cloudletId = cloudletId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public int getVmId() {
        return vmId;
    }

    public void setVmId(int vmId) {
        this.vmId = vmId;
    }
}
