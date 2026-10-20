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
package org.cloudsimplus.datacenters;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.function.Consumer;

/**
 * A class that provides a set of methods to datacenter the {@link Datacenter} class
 * using {@link Mockito}. Each method in this class provides a datacenter for a
 * method with the same name in the Datacenter class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class DatacenterMocker {
    /**
     * The created Datacenter datacenter object.
     */
    private final Datacenter datacenter;
    private final DatacenterCharacteristics characteristics;

    private DatacenterMocker() {
        this.datacenter = Mockito.mock(Datacenter.class);
        this.characteristics = Mockito.mock(DatacenterCharacteristics.class);
    }

    public static Datacenter createMock(final Consumer<DatacenterMocker> consumer) {
        final DatacenterMocker mocker = new DatacenterMocker();
        consumer.accept(mocker);
        return mocker.datacenter;
    }

    public OngoingStubbing<DatacenterCharacteristics> getCharacteristics() {
        return Mockito.when(datacenter.getCharacteristics()).thenReturn(characteristics);
    }

    public OngoingStubbing<Double> getCostPerBw(final double cost) {
        return Mockito.when(characteristics.getCostPerBw()).thenReturn(cost);
    }

    public OngoingStubbing<Double> getCostPerSecond(final double cost) {
        return Mockito.when(characteristics.getCostPerSecond()).thenReturn(cost);
    }

    public OngoingStubbing<Double> getCostPerMem(final double cost) {
        return Mockito.when(characteristics.getCostPerMem()).thenReturn(cost);
    }

    public OngoingStubbing<Double> getCostPerStorage(final double cost) {
        return Mockito.when(characteristics.getCostPerStorage()).thenReturn(cost);
    }
}
