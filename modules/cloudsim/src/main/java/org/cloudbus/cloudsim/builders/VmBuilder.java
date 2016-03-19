package org.cloudbus.cloudsim.builders;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBrokerSimple;
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
    private Class<? extends CloudletSchedulerAbstract> defaultCloudletSchedulerClass = CloudletSchedulerSpaceShared.class;    
    private long defaultSize = 10000;
    private int  defaultRAM = 512;
    private int  defaultMIPS = 1000;
    private long defaulBw = 1000;
    private int  defaultPEs = 1;
    private int numberOfCreatedVms;
    private final DatacenterBrokerSimple broker;
    private EventListener<Vm, Host> defaultOnHostAllocationListener;
    private EventListener<Vm, Host> defaultOnHostDeallocationListener;
    private EventListener<Vm, Datacenter> defaultOnVmCreationFailureListener;

    public VmBuilder(final DatacenterBrokerSimple broker) {
        if(broker == null)
           throw new RuntimeException("The broker parameter cannot be null."); 
        
        this.broker = broker;
        this.numberOfCreatedVms = 0;
        this.defaultOnHostAllocationListener = EventListener.NULL;
        this.defaultOnHostDeallocationListener = EventListener.NULL;
        this.defaultOnVmCreationFailureListener = EventListener.NULL;
    }
    
    public VmBuilder setDefaultOnHostDeallocationListener(final EventListener<Vm, Host> onHostDeallocationListener) {
        this.defaultOnHostDeallocationListener = onHostDeallocationListener;
        return this;
    }

    public VmBuilder setDefaultMIPS(int defaultMIPS) {
        this.defaultMIPS = defaultMIPS;
        return this;
    }

    public VmBuilder setDefaultBW(long defaultBW) {
        this.defaulBw = defaultBW;
        return this;
    }

    public VmBuilder setDefaultRAM(int defaultRAM) {
        this.defaultRAM = defaultRAM;
        return this;
    }

    public VmBuilder setDefaultOnHostAllocationListener(final EventListener<Vm, Host> onHostAllocationListener) {
        this.defaultOnHostAllocationListener  = onHostAllocationListener;
        return this;
    }

    public VmBuilder setDefaultSize(long defaultSize) {
        this.defaultSize = defaultSize;
        return this;
    }

    public VmBuilder setOnVmCreationFilatureListenerForAllVms(final EventListener<Vm, Datacenter> onVmCreationFailureListener) {
        this.defaultOnVmCreationFailureListener = onVmCreationFailureListener;
        return this;
    }

    public VmBuilder createAndSubmitVms(final int amount) {
        final List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            try {
                Vm vm = new VmSimple(numberOfCreatedVms++, 
                        broker.getId(), defaultMIPS, defaultPEs, defaultRAM, defaulBw, 
                        defaultSize, DatacenterBuilder.VMM, 
                        defaultCloudletSchedulerClass.newInstance());
                vm.setOnHostAllocationListener(defaultOnHostAllocationListener);
                vm.setOnHostDeallocationListener(defaultOnHostDeallocationListener);
                vm.setOnVmCreationFailureListener(defaultOnVmCreationFailureListener);
                vms.add(vm);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("A CloudletScheduler couldn't be instantiated", ex);
            }
        }
        broker.submitVmList(vms);
        return this;
    }

    public long getDefaulBw() {
        return defaulBw;
    }

    public VmBuilder setDefaultPEs(int defaultPEs) {
        this.defaultPEs = defaultPEs;
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

    public long getDefaultSize() {
        return defaultSize;
    }

    public List<Vm> getVms() {
        return broker.getVmList();
    }
    
    public int getDefaultRAM() {
        return defaultRAM;
    }

    public int getDefaultMIPS() {
        return defaultMIPS;
    }

    public int getDefaultPEs() {
        return defaultPEs;
    }

    public Class<? extends CloudletSchedulerAbstract> getDefaultCloudletSchedulerClass() {
        return defaultCloudletSchedulerClass;
    }

    public VmBuilder setDefaultCloudletScheduler(
            Class<? extends CloudletSchedulerAbstract> defaultCloudletScheduler) {
        this.defaultCloudletSchedulerClass = defaultCloudletScheduler;
        return this;
    }
}
