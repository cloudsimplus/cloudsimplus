package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.core.Nameable;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import java.util.Collections;
import java.util.List;

/**
 * An interface to be implemented by each class that provides Datacenter
 * features. The interface implements the Null Object Design Pattern in order to
 * start avoiding {@link NullPointerException} when using the
 * {@link Datacenter#NULL} object instead of attributing {@code null} to
 * {@link Datacenter} variables.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Datacenter extends Nameable {

    /**
     * Adds a file into the resource's storage before the experiment starts. If
     * the file is a master file, then it will be registered to the RC when the
     * experiment begins.
     *
     * @param file a DataCloud file
     * @return a tag number denoting whether this operation is a success or not
     */
    int addFile(File file);

    /**
     * Gets the host list.
     *
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
     * Gets the scheduling interval to process each event received by the
     * datacenter (in seconds). This value defines the interval in which
     * processing of Cloudlets will be updated. The interval doesn't affect the
     * processing of cloudlets, it only defines in which interval the processing
     * will be updated. For instance, if it is set a interval of 10 seconds, the
     * processing of cloudlets will be updated at every 10 seconds. By this way,
     * trying to get the amount of instructions the cloudlet has executed after
     * 5 seconds, by means of {@link Cloudlet#getCloudletFinishedSoFar(int)}, it
     * will not return an updated value. By this way, one should set the
     * scheduling interval to 5 to get an updated result. As longer is the
     * interval, faster will be the simulation execution.
     *
     * @return the scheduling interval
     */
    double getSchedulingInterval();

    /**
     * Gets the datacenter characteristics.
     *
     * @return the datacenter characteristics
     */
    DatacenterCharacteristics getCharacteristics();

    /**
     * A property that implements the Null Object Design Pattern for
     * {@link Datacenter} objects.
     */
    Datacenter NULL = new Datacenter() {
        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public int addFile(File file) {
            return 0;
        }

        @Override
        public List<Host> getHostList() {
            return Collections.emptyList();
        }

        @Override
        public VmAllocationPolicy getVmAllocationPolicy() {
            return VmAllocationPolicy.NULL;
        }

        @Override
        public List<Vm> getVmList() {
            return Collections.emptyList();
        }

        @Override
        public Host getHost(final int index) {
            return Host.NULL;
        }

        @Override
        public double getSchedulingInterval() {
            return 0;
        }

        @Override
        public DatacenterCharacteristics getCharacteristics() {
            return DatacenterCharacteristics.NULL;
        }
    };
}
