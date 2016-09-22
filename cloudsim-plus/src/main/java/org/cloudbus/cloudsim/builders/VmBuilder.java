package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.listeners.DatacenterToVmEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;

/**
 * A Builder class to create {@link Vm} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class VmBuilder {
    private CloudletScheduler cloudletScheduler;    
    private long size = 10000;
    private int  ram = 512;
    private double  mips = 1000;
    private long bw = 1000;
    private int  pes = 1;
    private int numberOfCreatedVms;
    private final DatacenterBrokerSimple broker;
    private EventListener<HostToVmEventInfo> onHostAllocationListener;
    private EventListener<HostToVmEventInfo> onHostDeallocationListener;
    private EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener;
    private EventListener<HostToVmEventInfo> onUpdateVmProcessingListener;

    public VmBuilder(final DatacenterBrokerSimple broker) {
        if(broker == null)
           throw new RuntimeException("The broker parameter cannot be null."); 
        
        this.broker = broker;
        this.numberOfCreatedVms = 0;
        this.onHostAllocationListener = EventListener.NULL;
        this.onHostDeallocationListener = EventListener.NULL;
        this.onVmCreationFailureListener = EventListener.NULL;
        this.onUpdateVmProcessingListener = EventListener.NULL;
        this.cloudletScheduler = new CloudletSchedulerSpaceShared();
    }
    
    public VmBuilder setOnHostDeallocationListener(final EventListener<HostToVmEventInfo> onHostDeallocationListener) {
        this.onHostDeallocationListener = onHostDeallocationListener;
        return this;
    }

    public VmBuilder setMips(double defaultMIPS) {
        this.mips = defaultMIPS;
        return this;
    }

    public VmBuilder setBw(long defaultBW) {
        this.bw = defaultBW;
        return this;
    }

    public VmBuilder setRam(int defaultRAM) {
        this.ram = defaultRAM;
        return this;
    }

    public VmBuilder setOnHostAllocationListener(final EventListener<HostToVmEventInfo> onHostAllocationListener) {
        this.onHostAllocationListener  = onHostAllocationListener;
        return this;
    }

    public VmBuilder setSize(long defaultSize) {
        this.size = defaultSize;
        return this;
    }

    public VmBuilder setOnVmCreationFilatureListenerForAllVms(final EventListener<DatacenterToVmEventInfo> onVmCreationFailureListener) {
        this.onVmCreationFailureListener = onVmCreationFailureListener;
        return this;
    }
    
    public VmBuilder createAndSubmitOneVm() {
        return createAndSubmitVms(1);
    }

    public VmBuilder createAndSubmitVms(final int amount) {
        final List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Vm vm = new VmSimple(numberOfCreatedVms++, 
                    broker.getId(), mips, pes, ram, bw, 
                    size, DatacenterBuilder.VMM, 
                    cloudletScheduler);
            vm.setOnHostAllocationListener(onHostAllocationListener);
            vm.setOnHostDeallocationListener(onHostDeallocationListener);
            vm.setOnVmCreationFailureListener(onVmCreationFailureListener);
            vm.setOnUpdateVmProcessingListener(onUpdateVmProcessingListener);
            vms.add(vm);
        }
        broker.submitVmList(vms);
        return this;
    }

    public long getBw() {
        return bw;
    }

    public VmBuilder setPes(int defaultPEs) {
        this.pes = defaultPEs;
        return this;
    }

    public Vm getVmById(final int id) {
        for (Vm vm : broker.getVmsWaitingList()) {
            if (vm.getId() == id) {
                return vm;
            }
        }
        return null;
    }

    public long getSize() {
        return size;
    }

    public List<Vm> getVms() {
        return broker.getVmsWaitingList();
    }
    
    public int getRam() {
        return ram;
    }

    public double getMips() {
        return mips;
    }

    public int getPes() {
        return pes;
    }

    public CloudletScheduler getCloudletSchedulerClass() {
        return cloudletScheduler;
    }

    public VmBuilder setCloudletScheduler(
            CloudletScheduler defaultCloudletScheduler) {
        this.cloudletScheduler = defaultCloudletScheduler;
        return this;
    }

    public EventListener<HostToVmEventInfo> getOnUpdateVmProcessingListener() {
        return onUpdateVmProcessingListener;
    }

    public VmBuilder setOnUpdateVmProcessingListener(EventListener<HostToVmEventInfo> onUpdateVmProcessing) {
        if(onUpdateVmProcessing != null) {
            this.onUpdateVmProcessingListener = onUpdateVmProcessing;
        }
        return this;
    }
}
