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
 * A network graph (topology) readers that creates a network topology from
 * a file in the <a href="http://www.cs.bu.edu/brite/user_manual/node29.html">BRITE format</a>.
 * A BRITE file is structured as follows:<br/>
 * <ul>
 * <li>Node-section: NodeID, xpos, ypos, indegree, outdegree, ASid, type(router/AS)
 * <li>Edge-section: EdgeID, fromNode, toNode, euclideanLength, linkDelay, linkBandwith, AS_from, AS_to,
 * type
 * </ul>
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class TopologyReaderBrite implements TopologyReader {
    private static final int PARSE_NOTHING = 0;
    private static final int PARSE_NODES = 1;
    private static final int PARSE_EDGES = 2;
    private int state = PARSE_NOTHING;

    /**
     * The network Topological Graph.
     */
    private TopologicalGraph graph;


    @Override
    public TopologicalGraph readGraphFile(final String filename) {
        return readGraphFile(ResourceLoader.getFileReader(filename));
    }

    @Override
    public TopologicalGraph readGraphFile(final InputStreamReader streamReader) {
        graph = new TopologicalGraph();
        try(BufferedReader reader = new BufferedReader(streamReader)) {
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                // functionality to diferentiate between all the parsing-states
                // state that should just find the start of node-declaration
                if (state == PARSE_NOTHING) {
                    if (nextLine.contains("Nodes:")) {
                        state = PARSE_NODES;
                    }
                }
                // the state to retrieve all node-information
                else if (state == PARSE_NODES) {
                    // perform the parsing of this node-line
                    parseNodeString(nextLine);
                }
                // the state to retrieve all edges-information
                else if (state == PARSE_EDGES) {
                    parseEdgesString(nextLine);
                }

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return graph;
    }

    /**
     * Parses nodes inside a line from the BRITE file.
     *
     * @param nodeLine A line read from the file
     */
    private void parseNodeString(String nodeLine) {
        // first test to step to the next parsing-state (edges)
        if (nodeLine.contains("Edges:")) {
            state = PARSE_EDGES;
            return;
        }

        // List of fields in the line to parse
        // NodeID, xpos, ypos, inDegree, outDegree, AS_id, type(router/AS)
        final Integer[] parsedFields = {0, 0, 0};
        if(!parseLine(nodeLine, parsedFields, Integer::valueOf)){
            return;
        }

        final Point2D coordinates = new Point2D(parsedFields[1], parsedFields[2]);
        final TopologicalNode topoNode = new TopologicalNode(parsedFields[0], coordinates);
        graph.addNode(topoNode);
    }

    /**
     * Parses edges inside a line from the BRITE file.
     *
     * @param nodeLine A line read from the file
     */
    private void parseEdgesString(String nodeLine) {

        // List of fields in the line to parse
        // EdgeID, fromNode, toNode, euclideanLength, linkDelay, linkBandwidth, AS_from, AS_to, type
        final Double[] parsedFields = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        if(!parseLine(nodeLine, parsedFields, Double::valueOf)){
            return;
        }

        final int fromNode = parsedFields[1].intValue();
        final int toNode = parsedFields[2].intValue();
        final double linkDelay = parsedFields[4];
        final double linkBandwidth = parsedFields[5];

        graph.addLink(new TopologicalLink(fromNode, toNode, linkDelay, linkBandwidth));
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
    private <T extends Number> boolean parseLine(String nodeLine, T[] parsedFields, Function<String, T> castFunction){
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
