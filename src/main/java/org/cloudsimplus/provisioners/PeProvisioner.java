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
package org.cloudsimplus.provisioners;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;

/// An interface that represents the provisioning policy
/// used by a host to provide virtual [Pe]s to its virtual machines.
/// It gets a physical PE from a [Host] and manages it to provide this PE as a virtual PE for
/// [Vm]s. That way, a given PE might be shared between different VMs.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.1
/// @deprecated The Host [org.cloudsimplus.resources.Processor] manages its cores without a provisioner.
/// See https://github.com/cloudsimplus/cloudsimplus/issues/592
@Deprecated(forRemoval = true)
public sealed interface PeProvisioner
    extends ResourceProvisioner permits PeProvisionerAbstract, PeProvisionerNull
{

    /// An attribute that implements the Null Object Design Pattern for PeProvisioner objects.
    PeProvisioner NULL = new PeProvisionerNull();

    /// Sets the [Pe] that this provisioner will manage.
    ///
    /// @param pe the Pe to set
    void setPe(Pe pe);

    /// Allocates an amount of MIPS from the physical PE to a new virtual PE for a given VM.
    /// The virtual PE to be added will use the total or partial MIPS capacity of the
    /// physical PE.
    ///
    /// @param vm the virtual machine for which the new virtual PE is being allocated
    /// @param mipsCapacity the MIPS to be allocated to the virtual PE of the given VM
    /// @return `true` if the virtual PE could be allocated; `false` otherwise
    @Override
    boolean allocateResourceForVm(Vm vm, long mipsCapacity);

    /// Gets the amount of allocated MIPS from the physical PE to a virtual PE of a VM.
    ///
    /// @param vm the virtual machine to get the allocated virtual PE MIPS
    /// @return the allocated virtual PE MIPS
    @Override
    long getAllocatedResourceForVm(Vm vm);

    /// Releases the virtual PE allocated to a given VM.
    ///
    /// @param vm the vm to release the virtual PE
    /// @return the previously allocated MIPS for the VM's virtual PE
    @Override
    long deallocateResourceForVm(Vm vm);

    /// @return the total allocated MIPS from the physical PE.
    @Override
    long getTotalAllocatedResource();

    /// @return the utilization percentage of the PE (in scale from 0 to 1).
    double getUtilization();
}
