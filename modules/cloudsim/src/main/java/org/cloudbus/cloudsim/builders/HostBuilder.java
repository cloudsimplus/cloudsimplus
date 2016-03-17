package org.cloudbus.cloudsim.builders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.PeSimple;
import org.cloudbus.cloudsim.VmSchedulerAbstract;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A Builder class to create {@link Host} objects.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class HostBuilder extends Builder {
    private double defaultMIPS = 2000;
    private int    defaultPEs = 1;
    private long   defaultBw = 10000;
    private long   defaultStorage = Consts.MILLION;
    private int    defaultRam = 1024;
    private Class<? extends VmSchedulerAbstract> defaultVmSchedulerClass = VmSchedulerTimeShared.class;
    
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
                    new PeBuilder().create(defaultPEs, defaultMIPS);
            Constructor cons =
                    defaultVmSchedulerClass.getConstructor(new Class[]{List.class});

            final Host host = new HostSimple(id,
                    new ResourceProvisionerSimple<>(new Ram(defaultRam)),
                    new ResourceProvisionerSimple<>(new Bandwidth(defaultBw)),
                    defaultStorage, peList, (VmSchedulerAbstract) cons.newInstance(peList));
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

    public double getDefaultMIPS() {
        return defaultMIPS;
    }

    public HostBuilder setDefaultMIPS(double defaultMIPS) {
        this.defaultMIPS = defaultMIPS;
        return this;
    }

    public int getDefaultPEs() {
        return defaultPEs;
    }

    public HostBuilder setDefaultPEs(int defaultPEs) {
        this.defaultPEs = defaultPEs;
        return this;
    }

    public long getDefaultBw() {
        return defaultBw;
    }

    public HostBuilder setDefaultBw(long defaultBw) {
        this.defaultBw = defaultBw;
        return this;
    }

    public long getDefaultStorage() {
        return defaultStorage;
    }

    public HostBuilder setDefaultStorage(long defaultStorage) {
        this.defaultStorage = defaultStorage;
        return this;
    }

    public int getDefaultRam() {
        return defaultRam;
    }

    public HostBuilder setDefaultRam(int defaultRam) {
        this.defaultRam = defaultRam;
        return this;
    }

    public Class<? extends VmSchedulerAbstract> getDefaultVmSchedulerClass() {
        return defaultVmSchedulerClass;
    }

    public HostBuilder setDefaultVmSchedulerClass(Class<? extends VmSchedulerAbstract> defaultVmSchedulerClass) {
        this.defaultVmSchedulerClass = defaultVmSchedulerClass;
        return this;
    }

}
