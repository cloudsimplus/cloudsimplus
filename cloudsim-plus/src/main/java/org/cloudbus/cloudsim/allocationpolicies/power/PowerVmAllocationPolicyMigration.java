package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.vms.Vm;

import javax.xml.crypto.Data;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An interface to be implemented by VM allocation policy for power-aware VMs
 * that detects {@link PowerHost} under and over CPU utilization.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface PowerVmAllocationPolicyMigration extends PowerVmAllocationPolicy {
    /**
     * Checks if host is over utilized.
     *
     * @param host the host
     * @return true, if the host is over utilized; false otherwise
     */
    boolean isHostOverUtilized(PowerHost host);

    /**
     * Gets the host CPU utilization threshold to detect over utilization.
     * It is a percentage value from 0 to 1.
     * Whether it is a static or dynamically defined threshold depends on each implementing class.
     *
     * @param host the host to get the over utilization threshold
     * @return the over utilization threshold
     */
    double getOverUtilizationThreshold(PowerHost host);

    /**
     * Checks if host is under utilized.
     *
     * @param host the host
     * @return true, if the host is under utilized; false otherwise
     */
    boolean isHostUnderUtilized(PowerHost host);

    /**
     * Gets the percentage of total CPU utilization
     * to indicate that a host is under used and its VMs have to be migrated.
     *
     * @return the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)
     */
    double getUnderUtilizationThreshold();

    /**
     * Sets the percentage of total CPU utilization
     * to indicate that a host is under used and its VMs have to be migrated.
     *
     * @param underUtilizationThreshold the under utilization threshold (in scale is from 0 to 1, where 1 is 100%)
     */
    void setUnderUtilizationThreshold(double underUtilizationThreshold);

    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerVmAllocationPolicyMigration}
     * objects.
     */
    PowerVmAllocationPolicyMigration NULL = new PowerVmAllocationPolicyMigration(){
        @Override public PowerHost findHostForVm(Vm vm) { return PowerHost.NULL; }
        @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
        @Override public void setDatacenter(Datacenter datacenter) {}
        @Override public boolean allocateHostForVm(Vm vm) { return false; }
        @Override public boolean allocateHostForVm(Vm vm, Host host) { return false; }
        @Override public void deallocateHostForVm(Vm vm) {}
        @Override public Host getHost(Vm vm) { return Host.NULL; }
        @Override public Host getHost(int vmId, int userId) { return Host.NULL; }
        @Override public <T extends Host> List<T> getHostList() { return Collections.emptyList(); }
        @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) { return Collections.emptyMap(); }
        @Override public boolean isHostOverUtilized(PowerHost host) { return false;}
        @Override public boolean isHostUnderUtilized(PowerHost host) { return false;}
        @Override public double getOverUtilizationThreshold(PowerHost host) { return 0; }
        @Override public double getUnderUtilizationThreshold() { return 0; }
        @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold) {}
    };

}
