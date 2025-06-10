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
package org.cloudsimplus.network.topologies.readers;

import org.cloudsimplus.network.topologies.Point2D;
import org.cloudsimplus.network.topologies.TopologicalLink;
import org.cloudsimplus.network.topologies.TopologicalNode;

/**
 * Represents the state of topology file parsing.
 * @author Thomas Hohnstein
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.0.2
 */
enum ParsingState {
    /**
     * A state indicating to just find the start of the node-declaration.
     */
    NOTHING {
        @Override
        void parse(final TopologyReaderBrite reader, final String line) {
            if (line.contains("Nodes:")) {
                reader.setState(ParsingState.NODES);
            }
        }
    },

    /**
     * A state indicating to retrieve all node-information.
     */
    NODES {
        @Override
        void parse(final TopologyReaderBrite reader, final String line) {
            // first test to step to the next parsing-state (edges)
            if (line.contains("Edges:")) {
                reader.setState(ParsingState.EDGES);
                return;
            }

            // List of fields in the line to parse
            // NodeID, xpos, ypos, inDegree, outDegree, AS_id, type (router/AS)
            final Integer[] parsedFields = {0, 0, 0};
            if (!reader.parseLine(line, parsedFields, Integer::valueOf)) {
                return;
            }

            final Point2D coordinates = new Point2D(parsedFields[1], parsedFields[2]);
            final TopologicalNode topoNode = new TopologicalNode(parsedFields[0], coordinates);
            reader.getGraph().addNode(topoNode);
        }
    },

    /**
     * A state indicating to retrieve all edges-information.
     */
    EDGES {
        @Override
        void parse(final TopologyReaderBrite reader, final String line) {
            // List of fields in the line to parse
            // EdgeID, fromNode, toNode, euclideanLength, linkDelay, linkBandwidth, AS_from, AS_to, type
            final Double[] parsedFields = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            if (!reader.parseLine(line, parsedFields, Double::valueOf)) {
                return;
            }

            final int fromNode = parsedFields[1].intValue();
            final int toNode = parsedFields[2].intValue();
            final double linkDelay = parsedFields[4];
            final double linkBandwidth = parsedFields[5];

            reader.getGraph().addLink(new TopologicalLink(fromNode, toNode, linkDelay, linkBandwidth));
        }
    };

    /**
     * Parses a line from the trace file.
     *
     * @param reader the reader processing the file
     * @param line   current line read from the file
     */
    abstract void parse(final TopologyReaderBrite reader, final String line);
}
