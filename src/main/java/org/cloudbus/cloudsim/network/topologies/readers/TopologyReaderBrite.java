/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.topologies.readers;

import org.cloudbus.cloudsim.network.topologies.TopologicalGraph;
import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * A network graph (topology) reader that creates a network topology from
 * a file in the <a href="http://www.cs.bu.edu/brite/user_manual/node29.html">BRITE format</a>.
 * A BRITE file is structured as follows:<br>
 * <ul>
 * <li>Node-section: NodeID, xpos, ypos, indegree, outdegree, ASid, type(router/AS)
 * <li>Edge-section: EdgeID, fromNode, toNode, euclideanLength, linkDelay, linkBandwidth, AS_from, AS_to,
 * type
 * </ul>
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class TopologyReaderBrite implements TopologyReader {
    private ParsingState state = ParsingState.NOTHING;

    /**
     * The network Topological Graph.
     */
    private TopologicalGraph graph;

    TopologicalGraph getGraph() {
        return graph;
    }

    void setState(final ParsingState state) {
        this.state = state;
    }

    @Override
    public TopologicalGraph readGraphFile(final String filename) {
        return readGraphFile(ResourceLoader.newInputStreamReader(filename));
    }

    @Override
    public TopologicalGraph readGraphFile(final InputStreamReader reader) {
        graph = new TopologicalGraph();
        try(var buffer = new BufferedReader(reader)) {
            buffer.lines().forEach(line -> state.parse(this, line));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return graph;
    }

    /**
     * Gets a line from a file and parses its fields, considering that all fields have the same type.
     *
     * @param nodeLine the line to be parsed
     * @param parsedFields an output array that will be filled with the values of the parsed fields.
     *                     The array is given empty and returned with the parsed values.
     *                     The number of elements in the array defines the number of fields to be parsed.
     * @param castFunction a function that will convert the String value from each parsed field to
     *                     the value of the elements of the output array.
     * @param <T>          the type of the values of the output array
     * @return true if any field was parsed, false otherwise
     */
    <T extends Number> boolean parseLine(
        final String nodeLine, final T[] parsedFields, final Function<String, T> castFunction)
    {
        final StringTokenizer tokenizer = new StringTokenizer(nodeLine);
        if (!tokenizer.hasMoreElements()) {
            return false;
        }

        for (int i = 0; tokenizer.hasMoreElements() && i < parsedFields.length; i++) {
            parsedFields[i] = castFunction.apply(tokenizer.nextToken());
        }

        return true;
    }
}
