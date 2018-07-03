/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudbus.cloudsim.resources;

/**
 * An abstract implementation of a {@link Resource}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public abstract class ResourceAbstract implements Resource {
    /** @see #getCapacity() */
    protected long capacity;

    public ResourceAbstract(final long capacity){
        if(!isCapacityValid(capacity)) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }

        this.capacity = capacity;
    }

    private boolean isCapacityValid(final long capacity) {
        return capacity >= 0;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getAllocatedResource() {
        return getCapacity() - getAvailableResource();
    }

    @Override
    public boolean isAmountAvailable(final long amountToCheck) {
        return getAvailableResource() >= amountToCheck;
    }

    @Override
    public boolean isAmountAvailable(final double amountToCheck) {
        return isAmountAvailable((long)amountToCheck);
    }

    public boolean isResourceAmountBeingUsed(final long amountToCheck) {
        return getAllocatedResource() >= amountToCheck;
    }

    public boolean isSuitable(final long newTotalAllocatedResource) {
        if(newTotalAllocatedResource <= getAllocatedResource()) {
            return true;
        }

        final long allocationDifference = newTotalAllocatedResource - getAllocatedResource();
        return isAmountAvailable(allocationDifference);
    }
}
