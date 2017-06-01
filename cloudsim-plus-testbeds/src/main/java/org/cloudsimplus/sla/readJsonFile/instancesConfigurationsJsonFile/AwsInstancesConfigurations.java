package org.cloudsimplus.sla.readJsonFile.instancesConfigurationsJsonFile;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the AWS instances configurations.
 *
 * Created by raysaoliveira on 01/06/17.
 */
public class AwsInstancesConfigurations {

    private List<AwsEC2Instance> instances;

    public AwsInstancesConfigurations() {
        this.instances = new ArrayList<>();
    }

    /**
     * @return the instances
     */
    public List<AwsEC2Instance> getInstances() {
        return instances;
    }

    /**
     * @param instances the instances to set
     */
    public void setInstances(List<AwsEC2Instance> instances) {
        if(instances == null){
            instances = new ArrayList<>();
        }
        this.instances = instances;
    }
}
