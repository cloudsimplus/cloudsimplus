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
package org.cloudsimplus.testbeds;

import org.apache.commons.statistics.descriptive.DoubleStatistics;
import org.apache.commons.statistics.descriptive.Statistic;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ConfidenceIntervalTest {
    private static final double DELTA = 1e-9;

    /// The critical value of the Student's t-distribution for 9 degrees of freedom and 95% confidence,
    /// from the [NIST table](https://www.itl.nist.gov/div898/handbook/eda/section3/eda3672.htm).
    private static final double CRITICAL_VALUE_9_DEGREES = 2.262157163;

    @Test
    void computesConfidenceInterval() {
        final double[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final var stats = DoubleStatistics.of(ConfidenceInterval.REQUIRED_STATISTICS, values);
        final var ci = new ConfidenceInterval(stats, "metric");

        final double stdDev = Math.sqrt(55.0 / 6.0); // sample standard deviation of 1..10
        final double errorMargin = CRITICAL_VALUE_9_DEGREES * stdDev / Math.sqrt(values.length);

        assertTrue(ci.isComputed());
        assertEquals(10, ci.getSamples());
        assertEquals(5.5, ci.getValue(), DELTA);
        assertEquals(stdDev, ci.getStdDev(), DELTA);
        assertEquals(CRITICAL_VALUE_9_DEGREES, ci.getCriticalValue(), DELTA);
        assertEquals(errorMargin, ci.getErrorMargin(), DELTA);
        assertEquals(5.5 - errorMargin, ci.getLowerLimit(), DELTA);
        assertEquals(5.5 + errorMargin, ci.getUpperLimit(), DELTA);
    }

    @Test
    void singleSampleHasNoConfidenceInterval() {
        final var stats = DoubleStatistics.of(ConfidenceInterval.REQUIRED_STATISTICS, 7);
        final var ci = new ConfidenceInterval(stats, "metric");
        assertFalse(ci.isComputed());
        assertEquals(7, ci.getValue());
        assertTrue(ConfidenceInterval.errorMargin(stats).isEmpty());
    }

    @Test
    void rejectsStatisticsWithoutRequiredValues() {
        final var stats = DoubleStatistics.of(EnumSet.of(Statistic.MIN), 1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> new ConfidenceInterval(stats, "metric"));
    }
}
