/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.network.topologies.readers;

import org.cloudsimplus.network.topologies.TopologicalGraph;

import java.io.InputStreamReader;
import java.io.UncheckedIOException;

/**
 * An interface to be implemented by classes that read
 * a network graph (topology) from a file with a specific format.
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 * @see TopologyReaderBrite
 */
public interface TopologyReader {
	/**
	 * Reads a file and creates a {@link TopologicalGraph} object.
	 *
	 * @param filename name of the file to read
	 * @return the created TopologicalGraph
	 * @throws UncheckedIOException when the file cannot be accessed
	 */
	TopologicalGraph readGraphFile(String filename);

    /**
     * Reads a file and creates an {@link TopologicalGraph} object.
     *
     * @param reader the {@link InputStreamReader} to read the file
     * @return the created TopologicalGraph
     * @throws UncheckedIOException when the file cannot be accessed
     */
    TopologicalGraph readGraphFile(InputStreamReader reader);
}
