/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.util;

import java.io.IOException;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;

/**
 * Provides methods to be implemented by classes that generate a
 * list of ({@link Cloudlet Cloudlets}) (jobs) to be submitted
 * to a DatacenterBroker for execution inside some VMs.
 * Such Cloudlets can be generated from different
 * sources such as XML or CSV files containing Cloudlets configurations
 * or from different formats of Datacenter trace files containing execution logs
 * of real applications that can be used to mimic the behaviour of these
 * application in a simulation environment.
 *
 * @author Marcos Dias de Assuncao
 *
 * @see WorkloadFileReader
 */
public interface WorkloadReader {

    /**
     * Generates a list of jobs ({@link Cloudlet Cloudlets}) to be executed.
     *
     * @return a generated Cloudlet list
     */
    List<Cloudlet> generateWorkload() throws IOException;
}
