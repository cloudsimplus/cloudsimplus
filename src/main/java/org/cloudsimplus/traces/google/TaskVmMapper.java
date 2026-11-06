/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.traces.google;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/// Maps tasks from a "task events" trace file to VMs, so that the tasks of a user
/// in the same machine run inside the same VM, and that VM is placed in that machine.
///
/// Trace lines must be provided in timestamp order, as they are in the trace files.
/// All the lines are processed before the simulation starts, so the machine and
/// resource requests of a task are known even though they appear after the line that submits it.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
final class TaskVmMapper {
    /// Identifies the VM of a user in a machine.
    private record VmKey(String userName, long machineId) {}

    /// The amount of resources requested by tasks.
    private record Demand(double cpuCores, double ram, double localDiskSpace) {
        private static final Demand NONE = new Demand(0, 0, 0);

        static Demand of(final TaskEvent evt) {
            return new Demand(evt.getResourceRequestForCpuCores(), evt.getResourceRequestForRam(), evt.getResourceRequestForLocalDiskSpace());
        }

        Demand plus(final Demand other) {
            return new Demand(cpuCores + other.cpuCores, ram + other.ram, localDiskSpace + other.localDiskSpace);
        }

        Demand minus(final Demand other) {
            return new Demand(cpuCores - other.cpuCores, ram - other.ram, localDiskSpace - other.localDiskSpace);
        }

        Demand max(final Demand other) {
            return new Demand(Math.max(cpuCores, other.cpuCores), Math.max(ram, other.ram), Math.max(localDiskSpace, other.localDiskSpace));
        }
    }

    private final Function<TaskVmRequest, Vm> vmCreationFunction;

    /// The VM of each task that has a known machine, where each key is the task ID.
    private final Map<Long, VmKey> taskVmKeys = new HashMap<>();

    /// The resources requested by each running task, where each key is the task ID.
    private final Map<Long, Demand> runningTasks = new HashMap<>();

    /// The resources currently requested by the running tasks of each VM.
    private final Map<VmKey, Demand> currentDemands = new HashMap<>();

    /// The highest amount of resources requested by the running tasks of each VM.
    private final Map<VmKey, Demand> peakDemands = new HashMap<>();

    /// The machine where each created VM must be placed.
    /// It uses object identity because a Vm's hash code changes when it gets an ID or a broker.
    private final Map<Vm, Long> vmMachines = new IdentityHashMap<>();

    TaskVmMapper(final Function<TaskVmRequest, Vm> vmCreationFunction) {
        this.vmCreationFunction = requireNonNull(vmCreationFunction);
    }

    /// Updates the machine and the resource demand of a task, according to a trace line.
    /// @param evt the event read from the trace line
    void process(final TaskEvent evt) {
        registerMachine(evt);
        switch (evt.getType()) {
            case SCHEDULE -> startTask(evt);
            case EVICT, FAIL, FINISH, KILL, LOST -> stopTask(evt);
            case UPDATE_RUNNING -> updateRunningTask(evt);
            default -> { /* Other events don't change the running tasks. */ }
        }
    }

    // shortcut: a task stays in its first machine, consider moving it when traces reschedule tasks on other machines often.
    private void registerMachine(final TaskEvent evt) {
        if (evt.getMachineId() >= 0) {
            taskVmKeys.putIfAbsent(evt.getUniqueTaskId(), new VmKey(evt.getUserName(), evt.getMachineId()));
        }
    }

    private void startTask(final TaskEvent evt) {
        final long taskId = evt.getUniqueTaskId();
        final VmKey key = taskVmKeys.get(taskId);
        if (key == null || runningTasks.containsKey(taskId)) {
            return;
        }

        final var demand = Demand.of(evt);
        runningTasks.put(taskId, demand);
        final var current = currentDemands.getOrDefault(key, Demand.NONE).plus(demand);
        currentDemands.put(key, current);
        peakDemands.merge(key, current, Demand::max);
    }

    private void stopTask(final TaskEvent evt) {
        final long taskId = evt.getUniqueTaskId();
        final Demand demand = runningTasks.remove(taskId);
        if (demand != null) {
            currentDemands.compute(taskVmKeys.get(taskId), (key, current) -> requireNonNull(current).minus(demand));
        }
    }

    /// Replaces the resource demand of a task that is running with the one in the trace line.
    private void updateRunningTask(final TaskEvent evt) {
        if (runningTasks.containsKey(evt.getUniqueTaskId())) {
            stopTask(evt);
            startTask(evt);
        }
    }

    /// Creates the VMs, submits them to their brokers and binds the Cloudlets to them.
    /// Cloudlets without a known machine aren't changed, so their brokers select a VM for them.
    /// The data that was only required to compute the VMs is then released.
    ///
    /// @param cloudlets the Cloudlets created from the trace
    /// @param brokerManager the manager that provides the broker of each user
    void bindCloudletsToVms(final Collection<Cloudlet> cloudlets, final BrokerManager brokerManager) {
        final Map<VmKey, Vm> vms = new HashMap<>();
        for (final Cloudlet cloudlet : cloudlets) {
            final VmKey key = taskVmKeys.get(cloudlet.getId());
            if (key != null) {
                cloudlet.setVm(vms.computeIfAbsent(key, k -> createVm(k, brokerManager)));
            }
        }

        taskVmKeys.clear();
        runningTasks.clear();
        currentDemands.clear();
        peakDemands.clear();
    }

    private Vm createVm(final VmKey key, final BrokerManager brokerManager) {
        final var peak = peakDemands.getOrDefault(key, Demand.NONE);
        final var request = new TaskVmRequest(key.userName(), key.machineId(), peak.cpuCores(), peak.ram(), peak.localDiskSpace());
        final Vm vm = vmCreationFunction.apply(request);
        vmMachines.put(vm, key.machineId());
        brokerManager.getBroker(key.userName()).submitVm(vm);
        return vm;
    }

    /// {@return the ID of the machine where a VM must be placed or null if the VM wasn't created by this mapper}
    /// @param vm the VM to get the machine ID
    @Nullable
    Long getMachineId(final Vm vm) {
        return vmMachines.get(vm);
    }
}
