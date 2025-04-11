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
package org.cloudsimplus.mocks;

import lombok.NonNull;
import org.cloudsimplus.core.CloudSimPlus;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.List;
import java.util.function.Consumer;

/**
 * A class that provides a set of methods to mock the {@link CloudSimPlus} class
 * using {@link Mockito}. Each method in this class provides a mock for a
 * method with the same name in the CloudSimPlus class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CloudSimMocker {
    /**
     * The created CloudSimPlus mock object.
     */
    private final CloudSimPlus mock;

    /**
     * Instantiates a CloudSimMocker that creates a {@link CloudSimPlus} mock
     * object. The constructor is used just internally by the
     * {@link #createMock(java.util.function.Consumer)} method to create a
     * Mocker object.
     */
    private CloudSimMocker() {
        this.mock = Mockito.mock(CloudSimPlus.class);
    }

    /**
     * Creates a CloudSimPlus mock object. It requires a {@link Consumer} object as
     * parameter (that can be a lambda expression) where the developer defines
     * what methods of the Mocker class will be called. Each CloudSimMocker's
     * method call defines a method in the mocked CloudSimPlus class that is
     * expected to be called.
     * <p>
     * An usage example can be as follows:
     * <br>
     * {@code CloudSimMocker.createMock(mocker -> mocker.clock(10).getEntityName(1));}
     * </p>
     * This example will mock the static methods clock and getEntityName
     * from CloudSimPlus class, making it return 10 and 1, respectively.
     *
     * <p>
     * If you a mocked method to return 2 different values in different calls,
     * use a code like that:
     * <br>
     * {@code CloudSimMocker.createMock(mocker -> mocker.clock(10).clock(11));}
     * </p>
     * This example will make the mocked CloudSimPlus.clock() method to return 10
     * for the first time it is called inside a unit test and 11 for the second
     * time.
     *
     * <p>
     * Realize that the provided examples uses lambda expression as a parameter
     * to the createMock method, defining which method calls are expected in the
     * mocked CloudSimPlus class.</p>
     *
     * @param consumer a {@link Consumer} that will receive the CloudSimMocker instance
     *                 to allow the developer to call some of its methods to mock
     *                 methods from CloudSimPlus class
     * @return the created CloudSimPlus mock object
     */
    public static CloudSimPlus createMock(final Consumer<CloudSimMocker> consumer) {
        final CloudSimMocker mocker = new CloudSimMocker();
        consumer.accept(mocker);
        Mockito.when(mocker.mock.isRunning()).thenReturn(true);
        return mocker.mock;
    }

    /**
     * Makes the {@link CloudSimPlus#clock()} method from the mocked CloudSimPlus class
     * to return a given value.
     *
     * @param clockTimeToReturn the value that the {@link CloudSimPlus#clock()}
     * method must return
     * @return
     */
    public OngoingStubbing<Double> clock(final double clockTimeToReturn) {
        return Mockito
                .when(mock.clock())
                .thenReturn(clockTimeToReturn);
    }

    /**
     * Makes the {@link CloudSimPlus#clock()} method from the mocked CloudSimPlus class
     * to return each one of the values inside the given List for each
     * time it is called.
     *
     * @param clockTimesToReturns the values that the {@link CloudSimPlus#clock()}
     * method will return in each call
     */
    public void clock(@NonNull final List<Double> clockTimesToReturns) {
        final var size = clockTimesToReturns.size();
        final var firstElement = clockTimesToReturns.get(0);
        final var lastElements = clockTimesToReturns.subList(1, size).toArray(Double[]::new);
        Mockito.when(mock.clock()).thenReturn(firstElement, lastElements);
    }

    public OngoingStubbing<String> clockStr() {
        return Mockito
            .when(mock.clockStr())
            .thenReturn("0");
    }

    public OngoingStubbing<Boolean> isTerminationTimeSet() {
        return Mockito
            .when(mock.isTerminationTimeSet())
            .thenReturn(false);
    }

    public OngoingStubbing<Boolean> isRunning() {
        return Mockito
            .when(mock.isRunning())
            .thenReturn(true);
    }

    public OngoingStubbing<Integer> getNumEntities() {
        return Mockito
            .when(mock.getNumEntities())
            .thenReturn(1);
    }

    public void addEntity() {
        Mockito.doNothing().when(mock).addEntity(Mockito.any());
    }

    /**
     * Makes the {@link CloudSimPlus#getMinTimeBetweenEvents()} method from the mocked CloudSimPlus class
     * to return a given value.
     *
     * @param clockTimeToReturn the value that the {@link CloudSimPlus#getMinTimeBetweenEvents()}
     * method must return
     * @return
     */
    public OngoingStubbing<Double> getMinTimeBetweenEvents(final double clockTimeToReturn) {
        return Mockito
            .when(mock.getMinTimeBetweenEvents())
            .thenReturn(clockTimeToReturn);
    }

    public void sendNow() {
        Mockito.doNothing()
               .when(mock)
               .sendNow(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    public void send() {
        Mockito.doNothing().when(mock).send(Mockito.any());
    }
}
