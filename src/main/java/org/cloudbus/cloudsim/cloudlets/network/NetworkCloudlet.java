/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

import java.util.*;

/**
 * NetworkCloudlet to support simulation of complex applications.
 * Each application is compounded of one or more {@link CloudletTask}s
 * for performing different kinds of processing.
 *
 * <p>
 * Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 *
 * TODO Check how to implement the NULL pattern for this class.
 */
public class NetworkCloudlet extends CloudletSimple {

    /** @see #getTasks() */
    private final List<CloudletTaskGroup> taskGroups;
    private CloudletTaskGroup defaultTaskGroup;

    /**
     * Creates a NetworkCloudlet with no priority and file size and output size equal to 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber the number of PEs this Cloudlet requires
     */
    public NetworkCloudlet(final long length, final int pesNumber) {
        this(-1, length, pesNumber);
    }

    /**
     * Creates a NetworkCloudlet with no priority and file size and output size equal to 1.
     *
     * @param id the unique ID of this cloudlet
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber the pes number
     */
    public NetworkCloudlet(final int id,  final long length, final int pesNumber) {
        super(id, length, pesNumber);
        this.defaultTaskGroup = new CloudletTaskGroup();
        this.taskGroups = new ArrayList<>();
        this.taskGroups.add(defaultTaskGroup);
    }

    public double getNumberOfTasks() {
    	
    	int numberOfTasks = 0;
    	
    	for(CloudletTaskGroup g : taskGroups) {
    		numberOfTasks += g.getNumberOfTasks();
    	}
    	
        return numberOfTasks;
    }

    /**
     * @return a read-only list of Cloudlet's tasks.
     */
    public List<CloudletTask> getTasks() {
    	
    	ArrayList<CloudletTask> list = new ArrayList<CloudletTask>();
    	
    	for(CloudletTaskGroup g : taskGroups) {
    		list.addAll(g.getTasks());
    	}
    	
        return Collections.unmodifiableList(list);
    }
    
    public List<CloudletTaskGroup> getTaskGroups(){
    	return Collections.unmodifiableList(taskGroups);
    }

    /**
     * Checks if some Cloudlet Task has started yet.
     *
     * @return true if some task has started, false otherwise
     */
    
    public boolean isTasksStarted() {
    	
    	boolean tasksStarted = false;
    	
    	for(CloudletTaskGroup g : taskGroups) {
    		tasksStarted = g.isActive() || tasksStarted;
    	}
    	
        return tasksStarted;
    }

    /**
     * Gets an {@link Optional} containing the current task
     * or an {@link Optional#empty()} if there is no current task yet.
     * @return
     */
    public Optional<CloudletTask> getCurrentTask() {
        return defaultTaskGroup.getCurrentTask();
    }
    
    public List<CloudletTask> getAllCurrentTasks(){
    	ArrayList<CloudletTask> allCurrentTasks = new ArrayList<CloudletTask>();
    	
    	for(CloudletTaskGroup g : getTaskGroups()) {
    		g.getCurrentTask().ifPresent(task -> allCurrentTasks.add(task));
    	}
    	
    	return allCurrentTasks;
    }


    @Override
    public boolean isFinished() {
        final boolean allTasksFinished = taskGroups.stream().allMatch(CloudletTaskGroup::isFinished);
        return super.isFinished() && allTasksFinished;
    }

    /**
     * {@inheritDoc}
     * <p>The length of a NetworkCloudlet is the
     * length sum of all its {@link CloudletExecutionTask}'s.</p>
     * @return the length sum of all {@link CloudletExecutionTask}'s
     */
    @Override
    public long getLength() {
        return getTaskGroups().stream()
                .mapToLong(CloudletTaskGroup::getLength)
                .sum();
    }

    /**
     * Adds a task to the {@link #getTasks() task list}
     * and links the task to the NetworkCloudlet.
     *
     * @param task Task to be added
     * @return the NetworkCloudlet instance
     */
    public NetworkCloudlet addTask(final CloudletTask task) {
        Objects.requireNonNull(task);
        task.setCloudlet(this);
        task.setTaskGroup(defaultTaskGroup);
        defaultTaskGroup.addTask(task);
        return this;
    }
    
    public boolean startNextTaskForAllGroupsWhereCurrentIsFinished(final double nextTaskStartTime) {
    	
    	boolean taskWasStarted = false;
    	
    	for(CloudletTaskGroup g : getTaskGroups()) {
    		taskWasStarted = g.startNextTaskIfCurrentIsFinished(nextTaskStartTime) || taskWasStarted;
    	}
    	
    	return taskWasStarted;
    }

    @Override
    public NetworkVm getVm() {
        return (NetworkVm)super.getVm();
    }

    @Override
    public Cloudlet setVm(final Vm vm) {
        if(vm == Vm.NULL)
            return super.setVm(NetworkVm.NULL);

        if(vm instanceof NetworkVm)
            return super.setVm(vm);

        throw new IllegalArgumentException("NetworkCloudlet can just be executed by a NetworkVm");
    }
}
