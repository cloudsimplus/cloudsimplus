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
package org.cloudbus.cloudsim.mocks;

import org.cloudbus.cloudsim.core.CloudSim;
import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

import java.util.List;
import java.util.function.Consumer;

/**
 * A class that provides a set of methods to mock the {@link CloudSim} class
 * using {@link EasyMock}. Each method in this class provides a mock for a
 * method with the same name in the CloudSim class.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class CloudSimMocker {
    /**
     * The created CloudSim mock object.
     */
    private final CloudSim mock;

    /**
     * Instantiates a CloudSimMocker that creates a {@link CloudSim} mock
     * object. The constructor is used just internally by the
     * {@link #createMock(java.util.function.Consumer)} method to create a
     * Mocker object.
     */
    private CloudSimMocker() {
        this.mock = EasyMock.createMock(CloudSim.class);
    }

    /**
     * Creates a CloudSim mock object. It requires a {@link Consumer} object as
     * parameter (that can be a lambda expression) where the developer defines
     * what methods of the Mocker class will be called. Each CloudSimMocker's
     * method call defines a method in the mocked CloudSim class that is
     * expected to be called.
     * <p>
     * An usage example can be as follows:
     * <br>
     * {@code CloudSimMocker.createMock(mocker -> mocker.clock(10).getEntityName(1));}
     * </p>
     * This example will mock the static methods clock and getEntityName
     * from CloudSim class, making it return 10 and 1, respectively.
     *
     * <p>
     * If you a mocked method to return 2 different values in different calls,
     * use a code like that:
     * <br>
     * {@code CloudSimMocker.createMock(mocker -> mocker.clock(10).clock(11));}
     * </p>
     * This example will make the mocked CloudSim.clock() method to return 10
     * for the first time it is called inside a unit test and 11 for the second
     * time.
     *
     * <p>
     * Realize that the provided examples uses lambda expression as a parameter
     * to the createMock method, defining which method calls are expected in the
     * mocked CloudSim class.</p>
     *
     * @param consumer a {@link Consumer} that will receive the CloudSimMocker instance
     *                 to allow the developer to call some of its methods to mock
     *                 methods from CloudSim class
     * @return the created CloudSim mock object
     */
    public static CloudSim createMock(final Consumer<CloudSimMocker> consumer) {
        final CloudSimMocker mocker = new CloudSimMocker();
        consumer.accept(mocker);
        EasyMock.expect(mocker.mock.isRunning()).andReturn(true).anyTimes();
        CloudSimMocker.replay(mocker.mock);
        return mocker.mock;
    }

    /**
     * Makes the {@link CloudSim#clock()} method from the mocked CloudSim class
     * to return a given value.
     *
     * @param clockTimeToReturn the value that the {@link CloudSim#clock()}
     * method must return
     * @return
     */
    public IExpectationSetters<Double> clock(final double clockTimeToReturn) {
        return EasyMock
                .expect(mock.clock())
                .andReturn(clockTimeToReturn);
    }

    public IExpectationSetters<Boolean> isTerminationTimeSet() {
        return EasyMock
            .expect(mock.isTerminationTimeSet())
            .andReturn(false);
    }

    public IExpectationSetters<Integer> getNumEntities() {
        return EasyMock
            .expect(mock.getNumEntities())
            .andReturn(1);
    }

    public IExpectationSetters<Integer> addEntity() {
        mock.addEntity(EasyMock.anyObject());
        return EasyMock.expectLastCall();
    }

    public IExpectationSetters<String> clockStr() {
        return EasyMock
            .expect(mock.clockStr())
            .andReturn("0");
    }

    /**
     * Makes the {@link CloudSim#clock()} method from the mocked CloudSim class
     * to return each one of the values inside the given List for each
     * time it is called.
     *
     * @param clockTimesToReturn the values that the {@link CloudSim#clock()}
     * method will return in each call
     */
    public void clock(final List<Integer> clockTimesToReturn) {
        clockTimesToReturn.stream().mapToDouble(time -> time).forEach(t -> EasyMock.expect(mock.clock()).andReturn(t).once());
    }

    /**
     * Makes the {@link CloudSim#getMinTimeBetweenEvents()} method from the mocked CloudSim class
     * to return a given value.
     *
     * @param clockTimeToReturn the value that the {@link CloudSim#getMinTimeBetweenEvents()}
     * method must return
     * @return
     */
    public IExpectationSetters<Double> getMinTimeBetweenEvents(final double clockTimeToReturn) {
        return EasyMock
            .expect(mock.getMinTimeBetweenEvents())
            .andReturn(clockTimeToReturn);
    }

    public void sendNow() {
        mock.sendNow(EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyInt(), EasyMock.anyObject());
        EasyMock.expectLastCall();
    }

    /**
     * Finishes the mocking process, making the mocked CloudSim class ready to
     * use. The method is used just internally as the final step in the
     * {@link #createMock(java.util.function.Consumer)} method.
     * @param mock the created CloudSim mock to replay
     */
    private static void replay(CloudSim mock) {
        EasyMock.replay(mock);
    }

    /**
     * Checks if the expected calls to CloudSim class were in fact made. Calling
     * this method is an optional step that might be performed, at the end of
     * the test case using the mocked CloudSim class, to verify if the methods
     * in CloudSim class that were expected to be called were in fact called the
     * number of expected times.
     * @param mock the created CloudSim mock to verify
     */
    public static void verify(CloudSim mock) {
        EasyMock.verify(mock);
    }

}
