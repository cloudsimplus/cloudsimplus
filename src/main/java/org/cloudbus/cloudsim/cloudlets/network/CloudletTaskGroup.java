package org.cloudbus.cloudsim.cloudlets.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.Identifiable;

public class CloudletTaskGroup implements Identifiable {

	private List<CloudletTask> tasks;

	/**
     * The index of the active running task or -1 if no task has started yet.
     */
	private int currentTaskNum;
	private NetworkCloudlet cloudlet;
	
	private long id;

	public CloudletTaskGroup() {
		tasks = new ArrayList<CloudletTask>();
		currentTaskNum = -1;
		id = -1;
	}

	public CloudletTaskGroup(List<CloudletTask> taskList) {
		this();
		tasks.addAll(taskList);
	}

	public void addTask(CloudletTask task) {
		task.setCloudlet(this.cloudlet);
		task.setTaskGroup(this);
		tasks.add(task);
	}

	public void removeTask(CloudletTask task) {
		task.removeTaskGroup();
		tasks.remove(task);
	}

	public List<CloudletTask> getTasks(){
		return Collections.unmodifiableList(tasks);
	}
	
	public int getNumberOfTasks() {
		return tasks.size();
	}

	public Optional<CloudletTask> getCurrentTask() {
		if (getCurrentTaskNum() < 0 || getCurrentTaskNum() >= getNumberOfTasks()) {
            return Optional.empty();
        }

        return Optional.of(tasks.get(getCurrentTaskNum()));
	}

	public int getCurrentTaskNum(){
		return currentTaskNum;
	}
	
	public boolean isEmpty() {
		return tasks.size() < 1;
	}

	public boolean isActive() {
		return currentTaskNum > -1;
	}

	public boolean isFinished() {
		boolean isFinished = true;
		for(CloudletTask t : this.tasks) {
			isFinished = t.isFinished() && isFinished;
		}
		return isFinished;
	}

	public NetworkCloudlet getCloudlet() {
		return cloudlet;
	}

	public void setCloudlet(NetworkCloudlet cloudlet) {
		this.cloudlet = cloudlet;
	}



	/**
     * Gets an {@link Optional} containing the next task in the list if the current task is finished.
     *
     * @return the next task if the current one is finished;
     *         otherwise an {@link Optional#empty()} if the current task is already the last one,
     *         or it is not finished yet.
     */
    Optional<CloudletTask> getNextTaskIfCurrentIsFinished(){

    	if(getCurrentTask().isPresent()) {
    		if(getCurrentTask().get().isActive()) {
                return Optional.empty();
            }
    	}
        

        if(this.currentTaskNum <= tasks.size()-1) {
            this.currentTaskNum++;
        }

        return getCurrentTask();
    }

    /**
     * Change the current task to the next one in order
     * to start executing it, if the current task is finished.
     *
     * @param nextTaskStartTime the time that the next task will start
     * @return true if the current task finished and the next one was started, false otherwise
     */
    boolean startNextTaskIfCurrentIsFinished(final double nextTaskStartTime){
        return
            getNextTaskIfCurrentIsFinished()
                .map(task -> task.setStartTime(nextTaskStartTime))
                .isPresent();
    }

    public long getLength() {
        return getTasks().stream()
                .filter(CloudletTask::isExecutionTask)
                .map(task -> (CloudletExecutionTask)task)
                .mapToLong(CloudletExecutionTask::getLength)
                .sum();
    }

    public void setId(long id) {
    	this.id = id;
    }
    
	@Override
	public long getId() {
		return this.id;
	}

}