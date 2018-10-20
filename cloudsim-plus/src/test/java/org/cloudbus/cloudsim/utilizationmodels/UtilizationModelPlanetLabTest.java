package org.cloudbus.cloudsim.utilizationmodels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilizationModelPlanetLabTest {

    /**
     * Time interval (in seconds) in which the data inside a PlanetLab trace file is collected.
     */
    public static final double SCHEDULING_INTERVAL = 300;

    public static final String FILE = "146-179_surfsnel_dsl_internl_net_colostate_557.dat";

    private UtilizationModelPlanetLab instance;

    @BeforeEach
    public void setUp() {
        instance = UtilizationModelPlanetLab.getInstance(FILE, SCHEDULING_INTERVAL);
    }

    @Test
    public void testGetIntervalSize1() {
        final int expected = 1;
        assertEquals(expected, instance.getIntervalSize(1, 2));
    }

    @Test
    public void testGetIntervalSize1EndLowerThanStart() {
        final int expected = 1;
        assertEquals(expected, instance.getIntervalSize(287, 0));
    }

    @Test
    public void testGetIntervalSize10EndLowerThanStart() {
        final int expected = 10;
        assertEquals(expected, instance.getIntervalSize(287, 9));
    }

    @Test
    public void testGetIntervalSize20EndLowerThanStart() {
        final int expected = 20;
        assertEquals(expected, instance.getIntervalSize(277, 9));
    }

    @Test
    public void testGetIntervalSize4EndLowerThanStart() {
        final int expected = 4;
        assertEquals(expected, instance.getIntervalSize(286, 2));
    }

    @Test
    public void testGetIntervalSize10() {
        final int expected = 10;
        assertEquals(expected, instance.getIntervalSize(1, 11));
    }

    @Test
    public void testGetSecondsInsideInterval1() {
        final int expected = 300;
        assertEquals(expected, instance.getSecondsInsideInterval(1, 2));
    }

    @Test
    public void testGetSecondsInsideInterval10() {
        final int expected = 300;
        assertEquals(expected, instance.getSecondsInsideInterval(1, 2));
    }

    @Test
    public void testGetSecondsInsideInterval10EndLowerThanStart() {
        final int expected = 3000;
        assertEquals(expected, instance.getSecondsInsideInterval(287, 9));
    }

    @Test
    public void testGetSecondsInsideInterval20EndLowerThanStart() {
        final int expected = 6000;
        assertEquals(expected, instance.getSecondsInsideInterval(277, 9));
    }

    @Test
    public void testGetUtilization() {
        final double expected1 = (24 + 0.2 * SCHEDULING_INTERVAL * (34 - 24) / SCHEDULING_INTERVAL) / 100;
        final double expected2 = (18 + 0.7 * SCHEDULING_INTERVAL * (21 - 18) / SCHEDULING_INTERVAL) / 100;

        assertAll(
            () -> assertEquals(0.24, instance.getUtilization(0)),
            () -> assertEquals(0.34, instance.getUtilization(1 * SCHEDULING_INTERVAL)),
            () -> assertEquals(expected1, instance.getUtilization(0.2 * SCHEDULING_INTERVAL), 0.01),
            () -> assertEquals(0.29, instance.getUtilization(2 * SCHEDULING_INTERVAL)),
            () -> assertEquals(0.18, instance.getUtilization(136 * SCHEDULING_INTERVAL)),
            () -> assertEquals(expected2, instance.getUtilization(136.7 * SCHEDULING_INTERVAL), 0.01),
            () -> assertEquals(0.51, instance.getUtilization(287 * SCHEDULING_INTERVAL))
        );
    }

}
