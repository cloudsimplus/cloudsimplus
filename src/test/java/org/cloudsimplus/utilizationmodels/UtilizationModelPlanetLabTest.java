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
package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.util.ResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilizationModelPlanetLabTest {
    private static final String TEMP_TRACE = "temp-planetlab-trace.txt";

    /// Time interval (in seconds) in which the data inside a PlanetLab trace file is collected.
    private static final int SCHEDULING_INTERVAL = 300;

    private static final String FILE = "146-179_surfsnel_dsl_internl_net_colostate_557.dat";

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

    private String createTempTraceFile(final int numLines, boolean includeHeaderWithLinesNumber){
        final OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
        final String dir = ResourceLoader.getResourcePath(getClass(), "./");
        final var path = Paths.get(dir, TEMP_TRACE);
        try (BufferedWriter writer = Files.newBufferedWriter(path, options)){
            if(includeHeaderWithLinesNumber){
                writer.write("#" + numLines + System.lineSeparator());
            }

            for (int i = 0; i < numLines; i++) {
                writer.write(i+System.lineSeparator());
            }

            return path.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void checkUtilizationValuesFromTempTrace(final UtilizationModelPlanetLab planetlab, final int linesToRead) {
        final Executable[] executables = new Executable[linesToRead];
        IntStream.range(0, linesToRead).forEach(i -> {
            final double utilizationPercent = Math.min(i / 100.0, 1);
            final String msg = "Value read from the file %s at line %d is not as expected".formatted(TEMP_TRACE, i);
            assertEquals(utilizationPercent, planetlab.getUtilization(i * SCHEDULING_INTERVAL), 0.0001, msg);
        });
    }

    @Test
    public void testRead3LinesFromFileWith4LinesAndNoHeader() {
        final int totalLines = 4;
        final int linesToRead = 3;
        final String path = createTempTraceFile(totalLines, false);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, linesToRead);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
    }

    @Test
    public void testRead3LinesFromFileWith4LinesAndHeader() {
        final int totalLines = 4;
        final int linesToRead = 3;
        final String path = createTempTraceFile(totalLines, true);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, linesToRead);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
    }

    @Test
    public void testScaleValues() {
        final double expectedValues[] = {0.0, 0.2, 0.4, 0.6};
        final int totalLines = expectedValues.length;
        final String path = createTempTraceFile(totalLines, true);
        final var planetlab = new UtilizationModelPlanetLab(path, cpuUtilization -> cpuUtilization*20);
        for (int i = 0; i < totalLines; i++) {
            final int time = i * SCHEDULING_INTERVAL;
            assertEquals(expectedValues[i], planetlab.getUtilization(time));
        }
    }

    @Test
    public void testReadAllLinesFromFileWithHeader() {
        final int totalLines = 4;
        final String path = createTempTraceFile(totalLines, true);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL);
        assertEquals(totalLines, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, totalLines);
    }

    @Test
    public void testReadAllLinesFromFileWith4LinesAndHeader() {
        final int totalLines = 4;
        final String path = createTempTraceFile(totalLines, true);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, totalLines);
        assertEquals(totalLines, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, totalLines);
    }

    @Test
    public void testReadAllLinesFromFileWith4LinesAndNoHeader() {
        final int totalLines = 4;
        final String path = createTempTraceFile(totalLines, false);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, totalLines);
        assertEquals(totalLines, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, totalLines);
    }

    @Test
    public void testReadDefaultNumLinesFromFileWithNoHeader() {
        final int linesToRead = UtilizationModelPlanetLab.DEF_DATA_SAMPLES;
        final int totalLines = linesToRead+10;
        final String path = createTempTraceFile(totalLines, false);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, linesToRead);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
    }

    @Test
    public void testReadDefaultNumLinesFromFileWithNoHeader2() {
        final int linesToRead = UtilizationModelPlanetLab.DEF_DATA_SAMPLES;
        final int totalLines = linesToRead+10;
        final String path = createTempTraceFile(totalLines, false);
        final var planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, -1);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
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

}
