package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface that represent data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when a VM migration is successful or not.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.3
 */
public interface DatacenterVmMigrationEventInfo extends VmDatacenterEventInfo {
    /**
     * Gets the VM that started a migration process.
     * @return
     */
    Vm getVm();

    /**
     * Indicates if the VM was successfully migrated or not.
     * @return
     */
    boolean isMigrationSuccessful();

    /**
     * Gets a VmDatacenterEventInfo instance from the given parameters.
     * The {@link #getDatacenter() Datacenter} attribute is defined as the {@link Datacenter} where the {@link Vm}
     * is running and the {@link #getTime()} is the current simulation time..
     *
     * @param listener the listener to be notified about the event
     * @param vm the {@link Vm} that fired the event
     */
    static DatacenterVmMigrationEventInfo of(final EventListener<DatacenterVmMigrationEventInfo> listener, final Vm vm, final boolean migrated) {
        final double time = vm.getSimulation().clock();
        return new DatacenterVmMigrationEventInfo() {
            @Override
            public Datacenter getDatacenter() {
                return vm.getHost().getDatacenter();
            }

            @Override
            public Vm getVm() {
                return vm;
            }

            @Override
            public boolean isMigrationSuccessful() {
                return migrated;
            }

            @Override
            public double getTime() {
                return time;
            }

            @Override
            public EventListener<DatacenterVmMigrationEventInfo> getListener() {
                return listener;
            }
        };
    }
}
