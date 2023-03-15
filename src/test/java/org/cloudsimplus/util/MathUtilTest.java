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
