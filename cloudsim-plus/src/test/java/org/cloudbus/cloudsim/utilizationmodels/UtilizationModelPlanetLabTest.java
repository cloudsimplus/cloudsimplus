package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.util.ResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilizationModelPlanetLabTest {
    private static final String TEMP_TRACE = "temp-planetlab-trace.txt";

    /**
     * Time interval (in seconds) in which the data inside a PlanetLab trace file is collected.
     */
    public static final int SCHEDULING_INTERVAL = 300;

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

    private String createTempTraceFile(final int numLines, boolean includeHeaderWithLinesNumber){
        final OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
        final String dir = ResourceLoader.getResourcePath(getClass(), "./");
        Path path = Paths.get(dir, TEMP_TRACE);
        try (final BufferedWriter writer = Files.newBufferedWriter(path, options)){
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
            final String msg = String.format("Value read from the file %s at line %d is not as expected", TEMP_TRACE, i);
            assertEquals(utilizationPercent, planetlab.getUtilization(i * SCHEDULING_INTERVAL), 0.0001, msg);
        });
    }

    @Test
    public void testRead3LinesFromFileWith4LinesAndNoHeader() {
        final int totalLines = 4;
        final int linesToRead = 3;
        final String path = createTempTraceFile(totalLines, false);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, linesToRead);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
    }

    @Test
    public void testRead3LinesFromFileWith4LinesAndHeader() {
        final int totalLines = 4;
        final int linesToRead = 3;
        final String path = createTempTraceFile(totalLines, true);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, linesToRead);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
    }

    @Test
    public void testScaleValues() {
        final double expectedValues[] = {0.0, 0.2, 0.4, 0.6};
        final int totalLines = expectedValues.length;
        final String path = createTempTraceFile(totalLines, true);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, cpuUtilization -> cpuUtilization*20);
        for (int i = 0; i < totalLines; i++) {
            final int time = i * SCHEDULING_INTERVAL;
            assertEquals(expectedValues[i], planetlab.getUtilization(time));
        }
    }

    @Test
    public void testReadAllLinesFromFileWithHeader() {
        final int totalLines = 4;
        final String path = createTempTraceFile(totalLines, true);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL);
        assertEquals(totalLines, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, totalLines);
    }

    @Test
    public void testReadAllLinesFromFileWith4LinesAndHeader() {
        final int totalLines = 4;
        final String path = createTempTraceFile(totalLines, true);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, totalLines);
        assertEquals(totalLines, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, totalLines);
    }

    @Test
    public void testReadAllLinesFromFileWith4LinesAndNoHeader() {
        final int totalLines = 4;
        final String path = createTempTraceFile(totalLines, false);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, totalLines);
        assertEquals(totalLines, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, totalLines);
    }

    @Test
    public void testReadDefaultNumLinesFromFileWithNoHeader() {
        final int linesToRead = UtilizationModelPlanetLab.DEF_DATA_SAMPLES;
        final int totalLines = linesToRead+10;
        final String path = createTempTraceFile(totalLines, false);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, linesToRead);
        assertEquals(linesToRead, planetlab.getDataSamples());
        checkUtilizationValuesFromTempTrace(planetlab, linesToRead);
    }

    @Test
    public void testReadDefaultNumLinesFromFileWithNoHeader2() {
        final int linesToRead = UtilizationModelPlanetLab.DEF_DATA_SAMPLES;
        final int totalLines = linesToRead+10;
        final String path = createTempTraceFile(totalLines, false);
        final UtilizationModelPlanetLab planetlab = new UtilizationModelPlanetLab(path, SCHEDULING_INTERVAL, -1);
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
