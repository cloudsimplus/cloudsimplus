package org.cloudbus.cloudsim.hosts.power;

import java.util.Collections;
import java.util.List;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkload;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;

/**
 * An interface to be implemented by power-aware Host classes.
 * The interface implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link PowerHost#NULL} object instead of attributing {@code null} to
 * {@link PowerHost} variables.
 *
 *
 * @author Manoel Campos da Silva Filho
 */
public interface PowerHost extends HostDynamicWorkload {
    /**
     * Gets the energy consumption using linear interpolation of the utilization change.
     *
     * @param fromUtilization the initial utilization percentage
     * @param toUtilization the final utilization percentage
     * @param time the time
     * @return the energy
     */
    double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time);

    /**
     * Gets the max power that can be consumed by the host.
     *
     * @return the max power
     */
    double getMaxPower();

    /**
     * Gets the power consumption of the host. For this moment it only computes the power consumed by PEs.
     *
     * @return the power consumption
     */
    double getPower();

    /**
     * Gets the power model.
     *
     * @return the power model
     */
    PowerModel getPowerModel();

    /**
     * Sets the power model.
     *
     * @param powerModel the new power model
     * @return
     */
    PowerHost setPowerModel(PowerModel powerModel);


    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerHost}
     * objects.
     */
    PowerHost NULL = new PowerHost() {
        @Override public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) { return 0; }
        @Override public double getMaxPower() { return 0; }
        @Override public double getPower() { return 0; }
        @Override public PowerModel getPowerModel() { return PowerModel.NULL; }
        @Override public PowerHost setPowerModel(PowerModel powerModel) { return PowerHost.NULL; }
        @Override public void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive) {}
        @Override public List<Vm> getCompletedVms() { return Collections.EMPTY_LIST; }
        @Override public double getMaxUtilization() { return 0; }
        @Override public double getMaxUtilizationAmongVmsPes(Vm vm) { return 0; }
        @Override public double getPreviousUtilizationMips() { return 0; }
        @Override public double getPreviousUtilizationOfCpu() { return 0; }
        @Override public List<HostStateHistoryEntry> getStateHistory() { return Collections.EMPTY_LIST; }
        @Override public long getUtilizationOfBw() { return 0; }
        @Override public double getUtilizationOfCpu() { return 0; }
        @Override public double getUtilizationOfCpuMips() { return 0; }
        @Override public long getUtilizationOfRam() { return 0; }
        @Override public double updateVmsProcessing(double currentTime) { return 0; }
        @Override public void addMigratingInVm(Vm vm) {}
        @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) { return false; }
        @Override public void deallocatePesForVm(Vm vm) {}
        @Override public List<Double> getAllocatedMipsForVm(Vm vm) { return Collections.EMPTY_LIST; }
        @Override public double getAvailableMips() { return 0; }
        @Override public long getAvailableStorage() { return 0; }
        @Override public long getBwCapacity() { return 0; }
        @Override public ResourceProvisioner getBwProvisioner() { return ResourceProvisioner.NULL; }
        @Override public Host setBwProvisioner(ResourceProvisioner bwProvisioner) { return Host.NULL; }
        @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
        @Override public double getMaxAvailableMips() { return 0; }
        @Override public int getNumberOfFreePes() { return 0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public List<Pe> getPeList() { return Collections.EMPTY_LIST; }
        @Override public long getRamCapacity() { return 0; }
        @Override public ResourceProvisioner getRamProvisioner() { return ResourceProvisioner.NULL; }
        @Override public Host setRamProvisioner(ResourceProvisioner ramProvisioner) { return Host.NULL; }
        @Override public long getStorageCapacity() { return 0; }
        @Override public double getTotalAllocatedMipsForVm(Vm vm) { return 0; }
        @Override public int getTotalMips() { return 0; }
        @Override public Vm getVm(int vmId, int userId) { return Vm.NULL; }
        @Override public <T extends Vm> List<T> getVmList() { return Collections.EMPTY_LIST; }
        @Override public VmScheduler getVmScheduler() { return VmScheduler.NULL; }
        @Override public Host setVmScheduler(VmScheduler vmScheduler) { return Host.NULL; }
        @Override public <T extends Vm> List<T> getVmsMigratingIn() { return Collections.EMPTY_LIST; }
        @Override public boolean isFailed() { return false; }
        @Override public boolean isSuitableForVm(Vm vm) { return false; }
        @Override public void reallocateMigratingInVms() {}
        @Override public void removeMigratingInVm(Vm vm) {}
        @Override public void setDatacenter(Datacenter datacenter) {}
        @Override public boolean setFailed(boolean failed) { return false; }
        @Override public Simulation getSimulation() { return Simulation.NULL; }
        @Override public Host setSimulation(Simulation simulation) { return this; }
        @Override public boolean setPeStatus(int peId, Pe.Status status) { return false; }
        @Override public boolean vmCreate(Vm vm) { return false; }
        @Override public void destroyVm(Vm vm) {}
        @Override public void destroyAllVms() {}
        @Override public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener() { return EventListener.NULL; }
        @Override public Host setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener) { return Host.NULL; }
        @Override public int getId() { return 0; }
        @Override public long getNumberOfWorkingPes(){return 0L;}
    };
}
