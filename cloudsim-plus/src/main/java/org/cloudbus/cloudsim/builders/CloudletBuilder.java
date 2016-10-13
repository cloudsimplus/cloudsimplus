package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;

/**
 * A Builder class to create {@link Cloudlet} objects.
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletBuilder extends Builder {
    private long length = 10000;
    private long outputSize = 300;
    private long fileSize = 300;
    private int  pes = 1;
    /**
     * The id of the VM to be binded to created cloudlets.
     * If the value is equals to -1, none VMs will be binded to the cloudlets.
     */
    private int  vmId = -1;
    private UtilizationModel utilizationModelRam;
    private UtilizationModel utilizationModelCpu;
    private UtilizationModel utilizationModelBw;

    private final List<Cloudlet> cloudlets;
    private int numberOfCreatedCloudlets;

    private final BrokerBuilderDecorator brokerBuilder;
    private final DatacenterBrokerSimple broker;

    private EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener = EventListener.NULL;
	private List<String> requiredFiles;

	public CloudletBuilder(final BrokerBuilderDecorator brokerBuilder, final DatacenterBrokerSimple broker) {
        if(brokerBuilder == null)
           throw new RuntimeException("The brokerBuilder parameter cannot be null.");
        if(broker == null)
           throw new RuntimeException("The broker parameter cannot be null.");

        this.brokerBuilder = brokerBuilder;
        setUtilizationModelCpuRamAndBw(new UtilizationModelFull());
        this.broker = broker;
        this.cloudlets = new ArrayList<>();
		this.requiredFiles = new ArrayList<>();
        this.numberOfCreatedCloudlets = 0;
    }

    /**
     * Sets the same utilization model for CPU, RAM and BW.
     * By this way, at a time t, every one of the 3 resources will use the same percentage
     * of its capacity.
     *
     * @param utilizationModel the utilization model to set
     * @return
     */
    public final CloudletBuilder setUtilizationModelCpuRamAndBw(UtilizationModel utilizationModel) {
        if(utilizationModel != null){
            this.utilizationModelCpu = utilizationModel;
            this.utilizationModelRam = utilizationModel;
            this.utilizationModelBw = utilizationModel;
        }
        return this;
    }

    public CloudletBuilder setVmId(int defaultVmId) {
        this.vmId = defaultVmId;
        return this;
    }

    public CloudletBuilder setFileSize(long defaultFileSize) {
        this.fileSize = defaultFileSize;
        return this;
    }

    public CloudletBuilder setRequiredFiles(List<String> requiredFiles){
	    this.requiredFiles = requiredFiles;
	    return this;
    }

    public List<Cloudlet> getCloudlets() {
        return cloudlets;
    }

    public CloudletBuilder setPEs(int defaultPEs) {
        this.pes = defaultPEs;
        return this;
    }

    public long getLength() {
        return length;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getOutputSize() {
        return outputSize;
    }

    public Cloudlet getCloudletById(final int id) {
        for (Cloudlet cloudlet : broker.getCloudletsWaitingList()) {
            if (cloudlet.getId() == id) {
                return cloudlet;
            }
        }
        return Cloudlet.NULL;
    }

    public CloudletBuilder setOutputSize(long defaultOutputSize) {
        this.outputSize = defaultOutputSize;
        return this;
    }

    public int getPes() {
        return pes;
    }

    public CloudletBuilder setLength(long defaultLength) {
        this.length = defaultLength;
        return this;
    }

    public CloudletBuilder createAndSubmitOneCloudlet() {
        return createAndSubmitCloudlets(1);
    }

    public CloudletBuilder createCloudlets(final int amount) {
        createCloudletsInternal(amount);
        return this;
    }

    private List<Cloudlet> createCloudletsInternal(final int amount) {
        List<Cloudlet> localList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final int cloudletId = numberOfCreatedCloudlets++;
            Cloudlet cloudlet =
                    new CloudletSimple(
                            cloudletId, length,
                            pes, fileSize,
                            outputSize,
                            utilizationModelCpu, utilizationModelRam, utilizationModelBw);
            cloudlet.setUserId(broker.getId());
            cloudlet.setOnCloudletFinishEventListener(onCloudletFinishEventListener);
	        requiredFiles.stream().forEach(cloudlet::addRequiredFile);
            localList.add(cloudlet);
        }
        cloudlets.addAll(localList);
        return localList;
    }

    public CloudletBuilder createAndSubmitCloudlets(final int amount) {
        List<Cloudlet> localList = createCloudletsInternal(amount);
        broker.submitCloudletList(localList);
        if(vmId != -1){
            localList.forEach(c -> broker.bindCloudletToVm(c.getId(), vmId));
        }
        return this;
    }

    /**
     * Submits the list of created cloudlets to the latest created broker.
     * @return the CloudletBuilder instance
     */
    public CloudletBuilder submitCloudlets(){
        broker.submitCloudletList(cloudlets);
        return this;
    }

    public BrokerBuilderDecorator getBrokerBuilder() {
        return brokerBuilder;
    }

    public EventListener<VmToCloudletEventInfo> getOnCloudletFinishEventListener() {
        return onCloudletFinishEventListener;
    }

    public CloudletBuilder setOnCloudletFinishEventListener(EventListener<VmToCloudletEventInfo> defaultOnCloudletFinishEventListener) {
        this.onCloudletFinishEventListener = defaultOnCloudletFinishEventListener;
        return this;
    }

    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    public CloudletBuilder setUtilizationModelRam(UtilizationModel utilizationModelRam) {
        if(utilizationModelRam != null){
            this.utilizationModelRam = utilizationModelRam;
        }
        return this;
    }

    public UtilizationModel getUtilizationModelCpu() {
        return utilizationModelCpu;
    }

    public CloudletBuilder setUtilizationModelCpu(UtilizationModel utilizationModelCpu) {
        if(utilizationModelCpu != null){
            this.utilizationModelCpu = utilizationModelCpu;
        }
        return this;
    }

    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    public CloudletBuilder setUtilizationModelBw(UtilizationModel utilizationModelBw) {
        if(utilizationModelBw != null){
            this.utilizationModelBw = utilizationModelBw;
        }
        return this;
    }
}
