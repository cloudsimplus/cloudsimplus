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

import org.cloudbus.cloudsim.util.TraceReaderAbstract;
import org.cloudsimplus.traces.TraceReaderBase;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An abstract class for creating <a href="https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md">Google Cluster Trace</a>
 * readers.
 *
 * <p>Check important details at {@link TraceReaderAbstract}.</p>
 *
 * @param <T> the type of objects that will be created for each line read from the trace file
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
abstract class GoogleTraceReaderAbstract<T> extends TraceReaderBase {
    /* default */ static final String VAL_SEPARATOR = " -> ";
    /* default */ static final String COL_SEPARATOR = " | ";

    /** A Set of objects immediately created from the trace file.
     * The type <T> of the objects depends on each concrete class.
     * For instance, the {@link GoogleMachineEventsTraceReader}
     * creates {@link org.cloudbus.cloudsim.hosts.Host}s.
     * The {@link GoogleTaskEventsTraceReader} creates
     * {@link org.cloudbus.cloudsim.cloudlets.Cloudlet}s.
     */
    private final Set<T> availableObjects;

    /* default */  GoogleTraceReaderAbstract(final String filePath, final InputStream reader) {
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
            try {
                readFile(this::processParsedLine);
            } catch (Exception e) {
                throw new RuntimeException("Error when processing the trace file. Current trace line: " + getLastLineNumber(), e);
            }

            postProcess();
        }

        return availableObjects;
    }

    /**
     * Executes any pre-process before starting to read the trace file,
     * such as checking if required attributes were set.
     *
     * @TODO Such a method should be defined as a Functional attribute.
     *       Since it won't be implemented by every subclass, by it being abstract,
     *       forces to subclasses to implement it (even if just including an empty method).
     */
    protected abstract void preProcess();

    /**
     * Executes any post-process after the trace file was totally parsed.
     *
     * @TODO Such a method should be defined as a Functional attribute.
     *       Since it won't be implemented by every subclass, by it being abstract,
     *       forces to subclasses to implement it (even if just including an empty method).
     */
    protected abstract void postProcess();

    /**
     * Process the parsed line according to the event type.
     *
     * @param parsedLineArray an array containing the field values from the last parsed trace line.
     * @return true if the parsed line was processed, false otherwise
     */
    /* default */ final boolean processParsedLine(final String[] parsedLineArray) {
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

    /* default */ String formatPercentValue(final double percent){
        return String.format("%.1f", percent*100);
    }

    /**
     * Adds an object T to the list of available objects.
     * @param object the object T to add
     * @return true if the object was added, false otherwise
     * @see #availableObjects
     */
    /* default */ final boolean addAvailableObject(final T object){
        return availableObjects.add(Objects.requireNonNull(object));
    }
}
