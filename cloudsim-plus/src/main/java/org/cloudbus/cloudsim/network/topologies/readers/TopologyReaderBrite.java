/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.topologies.readers;

import org.cloudbus.cloudsim.network.topologies.Point2D;
import org.cloudbus.cloudsim.network.topologies.TopologicalGraph;
import org.cloudbus.cloudsim.network.topologies.TopologicalLink;
import org.cloudbus.cloudsim.network.topologies.TopologicalNode;
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
    enum ParseState {
        /**
         * Represents the state indicating to just find the start of the node-declaration.
         */
        NOTHING {
            @Override
            void parse(final TopologyReaderBrite reader, final String line) {
                if (line.contains("Nodes:")) {
                    reader.state = ParseState.NODES;
                }
            }
        },

        /** Represents the state indicating to retrieve all node-information. */
        NODES {
            @Override
            void parse(final TopologyReaderBrite reader, final String line) {
                // first test to step to the next parsing-state (edges)
                if (line.contains("Edges:")) {
                    reader.state = ParseState.EDGES;
                    return;
                }

                // List of fields in the line to parse
                // NodeID, xpos, ypos, inDegree, outDegree, AS_id, type(router/AS)
                final Integer[] parsedFields = {0, 0, 0};
                if(!reader.parseLine(line, parsedFields, Integer::valueOf)){
                    return;
                }

                final Point2D coordinates = new Point2D(parsedFields[1], parsedFields[2]);
                final TopologicalNode topoNode = new TopologicalNode(parsedFields[0], coordinates);
                reader.graph.addNode(topoNode);
            }
        },

        /** Represents the state indicating to retrieve all edges-information. */
        EDGES {
            @Override
            void parse(final TopologyReaderBrite reader, final String line) {
                // List of fields in the line to parse
                // EdgeID, fromNode, toNode, euclideanLength, linkDelay, linkBandwidth, AS_from, AS_to, type
                final Double[] parsedFields = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                if(!reader.parseLine(line, parsedFields, Double::valueOf)){
                    return;
                }

                final int fromNode = parsedFields[1].intValue();
                final int toNode = parsedFields[2].intValue();
                final double linkDelay = parsedFields[4];
                final double linkBandwidth = parsedFields[5];

                reader.graph.addLink(new TopologicalLink(fromNode, toNode, linkDelay, linkBandwidth));
            }
        };

        /**
         * Parses a line from the trace file
         * @param reader the reader processing the file
         * @param line current line read from the file
         */
        abstract void parse(final TopologyReaderBrite reader, final String line);
    };

    private ParseState state = ParseState.NOTHING;

    /**
     * The network Topological Graph.
     */
    private TopologicalGraph graph;

    @Override
    public TopologicalGraph readGraphFile(final String filename) {
        return readGraphFile(ResourceLoader.newInputStreamReader(filename));
    }

    @Override
    public TopologicalGraph readGraphFile(final InputStreamReader reader) {
        graph = new TopologicalGraph();
        try(var buffer = new BufferedReader(reader)) {
            String nextLine;
            while ((nextLine = buffer.readLine()) != null) {
                state.parse(this, nextLine);
            }
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
    private <T extends Number> boolean parseLine(
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
