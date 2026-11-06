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

import org.cloudsimplus.vms.Vm;

/// The data to create the [Vm] that runs the tasks of a user in a given machine,
/// computed from a "task events" trace file.
/// See [GoogleTaskEventsTraceReader#mapTasksToHosts(java.util.function.Function)].
///
/// The peak values are the highest sum of the resource requests of the tasks running at the same time.
/// They are normalized according to the largest machine in the trace, as the fields they come from,
/// and may be higher than 1, since those fields don't limit how much a machine can be over-committed.
/// They are zero if no task with a known machine was scheduled.
///
/// @param userName the user that owns the tasks
/// @param machineId the ID of the machine where the tasks run
/// @param peakCpuCores the peak of the requested CPU cores
/// @param peakRam the peak of the requested RAM
/// @param peakLocalDiskSpace the peak of the requested local disk space
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public record TaskVmRequest(String userName, long machineId, double peakCpuCores, double peakRam, double peakLocalDiskSpace) {
}
