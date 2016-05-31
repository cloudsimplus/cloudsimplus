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

import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.network.datacenter.TaskStage.Stage;

/**
 * NetworkCloudlet class extends Cloudlet to support simulation of complex
 * applications. Each NetworkCloudlet represents a task of the
 * application. Each task consists of several stages.
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
 * @todo @author manoelcampos Attributes should be private
 * @todo @author manoelcampos The different cloudlet classes should have a class
 * hierarchy, by means of a super class and/or interface.
 * @todo @author manoelcampos The class has a lot of duplicated attributes that
 * exist in the super class. It has to be assessed if they in fact store
 * different data. If so, the attributes should have a different name to avoid
 * the strong confusion with the super class attributes.
 */
public class NetworkCloudlet extends CloudletSimple implements Comparable<Object> {

    /**
     * Time when cloudlet will be submitted.
     */
    public double submittime;

    /**
     * Cloudlet's start time.
     */
    public double starttime;

    /**
     * Time when cloudlet finishes execution.
     */
    public double finishtime;

    /**
     * Execution time for cloudlet.
     */
    public double exetime;

    /**
     * Number of cloudlet's stages .
     */
    private double numberOfStages;

    /**
     * Current stage of cloudlet execution, according to the values of the
     * {@link Stage} enum.
     */
    private int currentStageNum;

    /**
     * Start time of the current stage.
     */
    private double timeToStartStage;

    /**
     * Time spent in the current stage.
     */
    private double timeSpentInStage;

    /**
     * All stages which cloudlet execution.
     */
    private final List<TaskStage> stages;
    
    /** @see #getAppCloudlet() */
    private AppCloudlet appCloudlet;
    
    private NetworkCloudlet cloudlet;

    /**
     * Cloudlet's memory.
     *
     * @todo Required, allocated, used memory? It doesn't appear to be used.
     */
    private long memory;

    public NetworkCloudlet(
            int cloudletId,
            long cloudletLength,
            int pesNumber,
            long cloudletFileSize,
            long cloudletOutputSize,
            long memory,
            UtilizationModel utilizationModelCpu,
            UtilizationModel utilizationModelRam,
            UtilizationModel utilizationModelBw) {
        super(
                cloudletId,
                cloudletLength,
                pesNumber,
                cloudletFileSize,
                cloudletOutputSize,
                utilizationModelCpu,
                utilizationModelRam,
                utilizationModelBw);

        currentStageNum = -1;
        this.memory = memory;
        stages = new ArrayList<>();
    }

    @Override
    public int compareTo(Object arg0) {
        /**
         * @todo @author manoelcampos It doesn't make sense to always return 0.
         * Or implement or remove the method
         */
        return 0;
    }

    public double getSubmittime() {
        return submittime;
    }

    /**
     * Gets the {@link AppCloudlet} that owns this NetworkCloudlet.
     * @return 
     */
    public AppCloudlet getAppCloudlet() {
        return appCloudlet;
    }

    /**
     * Set the {@link AppCloudlet} that owns this NetworkCloudlet.
     * @param appCloudlet
     */
    public void setAppCloudlet(AppCloudlet appCloudlet) {
        this.appCloudlet = appCloudlet;
    }

    public double getNumberOfStages() {
        return numberOfStages;
    }

    public void setNumberOfStages(double numberOfStages) {
        this.numberOfStages = numberOfStages;
    }

    public List<TaskStage> getStages() {
        return stages;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public int getCurrentStageNum() {
        return currentStageNum;
    }

    public void setCurrentStageNum(int currentStageNum) {
        this.currentStageNum = currentStageNum;
    }

    public double getTimeToStartStage() {
        return timeToStartStage;
    }

    public void setTimeToStartStage(double timeToStartStage) {
        this.timeToStartStage = timeToStartStage;
    }

    public double getTimeSpentInStage() {
        return timeSpentInStage;
    }

    public void setTimeSpentInStage(double timeSpentInStage) {
        this.timeSpentInStage = timeSpentInStage;
    }

    /**
     * @return the cloudlet
     */
    public NetworkCloudlet getCloudlet() {
        return cloudlet;
    }

  

}
