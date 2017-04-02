/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.util.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class LogTest {
    private static final ByteArrayOutputStream OUTPUT = new ByteArrayOutputStream();
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final DecimalFormatSymbols dfs
        = DecimalFormatSymbols.getInstance(Locale.getDefault(Locale.Category.FORMAT));

    private static final String S123 = "123.0";
    private static final String TEST_S_TEST = "test %s test";
    private static final String TEST_TEST = "test test";
    private static final String NUMBERS = "123";
    public static final String TEST = "test";
    public static final String TEST_TEST_TEST = "test test test";
    public static final String FORMAT_D = "%d";
    public static final String FORMAT_F = "%.2f";

    @Before
    public void setUp() throws Exception {
        Log.setOutput(OUTPUT);
    }

    @Test
    public void testPrint() throws IOException {
        Log.print(TEST_TEST);
        assertEquals(TEST_TEST, OUTPUT.toString());
        OUTPUT.reset();

        Log.print(123);
        assertEquals(NUMBERS, OUTPUT.toString());
        OUTPUT.reset();

        Log.print(123L);
        assertEquals(NUMBERS, OUTPUT.toString());
        OUTPUT.reset();

        Log.print(123.0);
        assertEquals(S123, OUTPUT.toString());
        OUTPUT.reset();
    }

    @Test
    public void testPrintLine() throws IOException {
        Log.printLine(TEST_TEST);
        assertEquals(TEST_TEST + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printLine(123);
        assertEquals(NUMBERS + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printLine(123L);
        assertEquals(NUMBERS + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printLine(123.0);
        assertEquals(S123 + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();
    }

    @Test
    public void testFormat() throws IOException {
        Log.enable();
        Log.printFormatted(TEST_S_TEST, TEST);
        assertEquals(TEST_TEST_TEST, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormatted(FORMAT_D, 123);
        assertEquals(NUMBERS, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormatted(FORMAT_D, 123L);
        assertEquals(NUMBERS, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormatted(FORMAT_F, 123.01);
        assertEquals(NUMBERS + dfs.getDecimalSeparator() + "01", OUTPUT.toString());
        OUTPUT.reset();
    }

    @Test
    public void testFormatLine() throws IOException {
        OUTPUT.reset();
        Log.printFormattedLine(TEST_S_TEST, TEST);
        assertEquals(TEST_TEST_TEST + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormattedLine(FORMAT_D, 123);
        assertEquals(NUMBERS + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormattedLine(FORMAT_D, 123L);
        assertEquals(NUMBERS + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormattedLine(FORMAT_F, 123.01);
        assertEquals(NUMBERS + dfs.getDecimalSeparator() + "01" + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();
    }

    @Test
    public void testNotDisable1() throws IOException {
        OUTPUT.reset();
        Log.enable();
        assertFalse(Log.isDisabled());

        Log.print(TEST_TEST);
        assertEquals(TEST_TEST, OUTPUT.toString());
    }

    @Test
    public void testNotDisable2() throws IOException {
        Log.enable();
        OUTPUT.reset();

        Log.printLine(TEST_TEST);
        assertEquals(TEST_TEST + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormatted(TEST_S_TEST, TEST);
        assertEquals(TEST_TEST_TEST, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormattedLine(TEST_S_TEST, TEST);
        assertEquals(TEST_TEST_TEST + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();
    }

    @Test
    public void testDisable1() throws IOException {
        Log.disable();

        assertTrue(Log.isDisabled());

        Log.print(TEST_TEST);
        assertEquals("", OUTPUT.toString());
        OUTPUT.reset();

        Log.printLine(TEST_TEST);
        assertEquals("", OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormatted(TEST_S_TEST, TEST);
        assertEquals("", OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormattedLine(TEST_S_TEST, TEST);
        assertEquals("", OUTPUT.toString());
        OUTPUT.reset();
    }

    @Test
    public void testReEnabled() throws IOException {
        Log.enable();

        assertFalse(Log.isDisabled());

        Log.print(TEST_TEST);
        assertEquals(TEST_TEST, OUTPUT.toString());
        OUTPUT.reset();

        Log.printLine(TEST_TEST);
        assertEquals(TEST_TEST + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormatted(TEST_S_TEST, TEST);
        assertEquals(TEST_TEST_TEST, OUTPUT.toString());
        OUTPUT.reset();

        Log.printFormattedLine(TEST_S_TEST, TEST);
        assertEquals(TEST_TEST_TEST + LINE_SEPARATOR, OUTPUT.toString());
        OUTPUT.reset();
    }
}
