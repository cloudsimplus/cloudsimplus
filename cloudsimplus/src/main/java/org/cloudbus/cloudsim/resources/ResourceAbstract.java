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
package org.cloudbus.cloudsim.resources;

import org.apache.commons.lang3.StringUtils;

/**
 * An abstract implementation of a {@link Resource}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public abstract class ResourceAbstract implements Resource {
    /** @see #getCapacity() */
    protected long capacity;

    private final String unit;

    public ResourceAbstract(final long capacity, final String unit){
        this.capacity = validateCapacity(capacity);

        if(unit == null || StringUtils.isBlank(unit)) {
            throw new IllegalArgumentException("Resource measurement unit cannot be null or empty");
        }

        this.unit = unit;
    }

    private long validateCapacity(final long capacity) {
        if(capacity < 0){
            throw new IllegalArgumentException("Capacity cannot be negative");
        }

        return capacity;
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

    @Override
    public String getUnit() {
        return unit;
    }
}
