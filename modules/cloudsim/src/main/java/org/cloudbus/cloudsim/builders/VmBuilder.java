package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.listeners.EventListener;

/**
 * A Builder class to create {@link Vm} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class VmBuilder {
    private Class<? extends CloudletSchedulerAbstract> cloudletSchedulerClass = CloudletSchedulerSpaceShared.class;    
    private long size = 10000;
    private int  ram = 512;
    private int  mips = 1000;
    private long bw = 1000;
    private int  pes = 1;
    private int numberOfCreatedVms;
    private final DatacenterBrokerSimple broker;
    private EventListener<Vm, Host> onHostAllocationListener;
    private EventListener<Vm, Host> onHostDeallocationListener;
    private EventListener<Vm, Datacenter> onVmCreationFailureListener;

    public VmBuilder(final DatacenterBrokerSimple broker) {
        if(broker == null)
           throw new RuntimeException("The broker parameter cannot be null."); 
        
        this.broker = broker;
        this.numberOfCreatedVms = 0;
        this.onHostAllocationListener = EventListener.NULL;
        this.onHostDeallocationListener = EventListener.NULL;
        this.onVmCreationFailureListener = EventListener.NULL;
    }
    
    public VmBuilder setOnHostDeallocationListener(final EventListener<Vm, Host> onHostDeallocationListener) {
        this.onHostDeallocationListener = onHostDeallocationListener;
        return this;
    }

    public VmBuilder setMips(int defaultMIPS) {
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

    public VmBuilder setOnHostAllocationListener(final EventListener<Vm, Host> onHostAllocationListener) {
        this.onHostAllocationListener  = onHostAllocationListener;
        return this;
    }

    public VmBuilder setSize(long defaultSize) {
        this.size = defaultSize;
        return this;
    }

    public VmBuilder setOnVmCreationFilatureListenerForAllVms(final EventListener<Vm, Datacenter> onVmCreationFailureListener) {
        this.onVmCreationFailureListener = onVmCreationFailureListener;
        return this;
    }

    public VmBuilder createAndSubmitVms(final int amount) {
        final List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            try {
                Vm vm = new VmSimple(numberOfCreatedVms++, 
                        broker.getId(), mips, pes, ram, bw, 
                        size, DatacenterBuilder.VMM, 
                        cloudletSchedulerClass.newInstance());
                vm.setOnHostAllocationListener(onHostAllocationListener);
                vm.setOnHostDeallocationListener(onHostDeallocationListener);
                vm.setOnVmCreationFailureListener(onVmCreationFailureListener);
                vms.add(vm);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("A CloudletScheduler couldn't be instantiated", ex);
            }
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
        for (Vm vm : broker.getVmList()) {
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
        return broker.getVmList();
    }
    
    public int getRam() {
        return ram;
    }

    public int getMips() {
        return mips;
    }

    public int getPes() {
        return pes;
    }

    public Class<? extends CloudletSchedulerAbstract> getCloudletSchedulerClass() {
        return cloudletSchedulerClass;
    }

    public VmBuilder setCloudletScheduler(
            Class<? extends CloudletSchedulerAbstract> defaultCloudletScheduler) {
        this.cloudletSchedulerClass = defaultCloudletScheduler;
        return this;
    }
}
