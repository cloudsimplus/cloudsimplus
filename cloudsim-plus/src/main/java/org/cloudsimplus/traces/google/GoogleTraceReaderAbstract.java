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
package org.cloudsimplus.traces.google;

import org.cloudsimplus.traces.TraceReaderBase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An abstract class for creating <a href="https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md">Google Cluster Trace</a>
 * readers.
 *
 * @param <T> the type of objects that will be created for each line read from the trace file
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
abstract class GoogleTraceReaderAbstract<T> extends TraceReaderBase {
    private Consumer<? extends GoogleTraceReaderAbstract> preProcess;

    /** A Set of objects immediately created from the trace file.
     * The type <T> of the objects depends on each concrete class.
     * For instance, the {@link GoogleMachineEventsTraceReader}
     * creates {@link org.cloudbus.cloudsim.hosts.Host}s.
     * The {@link GoogleTaskEventsTraceReader} creates
     * {@link org.cloudbus.cloudsim.cloudlets.Cloudlet}s.
     */
    protected final Set<T> availableObjects;

    protected GoogleTraceReaderAbstract(final String filePath) throws FileNotFoundException {
        this(filePath, new FileInputStream(filePath));
    }

    protected GoogleTraceReaderAbstract(final String filePath, final InputStream reader) {
        super(filePath, reader);
        this.setFieldDelimiterRegex(",");
        availableObjects = new HashSet<>();
    }

    /**
     * Process the {@link #getFilePath() trace file} creating a Set of objects
     * described in the file.
     *
     * <p>It returns the Set of created objects that were available at timestamp 0 inside the trace file.</p>
     *
     * @return the Set of created objects that were available at timestamp 0 inside the trace file.
     */
    public Set<T> process() {
        preProcess();
        //If the file was not processed yet, process it
        if (availableObjects.isEmpty()) {
            readFile(this::processParsedLine);
            postProcess();
        }

        return availableObjects;
    }

    /**
     * Executes the pre-process before starting to read the trace file,
     * such as checking if required attributes were set.
     */
    protected abstract void preProcess();

    /**
     * Executes some post-process after the trace file was totally parsed.
     */
    protected abstract void postProcess();

    /**
     * Process the parsed line according to the event type.
     *
     * @param parsedLineArray an array containing the field values from the last parsed trace line.
     * @return true if the parsed line was processed, false otherwise
     */
    protected final boolean processParsedLine(final String[] parsedLineArray) {
        this.setLastParsedLineArray(parsedLineArray);
        return processParsedLineInternal();
    }

    /**
     * Process the last parsed trace line.
     * @return true if the parsed line was processed, false otherwise
     *
     * @see #processParsedLine(String[])
     * @see #getLastParsedLineArray()
     */
    protected abstract boolean processParsedLineInternal();


    public void setPreProcess(Consumer<? extends GoogleTraceReaderAbstract> preProcess) {
        this.preProcess = preProcess;
    }

    protected String formatPercentValue(final double percent){
        return String.format("%.1f", percent*100);
    }
}
