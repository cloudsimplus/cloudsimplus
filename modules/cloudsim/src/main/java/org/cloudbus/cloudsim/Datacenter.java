package org.cloudbus.cloudsim;

import java.util.Collections;
import java.util.List;

/**
 * An Interface to be implemented by Datacenter objects in order to provide
 * Datacenter functionalities. It also implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException} 
 * when using the {@link Datacenter#NULL} object instead
 * of attributing {@code null} to {@link Datacenter} variables.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface Datacenter {
    int getId();
    
    /**
     * Adds a file into the resource's storage before the experiment starts.
     * If the file is a master file, then it will be registered to the RC
     * when the experiment begins.
     *
     * @param file a DataCloud file
     * @return a tag number denoting whether this operation is a success or not
     */
    int addFile(File file);

    /**
     * Gets the host list.
     * @param <T> The generic type
     * @return the host list
     */
    <T extends Host> List<T> getHostList();
    
    Host getHost(final int index);

    /**
     * Gets the policy to be used by the datacenter to allocate VMs into hosts.
     *
     * @return the VM allocation policy
     * @see AbstractVmAllocationPolicy
     */
    VmAllocationPolicy getVmAllocationPolicy();

    /**
     * Gets the list of VMs submitted to be ran in some host of this datacenter.
     *
     * @param <T>
     * @return the vm list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * A property that implements the Null Object Design Pattern for {@link Datacenter}
     * objects.
     */
    public static final Datacenter NULL = new Datacenter() {
        @Override public int getId() { return 0; }
        @Override public int addFile(File file) { return 0;}
        @Override public List<Host> getHostList() { return Collections.emptyList();}
        @Override public VmAllocationPolicy getVmAllocationPolicy() { return VmAllocationPolicy.NULL; }
        @Override public List<Vm> getVmList() { return Collections.emptyList(); }
        @Override public Host getHost(final int index) { return Host.NULL; }
    };    
}
