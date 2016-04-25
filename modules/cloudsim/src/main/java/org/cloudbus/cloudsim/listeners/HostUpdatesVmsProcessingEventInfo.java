package org.cloudbus.cloudsim.listeners;

import org.cloudbus.cloudsim.Host;

/**
 * An {@link EventInfo} class that stores data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when a Host updates the processing of all its VMs.  
 * 
 * @see Host#getOnUpdateVmsProcessingListener() 
 * @author Manoel Campos da Silva Filho
 */
public class HostUpdatesVmsProcessingEventInfo extends HostEventInfoSimple  {
    private double completionTimeOfNextFinishingCloudlet;

    public HostUpdatesVmsProcessingEventInfo(double time, Host host) {
        super(time, host);
    }

    /**
     * @return the completion time of one next finishing cloudlet
     */
    public double getCompletionTimeOfNextFinishingCloudlet() {
        return completionTimeOfNextFinishingCloudlet;
    }

    /**
     * Sets the completion time of one next finishing cloudlet
     * @param completionTimeOfNextFinishingCloudlet
     */
    public void setCompletionTimeOfNextFinishingCloudlet(double completionTimeOfNextFinishingCloudlet) {
        this.completionTimeOfNextFinishingCloudlet = completionTimeOfNextFinishingCloudlet;
    }
    
    
}
