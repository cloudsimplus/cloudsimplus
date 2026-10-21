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
package org.cloudsimplus.resources;

import org.cloudsimplus.core.Machine;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

/// A limit on the CPU capacity a [Machine] can request from the machine where it runs
/// (such as a [Vm] requesting MIPS from its [Host], or a container from its VM).
/// It represents features such as VM CPU caps (KVM/libvirt quota, Xen cap, VMware limit)
/// and container CPU quotas (Docker `--cpus`).
///
/// It's not a [Processor] property, since a physical machine such as a [Host] never has a cap.
/// The cap only reduces the MIPS each core can use; the number of cores stays the same.
///
/// Implementations must be immutable, so one instance can be shared by any number of machines.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public sealed interface CpuCap permits CpuCapNone {
    /// A cap that doesn't limit the CPU: the machine can use the full MIPS of all its cores.
    CpuCap NONE = new CpuCapNone();

    /// Gets the MIPS each core of a machine can use, according to this cap.
    /// @param cores the number of cores of the machine
    /// @param coreMips the MIPS capacity of each core
    /// @return the MIPS each core can use (from 0 to `coreMips`)
    double limitCoreMips(long cores, double coreMips);
}
