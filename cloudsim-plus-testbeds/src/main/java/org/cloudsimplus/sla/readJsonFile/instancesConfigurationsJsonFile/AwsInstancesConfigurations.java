package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the AWS instances configurations.
 *
 * Created by raysaoliveira on 01/06/17.
 */
public class AwsInstancesConfigurations {

    private List<InstancesAws> instances;

    public AwsInstancesConfigurations() {
        this.instances = new ArrayList<>();
    }

    /**
     * @return the instances
     */
    public List<InstancesAws> getInstances() {
        return instances;
    }

    /**
     * @param instances the instances to set
     */
    public void setInstances(List<InstancesAws> instances) {
        if(instances == null){
            instances = new ArrayList<>();
        }
        this.instances = instances;
    }
}
