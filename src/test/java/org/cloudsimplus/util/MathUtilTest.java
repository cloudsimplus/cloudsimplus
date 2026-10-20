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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathUtilTest {
	public static final double[] DATA1 = { 105, 109, 107, 112, 102, 118, 115, 104, 110, 116, 108 };
	public static final double IQR1 = 10;
	public static final double SUM1 = 1206;
	public static final double[] DATA2 = { 2, 4, 7, -20, 22, -1, 0, -1, 7, 15, 8, 4, -4, 11, 11, 12, 3, 12, 18, 1 };
	public static final double IQR2 = 12;
	public static final double[] DATA3 = { 1, 1, 2, 2, 4, 6, 9 };
	public static final double MAD = 1;
	public static final double[] DATA4 = { 1, 1, 2, 2, 4, 6, 9, 0, 10, 0, 0, 0, 0, 0 };
	public static final int NON_ZERO = 9;
	public static final double[] NON_ZERO_TAIL = { 1, 1, 2, 2, 4, 6, 9, 0, 10 };

	@Test
	public void testMad() {
		assertEquals(MAD, MathUtil.mad(DATA3));
	}

	@Test
	public void testIqr1() {
		assertEquals(IQR1, MathUtil.iqr(DATA1));
	}

    @Test
    public void testIqr2() {
        assertEquals(IQR2, MathUtil.iqr(DATA2));
    }

	@Test
	public void testCountNonZeroBeginning() {
		assertEquals(NON_ZERO, MathUtil.countNonZeroBeginning(DATA4));
	}

	@Test
	public void testSum() {
		final List<Double> data1 = new ArrayList<Double>();
		for(final Double number : DATA1) {
			data1.add(number);
		}

		assertEquals(SUM1, MathUtil.sum(data1));

        final List<Double> data2 = new ArrayList<Double>();
		for (final Double number : DATA1) {
			data2.add(number / 10);
		}

		assertEquals(SUM1 / 10, MathUtil.sum(data2));
	}
}
