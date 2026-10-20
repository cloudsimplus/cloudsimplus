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
package org.cloudsimplus.util;

import org.cloudsimplus.cloudlets.Cloudlet;

/// A data class to store expected results about the execution of a given cloudlet
/// during unit test execution.
/// The class is supposed to be used by Unit and Integration/Functional Tests
/// in order to validate the results of a executed cloudlet.
///
/// @author Manoel Campos da Silva Filho
public final class ExpectedCloudletResults {
    /// @see #getExpectedExecTime()
    private final double expectedExecTime;
    /// @see #getExpectedStartTime()
    private final double expectedStartTime;
    /// @see #getExpectedFinishTime()
    private final double expectedFinishTime;
    /// @see #getCloudlet()
    private Cloudlet cloudlet;

    public ExpectedCloudletResults(final double execTime, final double startTime, final double finishTime) {
        this.expectedExecTime = execTime;
        this.expectedStartTime = startTime;
        this.expectedFinishTime = finishTime;
    }

    /// The expected execution time of the [#cloudlet].
    /// @return
    public double getExpectedExecTime() {
        return expectedExecTime;
    }

    /// The expected start time of the [#cloudlet].
    /// @return
    public double getExpectedStartTime() {
        return expectedStartTime;
    }

    /// The expected finish time of the [#cloudlet].
    /// @return
    public double getExpectedFinishTime() {
        return expectedFinishTime;
    }

    /// @return the cloudlet to check if execution results were as expected.
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    /// Sets the cloudlet to check if execution results were as expected.
    /// @param cloudlet
    public void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

}
