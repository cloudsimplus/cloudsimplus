/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.topologies.readers;

import org.cloudbus.cloudsim.network.topologies.TopologicalGraph;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An interface to be implemented by classes that read
 * a network graph (topology) from a file name with a specific format.
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public interface TopologyReader {

	/**
	 * Reads a file and creates an {@link TopologicalGraph} object.
	 *
	 * @param filename Name of the file to read
	 * @return The created TopologicalGraph
	 * @throws IOException when the file cannot be accessed
	 */
	TopologicalGraph readGraphFile(String filename);

    /**
     * Reads a file and creates an {@link TopologicalGraph} object.
     *
     * @param streamReader the {@link InputStreamReader} to read the file
     * @return The created TopologicalGraph
     * @throws IOException when the file cannot be accessed
     */
    TopologicalGraph readGraphFile(InputStreamReader streamReader);

}
