package org.cloudbus.cloudsim.power;

import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.HostStateHistoryEntry;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmScheduler;

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
     * Gets the power. For this moment only consumed by all PEs.
     *
     * @return the power
     */
    double getPower();

    /**
     * Gets the power model.
     *
     * @return the power model
     */
    PowerModel getPowerModel();
    
    /**
     * A property that implements the Null Object Design Pattern for {@link PowerHost}
     * objects.
     */
    PowerHost NULL = new PowerHost() {
        @Override public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) { return 0; }
        @Override public double getMaxPower() { return 0; }
        @Override public double getPower() { return 0; }
        @Override public PowerModel getPowerModel() { return PowerModel.NULL; }
        @Override public void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive) {}
        @Override public List<Vm> getCompletedVms() { return Collections.EMPTY_LIST; }
        @Override public double getMaxUtilization() { return 0; }
        @Override public double getMaxUtilizationAmongVmsPes(Vm vm) { return 0; }
        @Override public double getPreviousUtilizationMips() { return 0; }
        @Override public double getPreviousUtilizationOfCpu() { return 0; }
        @Override public List<HostStateHistoryEntry> getStateHistory() { return Collections.EMPTY_LIST; }
        @Override public double getUtilizationMips() { return 0; }
        @Override public long getUtilizationOfBw() { return 0L; }
        @Override public double getUtilizationOfCpu() { return 0; }
        @Override public double getUtilizationOfCpuMips() { return 0; }
        @Override public int getUtilizationOfRam() { return 0; }
        @Override public double updateVmsProcessing(double currentTime) { return 0; }
        @Override public void addMigratingInVm(Vm vm) {}
        @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) { return false; }
        @Override public void deallocatePesForVm(Vm vm) {}
        @Override public List<Double> getAllocatedMipsForVm(Vm vm) { return Collections.EMPTY_LIST; }
        @Override public double getAvailableMips() { return 0; }
        @Override public long getAvailableStorage() { return 0L; }
        @Override public long getBwCapacity() { return 0L; }
        @Override public ResourceProvisioner<Long> getBwProvisioner() { return ResourceProvisioner.NULL_LONG; }
        @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
        @Override public double getMaxAvailableMips() { return 0; }
        @Override public int getNumberOfFreePes() { return 0; }
        @Override public int getNumberOfPes() { return 0; }
        @Override public List<Pe> getPeList() { return Collections.EMPTY_LIST; }
        @Override public int getRamCapacity() { return 0; }
        @Override public ResourceProvisioner<Integer> getRamProvisioner() { return ResourceProvisioner.NULL_INT; }
        @Override public long getStorageCapacity() { return 0L; }
        @Override public double getTotalAllocatedMipsForVm(Vm vm) { return 0; }
        @Override public int getTotalMips() { return 0; }
        @Override public Vm getVm(int vmId, int userId) { return Vm.NULL; }
        @Override public <T extends Vm> List<T> getVmList() { return Collections.EMPTY_LIST; }
        @Override public VmScheduler getVmScheduler() { return VmScheduler.NULL; }
        @Override public <T extends Vm> List<T> getVmsMigratingIn() { return Collections.EMPTY_LIST; }
        @Override public boolean isFailed() { return false; }
        @Override public boolean isSuitableForVm(Vm vm) { return false; }
        @Override public void reallocateMigratingInVms() {}
        @Override public void removeMigratingInVm(Vm vm) {}
        @Override public void setDatacenter(Datacenter datacenter) {}
        @Override public boolean setFailed(String resName, boolean failed) { return false; }
        @Override public boolean setFailed(boolean failed) { return false; }
        @Override public boolean setPeStatus(int peId, Pe.Status status) { return false; }
        @Override public boolean vmCreate(Vm vm) { return false; }
        @Override public void vmDestroy(Vm vm) {}
        @Override public void vmDestroyAll() {}
        @Override public EventListener<HostUpdatesVmsProcessingEventInfo> getOnUpdateVmsProcessingListener() { return EventListener.NULL; }
        @Override public void setOnUpdateVmsProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> onUpdateVmsProcessingListener) {}
        @Override public int getId() { return 0; }
    };
}
