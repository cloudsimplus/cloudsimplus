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

/**
 * NetworkCloudlet class extends Cloudlet to support simulation of complex
 * applications. Each NetworkCloudlet represents a task of the application. 
 * Each task consists of several tasks.
 *
 * <p>
 * Please refer to following publication for more details:
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
     * Execution time for cloudlet.
     */
    public double exetime;

    /**
     * Current stage of cloudlet execution, according to the values of the
     * {@link Type} enum.
     */
    private int currentTaskNum;

    /**
     * All tasks which cloudlet execution.
     */
    private final List<CloudletTask> tasks;

    /**
     * @see #getAppCloudlet()
     */
    private AppCloudlet appCloudlet;

    /**
     * Cloudlet's memory.
     *
     * @todo Required, allocated, used memory? It doesn't appear to be used.
     */
    private long memory;

    public NetworkCloudlet(
            int id,
            final long cloudletLength,
            int pesNumber,
            long cloudletFileSize,
            long cloudletOutputSize,
            long memory,
            UtilizationModel utilizationModelCpu,
            UtilizationModel utilizationModelRam,
            UtilizationModel utilizationModelBw) {
        super(
                id,
                cloudletLength,
                pesNumber,
                cloudletFileSize,
                cloudletOutputSize,
                utilizationModelCpu,
                utilizationModelRam,
                utilizationModelBw);

        currentTaskNum = -1;
        this.memory = memory;
        tasks = new ArrayList<>();
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
     *
     * @return
     */
    public AppCloudlet getAppCloudlet() {
        return appCloudlet;
    }

    /**
     * Set the {@link AppCloudlet} that owns this NetworkCloudlet.
     *
     * @param appCloudlet
     */
    public void setAppCloudlet(AppCloudlet appCloudlet) {
        this.appCloudlet = appCloudlet;
    }

    public double getNumberOfTasks() {
        return tasks.size();
    }

    public List<CloudletTask> getTasks() {
        return tasks;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    /**
     * Gets the current task number.
     *
     * @return return the current task number if the cloudlet started executing,
     * -1 if the cloudlet hasn't started executing or the number of tasks if all
     * tasks have finished executing.
     */
    public int getCurrentTaskNum() {
        return currentTaskNum;
    }
    
    /**
     * Indicates if the NetworkCloudlet is executing 
     * its last task.
     * @return 
     */
    public boolean isTheLastTask(){
        return getCurrentTaskNum() >= tasks.size() - 1;
    }

    /**
     * Change the current task to the next one, in order
     * to start executing it.
     * @param nextTaskStartTime the time that the next task will start
     * @return the next task or null if there isn't any next task
     */
    public CloudletTask startNextTask(double nextTaskStartTime){
        CloudletTask previousTask = getCurrentTask();
        if(previousTask != null){
            previousTask.computeExecutionTime(nextTaskStartTime);
        }
        
        this.currentTaskNum++;
        CloudletTask nextTask = getCurrentTask();
        if(nextTask != null){
            nextTask.setStartTime(nextTaskStartTime);
        }
        return nextTask;
    }

    /**
     * Gets the current task.
     *
     * @return
     */
    protected CloudletTask getCurrentTask() {
        if (currentTaskNum < 0 || currentTaskNum >= tasks.size()) {
            return null;
        }

        return tasks.get(currentTaskNum);
    }

    @Override
    public boolean isFinished() {
        return super.isFinished() && getCurrentTaskNum() >= getNumberOfTasks();
    }

}
