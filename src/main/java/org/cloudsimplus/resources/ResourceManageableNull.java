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

/// A class that implements the Null Object Design Pattern for
/// [ResourceManageable] class.
///
/// @author Manoel Campos da Silva Filho
/// @see ResourceManageable#NULL
final class ResourceManageableNull implements ResourceManageable {
    @Override public boolean setCapacity(long newCapacity) {
        return false;
    }
    @Override public boolean sumCapacity(long amountToSum) { return false; }
    @Override public boolean addCapacity(long capacityToAdd) { return false; }
    @Override public boolean removeCapacity(long capacityToRemove) { return false; }
    @Override public boolean allocateResource(long amountToAllocate) {
        return false;
    }
    @Override public boolean setAllocatedResource(long newTotalAllocatedResource) {
        return false;
    }
    @Override public boolean deallocateResource(long amountToDeallocate) {
        return false;
    }
    @Override public boolean deallocateAndRemoveResource(long amountToDeallocate) { return false; }
    @Override public long deallocateAllResources() {
        return 0L;
    }
    @Override public boolean isResourceAmountBeingUsed(long amountToCheck) {
        return false;
    }
    @Override public boolean isSuitable(long newTotalAllocatedResource) {
        return false;
    }
    @Override public long getCapacity() {
        return 0L;
    }
    @Override public long getAvailableResource() {
        return 0L;
    }
    @Override public long getAllocatedResource() {
        return 0L;
    }
    @Override public boolean isAmountAvailable(long amountToCheck) {
        return false;
    }
    @Override public boolean isAmountAvailable(double amountToCheck) { return false; }
    @Override public String getUnit() { return ""; }
    @Override public boolean isFull() {
        return false;
    }
}
