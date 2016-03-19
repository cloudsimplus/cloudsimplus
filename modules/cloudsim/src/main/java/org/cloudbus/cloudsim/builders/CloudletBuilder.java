package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.listeners.EventListener;

/**
 * A Builder class to create {@link Cloudlet} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class CloudletBuilder extends Builder {
    private long defaultLength = 10000;
    private long defaultOutputSize = 300;
    private long defaultFileSize = 300;
    private int  defaultPEs = 1;
    
    private final List<Cloudlet> cloudlets;
    private int numberOfCreatedCloudlets;
    
    private final BrokerBuilderDecorator brokerBuilder;
    private final DatacenterBrokerSimple broker;
    
    private EventListener<Cloudlet, Vm> defaultOnCloudletFinishEventListener = EventListener.NULL;

    public CloudletBuilder(final BrokerBuilderDecorator brokerBuilder, final DatacenterBrokerSimple broker) {
        if(brokerBuilder == null)
           throw new RuntimeException("The brokerBuilder parameter cannot be null."); 
        if(broker == null)
           throw new RuntimeException("The broker parameter cannot be null."); 
        
        this.brokerBuilder = brokerBuilder;            
        this.broker = broker;
        this.cloudlets = new ArrayList<>();
        this.numberOfCreatedCloudlets = 0;        
    }

    public CloudletBuilder setDefaultFileSize(long defaultFileSize) {
        this.defaultFileSize = defaultFileSize;
        return this;
    }

    public List<Cloudlet> getCloudlets() {
        return cloudlets;
    }

    public CloudletBuilder setDefaultPEs(int defaultPEs) {
        this.defaultPEs = defaultPEs;
        return this;
    }

    public long getDefaultLength() {
        return defaultLength;
    }

    public long getDefaultFileSize() {
        return defaultFileSize;
    }

    public long getDefaultOutputSize() {
        return defaultOutputSize;
    }

    public Cloudlet getCloudletById(final int id) {
        for (Cloudlet cloudlet : broker.getCloudletList()) {
            if (cloudlet.getCloudletId() == id) {
                return cloudlet;
            }
        }
        return null;
    }

    public CloudletBuilder setDefaultOutputSize(long defaultOutputSize) {
        this.defaultOutputSize = defaultOutputSize;
        return this;
    }

    public int getDefaultPEs() {
        return defaultPEs;
    }

    public CloudletBuilder setDefaultLength(long defaultLength) {
        this.defaultLength = defaultLength;
        return this;
    }

    public CloudletBuilder createAndSubmitCloudlets(final int amount) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        for (int i = 0; i < amount; i++) {
            Cloudlet cloudlet =
                    new CloudletSimple(
                            numberOfCreatedCloudlets++, defaultLength, 
                            defaultPEs, defaultFileSize, 
                            defaultOutputSize, 
                            utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(broker.getId());
            cloudlet.setOnCloudletFinishEventListener(defaultOnCloudletFinishEventListener);
            cloudlets.add(cloudlet);
        }
        broker.submitCloudletList(cloudlets);
        return this;
    }

    public BrokerBuilderDecorator getBrokerBuilder() {
        return brokerBuilder;
    }

    public EventListener<Cloudlet, Vm> getDefaultOnCloudletFinishEventListener() {
        return defaultOnCloudletFinishEventListener;
    }

    public CloudletBuilder setDefaultOnCloudletFinishEventListener(EventListener<Cloudlet, Vm> defaultOnCloudletFinishEventListener) {
        this.defaultOnCloudletFinishEventListener = defaultOnCloudletFinishEventListener;
        return this;
    }
}
