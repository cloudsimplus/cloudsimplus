package org.cloudbus.cloudsim.builders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmSchedulerAbstract;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A Builder class to create {@link Host} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class HostBuilder extends Builder {
    private double mips = 2000;
    private int    pes = 1;
    private long   bw = 10000;
    private long   storage = Consts.MILLION;
    private int    ram = 1024;
    private Class<? extends VmSchedulerAbstract> vmSchedulerClass = VmSchedulerTimeShared.class;
    private EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener = EventListener.NULL;
    
    private int numberOfCreatedHosts;
    private final List<Host> hosts;

    public HostBuilder() {
        this.hosts = new ArrayList<>();
        this.numberOfCreatedHosts = 0;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    private Host createHost(final int id) {
        try {
            final List<Pe> peList =
                    new PeBuilder().create(pes, mips);
            Constructor cons =
                    vmSchedulerClass.getConstructor(new Class[]{List.class});

            final Host host = new HostSimple(id,
                    new ResourceProvisionerSimple<>(new Ram(ram)),
                    new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                    storage, peList, (VmSchedulerAbstract) cons.newInstance(peList));
            host.setOnUpdateVmsProcessingListener(onUpdateVmsProcessingListener);
            hosts.add(host);
            return host;
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("It wasn't possible to instantiate VmScheduler", ex);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("It wasn't possible to instantiate VmScheduler", ex);
        }
    }
    
    public HostBuilder createOneHost() {
        return createHosts(1);
    }
    
    public HostBuilder createHosts(final int amount) {
        validateAmount(amount);
        
        for (int i = 0; i < amount; i++) {
            hosts.add(createHost(numberOfCreatedHosts++));
        }
        return this;
    }    

    public double getMips() {
        return mips;
    }

    public HostBuilder setMips(double defaultMIPS) {
        this.mips = defaultMIPS;
        return this;
    }

    public int getPes() {
        return pes;
    }

    public HostBuilder setPes(int defaultPEs) {
        this.pes = defaultPEs;
        return this;
    }

    public long getBw() {
        return bw;
    }

    public HostBuilder setBw(long defaultBw) {
        this.bw = defaultBw;
        return this;
    }

    public long getStorage() {
        return storage;
    }

    public HostBuilder setStorage(long defaultStorage) {
        this.storage = defaultStorage;
        return this;
    }

    public int getRam() {
        return ram;
    }

    public HostBuilder setRam(int defaultRam) {
        this.ram = defaultRam;
        return this;
    }

    public Class<? extends VmSchedulerAbstract> getVmSchedulerClass() {
        return vmSchedulerClass;
    }

    public HostBuilder setVmSchedulerClass(Class<? extends VmSchedulerAbstract> defaultVmSchedulerClass) {
        this.vmSchedulerClass = defaultVmSchedulerClass;
        return this;
    }

    public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener() {
        return onUpdateVmsProcessingListener;
    }

    public HostBuilder setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener) {
        this.onUpdateVmsProcessingListener = onUpdateVmsProcessingListener;
        return this;
    }

}
